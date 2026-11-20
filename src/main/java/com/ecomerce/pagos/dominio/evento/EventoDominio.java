package com.ecomerce.pagos.dominio.evento;

import java.time.Instant;
import java.util.UUID;

/** Evento de dominio que se guarda en eventos_outbox y se publica en el broker. */
public sealed interface EventoDominio permits PaymentApproved, PaymentRejected {

    /** Agregado al que pertenece; se usa como clave de partición (orderId) para preservar el orden. */
    UUID ordenId();

    Instant ocurridoEn();

    /** Nombre del evento en el broker (se mantiene en inglés: es un contrato técnico). */
    default String tipo() {
        return getClass().getSimpleName();
    }
}
