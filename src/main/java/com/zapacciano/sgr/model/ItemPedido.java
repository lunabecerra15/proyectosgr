package com.zapacciano.sgr.model;

import jakarta.persistence.*;
import java.io.Serializable; // (Buena práctica para entidades compuestas)

// (Esta anotación @IdClass es para la clave primaria compuesta que te pasé antes)
@IdClass(ItemPedido.ItemPedidoId.class)
@Entity
@Table(name = "items_pedido")
public class ItemPedido {

    @Id
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Id
    @ManyToOne(fetch = FetchType.EAGER) // <-- ¡¡ESTE ES EL ARREGLO!!
    @JoinColumn(name = "producto_id")
    private Producto producto;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioUnitario;

    // --- Getters y Setters ---

    public Pedido getPedido() { return pedido; }
    public void setPedido(Pedido pedido) { this.pedido = pedido; }

    public Producto getProducto() { return producto; }
    public void setProducto(Producto producto) { this.producto = producto; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }

    public Double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(Double precioUnitario) { this.precioUnitario = precioUnitario; }

    // --- Clase interna para el @IdClass ---
    // (Esto ya deberías tenerlo del código anterior)
    public static class ItemPedidoId implements Serializable {
        private Long pedido;
        private Long producto;

        // Constructor vacío, equals y hashCode
        public ItemPedidoId() {}

        public ItemPedidoId(Long pedido, Long producto) {
            this.pedido = pedido;
            this.producto = producto;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ItemPedidoId that = (ItemPedidoId) o;
            if (!pedido.equals(that.pedido)) return false;
            return producto.equals(that.producto);
        }

        @Override
        public int hashCode() {
            int result = pedido.hashCode();
            result = 31 * result + producto.hashCode();
            return result;
        }
    }
}

