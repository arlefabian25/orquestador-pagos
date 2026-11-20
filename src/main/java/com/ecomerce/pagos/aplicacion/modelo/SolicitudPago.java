package com.ecomerce.pagos.aplicacion.modelo;

import com.ecomerce.pagos.dominio.modelo.Monto;

import java.util.UUID;

/** Datos para crear el pago en el proveedor. La referencia es el id del intento (idempotencia). */
public record SolicitudPago(UUID referencia, Monto monto, String urlRetorno, String idCorrelacion) {
}
