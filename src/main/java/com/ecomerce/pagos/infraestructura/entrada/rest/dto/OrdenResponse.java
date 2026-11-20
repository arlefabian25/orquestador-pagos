package com.ecomerce.pagos.infraestructura.entrada.rest.dto;

import com.ecomerce.pagos.dominio.modelo.IntentoPago;
import com.ecomerce.pagos.dominio.modelo.Orden;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrdenResponse(UUID id, String status, long amountMinor, String currency, Instant expiresAt,
                            List<PagoResponse> payments) {

    public static OrdenResponse de(Orden o, List<IntentoPago> intentos) {
        return new OrdenResponse(o.id(), o.estado().name(), o.monto().centavos(), o.monto().moneda(),
                o.expiraEn(), intentos.stream().map(PagoResponse::de).toList());
    }
}
