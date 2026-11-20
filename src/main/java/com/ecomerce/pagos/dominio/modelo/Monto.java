package com.ecomerce.pagos.dominio.modelo;

import java.util.Objects;

/**
 * Monto en unidades menores (centavos) y moneda ISO 4217 (sección 6.2).
 * Evita errores de redondeo y permite comparar exactamente con lo que notifica el proveedor.
 */
public record Monto(long centavos, String moneda) {
    public Monto {
        if (centavos <= 0) {
            throw new IllegalArgumentException("El monto debe ser positivo");
        }
        Objects.requireNonNull(moneda, "moneda");
        if (!moneda.matches("[A-Z]{3}")) {
            throw new IllegalArgumentException("Moneda ISO 4217 inválida: " + moneda);
        }
    }
}
