package com.tienda.pedidos.integracion;
import java.math.BigDecimal;
import org.slf4j.*;
import org.springframework.stereotype.Component;
@Component
public class PasarelaSimulada implements PasarelaPagos {
    private static final Logger log = LoggerFactory.getLogger(PasarelaSimulada.class);
    public String reembolsar(String referenciaPago, BigDecimal monto) { String referencia = "REF-" + referenciaPago; log.info("REEMBOLSO simulado de {} para el pago {} -> {}", monto, referenciaPago, referencia); return referencia; }
}