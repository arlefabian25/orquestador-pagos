package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import com.ecomerce.pagos.aplicacion.modelo.RegistroIdempotencia;
import com.ecomerce.pagos.aplicacion.puerto.salida.ClaveIdempotenciaRepositorio;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.ClaveIdempotenciaEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.ClaveIdempotenciaId;
import com.ecomerce.pagos.infraestructura.salida.persistencia.jpa.ClaveIdempotenciaJpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public class ClaveIdempotenciaRepositorioJpa implements ClaveIdempotenciaRepositorio {

    private final ClaveIdempotenciaJpaRepository jpa;

    public ClaveIdempotenciaRepositorioJpa(ClaveIdempotenciaJpaRepository jpa) {
        this.jpa = jpa;
    }

    /** Transacción corta e independiente (sección 7.2, paso 2). */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean reclamar(String clienteApiId, String clave, String hashSolicitud, Instant expiraEn) {
        return jpa.reclamar(clienteApiId, clave, hashSolicitud, expiraEn) == 1;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RegistroIdempotencia> buscar(String clienteApiId, String clave) {
        return jpa.findById(new ClaveIdempotenciaId(clienteApiId, clave))
                .map(e -> new RegistroIdempotencia(clienteApiId, clave, e.getHashSolicitud(),
                        RegistroIdempotencia.Estado.valueOf(e.getEstado()), e.getEstadoRespuesta(),
                        e.getCuerpoRespuesta(), e.getCreadoEn(), e.getExpiraEn()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completar(String clienteApiId, String clave, int estadoRespuesta, String cuerpoRespuesta) {
        ClaveIdempotenciaEntity e = jpa.findById(new ClaveIdempotenciaId(clienteApiId, clave))
                .orElseThrow(() -> new IllegalStateException("Clave de idempotencia no encontrada"));
        e.setEstado(RegistroIdempotencia.Estado.COMPLETED.name());
        e.setEstadoRespuesta(estadoRespuesta);
        e.setCuerpoRespuesta(cuerpoRespuesta);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void liberar(String clienteApiId, String clave) {
        jpa.deleteById(new ClaveIdempotenciaId(clienteApiId, clave));
    }
}
