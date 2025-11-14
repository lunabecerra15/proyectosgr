package com.zapacciano.sgr.config;

import com.zapacciano.sgr.model.Producto;
import com.zapacciano.sgr.repository.ProductoRepository;
import com.zapacciano.sgr.model.EstadoMesa;
import com.zapacciano.sgr.model.Mesa;
import com.zapacciano.sgr.repository.MesaRepository;
import com.zapacciano.sgr.model.Rol;
import com.zapacciano.sgr.model.Usuario;
import com.zapacciano.sgr.repository.UsuarioRepository;

import java.util.Arrays;
//import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final MesaRepository mesaRepository;
    private final ProductoRepository productoRepository;

    public DataInitializer(UsuarioRepository usuarioRepository, 
                           PasswordEncoder passwordEncoder,
                           MesaRepository mesaRepository,
                           ProductoRepository productoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mesaRepository = mesaRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        
        // --- 1. Cargar Usuarios ---
        // ¡¡AQUÍ ESTÁ EL ARREGLO!!
        // Ahora usamos setters para estar 100% seguros
        if (usuarioRepository.count() == 0) {
            
            // Creación del Admin
            Usuario admin = new Usuario();
            admin.setNombre("Admin");
            admin.setEmail("admin@zapacciano.com");
            admin.setPassword(passwordEncoder.encode("admin123")); // <-- Codificamos
            admin.setRol(Rol.ADMIN);
            
            // Creación del Mozo
            Usuario mozo = new Usuario();
            mozo.setNombre("Mozo");
            mozo.setEmail("mozo@zapacciano.com");
            mozo.setPassword(passwordEncoder.encode("mozo123")); // <-- Codificamos
            mozo.setRol(Rol.MOZO);

            // Guardamos ambos
            usuarioRepository.saveAll(Arrays.asList(admin, mozo));
            System.out.println("Usuarios (Admin, Mozo) de prueba creados CON SETTERS.");
        }
        
        // --- 2. Cargar las 12 Mesas (Esto ya estaba bien) ---
        if (mesaRepository.count() == 0) {
            // 4 mesas para 2 personas
            mesaRepository.save(new Mesa(1, 2, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(2, 2, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(3, 2, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(4, 2, EstadoMesa.LIBRE));
            
            // 6 mesas para 4 personas
            mesaRepository.save(new Mesa(5, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(6, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(7, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(8, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(9, 4, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(10, 4, EstadoMesa.LIBRE));
            
            // 2 mesas para 8 personas
            mesaRepository.save(new Mesa(11, 8, EstadoMesa.LIBRE));
            mesaRepository.save(new Mesa(12, 8, EstadoMesa.LIBRE));
            
            System.out.println("¡¡Las 12 mesas de prueba fueron creadas!!.");
        }
        
        // --- 3. Cargar Productos (Esto ya estaba bien) ---
        if (productoRepository.count() == 0) {
            // BEBIDAS
            productoRepository.save(new Producto("Agua mineral 500ml", "Agua sin gas", 3000.00, 100, "Bebidas"));
            productoRepository.save(new Producto("Lata cerveza 500ml", "Cerveza rubia, roja o negra", 5000.00, 100, "Bebidas"));
            productoRepository.save(new Producto("Bebida Gaseosa 350ml", "Línea Coca-Cola (Regular o Zero)", 3500.00, 100, "Bebidas"));
            productoRepository.save(new Producto("Agua saborizada 500ml", "Sabores varios", 3000.00, 100, "Bebidas"));
            
            // HAMBURGUESAS
            productoRepository.save(new Producto("Hamburguesa Zapacciano", "Doble con cheddar, panceta, cebolla crispy. Incluye papas fritas.", 20000.00, 100, "Hamburguesas"));
            productoRepository.save(new Producto("Hamburguesa Completa", "Simple con lechuga, tomate, y huevo frito. Incluye papas fritas.", 15000.00, 100, "Hamburguesas"));
            productoRepository.save(new Producto("Hamburguesa Kids", "Mini simple con cheddar. Incluye papas fritas.", 10000.00, 100, "Hamburguesas"));

            // PIZZAS
            productoRepository.save(new Producto("Pizza Mozzarella", "Salsa de tomate, mozzarella y orégano.", 14000.00, 100, "Pizzas"));
            productoRepository.save(new Producto("Pizza Napolitana", "Mozzarella, tomate fresco en rodajas y ajo.", 16000.00, 100, "Pizzas"));
            productoRepository.save(new Producto("Pizza con Jamón y Morrón", "Mozzarella, jamón cocido y morrones asados.", 16000.00, 100, "Pizzas"));
            productoRepository.save(new Producto("Pizza con Jamón Crudo y Rúcula", "Mozzarella, jamón crudo, rúcula y hebras de parmesano.", 20000.00, 100, "Pizzas"));
            productoRepository.save(new Producto("Pizza Cuatro Quesos", "Mozzarella, provolone, queso azul y parmesano.", 22000.00, 100, "Pizzas"));

            // PASTAS
            productoRepository.save(new Producto("Smoke Salmon Pasta", "Lingüini con salsa crema, salmón ahumado, morrones y eneldo.", 27000.00, 100, "Pastas"));
            productoRepository.save(new Producto("Chicken Thai Pasta", "Penne salteado con vegetales, pollo, salsa de soja y jengibre.", 23000.00, 100, "Pastas"));
            productoRepository.save(new Producto("Arizona Pasta", "Penne con salsa Alfredo, pollo, morrones y especias.", 22000.00, 100, "Pastas"));

            // POSTRES
            productoRepository.save(new Producto("Key Lime Pie", "Tarta de lima con base de galleta y merengue.", 10000.00, 100, "Postres"));
            productoRepository.save(new Producto("Volcán de Chocolate", "Con bocha de helado de crema americana.", 10000.00, 100, "Postres"));
            productoRepository.save(new Producto("Brownie Sundae", "Brownie tibio, helado, salsa de chocolate y nueces.", 11500.00, 100, "Postres"));
            productoRepository.save(new Producto("Zapacciano's Cheesecake", "Cheesecake de frutos rojos.", 13000.00, 100, "Postres"));

            System.out.println("¡¡Productos (menú) de prueba creados!! (" + productoRepository.count() + " productos)");
        }
    }
}