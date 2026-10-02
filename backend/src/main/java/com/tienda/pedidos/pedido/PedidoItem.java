package com.tienda.pedidos.pedido;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "pedido_item")
public class PedidoItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "producto_id", nullable = false) private Long productoId;
    @Column(nullable = false) private String sku;
    @Column(nullable = false) private int cantidad;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal precioUnitario;
    protected PedidoItem() {}
    public PedidoItem(Long productoId, String sku, int cantidad, BigDecimal precioUnitario) { this.productoId = productoId; this.sku = sku; this.cantidad = cantidad; this.precioUnitario = precioUnitario; }
    public Long getId() { return id; }
    public Long getProductoId() { return productoId; }
    public String getSku() { return sku; }
    public int getCantidad() { return cantidad; }
    public BigDecimal getPrecioUnitario() { return precioUnitario; }
}