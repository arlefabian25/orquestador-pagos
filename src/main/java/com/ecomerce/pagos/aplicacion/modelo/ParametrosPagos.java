package com.ecomerce.pagos.aplicacion.modelo;

import java.time.Duration;

/**
 * Parámetros que usan los casos de uso. Se construye en infraestructura a partir de application.yaml,
 * para que la capa de aplicación no dependa de la configuración de Spring.
 */
public record ParametrosPagos(
        Duration vigenciaOrden,                 // expira_en de la orden (p. ej. 30 min)
        Duration primeraConciliacion,           // UNKNOWN -> primera consulta (1 min)
        Duration createdHuerfanoDespuesDe,      // CREATED sin respuesta -> conciliación (2 min)
        Duration limiteConciliacion,            // reintentos agotados -> MANUAL_REVIEW (24 h)
        Duration vigenciaClaveIdempotencia,     // 24 h
        int loteInbox,
        int maxIntentosInbox,                   // 8
        int loteConciliacion,
        int loteOutbox) {
}
