package com.zapacciano.sgr.controller;

import com.zapacciano.sgr.model.*;
import com.zapacciano.sgr.repository.MesaRepository; 
import com.zapacciano.sgr.repository.ProductoRepository; 
import com.zapacciano.sgr.repository.UsuarioRepository;
import com.zapacciano.sgr.service.PdfService;
import com.zapacciano.sgr.service.PedidoService;
import com.zapacciano.sgr.service.ReservaService;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable; 
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.format.annotation.DateTimeFormat; 
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime; 
import java.util.List;
import java.util.Collection;

@Controller
public class WebController {

    @Autowired
    private PdfService pdfService;

    @Autowired
    private PedidoService pedidoService;
    
    @Autowired
    private ProductoRepository productoRepository;
    
    @Autowired
    private MesaRepository mesaRepository; // <-- Inyectado
    
    @Autowired
    private ReservaService reservaService;
    
    // La necesitamos para encontrar al Mozo logueado
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    // --- PÁGINAS PÚBLICAS / LOGIN ---

    @GetMapping("/")
    public String mostrarPaginaDeInicio() {
        return "index"; // Muestra index.html
    }
    
    @GetMapping("/login")
    public String mostrarPaginaDeLogin() {
        return "login";
    }

    // --- DASHBOARD ADMIN (/home) ---
    
    @GetMapping("/home")
    public String mostrarPaginaDeHome(Model model) {
        
        List<Pedido> pedidosActivos = pedidoService.getPedidosActivos(); 
        List<Pedido> ventasCompletadas = pedidoService.getReporteVentas(); 
        double totalVendido = ventasCompletadas.stream().mapToDouble(Pedido::getTotal).sum();
        
        model.addAttribute("pedidosActivos", pedidosActivos);
        model.addAttribute("ventas", ventasCompletadas);
        model.addAttribute("totalVendido", totalVendido);
        
        return "home";
    }

    @GetMapping("/home/reporte/pdf")
    public ResponseEntity<byte[]> descargarReporteVentas() {
        byte[] pdfBytes = pdfService.generarReporteVentas();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "Reporte_Ventas_Zapacciano.pdf");
        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
    
    // --- VISTAS MOZO (/pedidos) ---

    @GetMapping("/pedidos")
    public String mostrarPaginaDePedidos(
           Model model, 
            @AuthenticationPrincipal UserDetails userDetails, // <-- Para saber QUIÉN es el mozo
            @RequestParam(value = "exito", required = false) String exito,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "cobroExito", required = false) String cobroExito 
    ) {
        
        // 1. Encontrar al Mozo logueado
        Usuario mozo = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Mozo no encontrado"));

        // 2. Buscar los pedidos PENDIENTES solo de ESE mozo
        List<Pedido> pedidosActivosMozo = pedidoService.getPedidosActivosPorMozo(mozo);
        model.addAttribute("pedidosActivos", pedidosActivosMozo);

        // 3. Buscar mesas LIBRES para el formulario
        List<Mesa> mesasLibres = mesaRepository.findByEstado(EstadoMesa.LIBRE);
        model.addAttribute("mesasDisponibles", mesasLibres);

        // 4. Buscar todos los productos para el formulario
        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productosDisponibles", productos);

        // --- ¡¡LÍNEA DE ARREGLO!! ---
        // Pasamos el nombre del mozo al HTML de forma segura.
        model.addAttribute("nombreMozo", mozo.getNombre());
        
        // 5. Manejar los mensajes de feedback
        if (exito != null) {
            model.addAttribute("mensajeExito", "¡Pedido registrado correctamente!");
        }
        if (cobroExito != null) {
            model.addAttribute("mensajeExito", "¡Pedido Cobrado! La mesa ha sido liberada.");
        }
        if (error != null) {
            // ... (lógica de mensajes de error que ya tenías) ...
            model.addAttribute("mensajeError", "Error: No se pudo registrar el pedido.");
        }
        
        return "pedidos"; 
    }
    
    @PostMapping("/pedidos")
    public String registrarPedido(
            @RequestParam("mesa") int numeroMesa, // <-- Recibe el NÚMERO de mesa
            @RequestParam(value = "productoId", required = false) Long[] productoIds,
            @RequestParam(value = "cantidad", required = false) Integer[] cantidades,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        
        if (productoIds == null || productoIds.length == 0 || cantidades == null || cantidades.length == 0) {
            return "redirect:/pedidos?error=vacio";
        }
        
        long[] idsPrimitivos = new long[productoIds.length];
        for (int i = 0; i < productoIds.length; i++) {
            idsPrimitivos[i] = productoIds[i] != null ? productoIds[i] : 0; 
        }

        int[] cantsPrimitivas = new int[cantidades.length];
        for (int i = 0; i < cantidades.length; i++) {
            cantsPrimitivas[i] = cantidades[i] != null ? cantidades[i] : 0;
        }
        
        try {
            // Llama al servicio (que ahora SÍ puede buscar la mesa)
            pedidoService.registrarPedido(numeroMesa, idsPrimitivos, cantsPrimitivas, userDetails);
            return "redirect:/pedidos?exito=true";
            
        } catch (Exception e) {
            e.printStackTrace(); // <-- ¡El error real saldrá aquí en la consola!
            return "redirect:/pedidos?error=true";
        }
    }


    // --- ¡¡NUEVO ENDPOINT PARA EL BOTÓN 'COBRAR'!! ---
    
    /**
     * Procesa la acción de "Cobrar" un pedido.
     * Cambia el estado del Pedido a COMPLETADO y libera la Mesa.
     */
    @PostMapping("/pedidos/cobrar/{id}")
    public String cobrarPedido(
            @PathVariable("id") Long pedidoId, 
            RedirectAttributes redirectAttributes
    ) {
        try {
            pedidoService.cobrarPedido(pedidoId);
            // Enviamos un mensaje de éxito
            redirectAttributes.addAttribute("cobroExito", "true");
        } catch (Exception e) {
            e.printStackTrace();
            // Enviamos un mensaje de error
            redirectAttributes.addAttribute("error", "Error al cobrar el pedido.");
        }
        
        // Siempre redirigimos de vuelta a la página de pedidos
        return "redirect:/pedidos";
    }
    
    // --- VISTAS CLIENTE (/reservas) ---
    // (Esto ya funciona, no se toca)
    
    @GetMapping("/reservas")
    public String mostrarPaginaDeReservas(Model model) {
        model.addAttribute("reserva", new Reserva()); 
        return "reservas"; 
    }

    @PostMapping("/reservas")
    public String crearReserva(
            @RequestParam("nombreCliente") String nombreCliente,
            @RequestParam("telefonoCliente") String telefonoCliente,
            @RequestParam("emailCliente") String emailCliente,
            @RequestParam("fechaHora") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam("cantidadPersonas") int cantidadPersonas,
            Model model
    ) {
        try {
            Reserva nuevaReserva = reservaService.crearReserva(
                nombreCliente, telefonoCliente, emailCliente, fechaHora, cantidadPersonas
            );
            return "redirect:/reservas/pagar/" + nuevaReserva.getId();

        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error al crear la reserva: " + e.getMessage());
            return "reservas"; 
        }
    }
    
    @GetMapping("/reservas/pagar/{id}")
    public String mostrarPaginaDePago(@PathVariable("id") Long id, Model model) {
        try {
            Reserva reserva = reservaService.findById(id);
            model.addAttribute("reserva", reserva);
            return "pagar_seña"; 
        } catch (Exception e) {
            return "redirect:/reservas?error=no_encontrado";
        }
    }
    
    @PostMapping("/reservas/confirmar_pago")
    public String confirmarPago(@RequestParam("reservaId") Long reservaId) {
        try {
            reservaService.confirmarPago(reservaId);
            return "redirect:/reservas/exito/" + reservaId; 
        } catch (Exception e) {
            return "redirect:/reservas/pagar/" + reservaId + "?error=true";
        }
    }

    @GetMapping("/reservas/exito/{id}")
    public String mostrarReservaExitosa(@PathVariable("id") Long id, Model model) {
        model.addAttribute("reservaId", id);
        return "reserva_exitosa"; 
    }

    /**
     * Mostrar la pantalla de ingreso de datos de tarjeta.
     * Si se recibe reservaId como parámetro, lo carga y lo añade al modelo
     * para que `tarjeta.html` pueda mostrar información de la reserva.
     */
    @GetMapping("/tarjeta")
    public String mostrarPaginaTarjeta(@RequestParam(value = "reservaId", required = false) Long reservaId, Model model) {
        if (reservaId != null) {
            try {
                Reserva reserva = reservaService.findById(reservaId);
                model.addAttribute("reserva", reserva);
            } catch (Exception e) {
                // Si no se encuentra, simplemente no agregamos la reserva al modelo
                // y dejamos que la vista maneje la ausencia de datos.
            }
        }
        return "tarjeta";
    }
}