package com.ecomerce.pagos.infraestructura.configuracion;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita @Scheduled para el procesador del inbox, la conciliación y el publicador del outbox. */
@Configuration
@EnableScheduling
public class ProgramacionConfig {
}
