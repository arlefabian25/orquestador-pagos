package com.ecomerce.pagos.aplicacion.puerto.entrada;

/** Lo usa la tarea periódica del procesador del inbox. */
public interface ProcesarEventoProveedorUseCase {

    /** Procesa un lote de eventos pendientes. @return cuántos procesó. */
    int procesarPendientes();
}
