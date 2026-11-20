package com.ecomerce.pagos.infraestructura.salida.persistencia.jpa;

import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.EventoOutboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface EventoOutboxJpaRepository extends JpaRepository<EventoOutboxEntity, UUID> {

    /** Usa el índice parcial ix_outbox_pendientes; respeta el orden de creación. */
    @Query(value = """
            SELECT * FROM eventos_outbox
             WHERE estado = 'PENDING'
               AND proximo_intento_en <= :ahora
             ORDER BY creado_en
             LIMIT :limite
             FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<EventoOutboxEntity> tomarPendientes(@Param("ahora") Instant ahora, @Param("limite") int limite);
}
