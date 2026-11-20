package com.ecomerce.pagos.dominio.excepcion;

/** Base de las violaciones de reglas del dominio. */
public abstract class ReglaDeNegocioException extends RuntimeException {
    protected ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
