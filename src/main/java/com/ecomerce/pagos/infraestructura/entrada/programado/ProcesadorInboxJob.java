package com.ecomerce.pagos.infraestructura.entrada.programado;

import com.ecomerce.pagos.aplicacion.puerto.entrada.ProcesarEventoProveedorUseCase;
import com.ecomerce.pagos.infraestructura.configuracion.PropiedadesPagos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Procesador del inbox: tarea periódica que corre en TODAS las instancias. Se reparten el trabajo con
 * FOR UPDATE SKIP LOCKED, sin elección de líder ni locks distribuidos (sección 3.6).
 */
@Component
public class ProcesadorInboxJob {

    private static final Logger log = LoggerFactory.getLogger(ProcesadorInboxJob.class);

    private final ProcesarEventoProveedorUseCase casoDeUso;
    private final boolean habilitada;

    public ProcesadorInboxJob(ProcesarEventoProveedorUseCase casoDeUso, PropiedadesPagos propiedades) {
        this.casoDeUso = casoDeUso;
        this.habilitada = propiedades.tareas().habilitadas();
    }

    @Scheduled(fixedDelayString = "${pagos.inbox.intervalo}")
    public void ejecutar() {
        if (!habilitada) {
            return;
        }
        try {
            int cantidad = casoDeUso.procesarPendientes();
            if (cantidad > 0) {
                log.debug("Procesador del inbox: {} elementos", cantidad);
            }
        } catch (RuntimeException e) {
            // Nunca se propaga: la siguiente ejecución vuelve a intentarlo.
            log.error("Procesador del inbox falló", e);
        }
    }
}
