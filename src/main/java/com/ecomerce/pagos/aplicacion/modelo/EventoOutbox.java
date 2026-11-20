package com.ecomerce.pagos.aplicacion.modelo;

import java.util.Map;
import java.util.UUID;

/** Evento de dominio pendiente de publicar (tabla eventos_outbox). */
public record EventoOutbox(
        UUID id,                     // los consumidores lo usan para deduplicar
        UUID agregadoId,             // orderId: clave de partición en el broker
        String tipoEvento,
        String contenido,
        Map<String, String> cabeceras,
        int intentos) {
}
