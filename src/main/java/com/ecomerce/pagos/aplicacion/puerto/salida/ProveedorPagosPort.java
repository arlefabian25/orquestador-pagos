package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.aplicacion.modelo.SolicitudPago;

import java.util.UUID;

/** Puerto hacia la pasarela. La implementación aplica timeouts, reintentos, circuit breaker y bulkhead. */
public interface ProveedorPagosPort {

    /** Nombre del proveedor (se guarda en intentos_pago.proveedor). */
    String nombre();

    ResultadoProveedor crearPago(SolicitudPago solicitud);

    /** Usado por la conciliación y, opcionalmente, para confirmar un webhook. */
    ResultadoProveedor consultarPorReferencia(UUID referencia);

    /** Traduce el contenido de una notificación al resultado tipado (el formato es del proveedor). */
    ResultadoProveedor interpretarNotificacion(String contenido);
}
