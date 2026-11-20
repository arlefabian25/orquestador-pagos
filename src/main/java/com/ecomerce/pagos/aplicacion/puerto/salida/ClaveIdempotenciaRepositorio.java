package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.aplicacion.modelo.RegistroIdempotencia;

import java.time.Instant;
import java.util.Optional;

/** claves_idempotencia (sección 7.2). */
public interface ClaveIdempotenciaRepositorio {

    /**
     * INSERT ... (estado = IN_PROGRESS) ON CONFLICT DO NOTHING, en una transacción corta e independiente.
     * @return true si esta petición reclamó la clave.
     */
    boolean reclamar(String clienteApiId, String clave, String hashSolicitud, Instant expiraEn);

    Optional<RegistroIdempotencia> buscar(String clienteApiId, String clave);

    void completar(String clienteApiId, String clave, int estadoRespuesta, String cuerpoRespuesta);

    /** Error transitorio: se borra la clave para permitir el reintento del cliente. */
    void liberar(String clienteApiId, String clave);
}
