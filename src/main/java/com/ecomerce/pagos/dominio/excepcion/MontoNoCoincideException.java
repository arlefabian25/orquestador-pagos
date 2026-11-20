package com.ecomerce.pagos.dominio.excepcion;

import com.ecomerce.pagos.dominio.modelo.Monto;

/** El monto o la moneda notificados no coinciden con los del intento: no se aprueba (sección 1.3). */
public class MontoNoCoincideException extends ReglaDeNegocioException {
    public MontoNoCoincideException(Monto esperado, Monto recibido) {
        super("Monto esperado " + esperado + " pero el proveedor notificó " + recibido);
    }
}
