package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.modelo.ParametrosPagos;
import com.ecomerce.pagos.aplicacion.puerto.entrada.ProcesarEventoProveedorUseCase;
import com.ecomerce.pagos.aplicacion.puerto.salida.EventoProveedorRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.HistorialEstadosRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.IntentoPagoRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.OrdenRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.OutboxRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.ProveedorPagosPort;
import org.springframework.stereotype.Service;

import java.time.Clock;

/** Procesador del inbox (figuras 3 y 5, sección 8.4). */
@Service
public class ProcesarEventoProveedorService implements ProcesarEventoProveedorUseCase {

    private final EventoProveedorRepositorio inbox;
    private final IntentoPagoRepositorio intentos;
    private final OrdenRepositorio ordenes;
    private final HistorialEstadosRepositorio historial;
    private final OutboxRepositorio outbox;
    private final ProveedorPagosPort proveedor;
    private final ParametrosPagos parametros;
    private final Clock reloj;

    public ProcesarEventoProveedorService(EventoProveedorRepositorio inbox, IntentoPagoRepositorio intentos,
                                          OrdenRepositorio ordenes, HistorialEstadosRepositorio historial,
                                          OutboxRepositorio outbox, ProveedorPagosPort proveedor,
                                          ParametrosPagos parametros, Clock reloj) {
        this.inbox = inbox;
        this.intentos = intentos;
        this.ordenes = ordenes;
        this.historial = historial;
        this.outbox = outbox;
        this.proveedor = proveedor;
        this.parametros = parametros;
        this.reloj = reloj;
    }

    @Override
    public int procesarPendientes() {
        // Por cada evento de inbox.tomarPendientes(ahora, loteInbox) (FOR UPDATE SKIP LOCKED),
        // en UNA transacción por evento (TX3):
        //  1. resultado = proveedor.interpretarNotificacion(evento.contenido())
        //     (opcional: confirmar con proveedor.consultarPorReferencia(...)).
        //  2. Cargar el intento por evento.intentoPagoId().
        //  3. Aplicar la transición en el dominio:
        //       Aprobado  -> intento.aprobar(...) + orden.marcarPagada()
        //       Rechazado -> intento.rechazar(...)
        //       Aceptado  -> intento.marcarPendiente(...)
        //     - Optional.empty() (ya estaba en ese estado)      -> no-op idempotente.
        //     - TransicionInvalidaException (p. ej. PENDING tardío sobre un terminal)
        //                                                       -> inbox.marcarIgnorado(...).
        //     - MontoNoCoincideException                        -> MANUAL_REVIEW + alerta.
        //  4. Guardar intento y orden (bloqueo optimista), historial, outbox y inbox.marcarProcesado(...).
        //
        // Ante un error transitorio (BD, conflicto optimista, proveedor):
        //   intentos < maxIntentosInbox -> inbox.programarReintento(id, ahora + 10 s * 2^n con jitter, error)
        //   si no                       -> inbox.marcarFallido(id, error) (DLQ lógica) + alerta.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
