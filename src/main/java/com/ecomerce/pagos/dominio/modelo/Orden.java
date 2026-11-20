package com.ecomerce.pagos.dominio.modelo;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/** Orden de compra y su estado de pago. Sin dependencias de infraestructura. */
public class Orden {

    private final UUID id;
    private final String compradorId;
    private final Monto monto;
    private EstadoOrden estado;
    private final Instant expiraEn;
    private final long version;

    private Orden(UUID id, String compradorId, Monto monto, EstadoOrden estado, Instant expiraEn, long version) {
        this.id = Objects.requireNonNull(id);
        this.compradorId = Objects.requireNonNull(compradorId);
        this.monto = Objects.requireNonNull(monto);
        this.estado = Objects.requireNonNull(estado);
        this.expiraEn = Objects.requireNonNull(expiraEn);
        this.version = version;
    }

    public static Orden crear(UUID id, String compradorId, Monto monto, Instant expiraEn) {
        return new Orden(id, compradorId, monto, EstadoOrden.PENDING_PAYMENT, expiraEn, 0);
    }

    /** Reconstruye una orden leída de la base de datos. */
    public static Orden reconstruir(UUID id, String compradorId, Monto monto, EstadoOrden estado,
                                    Instant expiraEn, long version) {
        return new Orden(id, compradorId, monto, estado, expiraEn, version);
    }

    public boolean admiteNuevoIntento(Instant ahora) {
        return estado == EstadoOrden.PENDING_PAYMENT && ahora.isBefore(expiraEn);
    }

    /** Idempotente: marcar como pagada una orden ya pagada no hace nada. */
    public boolean marcarPagada() {
        if (estado == EstadoOrden.PAID) {
            return false;
        }
        if (estado != EstadoOrden.PENDING_PAYMENT) {
            // Aprobación tardía sobre una orden expirada o cancelada: conflicto y reembolso (sección 1.3).
            throw new IllegalStateException("La orden " + id + " está en " + estado + " y no puede pagarse");
        }
        estado = EstadoOrden.PAID;
        return true;
    }

    public boolean expirar(Instant ahora) {
        if (estado != EstadoOrden.PENDING_PAYMENT || ahora.isBefore(expiraEn)) {
            return false;
        }
        estado = EstadoOrden.EXPIRED;
        return true;
    }

    public UUID id() { return id; }
    public String compradorId() { return compradorId; }
    public Monto monto() { return monto; }
    public EstadoOrden estado() { return estado; }
    public Instant expiraEn() { return expiraEn; }
    public long version() { return version; }
}
