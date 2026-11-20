package com.ecomerce.pagos.infraestructura.salida.persistencia.jpa;

import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.ClaveIdempotenciaEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.ClaveIdempotenciaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface ClaveIdempotenciaJpaRepository extends JpaRepository<ClaveIdempotenciaEntity, ClaveIdempotenciaId> {

    /** Solo una petición concurrente "reclama" la clave. @return 1 si la reclamó, 0 si ya existía. */
    @Modifying
    @Query(value = """
            INSERT INTO claves_idempotencia (cliente_api_id, clave, hash_solicitud, estado, creado_en, expira_en)
            VALUES (:clienteApiId, :clave, :hashSolicitud, 'IN_PROGRESS', now(), :expiraEn)
            ON CONFLICT (cliente_api_id, clave) DO NOTHING
            """, nativeQuery = true)
    int reclamar(@Param("clienteApiId") String clienteApiId,
                 @Param("clave") String clave,
                 @Param("hashSolicitud") String hashSolicitud,
                 @Param("expiraEn") Instant expiraEn);
}
