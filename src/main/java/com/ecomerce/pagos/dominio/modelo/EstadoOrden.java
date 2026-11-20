package com.ecomerce.pagos.dominio.modelo;

/** Estados de la orden (sección 4.1). */
public enum EstadoOrden {
    PENDING_PAYMENT,
    PAID,
    EXPIRED,
    CANCELLED;

    public boolean esTerminal() {
        return this != PENDING_PAYMENT;
    }
}
