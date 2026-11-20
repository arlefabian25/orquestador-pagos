package com.ecomerce.pagos.aplicacion.puerto.entrada;

import com.ecomerce.pagos.dominio.modelo.IntentoPago;

import java.util.UUID;

public interface IniciarPagoUseCase {

    /**
     * Crea el intento (TX1), llama al proveedor fuera de toda transacción y guarda el resultado (TX2).
     * Devuelve el intento en PENDING (con url de redirección), UNKNOWN o en un estado final.
     */
    IntentoPago iniciar(Comando comando);

    record Comando(UUID ordenId, String urlRetorno, String idCorrelacion) { }
}
