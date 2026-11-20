package com.ecomerce.pagos.aplicacion.puerto.salida;

import java.util.UUID;

/** UUIDv7: no adivinables y ordenables en el tiempo (sección 6.2). */
public interface GeneradorIdentificadores {

    UUID nuevo();
}
