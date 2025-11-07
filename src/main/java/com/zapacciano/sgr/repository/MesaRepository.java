package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// --- ¡NUEVOS IMPORTS! ---
import java.util.Optional;
// --- FIN IMPORTS ---

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    // --- ¡¡ESTE ES EL MÉTODO QUE FALTABA!! ---
    /**
     * Busca una mesa por su número único.
     * Spring Data JPA crea la consulta automáticamente.
     * @param numeroMesa El número de la mesa (ej: 5)
     * @return Un Optional que contiene la Mesa si se encuentra.
     */
    Optional<Mesa> findByNumero(int numeroMesa);
    
}