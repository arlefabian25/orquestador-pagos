package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.puerto.entrada.ConsultarPagoUseCase;
import com.ecomerce.pagos.aplicacion.puerto.salida.IntentoPagoRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.OrdenRepositorio;
import com.ecomerce.pagos.dominio.modelo.IntentoPago;
import com.ecomerce.pagos.dominio.modelo.Orden;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ConsultarPagoService implements ConsultarPagoUseCase {

    private final OrdenRepositorio ordenes;
    private final IntentoPagoRepositorio intentos;

    public ConsultarPagoService(OrdenRepositorio ordenes, IntentoPagoRepositorio intentos) {
        this.ordenes = ordenes;
        this.intentos = intentos;
    }

    @Override
    public Optional<IntentoPago> consultarIntento(UUID intentoPagoId) {
        return intentos.buscarPorId(intentoPagoId);
    }

    @Override
    public Optional<Orden> consultarOrden(UUID ordenId) {
        return ordenes.buscarPorId(ordenId);
    }

    @Override
    public List<IntentoPago> consultarIntentosDeOrden(UUID ordenId) {
        return intentos.buscarPorOrden(ordenId);
    }
}
