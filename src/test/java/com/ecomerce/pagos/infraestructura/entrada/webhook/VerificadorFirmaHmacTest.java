package com.ecomerce.pagos.infraestructura.entrada.webhook;

import com.ecomerce.pagos.infraestructura.configuracion.PropiedadesPagos;
import org.junit.jupiter.api.Test;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HexFormat;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class VerificadorFirmaHmacTest {

    private static final String SECRETO = "secreto-de-prueba";
    private static final Instant AHORA = Instant.parse("2026-11-19T15:00:00Z");

    private final VerificadorFirmaHmac verificador = new VerificadorFirmaHmac(
            new PropiedadesPagos(new PropiedadesPagos.Proveedor("psp", "http://localhost", "k", SECRETO,
                    Duration.ofSeconds(2), Duration.ofSeconds(8), Duration.ofSeconds(5), Duration.ofMinutes(5)),
                    null, null, null, null, null, null, null),
            Clock.fixed(AHORA, ZoneOffset.UTC));

    @Test
    void firmaValida_seAcepta() throws Exception {
        String cuerpo = "{\"eventId\":\"E1\"}";
        String ts = String.valueOf(AHORA.getEpochSecond());

        assertThatCode(() -> verificador.verificar(cuerpo, ts, firmar(ts + "." + cuerpo))).doesNotThrowAnyException();
    }

    @Test
    void cuerpoAlterado_seRechaza() throws Exception {
        String ts = String.valueOf(AHORA.getEpochSecond());
        String firma = firmar(ts + ".{\"eventId\":\"E1\"}");

        assertThatThrownBy(() -> verificador.verificar("{\"eventId\":\"E2\"}", ts, firma))
                .isInstanceOf(FirmaInvalidaException.class);
    }

    @Test
    void timestampFueraDeVentana_seRechaza() throws Exception {
        String cuerpo = "{}";
        String ts = String.valueOf(AHORA.minusSeconds(600).getEpochSecond());

        assertThatThrownBy(() -> verificador.verificar(cuerpo, ts, firmar(ts + "." + cuerpo)))
                .isInstanceOf(FirmaInvalidaException.class);
    }

    private static String firmar(String mensaje) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(SECRETO.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        return HexFormat.of().formatHex(mac.doFinal(mensaje.getBytes(StandardCharsets.UTF_8)));
    }
}
