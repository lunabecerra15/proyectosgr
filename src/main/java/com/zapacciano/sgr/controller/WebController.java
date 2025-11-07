package com.zapacciano.sgr.controller;

import com.zapacciano.sgr.model.Cliente;
import com.zapacciano.sgr.model.EstadoReserva;
import com.zapacciano.sgr.model.Mesa; 
import com.zapacciano.sgr.model.Pedido;
import com.zapacciano.sgr.model.Producto; 
import com.zapacciano.sgr.model.Reserva;
import com.zapacciano.sgr.repository.MesaRepository; 
import com.zapacciano.sgr.repository.ProductoRepository; 
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

import java.time.LocalDateTime; 
import java.util.List;

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
            @RequestParam(value = "exito", required = false) String exito,
            @RequestParam(value = "error", required = false) String error
    ) {
        
        List<Producto> productos = productoRepository.findAll();
        List<Mesa> mesas = mesaRepository.findAll(); // <-- Busca las mesas
        
        model.addAttribute("productosDisponibles", productos);
        model.addAttribute("mesasDisponibles", mesas); // <-- Las pasa al HTML
        
        if (exito != null) {
            model.addAttribute("mensajeExito", "¡Pedido registrado correctamente!");
        }
        if (error != null) {
            if (error.equals("vacio")) {
                model.addAttribute("mensajeError", "Error: El pedido no puede estar vacío.");
            } else {
                model.addAttribute("mensajeError", "Error: No se pudo registrar el pedido. Verifique la mesa o los productos.");
            }
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
}