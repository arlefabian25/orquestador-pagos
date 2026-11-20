-- id: UUIDv7. Es también la clave de idempotencia y la referencia ante el proveedor.
CREATE TABLE intentos_pago (
    id                       uuid         PRIMARY KEY,
    orden_id                 uuid         NOT NULL REFERENCES ordenes (id),
    numero_intento           int          NOT NULL,
    proveedor                varchar(30)  NOT NULL,
    id_pago_proveedor        varchar(100),
    monto_centavos           bigint       NOT NULL,
    moneda                   char(3)      NOT NULL,
    estado                   varchar(20)  NOT NULL,
    url_redireccion          text,
    intentos_conciliacion    int          NOT NULL DEFAULT 0,
    proxima_conciliacion_en  timestamptz,
    id_correlacion           varchar(64)  NOT NULL,
    version                  bigint       NOT NULL DEFAULT 0,
    creado_en                timestamptz  NOT NULL DEFAULT now(),
    actualizado_en           timestamptz  NOT NULL DEFAULT now(),
    CONSTRAINT uq_intento_numero UNIQUE (orden_id, numero_intento)
);
