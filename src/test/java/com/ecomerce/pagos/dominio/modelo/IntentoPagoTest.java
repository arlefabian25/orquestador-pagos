package com.ecomerce.pagos.dominio.modelo;

import com.ecomerce.pagos.dominio.evento.PaymentApproved;
import com.ecomerce.pagos.dominio.excepcion.MontoNoCoincideException;
import com.ecomerce.pagos.dominio.excepcion.TransicionInvalidaException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** Pruebas unitarias de la máquina de estados, sin Spring (sección 11). */
class IntentoPagoTest {

    private static final Instant AHORA = Instant.parse("2026-11-19T15:00:00Z");
    private static final Monto MONTO = new Monto(200_000_00L, "COP");

    private IntentoPago nuevoIntento() {
        Orden orden = Orden.crear(UUID.randomUUID(), "comprador-1", MONTO, AHORA.plusSeconds(1800));
        return IntentoPago.crear(UUID.randomUUID(), orden, 1, "psp", "corr-1", AHORA.plusSeconds(120));
    }

    @Test
    void flujoExitoso_createdPendingApproved_generaEventoPaymentApproved() {
        IntentoPago intento = nuevoIntento();

        intento.marcarPendiente("P1", "https://psp/checkout/P1", OrigenTransicion.API, AHORA);
        var transicion = intento.aprobar("P1", MONTO, OrigenTransicion.WEBHOOK, AHORA);

        assertThat(transicion).isPresent();
        assertThat(intento.estado()).isEqualTo(EstadoIntento.APPROVED);
        assertThat(intento.extraerEventos()).singleElement().isInstanceOf(PaymentApproved.class);
    }

    @Test
    void aprobarDosVeces_esIdempotente() {
        IntentoPago intento = nuevoIntento();
        intento.aprobar("P1", MONTO, OrigenTransicion.WEBHOOK, AHORA);
        intento.extraerEventos();

        var segunda = intento.aprobar("P1", MONTO, OrigenTransicion.WEBHOOK, AHORA);

        assertThat(segunda).isEmpty();
        assertThat(intento.extraerEventos()).isEmpty();
    }

    @Test
    void pendingTardioSobreEstadoTerminal_esTransicionInvalida() {
        IntentoPago intento = nuevoIntento();
        intento.aprobar("P1", MONTO, OrigenTransicion.WEBHOOK, AHORA);

        assertThatThrownBy(() -> intento.marcarPendiente("P1", null, OrigenTransicion.WEBHOOK, AHORA))
                .isInstanceOf(TransicionInvalidaException.class);
    }

    @Test
    void montoDistinto_noSeAprueba() {
        IntentoPago intento = nuevoIntento();

        assertThatThrownBy(() -> intento.aprobar("P1", new Monto(1L, "COP"), OrigenTransicion.WEBHOOK, AHORA))
                .isInstanceOf(MontoNoCoincideException.class);
        assertThat(intento.estado()).isEqualTo(EstadoIntento.CREATED);
    }

    @Test
    void timeout_llevaAUnknown_yLaConciliacionPuedeAprobar() {
        IntentoPago intento = nuevoIntento();

        intento.marcarDesconocido("read timeout", OrigenTransicion.API, AHORA.plusSeconds(60), AHORA);
        intento.aprobar("P1", MONTO, OrigenTransicion.RECONCILIATION, AHORA.plusSeconds(60));

        assertThat(intento.estado()).isEqualTo(EstadoIntento.APPROVED);
        assertThat(intento.proximaConciliacionEn()).isNull();
    }
}
