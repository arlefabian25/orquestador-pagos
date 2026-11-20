package com.ecomerce.pagos.infraestructura.entrada.rest;

import com.ecomerce.pagos.aplicacion.puerto.entrada.ConsultarPagoUseCase;
import com.ecomerce.pagos.aplicacion.puerto.entrada.IniciarPagoUseCase;
import com.ecomerce.pagos.dominio.modelo.EstadoIntento;
import com.ecomerce.pagos.dominio.modelo.IntentoPago;
import com.ecomerce.pagos.infraestructura.entrada.rest.dto.IniciarPagoRequest;
import com.ecomerce.pagos.infraestructura.entrada.rest.dto.PagoResponse;
import jakarta.validation.Valid;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class PagoController {

    private final IniciarPagoUseCase iniciarPago;
    private final ConsultarPagoUseCase consultas;

    public PagoController(IniciarPagoUseCase iniciarPago, ConsultarPagoUseCase consultas) {
        this.iniciarPago = iniciarPago;
        this.consultas = consultas;
    }

    /** 201 PENDING + redirectUrl, 202 UNKNOWN, 409 si ya está pagada o tiene un intento activo. */
    @PostMapping("/orders/{ordenId}/payments")
    public ResponseEntity<PagoResponse> iniciar(@PathVariable UUID ordenId,
                                                @RequestHeader("Idempotency-Key") String claveIdempotencia,
                                                @Valid @RequestBody IniciarPagoRequest request) {
        // TODO envolver con IdempotenciaService y validar returnUrl contra la lista permitida.
        // TODO si el resultado fue NoDisponible (circuito abierto) responder 503 + Retry-After.
        IntentoPago intento = iniciarPago.iniciar(new IniciarPagoUseCase.Comando(ordenId, request.returnUrl(),
                MDC.get(FiltroCorrelacion.CLAVE_MDC)));
        HttpStatus estado = intento.estado() == EstadoIntento.UNKNOWN ? HttpStatus.ACCEPTED : HttpStatus.CREATED;
        return ResponseEntity.status(estado).body(PagoResponse.de(intento));
    }

    /** Polling del frontend. El retorno del navegador a returnUrl NO cambia el estado. */
    @GetMapping("/payments/{id}")
    public ResponseEntity<PagoResponse> consultar(@PathVariable UUID id) {
        return consultas.consultarIntento(id)
                .map(i -> ResponseEntity.ok(PagoResponse.de(i)))
                .orElse(ResponseEntity.notFound().build());
    }
}
