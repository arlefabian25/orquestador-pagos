package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.modelo.ParametrosPagos;
import com.ecomerce.pagos.aplicacion.puerto.entrada.CrearOrdenUseCase;
import com.ecomerce.pagos.aplicacion.puerto.salida.GeneradorIdentificadores;
import com.ecomerce.pagos.aplicacion.puerto.salida.OrdenRepositorio;
import com.ecomerce.pagos.dominio.modelo.Orden;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;

@Service
public class CrearOrdenService implements CrearOrdenUseCase {

    private final OrdenRepositorio ordenes;
    private final GeneradorIdentificadores ids;
    private final ParametrosPagos parametros;
    private final Clock reloj;

    public CrearOrdenService(OrdenRepositorio ordenes, GeneradorIdentificadores ids,
                             ParametrosPagos parametros, Clock reloj) {
        this.ordenes = ordenes;
        this.ids = ids;
        this.parametros = parametros;
        this.reloj = reloj;
    }

    @Override
    @Transactional
    public Orden crear(Comando comando) {
        Orden orden = Orden.crear(ids.nuevo(), comando.compradorId(), comando.monto(),
                reloj.instant().plus(parametros.vigenciaOrden()));
        return ordenes.guardar(orden);
    }
}
