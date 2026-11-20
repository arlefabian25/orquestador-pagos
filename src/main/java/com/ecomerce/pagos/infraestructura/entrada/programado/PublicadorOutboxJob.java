package com.ecomerce.pagos.infraestructura.entrada.programado;

import com.ecomerce.pagos.aplicacion.puerto.entrada.PublicarEventosUseCase;
import com.ecomerce.pagos.infraestructura.configuracion.PropiedadesPagos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Publicador del outbox: tarea periódica que corre en TODAS las instancias. Se reparten el trabajo con
 * FOR UPDATE SKIP LOCKED, sin elección de líder ni locks distribuidos (sección 3.6).
 */
@Component
public class PublicadorOutboxJob {

    private static final Logger log = LoggerFactory.getLogger(PublicadorOutboxJob.class);

    private final PublicarEventosUseCase casoDeUso;
    private final boolean habilitada;

    public PublicadorOutboxJob(PublicarEventosUseCase casoDeUso, PropiedadesPagos propiedades) {
        this.casoDeUso = casoDeUso;
        this.habilitada = propiedades.tareas().habilitadas();
    }

    @Scheduled(fixedDelayString = "${pagos.outbox.intervalo}")
    public void ejecutar() {
        if (!habilitada) {
            return;
        }
        try {
            int cantidad = casoDeUso.publicarPendientes();
            if (cantidad > 0) {
                log.debug("Publicador del outbox: {} elementos", cantidad);
            }
        } catch (RuntimeException e) {
            // Nunca se propaga: la siguiente ejecución vuelve a intentarlo.
            log.error("Publicador del outbox falló", e);
        }
    }
}
