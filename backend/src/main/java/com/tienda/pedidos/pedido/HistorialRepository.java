package com.tienda.pedidos.pedido;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
public interface HistorialRepository extends JpaRepository<HistorialPedido, Long> { List<HistorialPedido> findByPedidoIdOrderByIdAsc(Long pedidoId); }