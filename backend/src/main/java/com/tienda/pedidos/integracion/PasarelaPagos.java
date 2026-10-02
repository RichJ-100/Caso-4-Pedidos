package com.tienda.pedidos.integracion;
import java.math.BigDecimal;
public interface PasarelaPagos { String reembolsar(String referenciaPago, BigDecimal monto); }