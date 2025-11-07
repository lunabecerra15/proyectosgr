package com.zapacciano.sgr.security;

import com.zapacciano.sgr.model.Usuario;
import com.zapacciano.sgr.repository.UsuarioRepository;
import org.springframework.security.core.GrantedAuthority; // <-- 1. Importar
import org.springframework.security.core.authority.SimpleGrantedAuthority; // <-- 2. Importar
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List; // <-- 3. Importar

/**
 * Esta clase es el "puente" entre la tabla "usuarios" y Spring Security.
 * Su único trabajo es cargar un usuario por su email y convertirlo
 * en un objeto "UserDetails" que Spring Security pueda entender.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public CustomUserDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        
        // 4. Buscamos al usuario en nuestra base de datos por su email
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        // --- ¡CAMBIO IMPORTANTE AQUÍ! ---
        
        // 5. Creamos la "Autoridad" (Rol) para Spring Security
        // Spring Security REQUIERE que los roles tengan el prefijo "ROLE_"
        // Tomamos nuestro Enum (ej. Rol.ADMIN), lo convertimos a String ("ADMIN")
        // y le agregamos el prefijo.
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name());

        // 6. Devolvemos el "User" de Spring Security.
        // Este objeto contiene el email (username), la contraseña (¡YA HASHEADA!)
        // y la lista de sus roles (autoridades).
        return new User(
            usuario.getEmail(),
            usuario.getPassword(),
            List.of(authority) // <-- 7. Pasamos la lista de roles
        );
    }
}
