package com.ecomerce.pagos.infraestructura.entrada.rest;

import com.ecomerce.pagos.aplicacion.puerto.entrada.ConsultarPagoUseCase;
import com.ecomerce.pagos.aplicacion.puerto.entrada.CrearOrdenUseCase;
import com.ecomerce.pagos.dominio.modelo.Monto;
import com.ecomerce.pagos.dominio.modelo.Orden;
import com.ecomerce.pagos.infraestructura.entrada.rest.dto.CrearOrdenRequest;
import com.ecomerce.pagos.infraestructura.entrada.rest.dto.OrdenResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrdenController {

    private final CrearOrdenUseCase crearOrden;
    private final ConsultarPagoUseCase consultas;

    public OrdenController(CrearOrdenUseCase crearOrden, ConsultarPagoUseCase consultas) {
        this.crearOrden = crearOrden;
        this.consultas = consultas;
    }

    @PostMapping
    public ResponseEntity<OrdenResponse> crear(@RequestHeader("Idempotency-Key") String claveIdempotencia,
                                               @Valid @RequestBody CrearOrdenRequest request) {
        // TODO envolver con IdempotenciaService (sección 7.2) usando el cliente del JWT y claveIdempotencia.
        Orden orden = crearOrden.crear(new CrearOrdenUseCase.Comando(request.customerId(),
                new Monto(request.amountMinor(), request.currency())));
        return ResponseEntity.status(HttpStatus.CREATED).body(OrdenResponse.de(orden, List.of()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrdenResponse> consultar(@PathVariable UUID id) {
        return consultas.consultarOrden(id)
                .map(o -> ResponseEntity.ok(OrdenResponse.de(o, consultas.consultarIntentosDeOrden(id))))
                .orElse(ResponseEntity.notFound().build());
    }
}
