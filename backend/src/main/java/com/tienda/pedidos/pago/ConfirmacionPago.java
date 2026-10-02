package com.tienda.pedidos.pago;
import jakarta.validation.constraints.*;
public record ConfirmacionPago(@NotNull Long pedidoId, @NotNull ResultadoPago resultado, @NotBlank String referencia) {}