package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import com.ecomerce.pagos.aplicacion.puerto.salida.OrdenRepositorio;
import com.ecomerce.pagos.dominio.modelo.Orden;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.OrdenEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.jpa.OrdenJpaRepository;
import com.ecomerce.pagos.infraestructura.salida.persistencia.mapeador.OrdenMapeador;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class OrdenRepositorioJpa implements OrdenRepositorio {

    private final OrdenJpaRepository jpa;

    public OrdenRepositorioJpa(OrdenJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Orden guardar(Orden orden) {
        OrdenEntity entidad = jpa.findById(orden.id()).orElse(null);
        if (entidad == null) {
            entidad = new OrdenEntity(orden.id());
        } else {
            VerificadorVersion.verificar(entidad.getVersion(), orden.version(), orden.id());
        }
        OrdenMapeador.copiar(orden, entidad);
        return OrdenMapeador.aDominio(jpa.saveAndFlush(entidad));
    }

    @Override
    public Optional<Orden> buscarPorId(UUID id) {
        return jpa.findById(id).map(OrdenMapeador::aDominio);
    }
}
