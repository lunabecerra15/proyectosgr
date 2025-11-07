package com.zapacciano.sgr.model;

import jakarta.persistence.*;

@Entity
@Table(name = "mesas")
public class Mesa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private int numero;

    private int capacidad;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoMesa estado;

    // Constructores (vacío y completo)
    public Mesa() { }

    public Mesa(int numero, int capacidad, EstadoMesa estado) {
        this.numero = numero;
        this.capacidad = capacidad;
        this.estado = estado;
    }

    // Getters y Setters (¡importante!)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public EstadoMesa getEstado() { return estado; }
    public void setEstado(EstadoMesa estado) { this.estado = estado; }
}
