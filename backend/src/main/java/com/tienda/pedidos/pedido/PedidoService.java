package com.tienda.pedidos.pedido;

import com.tienda.pedidos.catalogo.*;
import com.tienda.pedidos.integracion.*;
import com.tienda.pedidos.pago.*;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PedidoService {
    private final PedidoRepository pedidos; private final ProductoRepository productos; private final PagoRepository pagos; private final HistorialRepository historial; private final PasarelaPagos pasarela; private final Notificador notificador; private final long reservaMinutos;
    public PedidoService(PedidoRepository pedidos, ProductoRepository productos, PagoRepository pagos, HistorialRepository historial, PasarelaPagos pasarela, Notificador notificador, @Value("${tienda.reserva-minutos:15}") long reservaMinutos) { this.pedidos = pedidos; this.productos = productos; this.pagos = pagos; this.historial = historial; this.pasarela = pasarela; this.notificador = notificador; this.reservaMinutos = reservaMinutos; }
    @Transactional(readOnly = true) public Optional<Pedido> porClave(String clave) { return pedidos.findByClaveIdempotencia(clave); }
    @Transactional(readOnly = true) public Pedido ver(Long id) { return pedidos.findById(id).orElseThrow(this::noEncontrado); }
    @Transactional(readOnly = true) public List<HistorialPedido> historial(Long id) { return historial.findByPedidoIdOrderByIdAsc(id); }

    @Transactional public Pedido crear(String clave, String email, String canal, List<SolicitudPedido.Item> solicitados) {
        Map<String, Integer> porSku = new TreeMap<>(); solicitados.forEach(i -> porSku.merge(i.sku(), i.cantidad(), Integer::sum));
        List<Producto> bloqueados = bloquearProductos(porSku.keySet());
        Set<String> encontrados = bloqueados.stream().map(Producto::getSku).collect(java.util.stream.Collectors.toSet());
        porSku.keySet().stream().filter(sku -> !encontrados.contains(sku)).findFirst().ifPresent(sku -> { throw new ResponseStatusException(HttpStatus.NOT_FOUND, "El SKU no existe: " + sku); });
        for (Producto p : bloqueados) if (p.disponible() < porSku.get(p.getSku())) throw new ResponseStatusException(HttpStatus.CONFLICT, "Sin stock suficiente para " + p.getSku() + " (disponible: " + p.disponible() + ")");
        Pedido pedido = new Pedido(clave, email, canal, "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(), LocalDateTime.now().plusMinutes(reservaMinutos));
        for (Producto p : bloqueados) { int cantidad = porSku.get(p.getSku()); p.reservar(cantidad); pedido.agregarItem(new PedidoItem(p.getId(), p.getSku(), cantidad, p.getPrecio())); }
        pedido = pedidos.saveAndFlush(pedido); registrar(pedido, "PEDIDO_CREADO canal=" + canal + " total=" + pedido.getTotal() + " reserva_hasta=" + pedido.getReservaExpiraEn()); notificador.notificar(email, "Recibimos tu pedido " + pedido.getId() + ". Paga con la referencia " + pedido.getReferenciaPago()); return pedido;
    }

    @Transactional public Pedido confirmarPago(String claveEvento, Long pedidoId, ResultadoPago resultado, String referencia) {
        Pedido pedido = pedidos.buscarConBloqueo(pedidoId).orElseThrow(this::noEncontrado); if (pagos.findByClaveEvento(claveEvento).isPresent()) return pedido;
        if (resultado == ResultadoPago.RECHAZADO) { pagos.save(new Pago(pedidoId, claveEvento, EstadoPago.RECHAZADO, referencia, pedido.getTotal())); registrar(pedido, "PAGO_RECHAZADO ref=" + referencia); return pedido; }
        if (pagos.findFirstByReferencia(referencia).isPresent()) return pedido;
        if (pedido.getEstado() == EstadoPedido.PENDIENTE_PAGO) { pedido.marcarPagado(); pagos.save(new Pago(pedidoId, claveEvento, EstadoPago.APROBADO, referencia, pedido.getTotal())); registrar(pedido, "PAGO_APROBADO ref=" + referencia); notificador.notificar(pedido.getClienteEmail(), "Pago recibido. Preparamos tu pedido " + pedido.getId()); }
        else if (pedido.getEstado() == EstadoPedido.EXPIRADO) {
            if (intentarReservar(pedido)) { pedido.marcarPagado(); pagos.save(new Pago(pedidoId, claveEvento, EstadoPago.APROBADO, referencia, pedido.getTotal())); registrar(pedido, "PAGO_APROBADO_RESERVA_RENOVADA ref=" + referencia); }
            else { reembolsarEvento(pedido, claveEvento, referencia); pedido.cancelar("SIN_STOCK_TRAS_PAGO"); registrar(pedido, "PAGO_APROBADO_SIN_STOCK_REEMBOLSADO ref=" + referencia); }
        } else { reembolsarEvento(pedido, claveEvento, referencia); registrar(pedido, "PAGO_NO_APLICABLE_REEMBOLSADO estado=" + pedido.getEstado() + " ref=" + referencia); }
        return pedido;
    }
    @Transactional public Pedido cancelar(Long id, String motivo) { Pedido p = pedidos.buscarConBloqueo(id).orElseThrow(this::noEncontrado); if (p.getEstado() == EstadoPedido.CANCELADO || p.getEstado() == EstadoPedido.EXPIRADO) return p; String m = motivo == null || motivo.isBlank() ? "CANCELADO_POR_CLIENTE" : motivo; if (p.getEstado() == EstadoPedido.PENDIENTE_PAGO) { liberarReserva(p); p.cancelar(m); } else if (p.getEstado() == EstadoPedido.PAGADO) { liberarReserva(p); pagos.findFirstByPedidoIdAndEstado(id, EstadoPago.APROBADO).ifPresent(x -> x.marcarReembolsado(pasarela.reembolsar(x.getReferencia(), p.getTotal()))); p.cancelar(m); } else throw new ResponseStatusException(HttpStatus.CONFLICT, "El pedido ya esta " + p.getEstado() + " y no se puede cancelar"); registrar(p, "PEDIDO_CANCELADO motivo=" + m); return p; }
    @Transactional public Pedido preparar(Long id) { Pedido p = pedidos.buscarConBloqueo(id).orElseThrow(this::noEncontrado); if (p.getEstado() == EstadoPedido.EN_PREPARACION || p.getEstado() == EstadoPedido.DESPACHADO) return p; if (p.getEstado() != EstadoPedido.PAGADO) throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se prepara un pedido PAGADO (estado actual: " + p.getEstado() + ")"); p.preparar(); registrar(p, "PEDIDO_EN_PREPARACION"); return p; }
    @Transactional public Pedido despachar(Long id, String guia) { Pedido p = pedidos.buscarConBloqueo(id).orElseThrow(this::noEncontrado); if (p.getEstado() == EstadoPedido.DESPACHADO) return p; if (p.getEstado() != EstadoPedido.EN_PREPARACION) throw new ResponseStatusException(HttpStatus.CONFLICT, "Solo se despacha un pedido EN_PREPARACION (estado actual: " + p.getEstado() + ")"); cantidades(p).forEach((producto, cantidad) -> productos.buscarConBloqueo(producto).ifPresent(x -> x.despachar(cantidad))); String g = guia == null || guia.isBlank() ? "GUIA-" + id : guia; p.despachar(g); registrar(p, "PEDIDO_DESPACHADO guia=" + g); return p; }
    @Transactional public void expirar(Long id) { Pedido p = pedidos.buscarConBloqueo(id).orElse(null); if (p == null || p.getEstado() != EstadoPedido.PENDIENTE_PAGO || p.getReservaExpiraEn() == null || p.getReservaExpiraEn().isAfter(LocalDateTime.now())) return; liberarReserva(p); p.expirar(); registrar(p, "RESERVA_VENCIDA inventario liberado"); }
    private List<Producto> bloquearProductos(Collection<String> skus) { List<Producto> result = new ArrayList<>(); for (Long id : productos.idsPorSkus(skus)) productos.buscarConBloqueo(id).ifPresent(result::add); return result; }
    private Map<Long,Integer> cantidades(Pedido p) { Map<Long,Integer> m = new TreeMap<>(); p.getItems().forEach(i -> m.merge(i.getProductoId(), i.getCantidad(), Integer::sum)); return m; }
    private boolean intentarReservar(Pedido p) { Map<Long,Integer> c = cantidades(p); List<Producto> b = new ArrayList<>(); c.keySet().forEach(id -> productos.buscarConBloqueo(id).ifPresent(b::add)); if (b.size() != c.size() || b.stream().anyMatch(x -> x.disponible() < c.get(x.getId()))) return false; b.forEach(x -> x.reservar(c.get(x.getId()))); return true; }
    private void liberarReserva(Pedido p) { cantidades(p).forEach((id, n) -> productos.buscarConBloqueo(id).ifPresent(x -> x.liberar(n))); }
    private void reembolsarEvento(Pedido p, String clave, String ref) { Pago pago = new Pago(p.getId(), clave, EstadoPago.APROBADO, ref, p.getTotal()); pago.marcarReembolsado(pasarela.reembolsar(ref, p.getTotal())); pagos.save(pago); }
    private void registrar(Pedido p, String evento) { historial.save(new HistorialPedido(p.getId(), p.getEstado().name(), evento)); }
    private ResponseStatusException noEncontrado() { return new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido no encontrado"); }
}