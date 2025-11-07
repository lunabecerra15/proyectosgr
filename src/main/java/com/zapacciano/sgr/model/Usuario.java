package com.zapacciano.sgr.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    
    @Column(unique = true, nullable = false)
    private String email; // Usado como username
    
    @Column(nullable = false)
    private String password; // Contraseña hasheada

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
    
    // Relación: Un usuario (Mozo) puede tener muchos pedidos
    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;

    // --- Constructores ---
    
    /**
     * Constructor vacío requerido por JPA.
     */
    public Usuario() {
    }

    /**
     * Constructor completo para crear usuarios fácilmente.
     */
    public Usuario(String nombre, String email, String password, Rol rol) {
        this.nombre = nombre;
        this.email = email;
        this.password = password;
        this.rol = rol;
    }

    // --- Getters y Setters ---
    
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public List<Pedido> getPedidos() {
        return pedidos;
    }

    public void setPedidos(List<Pedido> pedidos) {
        this.pedidos = pedidos;
    }
}