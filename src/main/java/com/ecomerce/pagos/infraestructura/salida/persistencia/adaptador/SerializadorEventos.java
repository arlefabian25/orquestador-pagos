package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import com.ecomerce.pagos.dominio.evento.EventoDominio;
import com.ecomerce.pagos.dominio.evento.PaymentApproved;
import com.ecomerce.pagos.dominio.evento.PaymentRejected;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte los eventos de dominio al JSON que se publica. Se escribe a mano para que el contrato
 * del mensaje sea explícito y no cambie si alguien renombra un campo de Java.
 */
final class SerializadorEventos {

    private SerializadorEventos() { }

    static String aJson(EventoDominio evento) {
        return switch (evento) {
            case PaymentApproved e -> """
                    {"eventType":"PaymentApproved","paymentAttemptId":"%s","orderId":"%s",\
                    "amountMinor":%d,"currency":"%s","occurredAt":"%s"}"""
                    .formatted(e.intentoPagoId(), e.ordenId(), e.monto().centavos(), e.monto().moneda(),
                            e.ocurridoEn());
            case PaymentRejected e -> """
                    {"eventType":"PaymentRejected","paymentAttemptId":"%s","orderId":"%s",\
                    "reason":"%s","occurredAt":"%s"}"""
                    .formatted(e.intentoPagoId(), e.ordenId(), escapar(e.motivo()), e.ocurridoEn());
        };
    }

    static String aJson(Map<String, String> mapa) {
        return mapa.entrySet().stream()
                .map(x -> "\"" + escapar(x.getKey()) + "\":\"" + escapar(x.getValue()) + "\"")
                .collect(Collectors.joining(",", "{", "}"));
    }

    static String escapar(String texto) {
        if (texto == null) {
            return "";
        }
        return texto.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }
}
