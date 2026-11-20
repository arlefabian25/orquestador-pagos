package com.ecomerce.pagos.dominio.modelo;

import com.ecomerce.pagos.dominio.evento.EventoDominio;
import com.ecomerce.pagos.dominio.evento.PaymentApproved;
import com.ecomerce.pagos.dominio.evento.PaymentRejected;
import com.ecomerce.pagos.dominio.excepcion.MontoNoCoincideException;
import com.ecomerce.pagos.dominio.excepcion.TransicionInvalidaException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Un intento de cobro de una orden. Contiene la máquina de estados (figura 2).
 *
 * <p>Cada método de transición devuelve {@code Optional.empty()} si el intento ya estaba en el
 * estado destino (repetir una transición es un no-op idempotente) y lanza
 * {@link TransicionInvalidaException} si la transición no está permitida.
 */
public class IntentoPago {

    private final UUID id;                 // UUIDv7: también es la clave de idempotencia y la referencia ante el proveedor
    private final UUID ordenId;
    private final int numeroIntento;
    private final String proveedor;
    private String idPagoProveedor;
    private final Monto monto;
    private EstadoIntento estado;
    private String urlRedireccion;
    private int intentosConciliacion;
    private Instant proximaConciliacionEn;
    private final String idCorrelacion;
    private final long version;

    private final List<EventoDominio> eventos = new ArrayList<>();

    private IntentoPago(UUID id, UUID ordenId, int numeroIntento, String proveedor, String idPagoProveedor,
                        Monto monto, EstadoIntento estado, String urlRedireccion, int intentosConciliacion,
                        Instant proximaConciliacionEn, String idCorrelacion, long version) {
        this.id = Objects.requireNonNull(id);
        this.ordenId = Objects.requireNonNull(ordenId);
        this.numeroIntento = numeroIntento;
        this.proveedor = Objects.requireNonNull(proveedor);
        this.idPagoProveedor = idPagoProveedor;
        this.monto = Objects.requireNonNull(monto);
        this.estado = Objects.requireNonNull(estado);
        this.urlRedireccion = urlRedireccion;
        this.intentosConciliacion = intentosConciliacion;
        this.proximaConciliacionEn = proximaConciliacionEn;
        this.idCorrelacion = Objects.requireNonNull(idCorrelacion);
        this.version = version;
    }

    /** Se crea en CREATED y se persiste ANTES de llamar al proveedor (TX1). */
    public static IntentoPago crear(UUID id, Orden orden, int numeroIntento, String proveedor,
                                    String idCorrelacion, Instant proximaConciliacionEn) {
        return new IntentoPago(id, orden.id(), numeroIntento, proveedor, null, orden.monto(),
                EstadoIntento.CREATED, null, 0, proximaConciliacionEn, idCorrelacion, 0);
    }

    public static IntentoPago reconstruir(UUID id, UUID ordenId, int numeroIntento, String proveedor,
                                          String idPagoProveedor, Monto monto, EstadoIntento estado,
                                          String urlRedireccion, int intentosConciliacion,
                                          Instant proximaConciliacionEn, String idCorrelacion, long version) {
        return new IntentoPago(id, ordenId, numeroIntento, proveedor, idPagoProveedor, monto, estado,
                urlRedireccion, intentosConciliacion, proximaConciliacionEn, idCorrelacion, version);
    }

    // ------------------------------------------------------------------ transiciones

    public Optional<Transicion> marcarPendiente(String idPagoProveedor, String urlRedireccion,
                                                OrigenTransicion origen, Instant ahora) {
        Optional<Transicion> t = transicionar(EstadoIntento.PENDING, origen, null, ahora);
        t.ifPresent(x -> {
            this.idPagoProveedor = idPagoProveedor;
            this.urlRedireccion = urlRedireccion;
        });
        return t;
    }

    /** Valida monto y moneda antes de aprobar: si no coinciden, no se aprueba. */
    public Optional<Transicion> aprobar(String idPagoProveedor, Monto montoNotificado,
                                        OrigenTransicion origen, Instant ahora) {
        if (!monto.equals(montoNotificado)) {
            throw new MontoNoCoincideException(monto, montoNotificado);
        }
        Optional<Transicion> t = transicionar(EstadoIntento.APPROVED, origen, null, ahora);
        t.ifPresent(x -> {
            this.idPagoProveedor = idPagoProveedor;
            this.proximaConciliacionEn = null;
            eventos.add(new PaymentApproved(id, ordenId, monto, ahora));
        });
        return t;
    }

    public Optional<Transicion> rechazar(String motivo, OrigenTransicion origen, Instant ahora) {
        Optional<Transicion> t = transicionar(EstadoIntento.REJECTED, origen, motivo, ahora);
        t.ifPresent(x -> {
            this.proximaConciliacionEn = null;
            eventos.add(new PaymentRejected(id, ordenId, motivo, ahora));
        });
        return t;
    }

    /**
     * Timeout o 5xx al crear el pago, o PENDING sin notificación en el plazo: no se sabe si cobró.
     * Se agenda la conciliación.
     */
    public Optional<Transicion> marcarDesconocido(String motivo, OrigenTransicion origen,
                                                  Instant proximaConciliacion, Instant ahora) {
        Optional<Transicion> t = transicionar(EstadoIntento.UNKNOWN, origen, motivo, ahora);
        t.ifPresent(x -> this.proximaConciliacionEn = proximaConciliacion);
        return t;
    }

    /** Error definitivo sin cobro (4xx del proveedor o el pago no existe tras la ventana de garantía). */
    public Optional<Transicion> fallar(String motivo, OrigenTransicion origen, Instant ahora) {
        Optional<Transicion> t = transicionar(EstadoIntento.FAILED, origen, motivo, ahora);
        t.ifPresent(x -> this.proximaConciliacionEn = null);
        return t;
    }

    public Optional<Transicion> expirar(OrigenTransicion origen, Instant ahora) {
        Optional<Transicion> t = transicionar(EstadoIntento.EXPIRED, origen, "el cliente no completó el pago", ahora);
        t.ifPresent(x -> this.proximaConciliacionEn = null);
        return t;
    }

    public Optional<Transicion> enviarARevisionManual(String motivo, Instant ahora) {
        Optional<Transicion> t = transicionar(EstadoIntento.MANUAL_REVIEW, OrigenTransicion.RECONCILIATION, motivo, ahora);
        t.ifPresent(x -> this.proximaConciliacionEn = null);
        return t;
    }

    /** Fija cuándo debe revisarse el intento si no llega notificación (p. ej. al quedar PENDING). */
    public void programarConciliacion(Instant cuando) {
        this.proximaConciliacionEn = cuando;
    }

    /** La conciliación no pudo resolver el intento: se reprograma con backoff. */
    public void reprogramarConciliacion(Instant proxima) {
        this.intentosConciliacion++;
        this.proximaConciliacionEn = proxima;
    }

    private Optional<Transicion> transicionar(EstadoIntento destino, OrigenTransicion origen, String motivo, Instant ahora) {
        if (estado == destino) {
            return Optional.empty();
        }
        if (!estado.puedePasarA(destino)) {
            throw new TransicionInvalidaException(estado, destino);
        }
        Transicion transicion = new Transicion(id, estado, destino, origen, motivo, ahora);
        estado = destino;
        return Optional.of(transicion);
    }

    /** Devuelve y limpia los eventos de dominio generados; el servicio los guarda en el outbox. */
    public List<EventoDominio> extraerEventos() {
        List<EventoDominio> copia = List.copyOf(eventos);
        eventos.clear();
        return copia;
    }

    public UUID id() { return id; }
    public UUID ordenId() { return ordenId; }
    public int numeroIntento() { return numeroIntento; }
    public String proveedor() { return proveedor; }
    public String idPagoProveedor() { return idPagoProveedor; }
    public Monto monto() { return monto; }
    public EstadoIntento estado() { return estado; }
    public String urlRedireccion() { return urlRedireccion; }
    public int intentosConciliacion() { return intentosConciliacion; }
    public Instant proximaConciliacionEn() { return proximaConciliacionEn; }
    public String idCorrelacion() { return idCorrelacion; }
    public long version() { return version; }
}
