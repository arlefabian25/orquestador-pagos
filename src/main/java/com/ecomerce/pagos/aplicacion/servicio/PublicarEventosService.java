package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.modelo.EventoOutbox;
import com.ecomerce.pagos.aplicacion.modelo.ParametrosPagos;
import com.ecomerce.pagos.aplicacion.puerto.entrada.PublicarEventosUseCase;
import com.ecomerce.pagos.aplicacion.puerto.salida.OutboxRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.PublicadorEventosPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

/**
 * Publicador del outbox (sección 8.2). Entrega "al menos una vez": si el proceso cae después de
 * publicar y antes del commit, el evento se publica de nuevo y el consumidor deduplica por su id.
 */
@Service
public class PublicarEventosService implements PublicarEventosUseCase {

    private static final Duration TOPE_BACKOFF = Duration.ofMinutes(5);

    private final OutboxRepositorio outbox;
    private final PublicadorEventosPort publicador;
    private final ParametrosPagos parametros;
    private final Clock reloj;

    public PublicarEventosService(OutboxRepositorio outbox, PublicadorEventosPort publicador,
                                  ParametrosPagos parametros, Clock reloj) {
        this.outbox = outbox;
        this.publicador = publicador;
        this.parametros = parametros;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public int publicarPendientes() {
        Instant ahora = reloj.instant();
        int publicados = 0;
        for (EventoOutbox evento : outbox.tomarPendientes(ahora, parametros.loteOutbox())) {
            try {
                publicador.publicar(evento);
                outbox.marcarPublicado(evento.id(), ahora);
                publicados++;
            } catch (RuntimeException e) {
                outbox.programarReintento(evento.id(), ahora.plus(backoff(evento.intentos())));
                // Se detiene el lote para no publicar fuera de orden los siguientes eventos.
                break;
            }
        }
        return publicados;
    }

    private static Duration backoff(int intentos) {
        long segundos = (long) Math.min(Math.pow(2, intentos), TOPE_BACKOFF.toSeconds());
        return Duration.ofSeconds(segundos);
    }
}
