package com.ecomerce.pagos.infraestructura.salida.persistencia.jpa;

import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.LlamadaProveedorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LlamadaProveedorJpaRepository extends JpaRepository<LlamadaProveedorEntity, Long> {
}
