package com.zapacciano.sgr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fecha;
    private double total;

    // --- ¡¡ARREGLO PARA EL BUG 'Data truncated'!! ---
    // Le decimos a JPA que guarde el Enum como un String ("PENDIENTE")
    // en lugar de un número (0), lo que rompía la BD.
    @Enumerated(EnumType.STRING)
    @Column(length = 20) // (Le damos espacio suficiente: 20 caracteres)
    private EstadoPedido estado;

    // --- Relaciones ---

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;

    // --- ¡¡ESTE ES EL ARREGLO 1/2 DEL 'AnnotationException'!! ---
    // Este es el campo 'reserva' que el 'mappedBy' de la clase Reserva
    // estaba buscando.
    @OneToOne
    @JoinColumn(name = "id_reserva_aplicada", referencedColumnName = "id", nullable = true)
    private Reserva reserva;

    // Relación con los items (esta ya estaba bien)
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> items = new ArrayList<>();

    // --- Constructores, Getters y Setters ---

    public Pedido() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Mesa getMesa() {
        return mesa;
    }

    public void setMesa(Mesa mesa) {
        this.mesa = mesa;
    }

    public Reserva getReserva() {
        return reserva;
    }

    public void setReserva(Reserva reserva) {
        this.reserva = reserva;
    }

    public List<ItemPedido> getItems() {
        return items;
    }

    public void setItems(List<ItemPedido> items) {
        this.items = items;
    }

    // Método de ayuda (si lo necesitas)
    public void addItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }
}