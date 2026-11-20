package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import com.ecomerce.pagos.aplicacion.puerto.salida.IntentoPagoRepositorio;
import com.ecomerce.pagos.dominio.modelo.IntentoPago;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.IntentoPagoEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.jpa.IntentoPagoJpaRepository;
import com.ecomerce.pagos.infraestructura.salida.persistencia.mapeador.IntentoPagoMapeador;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class IntentoPagoRepositorioJpa implements IntentoPagoRepositorio {

    private final IntentoPagoJpaRepository jpa;

    public IntentoPagoRepositorioJpa(IntentoPagoJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public IntentoPago guardar(IntentoPago intento) {
        IntentoPagoEntity entidad = jpa.findById(intento.id()).orElse(null);
        if (entidad == null) {
            entidad = new IntentoPagoEntity(intento.id());
        } else {
            VerificadorVersion.verificar(entidad.getVersion(), intento.version(), intento.id());
        }
        IntentoPagoMapeador.copiar(intento, entidad);
        // saveAndFlush: si el índice ux_intento_unico_activo o el de versión fallan, el error sale aquí.
        return IntentoPagoMapeador.aDominio(jpa.saveAndFlush(entidad));
    }

    @Override
    public Optional<IntentoPago> buscarPorId(UUID id) {
        return jpa.findById(id).map(IntentoPagoMapeador::aDominio);
    }

    @Override
    public List<IntentoPago> buscarPorOrden(UUID ordenId) {
        return jpa.findByOrdenIdOrderByNumeroIntento(ordenId).stream().map(IntentoPagoMapeador::aDominio).toList();
    }

    @Override
    public int contarPorOrden(UUID ordenId) {
        return (int) jpa.countByOrdenId(ordenId);
    }

    @Override
    public List<IntentoPago> tomarParaConciliar(Instant ahora, int limite) {
        return jpa.tomarParaConciliar(ahora, limite).stream().map(IntentoPagoMapeador::aDominio).toList();
    }
}
