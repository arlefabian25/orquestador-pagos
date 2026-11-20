package com.ecomerce.pagos.aplicacion.servicio;

import com.ecomerce.pagos.aplicacion.modelo.EventoProveedor;
import com.ecomerce.pagos.aplicacion.puerto.entrada.RegistrarEventoProveedorUseCase;
import com.ecomerce.pagos.aplicacion.puerto.salida.EventoProveedorRepositorio;
import com.ecomerce.pagos.aplicacion.puerto.salida.GeneradorIdentificadores;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Solo persiste en el inbox. El 200 al proveedor se responde después del commit (sección 3.3). */
@Service
public class RegistrarEventoProveedorService implements RegistrarEventoProveedorUseCase {

    private final EventoProveedorRepositorio inbox;
    private final GeneradorIdentificadores ids;

    public RegistrarEventoProveedorService(EventoProveedorRepositorio inbox, GeneradorIdentificadores ids) {
        this.inbox = inbox;
        this.ids = ids;
    }

    @Override
    @Transactional
    public boolean registrar(Comando c) {
        EventoProveedor evento = new EventoProveedor(ids.nuevo(), c.proveedor(), c.idEventoProveedor(),
                c.intentoPagoId(), c.tipoEvento(), c.contenidoSanitizado(), 0, c.ocurridoEnProveedor());
        return inbox.registrarSiNoExiste(evento);
    }
}
