CREATE TABLE claves_idempotencia (
    cliente_api_id    varchar(100)  NOT NULL,
    clave             varchar(100)  NOT NULL,
    hash_solicitud    char(64)      NOT NULL,
    estado            varchar(20)   NOT NULL CHECK (estado IN ('IN_PROGRESS', 'COMPLETED')),
    estado_respuesta  int,
    cuerpo_respuesta  jsonb,
    creado_en         timestamptz   NOT NULL DEFAULT now(),
    expira_en         timestamptz   NOT NULL,
    PRIMARY KEY (cliente_api_id, clave)
);
