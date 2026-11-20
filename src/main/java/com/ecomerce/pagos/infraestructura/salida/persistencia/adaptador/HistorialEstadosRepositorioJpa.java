package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import com.ecomerce.pagos.aplicacion.puerto.salida.HistorialEstadosRepositorio;
import com.ecomerce.pagos.dominio.modelo.Transicion;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.HistorialEstadoPagoEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.jpa.HistorialEstadoPagoJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public class HistorialEstadosRepositorioJpa implements HistorialEstadosRepositorio {

    private final HistorialEstadoPagoJpaRepository jpa;

    public HistorialEstadosRepositorioJpa(HistorialEstadoPagoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void registrar(Transicion t, UUID eventoProveedorId, String idCorrelacion) {
        jpa.save(new HistorialEstadoPagoEntity(t.intentoPagoId(),
                t.estadoOrigen() == null ? null : t.estadoOrigen().name(),
                t.estadoDestino().name(), t.origen().name(), t.motivo(), eventoProveedorId, idCorrelacion,
                t.ocurridoEn()));
    }
}
