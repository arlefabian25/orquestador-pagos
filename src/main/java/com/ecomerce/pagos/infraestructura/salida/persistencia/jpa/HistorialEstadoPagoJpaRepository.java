package com.ecomerce.pagos.infraestructura.salida.persistencia.jpa;

import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.HistorialEstadoPagoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialEstadoPagoJpaRepository extends JpaRepository<HistorialEstadoPagoEntity, Long> {

    List<HistorialEstadoPagoEntity> findByIntentoPagoIdOrderByOcurridoEn(UUID intentoPagoId);
}
