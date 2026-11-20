package com.ecomerce.pagos.infraestructura.salida.persistencia.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** Auditoría de cada llamada saliente al proveedor, sin datos sensibles. */
@Entity
@Table(name = "llamadas_proveedor")
public class LlamadaProveedorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intento_pago_id")
    private UUID intentoPagoId;

    @Column(name = "operacion", nullable = false)
    private String operacion;

    @Column(name = "resultado", nullable = false)
    private String resultado;

    @Column(name = "estado_http")
    private Integer estadoHttp;

    @Column(name = "duracion_ms", nullable = false)
    private int duracionMs;

    @Column(name = "codigo_error")
    private String codigoError;

    @Column(name = "id_correlacion")
    private String idCorrelacion;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    protected LlamadaProveedorEntity() { }

    public LlamadaProveedorEntity(UUID intentoPagoId, String operacion, String resultado, Integer estadoHttp,
                                  int duracionMs, String codigoError, String idCorrelacion, Instant creadoEn) {
        this.intentoPagoId = intentoPagoId;
        this.operacion = operacion;
        this.resultado = resultado;
        this.estadoHttp = estadoHttp;
        this.duracionMs = duracionMs;
        this.codigoError = codigoError;
        this.idCorrelacion = idCorrelacion;
        this.creadoEn = creadoEn;
    }

    public Long getId() { return id; }
}
