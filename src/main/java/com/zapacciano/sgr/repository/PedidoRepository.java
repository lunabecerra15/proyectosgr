package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.EstadoPedido;
import com.zapacciano.sgr.model.Pedido;
import com.zapacciano.sgr.model.Usuario; // <-- ¡¡IMPORTA ESTE!!
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    // ... (El findByEstadoWithDetails que ya tenías) ...
    @Query("SELECT DISTINCT p FROM Pedido p " +
           "JOIN FETCH p.mesa " +
           "JOIN FETCH p.usuario " +
           "LEFT JOIN FETCH p.items i " +
           "LEFT JOIN FETCH i.producto " +
           "WHERE p.estado = :estado")
    List<Pedido> findByEstadoWithDetails(@Param("estado") EstadoPedido estado);

    // --- ¡¡MÉTODO NUEVO AQUÍ!! ---
    // Busca pedidos PENDIENTES pero solo para un MOZO específico.
    @Query("SELECT DISTINCT p FROM Pedido p " +
           "JOIN FETCH p.mesa " +
           "JOIN FETCH p.usuario " +
           "LEFT JOIN FETCH p.items i " +
           "LEFT JOIN FETCH i.producto " +
           "WHERE p.estado = :estado AND p.usuario = :usuario")
    List<Pedido> findByEstadoAndUsuario(@Param("estado") EstadoPedido estado, @Param("usuario") Usuario usuario);
    
}