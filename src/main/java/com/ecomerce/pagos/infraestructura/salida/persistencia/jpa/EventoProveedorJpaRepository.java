package com.ecomerce.pagos.infraestructura.salida.persistencia.jpa;

import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.EventoProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventoProveedorJpaRepository extends JpaRepository<EventoProveedorEntity, UUID> {

    /**
     * Deduplicación de notificaciones (figura 5): si dos instancias reciben el mismo evento a la vez,
     * solo una inserción afecta filas. Los valores opcionales se pasan como texto ('' = sin valor) y se
     * convierten en SQL, para no enviar parámetros nulos sin tipo al driver.
     * @return 1 si se insertó, 0 si era un duplicado.
     */
    @Modifying
    @Query(value = """
            INSERT INTO eventos_proveedor
                (id, proveedor, id_evento_proveedor, intento_pago_id, tipo_evento, contenido,
                 estado_procesamiento, intentos, proximo_intento_en, ocurrido_en_proveedor)
            VALUES
                (:id, :proveedor, :idEventoProveedor, CAST(NULLIF(:intentoPagoId, '') AS uuid), :tipoEvento,
                 CAST(:contenido AS jsonb), 'RECEIVED', 0, now(), CAST(NULLIF(:ocurridoEnProveedor, '') AS timestamptz))
            ON CONFLICT (proveedor, id_evento_proveedor) DO NOTHING
            """, nativeQuery = true)
    int insertarSiNoExiste(@Param("id") UUID id,
                           @Param("proveedor") String proveedor,
                           @Param("idEventoProveedor") String idEventoProveedor,
                           @Param("intentoPagoId") String intentoPagoId,
                           @Param("tipoEvento") String tipoEvento,
                           @Param("contenido") String contenido,
                           @Param("ocurridoEnProveedor") String ocurridoEnProveedor);

    /** Usa el índice parcial ix_inbox_pendientes. */
    @Query(value = """
            SELECT * FROM eventos_proveedor
             WHERE estado_procesamiento IN ('RECEIVED', 'RETRY')
               AND proximo_intento_en <= :ahora
             ORDER BY proximo_intento_en
             LIMIT :limite
             FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<EventoProveedorEntity> tomarPendientes(@Param("ahora") Instant ahora, @Param("limite") int limite);
}
