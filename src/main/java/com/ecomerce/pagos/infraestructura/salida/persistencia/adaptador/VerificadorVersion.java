package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import jakarta.persistence.OptimisticLockException;

/**
 * Bloqueo optimista entre el dominio y JPA: si la fila cambió desde que se leyó el objeto de dominio,
 * se rechaza la escritura. Además, Hibernate añade "WHERE version = ?" al UPDATE.
 */
final class VerificadorVersion {

    private VerificadorVersion() { }

    static void verificar(Long versionEnBd, long versionLeida, Object id) {
        long actual = versionEnBd == null ? 0 : versionEnBd;
        if (actual != versionLeida) {
            throw new OptimisticLockException("La fila " + id + " cambió (versión " + versionLeida
                    + " -> " + actual + ")");
        }
    }
}
