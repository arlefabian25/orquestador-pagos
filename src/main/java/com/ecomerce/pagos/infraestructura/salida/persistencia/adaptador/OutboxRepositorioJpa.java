package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import com.ecomerce.pagos.aplicacion.modelo.EventoOutbox;
import com.ecomerce.pagos.aplicacion.puerto.salida.GeneradorIdentificadores;
import com.ecomerce.pagos.aplicacion.puerto.salida.OutboxRepositorio;
import com.ecomerce.pagos.dominio.evento.EventoDominio;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.EventoOutboxEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.jpa.EventoOutboxJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
public class OutboxRepositorioJpa implements OutboxRepositorio {

    private final EventoOutboxJpaRepository jpa;
    private final GeneradorIdentificadores ids;
    private final Clock reloj;

    public OutboxRepositorioJpa(EventoOutboxJpaRepository jpa, GeneradorIdentificadores ids, Clock reloj) {
        this.jpa = jpa;
        this.ids = ids;
        this.reloj = reloj;
    }

    @Override
    public void guardar(EventoDominio evento, String idCorrelacion) {
        UUID id = ids.nuevo();
        Map<String, String> cabeceras = Map.of(
                "messageId", id.toString(),
                "eventType", evento.tipo(),
                "correlationId", idCorrelacion == null ? "" : idCorrelacion);
        jpa.save(new EventoOutboxEntity(id, evento.ordenId(), evento.tipo(), SerializadorEventos.aJson(evento),
                SerializadorEventos.aJson(cabeceras), reloj.instant()));
    }

    @Override
    public List<EventoOutbox> tomarPendientes(Instant ahora, int limite) {
        return jpa.tomarPendientes(ahora, limite).stream()
                .map(e -> new EventoOutbox(e.getId(), e.getAgregadoId(), e.getTipoEvento(), e.getContenido(),
                        Map.of("messageId", e.getId().toString(), "eventType", e.getTipoEvento()),
                        e.getIntentos()))
                .toList();
    }

    @Override
    public void marcarPublicado(UUID id, Instant publicadoEn) {
        EventoOutboxEntity e = buscar(id);
        e.setEstado("PUBLISHED");
        e.setPublicadoEn(publicadoEn);
    }

    @Override
    public void programarReintento(UUID id, Instant proximoIntento) {
        EventoOutboxEntity e = buscar(id);
        e.setIntentos(e.getIntentos() + 1);
        e.setProximoIntentoEn(proximoIntento);
    }

    private EventoOutboxEntity buscar(UUID id) {
        return jpa.findById(id).orElseThrow(() -> new IllegalStateException("Evento de outbox no encontrado: " + id));
    }
}
