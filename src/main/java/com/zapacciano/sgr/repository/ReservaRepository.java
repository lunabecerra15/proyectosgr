package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.EstadoReserva; // <--- Importante
import com.zapacciano.sgr.model.Reserva;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List; // <--- Importante

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    // NUEVO MÉTODO POTENTE:
    // Busca reservas DESPUÉS de una fecha Y que tengan CIERTOS estados
    List<Reserva> findByFechaHoraAfterAndEstadoIn(LocalDateTime fecha, List<EstadoReserva> estados, Sort sort);

    // El de findAll lo dejamos para el historial
}