-- Inbox de notificaciones del proveedor. La restricción de deduplicación está en V8.
CREATE TABLE eventos_proveedor (
    id                     uuid          PRIMARY KEY,
    proveedor              varchar(30)   NOT NULL,
    id_evento_proveedor    varchar(100)  NOT NULL,
    intento_pago_id        uuid          REFERENCES intentos_pago (id),
    tipo_evento            varchar(50)   NOT NULL,
    contenido              jsonb         NOT NULL,
    estado_procesamiento   varchar(20)   NOT NULL DEFAULT 'RECEIVED'
        CHECK (estado_procesamiento IN ('RECEIVED', 'RETRY', 'PROCESSED', 'IGNORED', 'FAILED')),
    intentos               int           NOT NULL DEFAULT 0,
    proximo_intento_en     timestamptz   NOT NULL DEFAULT now(),
    ocurrido_en_proveedor  timestamptz,
    ultimo_error           varchar(500),
    creado_en              timestamptz   NOT NULL DEFAULT now()
);
