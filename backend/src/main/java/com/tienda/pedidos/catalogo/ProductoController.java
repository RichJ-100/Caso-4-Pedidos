package com.tienda.pedidos.catalogo;

import java.math.BigDecimal;
import java.util.List;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    public record ProductoRespuesta(String sku, String nombre, BigDecimal precio, int stock, int reservado, int disponible) {}
    private final ProductoRepository productos;
    public ProductoController(ProductoRepository productos) { this.productos = productos; }
    @GetMapping
    public List<ProductoRespuesta> listar() {
        return productos.findAll().stream().map(p -> new ProductoRespuesta(p.getSku(), p.getNombre(), p.getPrecio(), p.getStock(), p.getReservado(), p.disponible())).toList();
    }
}