package com.zapacciano.sgr.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservas")
public class Reserva {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaHora;
    private int cantidadPersonas;
    private double montoSeña; // Ej: 3 personas * 10000 = 30000

    @Enumerated(EnumType.STRING)
    private EstadoReserva estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mesa", nullable = true) // Puede ser null al inicio
    private Mesa mesa;

    // --- ¡¡AQUÍ ESTÁ EL ARREGLO!! ---
    // Antes decía: mappedBy = "reservaAplicada"
    // Ahora dice: mappedBy = "reserva" (¡el nombre real del campo en Pedido.java!)
    @OneToOne(mappedBy = "reserva", fetch = FetchType.LAZY)
    private Pedido pedido;
    // --- FIN DEL ARREGLO ---


    // --- Constructores, Getters y Setters ---
    public Reserva() {}

    // (Getters y Setters para todos los campos...)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }
    public int getCantidadPersonas() { return cantidadPersonas; }
    public void setCantidadPersonas(int cantidadPersonas) { this.cantidadPersonas = cantidadPersonas; }
    public double getMontoSeña() { return montoSeña; }
    public void setMontoSeña(double montoSeña) { this.montoSeña = montoSeña; }
    public EstadoReserva getEstado() { return estado; }
    public void setEstado(EstadoReserva estado) { this.estado = estado; }
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }
    public Mesa getMesa() { return mesa; }
    public void setMesa(Mesa mesa) { this.mesa = mesa; }
    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }
}