package com.tienda.pedidos.integracion;
import org.slf4j.*;
import org.springframework.stereotype.Component;
@Component
public class Notificador {
    private static final Logger log = LoggerFactory.getLogger(Notificador.class);
    public void notificar(String email, String mensaje) { log.info("NOTIFICACION a {}: {}", email, mensaje); }
}