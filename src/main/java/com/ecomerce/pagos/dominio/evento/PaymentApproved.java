package com.ecomerce.pagos.dominio.evento;

import com.ecomerce.pagos.dominio.modelo.Monto;

import java.time.Instant;
import java.util.UUID;

public record PaymentApproved(UUID intentoPagoId, UUID ordenId, Monto monto, Instant ocurridoEn)
        implements EventoDominio {
}
