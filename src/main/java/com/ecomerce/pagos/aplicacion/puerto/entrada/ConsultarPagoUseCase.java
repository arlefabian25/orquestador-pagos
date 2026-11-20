package com.ecomerce.pagos.aplicacion.puerto.entrada;

import com.ecomerce.pagos.dominio.modelo.IntentoPago;
import com.ecomerce.pagos.dominio.modelo.Orden;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConsultarPagoUseCase {

    Optional<IntentoPago> consultarIntento(UUID intentoPagoId);

    Optional<Orden> consultarOrden(UUID ordenId);

    List<IntentoPago> consultarIntentosDeOrden(UUID ordenId);
}
