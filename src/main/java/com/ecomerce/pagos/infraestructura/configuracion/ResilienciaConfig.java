package com.ecomerce.pagos.infraestructura.configuracion;

import com.ecomerce.pagos.infraestructura.salida.proveedor.ErrorTransitorioProveedor;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.core.IntervalFunction;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Circuit breaker, reintento y bulkhead hacia el proveedor (secciones 8.2 y 8.3).
 * Se configuran por código a partir de pagos.resiliencia para no depender del starter de Resilience4j.
 */
@Configuration
public class ResilienciaConfig {

    @Bean
    public CircuitBreaker circuitoProveedor(PropiedadesPagos propiedades) {
        var c = propiedades.resiliencia().circuito();
        CircuitBreakerConfig config = CircuitBreakerConfig.custom()
                .slidingWindowSize(c.ventana())
                .failureRateThreshold(c.umbralFallos())
                .slowCallRateThreshold(c.umbralLentas())
                .slowCallDurationThreshold(c.llamadaLenta())
                .waitDurationInOpenState(c.esperaAbierto())
                .permittedNumberOfCallsInHalfOpenState(c.llamadasSemiabierto())
                .recordExceptions(ErrorTransitorioProveedor.class)
                .build();
        return CircuitBreaker.of("proveedor", config);
    }

    /** Solo para crear pagos: 1 reintento con 300 ms + jitter, y solo ante errores transitorios. */
    @Bean
    public Retry reintentoCrearPago(PropiedadesPagos propiedades) {
        var r = propiedades.resiliencia().reintentoCrear();
        RetryConfig config = RetryConfig.custom()
                .maxAttempts(r.maxIntentos())
                .intervalFunction(IntervalFunction.ofRandomized(r.espera().toMillis()))
                .retryExceptions(ErrorTransitorioProveedor.class)
                .build();
        return Retry.of("proveedor-crear", config);
    }

    @Bean
    public Bulkhead bulkheadProveedor(PropiedadesPagos propiedades) {
        BulkheadConfig config = BulkheadConfig.custom()
                .maxConcurrentCalls(propiedades.resiliencia().bulkhead().maxConcurrentes())
                .maxWaitDuration(Duration.ZERO)       // si está lleno, falla de inmediato
                .build();
        return Bulkhead.of("proveedor", config);
    }
}
