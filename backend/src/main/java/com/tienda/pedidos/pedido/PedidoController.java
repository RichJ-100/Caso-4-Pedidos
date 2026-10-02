package com.tienda.pedidos.pedido;
import jakarta.validation.Valid;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
@RestController @RequestMapping("/api/pedidos")
public class PedidoController {
    private final PedidoService servicio; public PedidoController(PedidoService servicio) { this.servicio = servicio; }
    @PostMapping public ResponseEntity<PedidoRespuesta> crear(@RequestHeader("Idempotency-Key") String clave, @Valid @RequestBody SolicitudPedido solicitud) { Optional<Pedido> existente = servicio.porClave(clave); if (existente.isPresent()) return ResponseEntity.ok(respuesta(existente.get())); try { Pedido p = servicio.crear(clave, solicitud.clienteEmail(), solicitud.canal(), solicitud.items()); return ResponseEntity.status(HttpStatus.CREATED).body(respuesta(p)); } catch (DataIntegrityViolationException e) { Pedido p = servicio.porClave(clave).orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Solicitud duplicada en proceso, reintenta")); return ResponseEntity.ok(respuesta(p)); } }
    @GetMapping("/{id}") public PedidoRespuesta ver(@PathVariable Long id) { return respuesta(servicio.ver(id)); }
    @PostMapping("/{id}/cancelar") public PedidoRespuesta cancelar(@PathVariable Long id, @RequestParam(required = false) String motivo) { return respuesta(servicio.cancelar(id, motivo)); }
    @PostMapping("/{id}/preparar") public PedidoRespuesta preparar(@PathVariable Long id) { return respuesta(servicio.preparar(id)); }
    @PostMapping("/{id}/despachar") public PedidoRespuesta despachar(@PathVariable Long id, @RequestParam(required = false) String guia) { return respuesta(servicio.despachar(id, guia)); }
    private PedidoRespuesta respuesta(Pedido p) { return PedidoRespuesta.de(p, servicio.historial(p.getId())); }
}