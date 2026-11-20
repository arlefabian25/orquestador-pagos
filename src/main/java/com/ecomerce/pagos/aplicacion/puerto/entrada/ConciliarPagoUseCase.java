package com.ecomerce.pagos.aplicacion.puerto.entrada;

import java.util.UUID;

/** Lo usan la tarea periódica de conciliación y el endpoint de operaciones. */
public interface ConciliarPagoUseCase {

    /** Concilia un lote de intentos pendientes. @return cuántos resolvió. */
    int conciliarPendientes();

    /** Fuerza la conciliación de un intento (endpoint /admin). */
    void conciliar(UUID intentoPagoId);
}
