package com.ecomerce.pagos.infraestructura.entrada.rest.dto;

import com.ecomerce.pagos.dominio.modelo.IntentoPago;

import java.util.UUID;

public record PagoResponse(UUID id, UUID orderId, int attemptNumber, String status, long amountMinor,
                           String currency, String redirectUrl) {

    public static PagoResponse de(IntentoPago i) {
        return new PagoResponse(i.id(), i.ordenId(), i.numeroIntento(), i.estado().name(),
                i.monto().centavos(), i.monto().moneda(), i.urlRedireccion());
    }
}
