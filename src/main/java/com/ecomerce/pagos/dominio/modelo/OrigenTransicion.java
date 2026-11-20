package com.ecomerce.pagos.dominio.modelo;

/** Quién provocó un cambio de estado; se guarda en historial_estados_pago.origen. */
public enum OrigenTransicion {
    API,
    WEBHOOK,
    RECONCILIATION,
    OPERACION
}
