package com.tienda.pedidos.config;
import com.tienda.pedidos.catalogo.*;
import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
@Component
public class DatosIniciales implements CommandLineRunner {
    private final ProductoRepository productos; public DatosIniciales(ProductoRepository productos) { this.productos = productos; }
    public void run(String... args) { if (productos.count() == 0) { productos.save(new Producto("CAM-001", "Camiseta basica", new BigDecimal("15.00"), 10)); productos.save(new Producto("TAZ-001", "Taza de ceramica", new BigDecimal("8.50"), 5)); productos.save(new Producto("ESC-001", "Figura edicion limitada", new BigDecimal("99.00"), 1)); } }
}