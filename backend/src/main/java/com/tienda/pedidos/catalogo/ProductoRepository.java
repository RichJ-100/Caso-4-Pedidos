package com.tienda.pedidos.catalogo;

import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    @Query("select p.id from Producto p where p.sku in :skus order by p.id")
    List<Long> idsPorSkus(@Param("skus") Collection<String> skus);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Producto p where p.id = :id")
    Optional<Producto> buscarConBloqueo(@Param("id") Long id);
}