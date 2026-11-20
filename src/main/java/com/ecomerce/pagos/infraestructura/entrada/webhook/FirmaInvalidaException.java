package com.ecomerce.pagos.infraestructura.entrada.webhook;

public class FirmaInvalidaException extends RuntimeException {
    public FirmaInvalidaException(String motivo) {
        super(motivo);
    }
}
