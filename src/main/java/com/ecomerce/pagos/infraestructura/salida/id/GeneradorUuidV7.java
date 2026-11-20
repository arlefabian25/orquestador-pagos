package com.ecomerce.pagos.infraestructura.salida.id;

import com.ecomerce.pagos.aplicacion.puerto.salida.GeneradorIdentificadores;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Clock;
import java.util.UUID;

/**
 * UUID versión 7 (RFC 9562): 48 bits de milisegundos + 74 bits aleatorios.
 * Ordenable en el tiempo (menos fragmentación de índices B-tree) y no adivinable.
 */
@Component
public class GeneradorUuidV7 implements GeneradorIdentificadores {

    private final SecureRandom aleatorio = new SecureRandom();
    private final Clock reloj;

    public GeneradorUuidV7(Clock reloj) {
        this.reloj = reloj;
    }

    @Override
    public UUID nuevo() {
        long milisegundos = reloj.millis() & 0xFFFF_FFFF_FFFFL;
        long masSignificativos = (milisegundos << 16) | 0x7000L | (aleatorio.nextInt() & 0x0FFFL);
        long menosSignificativos = (aleatorio.nextLong() & 0x3FFF_FFFF_FFFF_FFFFL) | 0x8000_0000_0000_0000L;
        return new UUID(masSignificativos, menosSignificativos);
    }
}
