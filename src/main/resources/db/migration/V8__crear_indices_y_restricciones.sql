-- Sección 6.3 del documento de diseño.

-- Un solo intento activo por orden (evita intentos simultáneos)
CREATE UNIQUE INDEX ux_intento_unico_activo ON intentos_pago(orden_id)
  WHERE estado IN ('CREATED','PENDING','UNKNOWN');

-- Como máximo un intento aprobado por orden (última barrera contra doble cobro)
CREATE UNIQUE INDEX ux_intento_unico_aprobado ON intentos_pago(orden_id)
  WHERE estado = 'APPROVED';

-- El ID del proveedor es único cuando existe
CREATE UNIQUE INDEX ux_intento_pago_proveedor ON intentos_pago(proveedor, id_pago_proveedor)
  WHERE id_pago_proveedor IS NOT NULL;

-- Deduplicación de notificaciones
ALTER TABLE eventos_proveedor ADD CONSTRAINT ux_evento_proveedor UNIQUE (proveedor, id_evento_proveedor);

-- Colas de trabajo (índices parciales pequeños y rápidos)
CREATE INDEX ix_intento_conciliacion ON intentos_pago(proxima_conciliacion_en)
  WHERE estado IN ('CREATED','PENDING','UNKNOWN');
CREATE INDEX ix_inbox_pendientes ON eventos_proveedor(proximo_intento_en)
  WHERE estado_procesamiento IN ('RECEIVED','RETRY');
CREATE INDEX ix_outbox_pendientes ON eventos_outbox(creado_en)
  WHERE estado = 'PENDING';

-- Consultas de trazabilidad
CREATE INDEX ix_historial_intento ON historial_estados_pago(intento_pago_id, ocurrido_en);
CREATE INDEX ix_ordenes_comprador ON ordenes(comprador_id, creado_en DESC);
CREATE INDEX ix_llamadas_intento ON llamadas_proveedor(intento_pago_id, creado_en);

-- Integridad de valores
ALTER TABLE intentos_pago ADD CONSTRAINT ck_monto_positivo CHECK (monto_centavos > 0);
ALTER TABLE intentos_pago ADD CONSTRAINT ck_estado CHECK (estado IN
  ('CREATED','PENDING','UNKNOWN','APPROVED','REJECTED','FAILED','EXPIRED','MANUAL_REVIEW'));
