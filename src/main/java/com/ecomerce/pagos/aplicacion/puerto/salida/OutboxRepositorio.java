package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.aplicacion.modelo.EventoOutbox;
import com.ecomerce.pagos.dominio.evento.EventoDominio;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** eventos_outbox: se escribe en la MISMA transacción que el cambio de estado. */
public interface OutboxRepositorio {

    void guardar(EventoDominio evento, String idCorrelacion);

    /** Eventos PENDING en orden de creación, con FOR UPDATE SKIP LOCKED. */
    List<EventoOutbox> tomarPendientes(Instant ahora, int limite);

    void marcarPublicado(UUID id, Instant publicadoEn);

    void programarReintento(UUID id, Instant proximoIntento);
}
