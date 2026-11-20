package com.ecomerce.pagos.aplicacion.modelo;

import java.time.Instant;
import java.util.UUID;

/** Notificación del proveedor guardada en el inbox (tabla eventos_proveedor). */
public record EventoProveedor(
        UUID id,
        String proveedor,
        String idEventoProveedor,
        UUID intentoPagoId,          // referencia del comercio que viene en la notificación (puede ser null)
        String tipoEvento,
        String contenido,            // payload sanitizado (sin datos sensibles)
        int intentos,
        Instant ocurridoEnProveedor) {
}
