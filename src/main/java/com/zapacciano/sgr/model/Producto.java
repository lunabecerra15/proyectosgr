package com.zapacciano.sgr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String descripcion;
    private double precio;
    private int stock;

    // --- ¡¡CAMBIO AQUÍ!! ---
    // Agregamos el nuevo campo para agrupar el menú
    private String categoria;
    // --- FIN DEL CAMBIO ---

    // Constructor vacío para JPA
    public Producto() {
    }

    // --- ¡¡CAMBIO AQUÍ!! ---
    // Actualizamos el constructor para que también incluya la categoría
    public Producto(String nombre, String descripcion, double precio, int stock, String categoria) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.stock = stock;
        this.categoria = categoria; // Asignamos la categoría
    }
    // --- FIN DEL CAMBIO ---

    // --- Getters y Setters ---
    // (Asegúrate de agregar el getter y setter para 'categoria')

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    // --- ¡¡CAMBIO AQUÍ!! ---
    // Nuevo Getter y Setter
    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
    // --- FIN DEL CAMBIO ---
}