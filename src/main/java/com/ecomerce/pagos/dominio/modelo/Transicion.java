package com.ecomerce.pagos.dominio.modelo;

import java.time.Instant;
import java.util.UUID;

/** Un cambio de estado aplicado a un intento. Cada una se guarda en historial_estados_pago. */
public record Transicion(
        UUID intentoPagoId,
        EstadoIntento estadoOrigen,
        EstadoIntento estadoDestino,
        OrigenTransicion origen,
        String motivo,
        Instant ocurridoEn) {
}
