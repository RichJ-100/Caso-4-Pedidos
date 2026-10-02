package com.tienda.pedidos.catalogo;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "producto")
public class Producto {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true) private String sku;
    @Column(nullable = false) private String nombre;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal precio;
    @Column(nullable = false) private int stock;
    @Column(nullable = false) private int reservado;

    protected Producto() {}
    public Producto(String sku, String nombre, BigDecimal precio, int stock) {
        this.sku = sku; this.nombre = nombre; this.precio = precio; this.stock = stock;
    }
    public int disponible() { return stock - reservado; }
    public void reservar(int cantidad) { reservado += cantidad; }
    public void liberar(int cantidad) { reservado = Math.max(0, reservado - cantidad); }
    public void despachar(int cantidad) { stock -= cantidad; reservado = Math.max(0, reservado - cantidad); }
    public Long getId() { return id; }
    public String getSku() { return sku; }
    public String getNombre() { return nombre; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public int getReservado() { return reservado; }
}