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
     */
    @Transactional
    public void actualizarPrecioProducto(Long productoId, double nuevoPrecio) {
        // Buscamos el producto en la base de datos
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con ID: " + productoId));
            
        // Cambiamos precio
        producto.setPrecio(nuevoPrecio);
        
        // Guardamos producto
        productoRepository.save(producto);
    }

    /**
     * Guarda un nuevo producto en la base de datos.
     * Verifica si trae imagen, si no, le pone una por defecto.
     */
    @Transactional
    public void crearProducto(Producto producto) {
        // Validación: Si la imagen es nula o texto vacío, ponemos placeholder
        if (producto.getImagenUrl() == null || producto.getImagenUrl().trim().isEmpty()) {
            producto.setImagenUrl("https://placehold.co/600x400?text=Sin+Imagen"); 
        }
        
        // Guardamos el nuevo producto
        productoRepository.save(producto);
    }

}