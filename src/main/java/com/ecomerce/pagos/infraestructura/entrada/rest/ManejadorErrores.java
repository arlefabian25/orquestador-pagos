package com.ecomerce.pagos.infraestructura.entrada.rest;

import com.ecomerce.pagos.dominio.excepcion.OrdenNoPagableException;
import com.ecomerce.pagos.dominio.excepcion.ReglaDeNegocioException;
import com.ecomerce.pagos.infraestructura.entrada.webhook.FirmaInvalidaException;
import jakarta.persistence.OptimisticLockException;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.core.JacksonException;

/** Errores en formato RFC 9457 (Problem Details) con el correlationId (sección 3.4). */
@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(OrdenNoPagableException.class)
    public ProblemDetail ordenNoPagable(OrdenNoPagableException e) {
        return problema(HttpStatus.CONFLICT, e.getMessage());
    }

    @ExceptionHandler(ReglaDeNegocioException.class)
    public ProblemDetail reglaDeNegocio(ReglaDeNegocioException e) {
        return problema(HttpStatusCode.valueOf(422), e.getMessage());
    }

    /** Lo lanza, por ejemplo, el índice ux_intento_unico_activo ante dos intentos simultáneos. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ProblemDetail conflictoDeDatos(DataIntegrityViolationException e) {
        return problema(HttpStatus.CONFLICT, "La operación entra en conflicto con el estado actual");
    }

    @ExceptionHandler(OptimisticLockException.class)
    public ProblemDetail conflictoConcurrente(OptimisticLockException e) {
        return problema(HttpStatus.CONFLICT, "El recurso cambió; vuelva a consultarlo");
    }

    @ExceptionHandler(FirmaInvalidaException.class)
    public ProblemDetail firmaInvalida(FirmaInvalidaException e) {
        return problema(HttpStatus.UNAUTHORIZED, "Firma inválida");
    }

    /** Cuerpo o notificación mal formados (p. ej. webhook con un esquema inválido). */
    @ExceptionHandler({JacksonException.class, IllegalArgumentException.class})
    public ProblemDetail entradaInvalida(RuntimeException e) {
        return problema(HttpStatus.BAD_REQUEST, "Solicitud mal formada");
    }

    /** Mientras el esqueleto tenga casos de uso sin implementar. */
    @ExceptionHandler(UnsupportedOperationException.class)
    public ProblemDetail noImplementado(UnsupportedOperationException e) {
        return problema(HttpStatus.NOT_IMPLEMENTED, e.getMessage());
    }

    private static ProblemDetail problema(HttpStatusCode estado, String detalle) {
        ProblemDetail p = ProblemDetail.forStatusAndDetail(estado, detalle);
        p.setProperty("correlationId", MDC.get(FiltroCorrelacion.CLAVE_MDC));
        return p;
    }
}
