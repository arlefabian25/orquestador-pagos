package com.ecomerce.pagos.infraestructura.salida.mensajeria;

import com.ecomerce.pagos.aplicacion.modelo.EventoOutbox;
import com.ecomerce.pagos.aplicacion.puerto.salida.PublicadorEventosPort;
import com.ecomerce.pagos.infraestructura.configuracion.PropiedadesPagos;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.header.internals.RecordHeader;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Publica en Kafka con clave = orderId para preservar el orden por orden (sección 3.5). */
@Component
public class PublicadorKafkaAdapter implements PublicadorEventosPort {

    private final KafkaTemplate<String, String> kafka;
    private final String topico;

    public PublicadorKafkaAdapter(KafkaTemplate<String, String> kafka, PropiedadesPagos propiedades) {
        this.kafka = kafka;
        this.topico = propiedades.outbox().topico();
    }

    @Override
    public void publicar(EventoOutbox evento) {
        ProducerRecord<String, String> registro =
                new ProducerRecord<>(topico, evento.agregadoId().toString(), evento.contenido());
        evento.cabeceras().forEach((k, v) ->
                registro.headers().add(new RecordHeader(k, v.getBytes(StandardCharsets.UTF_8))));
        try {
            // Se espera la confirmación del broker: si falla, el evento sigue PENDING y se reintenta.
            kafka.send(registro).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Publicación interrumpida", e);
        } catch (ExecutionException | TimeoutException e) {
            throw new IllegalStateException("El broker no confirmó el evento " + evento.id(), e);
        }
    }
}
