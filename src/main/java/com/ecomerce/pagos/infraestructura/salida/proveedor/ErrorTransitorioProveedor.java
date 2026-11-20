package com.ecomerce.pagos.infraestructura.salida.proveedor;

/**
 * Error que vale la pena reintentar: timeout, error de conexión, 429, 502, 503 o 504 (sección 8.2).
 * Es la única excepción que cuenta para el circuit breaker y el reintento.
 */
public class ErrorTransitorioProveedor extends RuntimeException {

    private final Integer estadoHttp;

    public ErrorTransitorioProveedor(String mensaje, Integer estadoHttp, Throwable causa) {
        super(mensaje, causa);
        this.estadoHttp = estadoHttp;
    }

    public Integer estadoHttp() {
        return estadoHttp;
    }
}
