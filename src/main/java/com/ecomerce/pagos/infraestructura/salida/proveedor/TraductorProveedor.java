package com.ecomerce.pagos.infraestructura.salida.proveedor;

import com.ecomerce.pagos.aplicacion.puerto.salida.ResultadoProveedor;
import com.ecomerce.pagos.dominio.modelo.Monto;
import com.ecomerce.pagos.infraestructura.salida.proveedor.dto.NotificacionProveedorDto;
import com.ecomerce.pagos.infraestructura.salida.proveedor.dto.PagoProveedorResponse;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

/**
 * Capa anticorrupción: traduce el formato del proveedor a tipos propios.
 * Es el único lugar que conoce los estados y campos de la pasarela.
 */
@Component
public class TraductorProveedor {

    private final JsonMapper json = JsonMapper.builder().build();

    public NotificacionProveedorDto leerNotificacion(String contenido) {
        return json.readValue(contenido, NotificacionProveedorDto.class);
    }

    public ResultadoProveedor aResultado(PagoProveedorResponse pago) {
        // TODO ajustar a los estados reales de la pasarela.
        return switch (pago.status() == null ? "" : pago.status().toUpperCase()) {
            case "APPROVED", "CAPTURED" ->
                    new ResultadoProveedor.Aprobado(pago.id(), new Monto(pago.amountMinor(), pago.currency()));
            case "DECLINED", "REJECTED" -> new ResultadoProveedor.Rechazado(pago.id(), pago.declineReason());
            case "PENDING", "REQUIRES_ACTION" -> new ResultadoProveedor.Aceptado(pago.id(), pago.redirectUrl());
            default -> new ResultadoProveedor.Incierto("estado desconocido del proveedor: " + pago.status());
        };
    }

    /**
     * Quita datos sensibles antes de guardar la notificación (sección 9.3).
     * TODO eliminar o enmascarar PAN, nombre del titular, email, documento, etc. según el contrato real.
     */
    public String sanitizar(String contenido) {
        return contenido;
    }
}
