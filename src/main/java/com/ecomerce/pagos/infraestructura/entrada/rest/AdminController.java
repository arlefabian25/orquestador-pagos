package com.ecomerce.pagos.infraestructura.entrada.rest;

import com.ecomerce.pagos.aplicacion.puerto.entrada.ConciliarPagoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Operaciones (rol PAYMENTS_OPS / scope pagos.admin), con auditoría.
 * TODO reprocesar eventos FAILED del inbox y resolver MANUAL_REVIEW.
 */
@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ConciliarPagoUseCase conciliacion;

    public AdminController(ConciliarPagoUseCase conciliacion) {
        this.conciliacion = conciliacion;
    }

    @PostMapping("/payments/{id}/reconcile")
    public ResponseEntity<Void> conciliar(@PathVariable UUID id) {
        conciliacion.conciliar(id);
        return ResponseEntity.accepted().build();
    }
}
