package com.tienda.pedidos.pedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
public record PedidoRespuesta(Long id, EstadoPedido estado, String clienteEmail, String canal, BigDecimal total, String referenciaPago, LocalDateTime reservaExpiraEn, String motivoCancelacion, String guiaEnvio, LocalDateTime creadoEn, List<ItemRespuesta> items, List<EventoRespuesta> historial) {
    public record ItemRespuesta(String sku, int cantidad, BigDecimal precioUnitario) {}
    public record EventoRespuesta(String estado, String evento, LocalDateTime fecha) {}
    public static PedidoRespuesta de(Pedido p, List<HistorialPedido> h) { return new PedidoRespuesta(p.getId(), p.getEstado(), p.getClienteEmail(), p.getCanal(), p.getTotal(), p.getReferenciaPago(), p.getReservaExpiraEn(), p.getMotivoCancelacion(), p.getGuiaEnvio(), p.getCreadoEn(), p.getItems().stream().map(i -> new ItemRespuesta(i.getSku(), i.getCantidad(), i.getPrecioUnitario())).toList(), h.stream().map(x -> new EventoRespuesta(x.getEstado(), x.getEvento(), x.getCreadoEn())).toList()); }
}