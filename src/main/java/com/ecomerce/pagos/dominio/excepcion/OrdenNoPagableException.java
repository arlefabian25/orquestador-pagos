package com.ecomerce.pagos.dominio.excepcion;

import java.util.UUID;

/** La orden no admite un nuevo intento (ya pagada, expirada o con un intento activo). */
public class OrdenNoPagableException extends ReglaDeNegocioException {
    public OrdenNoPagableException(UUID ordenId, String motivo) {
        super("La orden " + ordenId + " no admite un nuevo intento: " + motivo);
    }
}
