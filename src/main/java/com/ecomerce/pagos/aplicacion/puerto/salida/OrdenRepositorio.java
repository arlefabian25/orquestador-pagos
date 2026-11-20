package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.dominio.modelo.Orden;

import java.util.Optional;
import java.util.UUID;

public interface OrdenRepositorio {

    /** Inserta o actualiza con bloqueo optimista (falla si la versión cambió). */
    Orden guardar(Orden orden);

    Optional<Orden> buscarPorId(UUID id);
}
