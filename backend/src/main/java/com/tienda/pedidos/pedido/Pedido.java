package com.tienda.pedidos.pedido;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "pedido")
public class Pedido {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "clave_idempotencia", nullable = false, unique = true) private String claveIdempotencia;
    @Column(nullable = false) private String clienteEmail;
    @Column(nullable = false) private String canal;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private EstadoPedido estado;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal total;
    @Column(nullable = false) private String referenciaPago;
    private LocalDateTime reservaExpiraEn;
    @Column(length = 100) private String motivoCancelacion;
    private String guiaEnvio;
    @Column(nullable = false) private LocalDateTime creadoEn;
    private LocalDateTime pagadoEn;
    private LocalDateTime despachadoEn;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER) @JoinColumn(name = "pedido_id") private List<PedidoItem> items = new ArrayList<>();
    protected Pedido() {}
    public Pedido(String clave, String email, String canal, String referencia, LocalDateTime expira) {
        claveIdempotencia = clave; clienteEmail = email; this.canal = canal; referenciaPago = referencia; reservaExpiraEn = expira; estado = EstadoPedido.PENDIENTE_PAGO; total = BigDecimal.ZERO; creadoEn = LocalDateTime.now();
    }
    public void agregarItem(PedidoItem item) { items.add(item); total = total.add(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad()))); }
    public void marcarPagado() { estado = EstadoPedido.PAGADO; pagadoEn = LocalDateTime.now(); reservaExpiraEn = null; motivoCancelacion = null; }
    public void expirar() { estado = EstadoPedido.EXPIRADO; motivoCancelacion = "RESERVA_VENCIDA"; reservaExpiraEn = null; }
    public void cancelar(String motivo) { estado = EstadoPedido.CANCELADO; motivoCancelacion = motivo; reservaExpiraEn = null; }
    public void preparar() { estado = EstadoPedido.EN_PREPARACION; }
    public void despachar(String guia) { estado = EstadoPedido.DESPACHADO; guiaEnvio = guia; despachadoEn = LocalDateTime.now(); }
    public Long getId() { return id; } public String getClaveIdempotencia() { return claveIdempotencia; } public String getClienteEmail() { return clienteEmail; } public String getCanal() { return canal; } public EstadoPedido getEstado() { return estado; } public BigDecimal getTotal() { return total; } public String getReferenciaPago() { return referenciaPago; } public LocalDateTime getReservaExpiraEn() { return reservaExpiraEn; } public String getMotivoCancelacion() { return motivoCancelacion; } public String getGuiaEnvio() { return guiaEnvio; } public LocalDateTime getCreadoEn() { return creadoEn; } public LocalDateTime getPagadoEn() { return pagadoEn; } public LocalDateTime getDespachadoEn() { return despachadoEn; } public List<PedidoItem> getItems() { return items; }
}