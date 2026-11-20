package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.dominio.modelo.Transicion;

import java.util.UUID;

/** Bitácora append-only de transiciones (historial_estados_pago). */
public interface HistorialEstadosRepositorio {

    void registrar(Transicion transicion, UUID eventoProveedorId, String idCorrelacion);
}
