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

    //Le decimos a JPA que guarde el Enum como un String ("PENDIENTE")
    @Enumerated(EnumType.STRING)
    @Column(length = 20) //20 caracteres
    private EstadoPedido estado;

    //Relaciones

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa")
    private Mesa mesa;

    // Este es el campo 'reserva' que el 'mappedBy' de la clase Reserva busca
    @OneToOne
    @JoinColumn(name = "id_reserva_aplicada", referencedColumnName = "id", nullable = true)
    private Reserva reserva;

    // Relación con los items
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ItemPedido> items = new ArrayList<>();

    // Cierre de Caja
    @Column(columnDefinition = "boolean default false")
    private boolean reporteGenerado = false;

    //Constructores, Getters y Setters

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

    public void addItem(ItemPedido item) {
        items.add(item);
        item.setPedido(this);
    }

    public boolean isReporteGenerado() {
        return reporteGenerado;
    }

    public void setReporteGenerado(boolean reporteGenerado) {
        this.reporteGenerado = reporteGenerado;
    }
}