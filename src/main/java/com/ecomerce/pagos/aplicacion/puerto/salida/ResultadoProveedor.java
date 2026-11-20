package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.dominio.modelo.Monto;

/**
 * Resultado tipado de una interacción con el proveedor (capa anticorrupción, sección 3.3).
 * El dominio nunca ve los DTO ni los códigos HTTP del proveedor.
 */
public sealed interface ResultadoProveedor {

    /** El pago está en curso; el cliente debe ir al portal (PENDING). */
    record Aceptado(String idPagoProveedor, String urlRedireccion) implements ResultadoProveedor { }

    record Aprobado(String idPagoProveedor, Monto monto) implements ResultadoProveedor { }

    record Rechazado(String idPagoProveedor, String motivo) implements ResultadoProveedor { }

    /** Error definitivo sin cobro (4xx de validación). */
    record Fallido(String motivo) implements ResultadoProveedor { }

    /** Consulta por referencia: el proveedor no tiene el pago. */
    record NoEncontrado() implements ResultadoProveedor { }

    /** Timeout o 5xx: no se sabe si se cobró. Lleva el intento a UNKNOWN. */
    record Incierto(String causa) implements ResultadoProveedor { }

    /** Circuito abierto o bulkhead lleno: no se llamó al proveedor, así que no hubo cobro. */
    record NoDisponible(String causa) implements ResultadoProveedor { }
}
