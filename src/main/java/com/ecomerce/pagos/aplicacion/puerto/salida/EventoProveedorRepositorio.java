package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.aplicacion.modelo.EventoProveedor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Inbox de notificaciones (eventos_proveedor). */
public interface EventoProveedorRepositorio {

    /**
     * INSERT ... ON CONFLICT (proveedor, id_evento_proveedor) DO NOTHING.
     * @return true si se insertó; false si era un duplicado.
     */
    boolean registrarSiNoExiste(EventoProveedor evento);

    /** Eventos RECEIVED o RETRY cuyo próximo intento ya llegó, con FOR UPDATE SKIP LOCKED. */
    List<EventoProveedor> tomarPendientes(Instant ahora, int limite);

    void marcarProcesado(UUID id);

    void marcarIgnorado(UUID id, String motivo);

    void programarReintento(UUID id, Instant proximoIntento, String error);

    /** Reintentos agotados: DLQ lógica, requiere alerta y reproceso manual. */
    void marcarFallido(UUID id, String error);
}
