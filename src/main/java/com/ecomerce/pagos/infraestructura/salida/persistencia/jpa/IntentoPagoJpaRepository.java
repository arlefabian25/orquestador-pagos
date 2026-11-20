package com.ecomerce.pagos.infraestructura.salida.persistencia.jpa;

import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.IntentoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface IntentoPagoJpaRepository extends JpaRepository<IntentoPagoEntity, UUID> {

    List<IntentoPagoEntity> findByOrdenIdOrderByNumeroIntento(UUID ordenId);

    long countByOrdenId(UUID ordenId);

    /** Usa el índice parcial ix_intento_conciliacion. Varias instancias se reparten las filas sin bloquearse. */
    @Query(value = """
            SELECT * FROM intentos_pago
             WHERE estado IN ('CREATED', 'PENDING', 'UNKNOWN')
               AND proxima_conciliacion_en <= :ahora
             ORDER BY proxima_conciliacion_en
             LIMIT :limite
             FOR UPDATE SKIP LOCKED
            """, nativeQuery = true)
    List<IntentoPagoEntity> tomarParaConciliar(@Param("ahora") Instant ahora, @Param("limite") int limite);
}
