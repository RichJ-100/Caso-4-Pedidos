package com.tienda.pedidos.pago;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Entity @Table(name = "pago")
public class Pago {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "pedido_id", nullable = false) private Long pedidoId;
    @Column(name = "clave_evento", nullable = false, unique = true) private String claveEvento;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EstadoPago estado;
    @Column(nullable = false) private String referencia;
    private String referenciaReembolso;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal monto;
    @Column(nullable = false) private LocalDateTime creadoEn;
    protected Pago() {}
    public Pago(Long pedidoId, String claveEvento, EstadoPago estado, String referencia, BigDecimal monto) { this.pedidoId = pedidoId; this.claveEvento = claveEvento; this.estado = estado; this.referencia = referencia; this.monto = monto; creadoEn = LocalDateTime.now(); }
    public void marcarReembolsado(String referencia) { estado = EstadoPago.REEMBOLSADO; referenciaReembolso = referencia; }
    public Long getId() { return id; } public Long getPedidoId() { return pedidoId; } public String getClaveEvento() { return claveEvento; } public EstadoPago getEstado() { return estado; } public String getReferencia() { return referencia; } public String getReferenciaReembolso() { return referenciaReembolso; } public BigDecimal getMonto() { return monto; } public LocalDateTime getCreadoEn() { return creadoEn; }
}