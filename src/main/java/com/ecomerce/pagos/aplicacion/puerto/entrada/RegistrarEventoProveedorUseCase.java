package com.ecomerce.pagos.aplicacion.puerto.entrada;

import java.time.Instant;
import java.util.UUID;

/** Lo usa el receptor de webhooks: solo guarda en el inbox, sin lógica de negocio. */
public interface RegistrarEventoProveedorUseCase {

    /** @return true si se guardó; false si era un duplicado (se responde 200 igual). */
    boolean registrar(Comando comando);

    record Comando(String proveedor, String idEventoProveedor, UUID intentoPagoId, String tipoEvento,
                   String contenidoSanitizado, Instant ocurridoEnProveedor) { }
}
