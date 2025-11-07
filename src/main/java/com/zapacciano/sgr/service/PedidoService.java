package com.zapacciano.sgr.service;

import com.zapacciano.sgr.model.*;
import com.zapacciano.sgr.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    @Transactional
    public Pedido registrarPedido(int numeroMesa, long[] productoIds, int[] cantidades, UserDetails userDetails) {
        
        Usuario mozo = usuarioRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Mozo no encontrado: " + userDetails.getUsername()));

        Mesa mesa = mesaRepository.findByNumero(numeroMesa)
                .orElseThrow(() -> new RuntimeException("Mesa no encontrada con número: " + numeroMesa));

        // --- ¡¡AQUÍ ESTÁ EL ARREGLO!! ---
        
        // 1. Crear y GUARDAR el Pedido (Padre) PRIMERO
        Pedido pedido = new Pedido();
        pedido.setFecha(LocalDateTime.now());
        pedido.setMesa(mesa);
        pedido.setUsuario(mozo);
        pedido.setEstado(EstadoPedido.PENDIENTE);
        pedido.setTotal(0); // Total temporal
        
        // ¡Lo guardamos ANTES del bucle para que tenga un ID!
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        // 2. Ahora creamos los "hijos" (Items)
        double totalPedido = 0;
        
        for (int i = 0; i < productoIds.length; i++) {
            long id = productoIds[i];
            int cant = cantidades[i];
            
            Producto producto = productoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + id));
            
            double subtotal = producto.getPrecio() * cant;
            totalPedido += subtotal;

            ItemPedido item = new ItemPedido();
            
            // ¡Le asignamos el Pedido que YA TIENE ID!
            item.setPedido(pedidoGuardado); 
            item.setProducto(producto);
            item.setCantidad(cant);
            item.setPrecioUnitario(producto.getPrecio());
            
             itemPedidoRepository.save(item); // Ahora esto funciona
        }

        // 3. Actualizamos el Pedido "Padre" con el total final
        pedidoGuardado.setTotal(totalPedido);
        pedidoRepository.save(pedidoGuardado); // Guardamos la actualización

        // 4. Actualizar el estado de la mesa
        mesa.setEstado(EstadoMesa.OCUPADO);
        mesaRepository.save(mesa);

        return pedidoGuardado;
    }

    public List<Pedido> getReporteVentas() {
        return pedidoRepository.findByEstadoWithDetails(EstadoPedido.COMPLETADO);
    }
    
    public List<Pedido> getPedidosActivos() {
        return pedidoRepository.findByEstadoWithDetails(EstadoPedido.PENDIENTE);
    }
}