package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.modelo.ParametrosPagos;
import com.ecomerce.pagos.aplicacion.puerto.entrada.ConciliarPagoUseCase;
import com.ecomerce.pagos.aplicacion.puerto.salida.HistorialEstadosRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.IntentoPagoRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.OrdenRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.OutboxRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.ProveedorPagosPort;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.UUID;

/** Proceso de conciliación (figura 4, sección 8.5). */
@Service
public class ConciliarPagoService implements ConciliarPagoUseCase {

    private final IntentoPagoRepositorio intentos;
    private final OrdenRepositorio ordenes;
    private final HistorialEstadosRepositorio historial;
    private final OutboxRepositorio outbox;
    private final ProveedorPagosPort proveedor;
    private final ParametrosPagos parametros;
    private final Clock reloj;

    public ConciliarPagoService(IntentoPagoRepositorio intentos, OrdenRepositorio ordenes,
                                HistorialEstadosRepositorio historial, OutboxRepositorio outbox,
                                ProveedorPagosPort proveedor, ParametrosPagos parametros, Clock reloj) {
        this.intentos = intentos;
        this.ordenes = ordenes;
        this.historial = historial;
        this.outbox = outbox;
        this.proveedor = proveedor;
        this.parametros = parametros;
        this.reloj = reloj;
    }

    @Override
    public int conciliarPendientes() {
        // intentos.tomarParaConciliar(ahora, loteConciliacion) (FOR UPDATE SKIP LOCKED) y conciliar cada uno.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    @Override
    public void conciliar(UUID intentoPagoId) {
        // resultado = proveedor.consultarPorReferencia(intentoPagoId), luego:
        //   Aprobado     -> aprobar + orden.marcarPagada() + outbox (origen RECONCILIATION)
        //   Rechazado    -> rechazar + outbox
        //   Aceptado     -> marcarPendiente (guarda la url de redirección para el polling del frontend)
        //   NoEncontrado -> si pasó la ventana de garantía: fallar ("no existe en el proveedor");
        //                   la orden admite un nuevo intento
        //   Incierto / NoDisponible -> intento.reprogramarConciliacion(backoff 1, 2, 5, 15, 30 min, luego 1 h)
        // Si pasó limiteConciliacion (24 h) sin resolver -> enviarARevisionManual(...) + alerta.
        // PENDING vencido sin notificación -> marcarDesconocido(..., RECONCILIATION, ...) antes de consultar.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
