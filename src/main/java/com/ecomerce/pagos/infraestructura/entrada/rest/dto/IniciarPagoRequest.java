package com.ecomerce.pagos.infraestructura.entrada.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** returnUrl debe pertenecer a una lista permitida (evita redirecciones abiertas, sección 9). */
public record IniciarPagoRequest(@NotBlank String returnUrl) {
}
