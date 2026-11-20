package com.ecomerce.pagos.aplicacion.puerto.entrada;

/** Lo usa la tarea periódica del publicador del outbox. */
public interface PublicarEventosUseCase {

    /** Publica un lote de eventos pendientes. @return cuántos publicó. */
    int publicarPendientes();
}
