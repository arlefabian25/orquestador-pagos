package com.ecomerce.pagos.dominio.excepcion;

import com.ecomerce.pagos.dominio.modelo.EstadoIntento;

/** Se intentó una transición no permitida por la máquina de estados (sección 4.3). */
public class TransicionInvalidaException extends ReglaDeNegocioException {
    public TransicionInvalidaException(EstadoIntento desde, EstadoIntento hacia) {
        super("Transición no permitida: " + desde + " -> " + hacia);
    }
}
