package com.ecomerce.pagos.aplicacion.puerto.entrada;

import com.ecomerce.pagos.dominio.modelo.Monto;
import com.ecomerce.pagos.dominio.modelo.Orden;

public interface CrearOrdenUseCase {

    Orden crear(Comando comando);

    record Comando(String compradorId, Monto monto) { }
}
