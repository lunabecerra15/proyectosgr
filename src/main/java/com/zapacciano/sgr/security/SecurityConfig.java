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
            // ¡¡ESTA LÍNEA ES CRUCIAL!!
            // Desactiva CSRF, lo que arregla el "Confirmar Pago Simulado"
            .csrf(csrf -> csrf.disable())
            
            .authorizeHttpRequests(authz -> authz
                // --- PÁGINAS PÚBLICAS / DE CLIENTES ---
                // ¡¡ESTA REGLA ARREGLA TODO EL FLUJO DE RESERVAS!!
                // Permite todo lo que empiece con /reservas/ y también el /
                .requestMatchers("/", "/reservas/**", "/css/**", "/js/**", "/api/mesas/**").permitAll()
                
                // --- PÁGINAS PROTEGIDAS POR ROL ---
                .requestMatchers("/home/**").hasRole("ADMIN")
                .requestMatchers("/pedidos/**").hasRole("MOZO")
                
                // --- TODO LO DEMÁS ---
                // Cualquier otra página (que no hayamos listado)
                // requiere que el usuario esté, al menos, logueado.
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