package com.ecomerce.pagos.dominio.evento;

import java.time.Instant;
import java.util.UUID;

public record PaymentRejected(UUID intentoPagoId, UUID ordenId, String motivo, Instant ocurridoEn)
        implements EventoDominio {
}
