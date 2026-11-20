package com.ecomerce.pagos.infraestructura.salida.proveedor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/** Respuesta de la pasarela al crear o consultar un pago (ajustar al contrato real). */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PagoProveedorResponse(String id, String reference, String status, Long amountMinor,
                                    String currency, String redirectUrl, String declineReason) {
}
