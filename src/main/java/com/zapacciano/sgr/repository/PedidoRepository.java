package com.zapacciano.sgr.repository;

import com.zapacciano.sgr.model.EstadoPedido;
import com.zapacciano.sgr.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * --- ¡¡CONSULTA CORREGIDA!! ---
     * Ahora no solo trae los items, sino también el PRODUCTO
     * asociado a cada item, todo en una sola consulta.
     */
    @Query("SELECT DISTINCT p FROM Pedido p " +
           "JOIN FETCH p.mesa " +
           "JOIN FETCH p.usuario " +
           "LEFT JOIN FETCH p.items item " + // "item" es un alias para el join
           "LEFT JOIN FETCH item.producto " + // <-- ¡ESTA ES LA LÍNEA QUE FALTABA!
           "WHERE p.estado = :estado")
    List<Pedido> findByEstadoWithDetails(@Param("estado") EstadoPedido estado);

}