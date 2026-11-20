package com.ecomerce.pagos.infraestructura.configuracion;

import com.ecomerce.pagos.aplicacion.modelo.ParametrosPagos;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
@EnableConfigurationProperties(PropiedadesPagos.class)
public class AplicacionConfig {

    /** Reloj inyectable: en las pruebas se reemplaza por Clock.fixed(...) para controlar el tiempo. */
    @Bean
    public Clock reloj() {
        return Clock.systemUTC();
    }

    @Bean
    public ParametrosPagos parametrosPagos(PropiedadesPagos p) {
        return new ParametrosPagos(
                p.orden().vigencia(),
                p.conciliacion().primeraConsulta(),
                p.conciliacion().createdHuerfanoDespuesDe(),
                p.conciliacion().limiteTotal(),
                p.idempotencia().expiracion(),
                p.inbox().tamanoLote(),
                p.inbox().maxIntentos(),
                p.conciliacion().tamanoLote(),
                p.outbox().tamanoLote());
    }
}
