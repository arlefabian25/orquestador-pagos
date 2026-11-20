package com.ecomerce.pagos.infraestructura.entrada.webhook;

import com.ecomerce.pagos.aplicacion.puerto.entrada.RegistrarEventoProveedorUseCase;
import com.ecomerce.pagos.infraestructura.salida.proveedor.TraductorProveedor;
import com.ecomerce.pagos.infraestructura.salida.proveedor.dto.NotificacionProveedorDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Receptor de webhooks: verifica, guarda en el inbox y responde. Sin lógica de negocio ni llamadas
 * al proveedor, para responder en milisegundos (sección 3.3).
 *   200 -> aceptado o duplicado | 401 -> firma inválida | 400 -> esquema | 5xx -> no se pudo persistir
 */
@RestController
public class WebhookController {

    private final VerificadorFirmaHmac verificador;
    private final TraductorProveedor traductor;
    private final RegistrarEventoProveedorUseCase registrar;

    public WebhookController(VerificadorFirmaHmac verificador, TraductorProveedor traductor,
                             RegistrarEventoProveedorUseCase registrar) {
        this.verificador = verificador;
        this.traductor = traductor;
        this.registrar = registrar;
    }

    @PostMapping("/webhooks/{proveedor}")
    public ResponseEntity<Void> recibir(@PathVariable String proveedor,
                                        @RequestHeader(value = "X-Signature", required = false) String firma,
                                        @RequestHeader(value = "X-Timestamp", required = false) String timestamp,
                                        @RequestBody String cuerpo) {
        // 1. La firma se verifica sobre el cuerpo crudo, antes de deserializarlo.
        verificador.verificar(cuerpo, timestamp, firma);

        // 2. Validar el esquema. TODO responder 400 si faltan campos obligatorios.
        NotificacionProveedorDto n = traductor.leerNotificacion(cuerpo);
        UUID referencia = n.payment() == null || n.payment().reference() == null
                ? null : UUID.fromString(n.payment().reference());

        // 3. Guardar en el inbox. Si es duplicado, también se responde 200 (sin efectos).
        registrar.registrar(new RegistrarEventoProveedorUseCase.Comando(proveedor, n.eventId(), referencia,
                n.eventType(), traductor.sanitizar(cuerpo), n.occurredAt()));

        // 4. El 200 sale después del commit: si la BD falla, se responde 5xx y el proveedor reintenta.
        return ResponseEntity.ok().build();
    }
}
