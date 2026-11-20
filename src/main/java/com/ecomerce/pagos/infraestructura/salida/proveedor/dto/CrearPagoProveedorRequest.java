package com.ecomerce.pagos.infraestructura.salida.proveedor.dto;

/**
 * Cuerpo para crear un pago en la pasarela. Los nombres siguen el contrato del proveedor (en inglés)
 * y deben ajustarse a su documentación real.
 */
public record CrearPagoProveedorRequest(String reference, long amountMinor, String currency, String returnUrl) {
}
