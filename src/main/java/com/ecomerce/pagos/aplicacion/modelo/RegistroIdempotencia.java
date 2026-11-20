package com.ecomerce.pagos.aplicacion.modelo;

import java.time.Instant;

/** Fila de claves_idempotencia (sección 7.2). */
public record RegistroIdempotencia(
        String clienteApiId,
        String clave,
        String hashSolicitud,
        Estado estado,
        Integer estadoRespuesta,
        String cuerpoRespuesta,
        Instant creadoEn,
        Instant expiraEn) {

    public enum Estado { IN_PROGRESS, COMPLETED }
}
