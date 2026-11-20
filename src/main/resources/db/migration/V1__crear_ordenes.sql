CREATE TABLE ordenes (
    id              uuid         PRIMARY KEY,
    comprador_id    varchar(64)  NOT NULL,
    monto_centavos  bigint       NOT NULL CHECK (monto_centavos > 0),
    moneda          char(3)      NOT NULL,
    estado          varchar(20)  NOT NULL
        CHECK (estado IN ('PENDING_PAYMENT', 'PAID', 'EXPIRED', 'CANCELLED')),
    expira_en       timestamptz  NOT NULL,
    version         bigint       NOT NULL DEFAULT 0,
    creado_en       timestamptz  NOT NULL DEFAULT now(),
    actualizado_en  timestamptz  NOT NULL DEFAULT now()
);
