package com.tienda.pedidos.pedido;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.util.List;
public record SolicitudPedido(@NotBlank @Email String clienteEmail, @NotBlank @Pattern(regexp = "WEB|INSTAGRAM|FACEBOOK|WHATSAPP", message = "canal debe ser WEB, INSTAGRAM, FACEBOOK o WHATSAPP") String canal, @NotEmpty @Valid List<Item> items) {
    public record Item(@NotBlank String sku, @Min(1) int cantidad) {}
}