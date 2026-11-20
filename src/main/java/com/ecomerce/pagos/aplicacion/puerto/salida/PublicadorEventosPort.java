package com.ecomerce.pagos.aplicacion.puerto.salida;

import com.ecomerce.pagos.aplicacion.modelo.EventoOutbox;

/** Publica en el broker. Debe lanzar excepción si el broker no confirma la escritura. */
public interface PublicadorEventosPort {

    void publicar(EventoOutbox evento);
}
