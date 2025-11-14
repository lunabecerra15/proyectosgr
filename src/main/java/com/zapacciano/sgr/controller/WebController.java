package com.zapacciano.sgr.controller;

import com.zapacciano.sgr.model.*;
import com.zapacciano.sgr.repository.MesaRepository;
import com.zapacciano.sgr.repository.ProductoRepository;
import com.zapacciano.sgr.repository.UsuarioRepository;
import com.zapacciano.sgr.service.PdfService;
import com.zapacciano.sgr.service.PedidoService;
import com.zapacciano.sgr.service.ReservaService;
import com.zapacciano.sgr.service.ProductoService;

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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class WebController {

    @Autowired
    private PdfService pdfService;
    @Autowired
    private PedidoService pedidoService;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private ReservaService reservaService;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private ProductoService productoService;

    // --- PÁGINAS PÚBLICAS / LOGIN ---
    @GetMapping("/")
    public String mostrarPaginaDeInicio() { return "index"; }

    @GetMapping("/login")
    public String mostrarPaginaDeLogin() { return "login"; }

    // --- DASHBOARD ADMIN ---
    @GetMapping("/home")
    public String mostrarPaginaDeHome(Model model) {

        model.addAttribute("pedidosActivos", Collections.emptyList());

        List<Pedido> ventasCompletadas = pedidoService.getReporteVentas();
        double totalVendido = ventasCompletadas.stream()
                .mapToDouble(Pedido::getTotal)
                .sum();

        model.addAttribute("ventas", ventasCompletadas);
        model.addAttribute("totalVendido", totalVendido);

        return "home";
    }

    @GetMapping("/home/reporte/pdf")
    public ResponseEntity<byte[]> descargarReporteVentas() {

        // 1. Buscamos las ventas (Gracias al cambio en el Service, solo trae las nuevas)
        List<Pedido> ventas = pedidoService.getReporteVentas();

        // 2. Generamos el PDF con esos datos
        byte[] pdfBytes = pdfService.generarReporteVentas(ventas);

        // Le decimos a la base de datos: "Estas ventas ya las imprimí, archívalas".
        pedidoService.marcarVentasComoReportadas(ventas);

        // 4. Preparamos la descarga del archivo
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        
        // Le puse la fecha al nombre del archivo para que quede más ordenado
        String nombreArchivo = "Cierre_Caja_" + LocalDateTime.now().toLocalDate() + ".pdf";
        headers.setContentDispositionFormData("attachment", nombreArchivo);

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

    // --- VISTAS MOZO ---
    @GetMapping("/pedidos")
    public String mostrarPaginaDePedidos(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(value = "exito", required = false) String exito,
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "cobroExito", required = false) String cobroExito
    ) {

        Usuario mozo = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Mozo no encontrado"));

        List<Pedido> pedidosActivosMozo = pedidoService.getPedidosActivosPorMozo(mozo);
        model.addAttribute("pedidosActivos", pedidosActivosMozo);

        List<Mesa> mesas = mesaRepository.findAll();
        model.addAttribute("mesasDisponibles", mesas);

        List<Producto> productos = productoRepository.findAll();
        model.addAttribute("productosDisponibles", productos);

        model.addAttribute("nombreMozo", mozo.getNombre());

        if (exito != null) {
            model.addAttribute("mensajeExito", "¡Pedido registrado correctamente!");
        }
        if (cobroExito != null) {
            model.addAttribute("mensajeExito", "¡Pedido Cobrado! La mesa ha sido liberada.");
        }
        if (error != null) {
            if (error.equals("vacio")) {
                model.addAttribute("mensajeError", "Error: El pedido no puede estar vacío.");
            } else {
                model.addAttribute("mensajeError", "Error: No se pudo registrar el pedido.");
            }
        }

        return "pedidos";
    }

    @PostMapping("/pedidos")
    public String registrarPedido(
            @RequestParam("mesa") int numeroMesa,
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
            pedidoService.registrarPedido(numeroMesa, idsPrimitivos, cantsPrimitivas, userDetails);
            return "redirect:/pedidos?exito=true";
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pedidos?error=true";
        }
    }

    @PostMapping("/pedidos/cobrar/{id}")
    public String cobrarPedido(
            @PathVariable("id") Long pedidoId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            pedidoService.cobrarPedido(pedidoId);
            redirectAttributes.addAttribute("cobroExito", "true");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Error al cobrar el pedido.");
        }
        return "redirect:/pedidos";
    }

    // --- RESERVAS CLIENTE ---
    @GetMapping("/reservas")
    public String mostrarPaginaDeReservas(Model model,
                                          @RequestParam(value = "error", required = false) String error) {
        model.addAttribute("reserva", new Reserva());
        if (error != null) {
            model.addAttribute("error", error);
        }
        return "reservas";
    }

    @PostMapping("/reservas")
    public String crearReserva(
            @RequestParam("nombreCliente") String nombreCliente,
            @RequestParam("telefonoCliente") String telefonoCliente,
            @RequestParam("emailCliente") String emailCliente,
            @RequestParam("fechaHora") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaHora,
            @RequestParam("cantidadPersonas") int cantidadPersonas,
            RedirectAttributes redirectAttributes
    ) {
        try {
            Reserva nuevaReserva = reservaService.crearReserva(
                    nombreCliente, telefonoCliente, emailCliente, fechaHora, cantidadPersonas
            );
            return "redirect:/reservas/pagar/" + nuevaReserva.getId();

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/reservas";
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

    @GetMapping("/tarjeta")
    public String mostrarPaginaTarjeta(
            @RequestParam(value = "reservaId", required = false) Long reservaId,
            Model model
    ) {
        if (reservaId != null) {
            try {
                Reserva reserva = reservaService.findById(reservaId);
                model.addAttribute("reserva", reserva);
            } catch (Exception e) {
                return "redirect:/reservas/pagar/" + reservaId + "?error=no_encontrado";
            }
        }
        return "tarjeta";
    }

    @PostMapping("/tarjeta")
    public String procesarPagoSimulado(
            @RequestParam("reservaId") Long reservaId,
            @RequestParam(value = "numeroTarjeta", required = false) String numeroTarjeta,
            @RequestParam(value = "nombreTitular", required = false) String nombreTitular,
            @RequestParam(value = "vencimiento", required = false) String vencimiento,
            @RequestParam(value = "cvv", required = false) String cvv,
            RedirectAttributes redirectAttributes
    ) {
        try {
            reservaService.confirmarPago(reservaId);
            return "redirect:/reservas/exito/" + reservaId;

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/tarjeta?reservaId=" + reservaId + "&error=pago_fallido";
        }
    }

    @GetMapping("/reservas/exito/{id}")
    public String mostrarReservaExitosa(@PathVariable("id") Long id, Model model) {
        model.addAttribute("reservaId", id);
        return "reserva_exitosa";
    }

    // --- MENÚ PÚBLICO ---
    @GetMapping("/menu")
    public String mostrarMenuPublico(Model model) {
        List<Producto> todosLosProductos = productoRepository.findAll();
        Map<String, List<Producto>> menuAgrupado = todosLosProductos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria));
        model.addAttribute("menuAgrupado", menuAgrupado);
        return "menu";
    }


    @GetMapping("/admin/productos")
    public String mostrarGestionProductos(
            Model model,
            @RequestParam(value="exito", required = false) String exito
    ) {
        // 1. Buscamos todos los productos
        List<Producto> productos = productoRepository.findAll();
        
        // 2. Los pasamos al HTML
        model.addAttribute("productos", productos);
        
        // 3. (Opcional) Pasamos el mensaje de éxito si venimos de un update
        if (exito != null) {
            model.addAttribute("exito", "¡Precio actualizado correctamente!");
        }
        
        // 4. Mostramos la nueva página
        return "admin-productos"; // Muestra /templates/admin-productos.html
    }
    
    /**
     * Recibe el formulario de actualización de precio de un producto.
     */
    @PostMapping("/admin/productos/editar")
    public String actualizarPrecioProducto(
            @RequestParam("productoId") Long productoId,
            @RequestParam("precio") double nuevoPrecio,
            RedirectAttributes redirectAttributes
    ) {
        try {
            // 1. Llamamos al nuevo servicio para que haga el trabajo
            productoService.actualizarPrecioProducto(productoId, nuevoPrecio);
            
            // 2. Redirigimos de vuelta con un mensaje de éxito
            redirectAttributes.addAttribute("exito", "true");
            
        } catch (Exception e) {
            e.printStackTrace();
            // (Si quisiéramos, podríamos agregar un mensaje de error aquí)
        }
        
        return "redirect:/admin/productos";
    }
}