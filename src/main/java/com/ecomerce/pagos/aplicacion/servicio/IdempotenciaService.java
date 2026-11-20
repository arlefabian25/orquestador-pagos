package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.modelo.ParametrosPagos;
import com.ecomerce.pagos.aplicacion.puerto.salida.ClaveIdempotenciaRepositorio;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.util.function.Supplier;

/**
 * Algoritmo de Idempotency-Key de la sección 7.2. Lo usan los controladores de órdenes y pagos.
 */
@Service
public class IdempotenciaService {

    private final ClaveIdempotenciaRepositorio claves;
    private final ParametrosPagos parametros;
    private final Clock reloj;

    public IdempotenciaService(ClaveIdempotenciaRepositorio claves, ParametrosPagos parametros, Clock reloj) {
        this.claves = claves;
        this.parametros = parametros;
        this.reloj = reloj;
    }

    /**
     * 1. claves.reclamar(...) en una TX corta.
     * 2. Si se reclamó: ejecutar la operación, guardar estado y cuerpo de la respuesta y marcar COMPLETED.
     *    Error no reintentable -> se guarda la respuesta de error. Error transitorio -> claves.liberar(...).
     * 3. Si ya existía con otro hash -> 422.
     * 4. Si ya existía COMPLETED -> devolver exactamente la respuesta guardada.
     * 5. Si ya existía IN_PROGRESS -> 409 con Retry-After (o liberarla si quedó abandonada).
     */
    public RespuestaIdempotente ejecutar(String clienteApiId, String clave, String hashSolicitud,
                                         Supplier<RespuestaIdempotente> operacion) {
        throw new UnsupportedOperationException("Pendiente de implementar");
    }

    /** Respuesta HTTP que se memoriza en claves_idempotencia. */
    public record RespuestaIdempotente(int estadoHttp, String cuerpoJson) { }
}
