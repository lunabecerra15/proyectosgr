package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// --- ¡NUEVOS IMPORTS! ---
import java.util.Optional;
// --- FIN IMPORTS ---

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    // --- ¡¡ESTE ES EL MÉTODO QUE FALTABA!! ---
    /**
     * Busca un cliente por su dirección de email.
     * Spring Data JPA crea la consulta automáticamente basado en el nombre del método.
     * @param email El email a buscar.
     * @return Un Optional que contiene al Cliente si se encuentra, o vacío si no.
     */
    Optional<Cliente> findByEmail(String email);
    
}