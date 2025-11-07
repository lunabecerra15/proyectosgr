package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    // Aquí pondremos más consultas en el futuro
}
