package com.ecomerce.pagos.infraestructura.salida.proveedor;

import com.ecomerce.pagos.aplicacion.modelo.SolicitudPago;
import com.ecomerce.pagos.aplicacion.puerto.salida.ProveedorPagosPort;
import com.ecomerce.pagos.aplicacion.puerto.salida.ResultadoProveedor;
import com.ecomerce.pagos.infraestructura.configuracion.ClienteHttpProveedorConfig.ClientesProveedor;
import com.ecomerce.pagos.infraestructura.configuracion.PropiedadesPagos;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.LlamadaProveedorEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.jpa.LlamadaProveedorJpaRepository;
import com.ecomerce.pagos.infraestructura.salida.proveedor.dto.CrearPagoProveedorRequest;
import com.ecomerce.pagos.infraestructura.salida.proveedor.dto.PagoProveedorResponse;
import io.github.resilience4j.bulkhead.Bulkhead;
import io.github.resilience4j.bulkhead.BulkheadFullException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * Adaptador HTTP hacia la pasarela (sección 3.3). Orden de las protecciones:
 * reintento( circuit breaker( bulkhead( llamada HTTP con timeouts ) ) ).
 */
@Component
public class ProveedorPagosHttpAdapter implements ProveedorPagosPort {

    private final ClientesProveedor clientes;
    private final PropiedadesPagos.Proveedor config;
    private final CircuitBreaker circuito;
    private final Retry reintentoCrear;
    private final Bulkhead bulkhead;
    private final TraductorProveedor traductor;
    private final LlamadaProveedorJpaRepository auditoria;
    private final Clock reloj;

    public ProveedorPagosHttpAdapter(ClientesProveedor clientes, PropiedadesPagos propiedades,
                                     CircuitBreaker circuitoProveedor, Retry reintentoCrearPago,
                                     Bulkhead bulkheadProveedor, TraductorProveedor traductor,
                                     LlamadaProveedorJpaRepository auditoria, Clock reloj) {
        this.clientes = clientes;
        this.config = propiedades.proveedor();
        this.circuito = circuitoProveedor;
        this.reintentoCrear = reintentoCrearPago;
        this.bulkhead = bulkheadProveedor;
        this.traductor = traductor;
        this.auditoria = auditoria;
        this.reloj = reloj;
    }

    @Override
    public String nombre() {
        return config.nombre();
    }

    @Override
    public ResultadoProveedor crearPago(SolicitudPago s) {
        Supplier<PagoProveedorResponse> llamada = () -> clientes.crear().post()
                .uri("/payments")
                .header("Authorization", "Bearer " + config.apiKey())
                .header("Idempotency-Key", s.referencia().toString())   // permite reintentar sin doble cobro
                .header("X-Correlation-Id", s.idCorrelacion())
                .body(new CrearPagoProveedorRequest(s.referencia().toString(), s.monto().centavos(),
                        s.monto().moneda(), s.urlRetorno()))
                .retrieve()
                .body(PagoProveedorResponse.class);
        return ejecutar("CREAR_PAGO", s.referencia(), s.idCorrelacion(), llamada, true);
    }

    @Override
    public ResultadoProveedor consultarPorReferencia(UUID referencia) {
        Supplier<PagoProveedorResponse> llamada = () -> clientes.consulta().get()
                .uri("/payments?reference={ref}", referencia)
                .header("Authorization", "Bearer " + config.apiKey())
                .retrieve()
                .body(PagoProveedorResponse.class);
        return ejecutar("CONSULTAR_PAGO", referencia, null, llamada, false);
    }

    @Override
    public ResultadoProveedor interpretarNotificacion(String contenido) {
        return traductor.aResultado(traductor.leerNotificacion(contenido).payment());
    }

    private ResultadoProveedor ejecutar(String operacion, UUID intentoPagoId, String idCorrelacion,
                                        Supplier<PagoProveedorResponse> llamada, boolean conReintento) {
        Supplier<PagoProveedorResponse> protegida = Bulkhead.decorateSupplier(bulkhead, () -> traducirErrores(llamada));
        protegida = CircuitBreaker.decorateSupplier(circuito, protegida);
        if (conReintento) {
            protegida = Retry.decorateSupplier(reintentoCrear, protegida);
        }

        Instant inicio = reloj.instant();
        ResultadoProveedor resultado;
        Integer estadoHttp = null;
        try {
            resultado = traductor.aResultado(protegida.get());
            estadoHttp = 200;
        } catch (CallNotPermittedException | BulkheadFullException e) {
            // No se llamó al proveedor: no hubo cobro. La API responde 503 + Retry-After.
            resultado = new ResultadoProveedor.NoDisponible(e.getClass().getSimpleName());
        } catch (ErrorTransitorioProveedor e) {
            // Reintentos agotados: no se sabe si cobró -> UNKNOWN y conciliación.
            resultado = new ResultadoProveedor.Incierto(e.getMessage());
            estadoHttp = e.estadoHttp();
        } catch (HttpClientErrorException.NotFound e) {
            resultado = new ResultadoProveedor.NoEncontrado();
            estadoHttp = 404;
        } catch (HttpClientErrorException e) {
            // 4xx de validación: error definitivo, sin cobro. No se reintenta.
            resultado = new ResultadoProveedor.Fallido("HTTP " + e.getStatusCode().value());
            estadoHttp = e.getStatusCode().value();
        }
        auditar(intentoPagoId, operacion, resultado, estadoHttp, inicio, idCorrelacion);
        return resultado;
    }

    private static PagoProveedorResponse traducirErrores(Supplier<PagoProveedorResponse> llamada) {
        try {
            return llamada.get();
        } catch (ResourceAccessException e) {
            throw new ErrorTransitorioProveedor("timeout o error de conexión", null, e);
        } catch (HttpServerErrorException e) {
            throw new ErrorTransitorioProveedor("HTTP " + e.getStatusCode().value(), e.getStatusCode().value(), e);
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new ErrorTransitorioProveedor("HTTP 429", 429, e);
        }
    }

    private void auditar(UUID intentoPagoId, String operacion, ResultadoProveedor resultado, Integer estadoHttp,
                         Instant inicio, String idCorrelacion) {
        Instant fin = reloj.instant();
        auditoria.save(new LlamadaProveedorEntity(intentoPagoId, operacion, resultado.getClass().getSimpleName(),
                estadoHttp, (int) (fin.toEpochMilli() - inicio.toEpochMilli()),
                resultado instanceof ResultadoProveedor.Incierto i ? i.causa() : null, idCorrelacion, fin));
    }
}
