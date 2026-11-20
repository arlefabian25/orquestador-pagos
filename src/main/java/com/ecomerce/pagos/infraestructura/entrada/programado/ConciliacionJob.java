package com.ecomerce.pagos.infraestructura.entrada.programado;

import com.ecomerce.pagos.aplicacion.puerto.entrada.ConciliarPagoUseCase;
import com.ecomerce.pagos.infraestructura.configuracion.PropiedadesPagos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Proceso de conciliación: tarea periódica que corre en TODAS las instancias. Se reparten el trabajo con
 * FOR UPDATE SKIP LOCKED, sin elección de líder ni locks distribuidos (sección 3.6).
 */
@Component
public class ConciliacionJob {

    private static final Logger log = LoggerFactory.getLogger(ConciliacionJob.class);

    private final ConciliarPagoUseCase casoDeUso;
    private final boolean habilitada;

    public ConciliacionJob(ConciliarPagoUseCase casoDeUso, PropiedadesPagos propiedades) {
        this.casoDeUso = casoDeUso;
        this.habilitada = propiedades.tareas().habilitadas();
    }

    @Scheduled(fixedDelayString = "${pagos.conciliacion.intervalo}")
    public void ejecutar() {
        if (!habilitada) {
            return;
        }
        try {
            int cantidad = casoDeUso.conciliarPendientes();
            if (cantidad > 0) {
                log.debug("Proceso de conciliación: {} elementos", cantidad);
            }
        } catch (RuntimeException e) {
            // Nunca se propaga: la siguiente ejecución vuelve a intentarlo.
            log.error("Proceso de conciliación falló", e);
        }
    }
}
