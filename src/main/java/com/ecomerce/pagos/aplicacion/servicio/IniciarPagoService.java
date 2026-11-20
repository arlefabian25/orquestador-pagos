package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.modelo.ParametrosPagos;
import com.ecomerce.pagos.aplicacion.puerto.entrada.IniciarPagoUseCase;
import com.ecomerce.pagos.aplicacion.puerto.salida.GeneradorIdentificadores;
import com.ecomerce.pagos.aplicacion.puerto.salida.HistorialEstadosRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.IntentoPagoRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.OrdenRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.ProveedorPagosPort;
import com.ecomerce.pagos.dominio.modelo.IntentoPago;
import org.springframework.stereotype.Service;

import java.time.Clock;

/**
 * Flujo de las figuras 3 y 4.
 *
 * <p>Este método NO es @Transactional: la llamada al proveedor no puede quedar dentro de una
 * transacción (agotaría el pool ante un proveedor lento). Usa dos transacciones cortas (TX1 y TX2),
 * por ejemplo con un {@code TransactionTemplate}.
 */
@Service
public class IniciarPagoService implements IniciarPagoUseCase {

    private final OrdenRepositorio ordenes;
    private final IntentoPagoRepositorio intentos;
    private final HistorialEstadosRepositorio historial;
    private final ProveedorPagosPort proveedor;
    private final GeneradorIdentificadores ids;
    private final ParametrosPagos parametros;
    private final Clock reloj;

    public IniciarPagoService(OrdenRepositorio ordenes, IntentoPagoRepositorio intentos,
                              HistorialEstadosRepositorio historial, ProveedorPagosPort proveedor,
                              GeneradorIdentificadores ids, ParametrosPagos parametros, Clock reloj) {
        this.ordenes = ordenes;
        this.intentos = intentos;
        this.historial = historial;
        this.proveedor = proveedor;
        this.ids = ids;
        this.parametros = parametros;
        this.reloj = reloj;
    }

    @Override
    public IntentoPago iniciar(Comando comando) {
        // TX1
        //  1. Cargar la orden y verificar orden.admiteNuevoIntento(ahora); si no -> OrdenNoPagableException (409).
        //  2. Crear IntentoPago.crear(ids.nuevo(), orden, contarPorOrden + 1, proveedor.nombre(), idCorrelacion,
        //     ahora + createdHuerfanoDespuesDe) y guardarlo en CREATED.
        //     El índice ux_intento_unico_activo rechaza un segundo intento activo concurrente -> 409.
        //
        // Fuera de transacción
        //  3. proveedor.crearPago(new SolicitudPago(intento.id(), monto, urlRetorno, idCorrelacion)).
        //     Si el resultado es NoDisponible (circuito abierto) -> responder 503 + Retry-After.
        //
        // TX2: según el ResultadoProveedor
        //  4. Aceptado  -> intento.marcarPendiente(...)
        //     Aprobado  -> intento.aprobar(...) + orden.marcarPagada() + outbox
        //     Rechazado -> intento.rechazar(...) + outbox
        //     Fallido   -> intento.fallar(...)
        //     Incierto  -> intento.marcarDesconocido(..., ahora + primeraConciliacion, ahora)
        //  5. Guardar el intento, registrar cada Transicion en el historial y los eventos en el outbox.
        //
        // Si el servicio cae entre TX1 y TX2, el intento queda CREATED y la conciliación lo resuelve.
        throw new UnsupportedOperationException("Pendiente de implementar");
    }
}
