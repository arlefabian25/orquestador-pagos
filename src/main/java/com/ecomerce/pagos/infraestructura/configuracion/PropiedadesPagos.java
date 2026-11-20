package com.ecomerce.pagos.infraestructura.configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/** Propiedades "pagos.*" de application.yaml. */
@ConfigurationProperties("pagos")
public record PropiedadesPagos(
        Proveedor proveedor,
        Orden orden,
        Inbox inbox,
        Conciliacion conciliacion,
        Outbox outbox,
        Idempotencia idempotencia,
        Tareas tareas,
        Resiliencia resiliencia) {

    public record Proveedor(String nombre, String urlBase, String apiKey, String secretoWebhook,
                            Duration timeoutConexion, Duration timeoutLecturaCrear,
                            Duration timeoutLecturaConsulta, Duration ventanaFirma) { }

    public record Orden(Duration vigencia) { }

    public record Inbox(Duration intervalo, int tamanoLote, int maxIntentos) { }

    public record Conciliacion(Duration intervalo, Duration primeraConsulta, Duration createdHuerfanoDespuesDe,
                               Duration limiteTotal, int tamanoLote) { }

    public record Outbox(Duration intervalo, int tamanoLote, String topico) { }

    public record Idempotencia(Duration expiracion) { }

    /** Permite apagar las tareas periódicas (útil mientras los casos de uso no estén implementados). */
    public record Tareas(boolean habilitadas) { }

    public record Resiliencia(ConfigCircuito circuito, ConfigReintento reintentoCrear, ConfigBulkhead bulkhead) {

        public record ConfigCircuito(int ventana, float umbralFallos, float umbralLentas, Duration llamadaLenta,
                                     Duration esperaAbierto, int llamadasSemiabierto) { }

        public record ConfigReintento(int maxIntentos, Duration espera) { }

        public record ConfigBulkhead(int maxConcurrentes) { }
    }
}
