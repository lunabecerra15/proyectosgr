package com.zapacciano.sgr.service;

import com.zapacciano.sgr.model.Producto;
import com.zapacciano.sgr.repository.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    /**
     * Busca un producto por su ID, actualiza el precio y lo guarda.
     * @param productoId El ID del producto a actualizar.
     * @param nuevoPrecio El nuevo precio a establecer.
     * @throws RuntimeException Si el producto no se encuentra.
     */
    @Transactional
    public void actualizarPrecioProducto(Long productoId, double nuevoPrecio) {
        
        // 1. Buscamos el producto en la base de datos
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
            
        // 2. Actualizamos el precio
        producto.setPrecio(nuevoPrecio);
        
        // 3. Guardamos los cambios
        // (Como estamos en una @Transactional, esto a veces no es 100% necesario,
        // pero hacerlo explícito es una buena práctica).
        productoRepository.save(producto);
    }
}