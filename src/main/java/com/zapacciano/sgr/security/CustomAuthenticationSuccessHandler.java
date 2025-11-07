package com.zapacciano.sgr.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

/**
 * Esta clase es nuestro "Recepcionista Inteligente".
 * Se activa DESPUÉS de un login exitoso y decide a qué URL
 * redirigir al usuario basado en su ROL.
 */
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. Obtenemos la lista de roles (Autoridades) del usuario que inició sesión
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        // 2. Revisamos los roles uno por uno
        for (GrantedAuthority authority : authorities) {
            
            // 3. Si encontramos el rol "ROLE_ADMIN"...
            if (authority.getAuthority().equals("ROLE_ADMIN")) {
                // ...lo mandamos a la página de admin
                response.sendRedirect("/home"); 
                return; // ¡Importante! Terminamos la ejecución
            }
            
            // 4. Si encontramos el rol "ROLE_MOZO"...
            if (authority.getAuthority().equals("ROLE_MOZO")) {
                // ...lo mandamos a la página de pedidos
                response.sendRedirect("/pedidos");
                return; // Terminamos la ejecución
            }
        }

        // 5. Si por alguna razón no tiene un rol conocido (un "fallback")
        // Lo mandamos a la página de login con un error.
        response.sendRedirect("/login?error=true");
    }
}

