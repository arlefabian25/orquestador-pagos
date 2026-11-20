package com.ecomerce.pagos.dominio.modelo;

import java.util.EnumSet;
import java.util.Set;

/** Estados del intento de pago y transiciones permitidas (secciones 4.2 y 4.3, figura 2). */
public enum EstadoIntento {
    CREATED,
    PENDING,
    UNKNOWN,
    MANUAL_REVIEW,
    APPROVED,
    REJECTED,
    FAILED,
    EXPIRED;

    private static final Set<EstadoIntento> TERMINALES = EnumSet.of(APPROVED, REJECTED, FAILED, EXPIRED);
    private static final Set<EstadoIntento> ACTIVOS = EnumSet.of(CREATED, PENDING, UNKNOWN);

    public boolean esTerminal() {
        return TERMINALES.contains(this);
    }

    /** Un intento activo bloquea la creación de otro intento para la misma orden. */
    public boolean esActivo() {
        return ACTIVOS.contains(this);
    }

    public boolean puedePasarA(EstadoIntento destino) {
        return switch (this) {
            case CREATED -> EnumSet.of(PENDING, UNKNOWN, FAILED, APPROVED, REJECTED).contains(destino);
            case PENDING -> EnumSet.of(APPROVED, REJECTED, EXPIRED, UNKNOWN).contains(destino);
            case UNKNOWN -> EnumSet.of(PENDING, APPROVED, REJECTED, FAILED, MANUAL_REVIEW).contains(destino);
            case MANUAL_REVIEW -> EnumSet.of(APPROVED, REJECTED, FAILED).contains(destino);
            case APPROVED, REJECTED, FAILED, EXPIRED -> false;
        };
    }
}
