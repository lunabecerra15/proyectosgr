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
            
            // Bebidas
            productoRepository.save(new Producto("Agua mineral 500ml", "Bebida", 3000.00, 100)); // ID 1
            productoRepository.save(new Producto("Lata cerveza 500ml", "Bebida", 5000.00, 100)); // ID 2
            // ... (el resto de los 19 productos) ...
            productoRepository.save(new Producto("Bebida Gaseosa-linea coca cola 350ml", "Bebida", 3500.00, 100));
            productoRepository.save(new Producto("Agua saborizada 500ml", "Bebida", 3000.00, 100));
            productoRepository.save(new Producto("Hamburguesa Zapacciano", "Doble cheddar, panceta, cebolla crispy, papas", 20000.00, 100));
            productoRepository.save(new Producto("Hamburguesa Completa", "Simple, lechuga, tomate, huevo frito, papas", 15000.00, 100));
            productoRepository.save(new Producto("Hamburguesa kids", "Mini simple, cheddar, papas", 10000.00, 100));
            productoRepository.save(new Producto("Pizza Mozzarella", "Salsa, mozzarella, aceitunas", 14000.00, 100));
            productoRepository.save(new Producto("Pizza Napolitana", "Mozzarella, tomate, ajo, aceitunas", 16000.00, 100));
            productoRepository.save(new Producto("Pizza con Jamón y morrón", "Mozzarella, jamón, morrón, aceitunas", 16000.00, 100));
            productoRepository.save(new Producto("Pizza con Jamón Crudo y Rucula", "Mozzarella, crudo, rúcula, parmesano", 20000.00, 100));
            productoRepository.save(new Producto("Pizza Cuatro Quesos", "Mozzarella, provolone, roquefort, parmesano", 22000.00, 100));
            productoRepository.save(new Producto("Smoke Salmon Pasta", "Lingüini, crema, salmón ahumado, morrones, eneldo", 27000.00, 100));
            productoRepository.save(new Producto("Chicken Thai Pasta", "Penne, vegetales, pollo, salsa Thai, soja, jengibre", 23000.00, 100));
            productoRepository.save(new Producto("Arizona Pasta", "Penne, salsa Alfredo, pollo, morrones, especias", 22000.00, 100));
            productoRepository.save(new Producto("Key Lime Pie", "Porción de torta de lima", 10000.00, 100));
            productoRepository.save(new Producto("Volcan de chocolate", "Volcán con bocha de helado", 10000.00, 100));
            productoRepository.save(new Producto("Brownie Sundae", "Brownie tibio con helado y salsa", 11500.00, 100));
            productoRepository.save(new Producto("Zapacciano's cheesecake", "Cheesecake de frutos rojos", 13000.00, 100));

            System.out.println("¡¡Productos (menú) de prueba creados!! (19 productos)");
        }
    }
}