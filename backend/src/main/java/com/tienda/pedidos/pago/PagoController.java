package com.tienda.pedidos.pago;
import com.tienda.pedidos.pedido.*;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/pagos")
public class PagoController {
    private final PedidoService servicio; public PagoController(PedidoService servicio) { this.servicio = servicio; }
    @PostMapping("/confirmacion") public PedidoRespuesta confirmar(@RequestHeader("Idempotency-Key") String clave, @Valid @RequestBody ConfirmacionPago c) { Pedido p = servicio.confirmarPago(clave, c.pedidoId(), c.resultado(), c.referencia()); return PedidoRespuesta.de(p, servicio.historial(p.getId())); }
}