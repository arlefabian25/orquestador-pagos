package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.dominio.modelo.IntentoPago;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IntentoPagoRepositorio {

    /** Inserta o actualiza con bloqueo optimista (falla si la versión cambió). */
    IntentoPago guardar(IntentoPago intento);

    Optional<IntentoPago> buscarPorId(UUID id);

    List<IntentoPago> buscarPorOrden(UUID ordenId);

    int contarPorOrden(UUID ordenId);

    /**
     * Toma intentos CREATED huérfanos, UNKNOWN y PENDING vencidos cuya próxima conciliación ya llegó,
     * con FOR UPDATE SKIP LOCKED (debe llamarse dentro de una transacción).
     */
    List<IntentoPago> tomarParaConciliar(Instant ahora, int limite);
}
