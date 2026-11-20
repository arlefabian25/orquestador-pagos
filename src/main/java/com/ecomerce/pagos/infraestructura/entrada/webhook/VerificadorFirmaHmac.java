package com.ecomerce.pagos.infraestructura.entrada.webhook;

import com.ecomerce.pagos.infraestructura.configuracion.PropiedadesPagos;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Autenticidad e integridad de las notificaciones (sección 9.1):
 * HMAC-SHA256 sobre "timestamp.cuerpo", comparación en tiempo constante y ventana de ±5 min
 * contra ataques de repetición. Ajustar el formato exacto al que use la pasarela.
 */
@Component
public class VerificadorFirmaHmac {

    private final byte[] secreto;
    private final Duration ventana;
    private final Clock reloj;

    public VerificadorFirmaHmac(PropiedadesPagos propiedades, Clock reloj) {
        this.secreto = propiedades.proveedor().secretoWebhook().getBytes(StandardCharsets.UTF_8);
        this.ventana = propiedades.proveedor().ventanaFirma();
        this.reloj = reloj;
    }

    public void verificar(String cuerpo, String timestampEpochSegundos, String firmaHex) {
        if (cuerpo == null || timestampEpochSegundos == null || firmaHex == null) {
            throw new FirmaInvalidaException("faltan cabeceras de firma");
        }
        Instant emitido;
        try {
            emitido = Instant.ofEpochSecond(Long.parseLong(timestampEpochSegundos));
        } catch (NumberFormatException e) {
            throw new FirmaInvalidaException("timestamp inválido");
        }
        if (Duration.between(emitido, reloj.instant()).abs().compareTo(ventana) > 0) {
            throw new FirmaInvalidaException("fuera de la ventana de tiempo");
        }
        byte[] esperada = hmac(timestampEpochSegundos + "." + cuerpo);
        byte[] recibida;
        try {
            recibida = HexFormat.of().parseHex(firmaHex);
        } catch (IllegalArgumentException e) {
            throw new FirmaInvalidaException("firma mal formada");
        }
        if (!MessageDigest.isEqual(esperada, recibida)) {
            throw new FirmaInvalidaException("la firma no coincide");
        }
    }

    private byte[] hmac(String mensaje) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secreto, "HmacSHA256"));
            return mac.doFinal(mensaje.getBytes(StandardCharsets.UTF_8));
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("HmacSHA256 no disponible", e);
        }
    }
}
