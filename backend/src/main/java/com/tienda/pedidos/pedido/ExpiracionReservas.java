package com.tienda.pedidos.pedido;
import java.time.LocalDateTime;
import org.slf4j.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
@Component
public class ExpiracionReservas {
    private static final Logger log = LoggerFactory.getLogger(ExpiracionReservas.class); private final PedidoRepository pedidos; private final PedidoService servicio;
    public ExpiracionReservas(PedidoRepository pedidos, PedidoService servicio) { this.pedidos = pedidos; this.servicio = servicio; }
    @Scheduled(fixedDelay = 30000, initialDelay = 30000) public void liberarVencidas() { var vencidos = pedidos.findByEstadoAndReservaExpiraEnBefore(EstadoPedido.PENDIENTE_PAGO, LocalDateTime.now()); vencidos.forEach(p -> servicio.expirar(p.getId())); if (!vencidos.isEmpty()) log.info("Reservas vencidas procesadas: {}", vencidos.size()); }
}