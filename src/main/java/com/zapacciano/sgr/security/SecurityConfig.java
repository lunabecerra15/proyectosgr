package com.zapacciano.sgr.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final AuthenticationSuccessHandler customAuthenticationSuccessHandler;

    public SecurityConfig(UserDetailsService userDetailsService, 
                          AuthenticationSuccessHandler customAuthenticationSuccessHandler) {
        this.userDetailsService = userDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        return builder.build();
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        
        http
            // Deshabilitamos CSRF para que funcionen los POST (como el de pago)
            .csrf(csrf -> csrf.disable()) 
            
            .authorizeHttpRequests(authz -> authz
                
                // --- PÁGINAS PÚBLICAS (Cliente) ---
                .requestMatchers(
                        "/", 
                        "/menu", 
                        "/reservas", 
                        "/reservas/pagar/**", 
                        "/tarjeta",
                        "/reservas/exito/**",
                        "/css/**", "/js/**", "/api/mesas/**" // (Recursos estáticos y API pública)
                ).permitAll()
                
                // --- PÁGINAS DE ADMIN ---
                // ¡¡AQUÍ ESTÁ EL CAMBIO!!
                // Agregamos "/admin/**" a las rutas protegidas del Admin.
                .requestMatchers("/home/**", "/admin/**").hasRole("ADMIN") 
                
                // --- PÁGINAS DE MOZO ---
                .requestMatchers("/pedidos/**").hasRole("MOZO")
                
                // Todo lo demás requiere estar logueado
                .anyRequest().authenticated() 
            )
            .formLogin(form -> form
                .loginPage("/login") 
                .loginProcessingUrl("/login")
                .successHandler(customAuthenticationSuccessHandler) // <-- Usar el recepcionista
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );

        return http.build();
    }
}