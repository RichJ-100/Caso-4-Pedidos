package com.tienda.pedidos.pedido;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity @Table(name = "historial_pedido")
public class HistorialPedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "pedido_id", nullable = false) private Long pedidoId;
    @Column(nullable = false) private String estado;
    @Column(nullable = false, length = 300) private String evento;
    @Column(nullable = false) private LocalDateTime creadoEn;
    protected HistorialPedido() {}
    public HistorialPedido(Long pedidoId, String estado, String evento) { this.pedidoId = pedidoId; this.estado = estado; this.evento = evento; creadoEn = LocalDateTime.now(); }
    public Long getId() { return id; } public Long getPedidoId() { return pedidoId; } public String getEstado() { return estado; } public String getEvento() { return evento; } public LocalDateTime getCreadoEn() { return creadoEn; }
}