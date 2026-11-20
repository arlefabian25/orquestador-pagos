package com.ecomerce.pagos.infraestructura.salida.proveedor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;

/** Notificación (webhook) de la pasarela (ajustar al contrato real). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record NotificacionProveedorDto(String eventId, String eventType, Instant occurredAt,
                                       PagoProveedorResponse payment) {
}
