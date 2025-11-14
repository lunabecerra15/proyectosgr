package com.zapacciano.sgr.service;

import com.zapacciano.sgr.model.*;
import com.zapacciano.sgr.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;
    @Autowired
    private ItemPedidoRepository itemPedidoRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private MesaRepository mesaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    // ... (El método registrarPedido(...) ya está perfecto y no cambia) ...
    @Transactional
    public Pedido registrarPedido(int numeroMesa, long[] productoIds, int[] cantidades, UserDetails userDetails) {
        
        Usuario mozo = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Mozo no encontrado: " + userDetails.getUsername()));

        Mesa mesa = mesaRepository.findByNumero(numeroMesa)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con número: " + numeroMesa));

        // 1. Guardar el Pedido "Padre" PRIMERO para obtener un ID
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setMesa(mesa);
        pedido.setUsuario(mozo);
        pedido.setEstado(EstadoPedido.PENDIENTE); // ¡Correcto!
        pedido.setTotal(0); // Total temporal
        
        // ¡¡AQUÍ!! Guardamos el pedido ANTES de los items
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 4. Calcular el total y crear los items
        double totalPedido = 0;
        
        for (int i = 0; i < productoIds.length; i++) {
            long id = productoIds[i];
            int cant = cantidades[i];
            
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
            
            double subtotal = producto.getPrecio() * cant;
            totalPedido += subtotal;

            ItemPedido item = new ItemPedido();
            item.setPedido(pedidoGuardado); // ¡¡Usamos el pedido que SÍ tiene ID!!
            item.setProducto(producto);
            item.setCantidad(cant);
            item.setPrecioUnitario(producto.getPrecio());
            
            itemPedidoRepository.save(item);
        }

        // 5. Actualizar el pedido principal (que ya existe) con el total final
        pedidoGuardado.setTotal(totalPedido);
        pedidoRepository.save(pedidoGuardado); // Es un 'update'

        // 6. Actualizar el estado de la mesa
        mesa.setEstado(EstadoMesa.OCUPADO); // ¡Correcto!
        mesaRepository.save(mesa);

        return pedidoGuardado;
    }


    // --- ¡¡MÉTODO NUEVO!! ---
    /**
     * Busca los pedidos PENDIENTES (activos) de un Mozo específico.
     */
    @Transactional(readOnly = true)
    public List<Pedido> getPedidosActivosPorMozo(Usuario mozo) {
        // Usamos el nuevo método del repositorio
        return pedidoRepository.findByEstadoAndUsuario(EstadoPedido.PENDIENTE, mozo);
    }
    
    // --- ¡¡MÉTODO NUEVO!! ---
    /**
     * Cambia un pedido de PENDIENTE a COMPLETADO y libera la mesa.
     */
    @Transactional
    public void cobrarPedido(Long pedidoId) {
        // 1. Encontrar el pedido
        Pedido pedido = pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado: " + pedidoId));
        
        // 2. Cambiar su estado
        pedido.setEstado(EstadoPedido.COMPLETADO);
        pedidoRepository.save(pedido);
        
        // 3. Encontrar y liberar la mesa
        Mesa mesa = pedido.getMesa();
        if (mesa != null) {
            mesa.setEstado(EstadoMesa.LIBRE);
            mesaRepository.save(mesa);
        }
        // (Aquí podríamos verificar si la mesa tiene OTRAS reservas pendientes,
        // pero por ahora la liberamos)
    }


    // --- ¡¡LÓGICA ACTUALIZADA!! ---
    /**
     * Obtiene los pedidos PENDIENTES para el panel del Admin.
     * SEGÚN TU NUEVA LÓGICA, EL ADMIN YA NO VE ESTO.
     * Devolvemos una lista vacía para no romper el WebController (lo limpiaremos después).
     */
    public List<Pedido> getPedidosActivos() {
        // Devolver los pedidos PENDIENTES con sus detalles para que el admin los vea.
        return pedidoRepository.findByEstadoWithDetails(EstadoPedido.PENDIENTE);
    }

  
    public List<Pedido> getReporteVentas() {
        // Usamos el nuevo método del repositorio que filtra por reporteGenerado = false
        return pedidoRepository.findByEstadoAndReporteGeneradoFalse(EstadoPedido.COMPLETADO);
    }

    @Transactional
    public void marcarVentasComoReportadas(List<Pedido> pedidos) {
        for (Pedido p : pedidos) {
            p.setReporteGenerado(true);
        }
        pedidoRepository.saveAll(pedidos);
    }
}