package com.tienda.pedidos.metricas;
import com.tienda.pedidos.pago.*;
import com.tienda.pedidos.pedido.*;
import java.time.Duration;
import java.util.*;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/metricas")
public class MetricasController {
    private final PedidoRepository pedidos; private final PagoRepository pagos;
    public MetricasController(PedidoRepository pedidos, PagoRepository pagos) { this.pedidos = pedidos; this.pagos = pagos; }
    @GetMapping public Map<String,Object> metricas() { Map<String,Long> estados = new LinkedHashMap<>(); for (EstadoPedido e : EstadoPedido.values()) estados.put(e.name(), pedidos.countByEstado(e)); var despachados = pedidos.findByEstado(EstadoPedido.DESPACHADO); OptionalDouble promedio = despachados.stream().filter(p -> p.getDespachadoEn() != null).mapToLong(p -> Duration.between(p.getCreadoEn(), p.getDespachadoEn()).toSeconds()).average(); Map<String,Object> r = new LinkedHashMap<>(); r.put("pedidosPorEstado", estados); r.put("pedidosCompletados", estados.get("DESPACHADO")); r.put("cancelaciones", estados.get("CANCELADO")); r.put("reservasVencidas", estados.get("EXPIRADO")); r.put("erroresDePago", pagos.countByEstado(EstadoPago.RECHAZADO)); r.put("pagosReembolsados", pagos.countByEstado(EstadoPago.REEMBOLSADO)); r.put("minutosPromedioCompraADespacho", promedio.isPresent() ? Math.round(promedio.getAsDouble() / 60.0 * 100.0) / 100.0 : null); return r; }
}