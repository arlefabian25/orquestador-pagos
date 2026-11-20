CREATE TABLE eventos_outbox (
    id                  uuid         PRIMARY KEY,
    agregado_id         uuid         NOT NULL,
    tipo_evento         varchar(50)  NOT NULL,
    contenido           jsonb        NOT NULL,
    cabeceras           jsonb,
    estado              varchar(20)  NOT NULL DEFAULT 'PENDING'
        CHECK (estado IN ('PENDING', 'PUBLISHED')),
    intentos            int          NOT NULL DEFAULT 0,
    proximo_intento_en  timestamptz  NOT NULL DEFAULT now(),
    creado_en           timestamptz  NOT NULL DEFAULT now(),
    publicado_en        timestamptz
);
