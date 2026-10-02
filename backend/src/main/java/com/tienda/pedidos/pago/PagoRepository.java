package com.tienda.pedidos.pago;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByClaveEvento(String claveEvento);
    Optional<Pago> findFirstByReferencia(String referencia);
    Optional<Pago> findFirstByPedidoIdAndEstado(Long pedidoId, EstadoPago estado);
    long countByEstado(EstadoPago estado);
}