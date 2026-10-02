package com.tienda.pedidos.web;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
@RestController
public class InicioController {
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public String inicio() { return """
            <h1>Pedidos - Tienda en linea</h1>
            <ul>
              <li><a href="/api/health">/api/health</a> (estado del servicio)</li>
              <li><a href="/api/productos">/api/productos</a> (catalogo y stock)</li>
              <li><a href="/api/metricas">/api/metricas</a> (metricas de negocio)</li>
              <li><a href="/swagger-ui/index.html">/swagger-ui/index.html</a> (probar la API)</li>
            </ul>
            """; }
}