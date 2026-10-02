package com.tienda.pedidos.pedido;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    Optional<Pedido> findByClaveIdempotencia(String clave);
    List<Pedido> findByEstado(EstadoPedido estado);
    long countByEstado(EstadoPedido estado);
    List<Pedido> findByEstadoAndReservaExpiraEnBefore(EstadoPedido estado, LocalDateTime limite);
    @Lock(LockModeType.PESSIMISTIC_WRITE) @Query("select p from Pedido p where p.id = :id") Optional<Pedido> buscarConBloqueo(@Param("id") Long id);
}