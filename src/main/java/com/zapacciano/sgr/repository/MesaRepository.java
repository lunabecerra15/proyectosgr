package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.EstadoMesa; 
import com.zapacciano.sgr.model.Mesa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MesaRepository extends JpaRepository<Mesa, Long> {

    Optional<Mesa> findByNumero(int numero);
    Optional<Mesa> findFirstByCapacidadGreaterThanEqualAndEstadoOrderByIdAsc(int capacidad, EstadoMesa estado);
    
}