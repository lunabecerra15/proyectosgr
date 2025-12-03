package com.zapacciano.sgr.config;

import com.zapacciano.sgr.model.*;
import com.zapacciano.sgr.repository.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;
    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    

    public DataInitializer(UsuarioRepository usuarioRepository, 
                           PasswordEncoder passwordEncoder,
                           MesaRepository mesaRepository,
                           ProductoRepository productoRepository,
                           ReservaRepository reservaRepository,
                           ClienteRepository clienteRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mesaRepository = mesaRepository;
        this.productoRepository = productoRepository;
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        
        // --- 1. CARGAR USUARIOS ---
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setEmail("admin@zapacciano.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRol(Rol.ADMIN);
            
            Usuario mozo = new Usuario();
            mozo.setNombre("Mozo");
            mozo.setEmail("mozo@zapacciano.com");
            mozo.setPassword(passwordEncoder.encode("mozo123"));
            mozo.setRol(Rol.MOZO);

            usuarioRepository.saveAll(Arrays.asList(admin, mozo));
            System.out.println(">>> Usuarios creados correctamente.");
        }
        
        // --- 2. CARGAR MESAS ---
        if (mesaRepository.count() == 0) {
            // Mesas x2
            mesaRepository.save(new Mesa(1, 2, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(2, 2, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(3, 2, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(4, 2, EstadoMesa.LIBRE));
            // Mesas x4
            mesaRepository.save(new Mesa(5, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(6, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(7, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(8, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(9, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(10, 4, EstadoMesa.LIBRE));
            // Mesas x8
            mesaRepository.save(new Mesa(11, 8, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(12, 8, EstadoMesa.LIBRE));
            
            System.out.println(">>> Mesas creadas correctamente.");
        }
        
        // --- 3. CARGAR PRODUCTOS CON FOTOS ---
        if (productoRepository.count() == 0) {
            
            // BEBIDAS
            guardarProducto("Agua mineral 500ml", "Agua sin gas", 3000.00, 100, "Bebidas", "https://images.unsplash.com/photo-1548839140-29a749e1cf4d?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Lata cerveza 500ml", "Cerveza rubia, roja o negra", 5000.00, 100, "Bebidas", "https://images.unsplash.com/photo-1608270586620-248524c67de9?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Bebida Gaseosa 350ml", "Línea Coca-Cola", 3500.00, 100, "Bebidas", "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Agua saborizada 500ml", "Sabores varios", 3000.00, 100, "Bebidas", "https://images.unsplash.com/photo-1583577612013-4fecf7bf8f13?q=80&w=666&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
            
            // HAMBURGUESAS
            guardarProducto("Hamburguesa Zapacciano", "Doble con cheddar, panceta, cebolla crispy.", 20000.00, 100, "Hamburguesas", "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Hamburguesa Completa", "Simple con lechuga, tomate, y huevo frito.", 15000.00, 100, "Hamburguesas", "https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Hamburguesa Kids", "Mini simple con cheddar.", 10000.00, 100, "Hamburguesas", "https://images.unsplash.com/photo-1551782450-a2132b4ba21d?auto=format&fit=crop&w=500&q=60");

            // PIZZAS
            guardarProducto("Pizza Mozzarella", "Salsa de tomate, mozzarella y orégano.", 14000.00, 100, "Pizzas", "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Pizza Napolitana", "Mozzarella, tomate fresco y ajo.", 16000.00, 100, "Pizzas", "https://images.pexels.com/photos/13814644/pexels-photo-13814644.jpeg");
            guardarProducto("Pizza con Jamón y Morrón", "Mozzarella, jamón cocido y morrones.", 16000.00, 100, "Pizzas", "https://images.unsplash.com/photo-1565299624946-b28f40a0ae38?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Pizza Jamón Crudo y Rúcula", "Mozzarella, jamón crudo, rúcula y parmesano.", 20000.00, 100, "Pizzas", "https://images.unsplash.com/photo-1513104890138-7c749659a591?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Pizza Cuatro Quesos", "Mozzarella, provolone, azul y parmesano.", 22000.00, 100, "Pizzas", "https://images.unsplash.com/photo-1571407970349-bc81e7e96d47?auto=format&fit=crop&w=500&q=60");

            // PASTAS
            guardarProducto("Smoke Salmon Pasta", "Lingüini con salsa crema y salmón.", 27000.00, 100, "Pastas", "https://images.unsplash.com/photo-1555949258-eb67b1ef0ceb?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Chicken Thai Pasta", "Penne salteado con vegetales y pollo.", 23000.00, 100, "Pastas", "https://images.unsplash.com/photo-1608835291093-394b0c943a75?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Arizona Pasta", "Penne con salsa Alfredo y pollo.", 22000.00, 100, "Pastas", "https://images.unsplash.com/photo-1611270629569-8b357cb88da9?auto=format&fit=crop&w=500&q=60");

            // POSTRES
            guardarProducto("Key Lime Pie", "Tarta de lima.", 10000.00, 100, "Postres", "https://images.unsplash.com/photo-1586718418497-76bcc1e1dbfb?q=80&w=1376&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D");
            guardarProducto("Volcán de Chocolate", "Con bocha de helado.", 10000.00, 100, "Postres", "https://images.unsplash.com/photo-1624353365286-3f8d62daad51?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Brownie Sundae", "Brownie tibio con helado.", 11500.00, 100, "Postres", "https://images.unsplash.com/photo-1564355808539-22fda35bed7e?auto=format&fit=crop&w=500&q=60");
            guardarProducto("Cheesecake", "Cheesecake de frutos rojos.", 13000.00, 100, "Postres", "https://images.unsplash.com/photo-1524351199678-941a58a3df50?auto=format&fit=crop&w=500&q=60");

            System.out.println(">>> Productos con FOTOS creados correctamente.");
        }

        if (reservaRepository.count() == 0) {
            
            // Primero creamos un Cliente Ficticio
            Cliente cliente = new Cliente();
            cliente.setNombre("Juan Probador");
            cliente.setEmail("juan@test.com");
            cliente.setTelefono("11-2233-4455");
            clienteRepository.save(cliente);

            // CASO A: Reserva de AYER (minusDays 1)
            // Esta NO debería aparecer en la pantalla principal, solo en Historial
            Reserva ayer = new Reserva();
            ayer.setFechaHora(LocalDateTime.now().minusDays(1)); // <--- AYER
            ayer.setCantidadPersonas(2);
            ayer.setEstado(EstadoReserva.CONFIRMADA);
            ayer.setMontoSeña(20000);
            ayer.setCliente(cliente);
            ayer.setMesa(mesaRepository.findById(1L).orElse(null));
            reservaRepository.save(ayer);

            // CASO B: Reserva de HOY más tarde (plusHours 2)
            // Esta SÍ debe aparecer en el Dashboard
            Reserva hoy = new Reserva();
            hoy.setFechaHora(LocalDateTime.now().withHour(20).withMinute(0)); //20:00
            hoy.setCantidadPersonas(4);
            hoy.setEstado(EstadoReserva.CONFIRMADA);
            hoy.setMontoSeña(40000);
            hoy.setCliente(cliente);
            hoy.setMesa(mesaRepository.findById(3L).orElse(null));
            reservaRepository.save(hoy);

            // CASO C: Reserva por TRANSFERENCIA (Para probar el botón de Aprobar)
            Reserva transf = new Reserva();
            transf.setFechaHora(LocalDateTime.now().plusDays(1)); // <--- MAÑANA
            transf.setCantidadPersonas(4);
            transf.setEstado(EstadoReserva.EN_REVISION); // <--- Para probar botón aprobar
            transf.setMontoSeña(40000);
            transf.setCodigoComprobante("TRANSF-12345678");
            transf.setCliente(cliente);
            transf.setMesa(mesaRepository.findById(4L).orElse(null));
            reservaRepository.save(transf);

            System.out.println(">>> Reservas de prueba cargadas (Ayer, Hoy y Mañana).");
        }
    }

    // --- Método auxiliar para no repetir código ---
    private void guardarProducto(String nombre, String desc, Double precio, int stock, String cat, String img) {
        Producto p = new Producto(nombre, desc, precio, stock, cat);
        p.setImagenUrl(img);
        productoRepository.save(p);
    }
}