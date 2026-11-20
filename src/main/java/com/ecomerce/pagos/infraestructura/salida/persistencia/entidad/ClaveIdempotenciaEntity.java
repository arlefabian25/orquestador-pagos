package com.ecomerce.pagos.infraestructura.salida.persistencia.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

/** La inserción (reclamar la clave) se hace con SQL nativo (ON CONFLICT DO NOTHING). */
@Entity
@Table(name = "claves_idempotencia")
public class ClaveIdempotenciaEntity {

    @EmbeddedId
    private ClaveIdempotenciaId id;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "hash_solicitud", nullable = false, length = 64)
    private String hashSolicitud;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "estado_respuesta")
    private Integer estadoRespuesta;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "cuerpo_respuesta", columnDefinition = "jsonb")
    private String cuerpoRespuesta;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "expira_en", nullable = false)
    private Instant expiraEn;

    protected ClaveIdempotenciaEntity() { }

    public ClaveIdempotenciaId getId() { return id; }
    public String getHashSolicitud() { return hashSolicitud; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Integer getEstadoRespuesta() { return estadoRespuesta; }
    public void setEstadoRespuesta(Integer estadoRespuesta) { this.estadoRespuesta = estadoRespuesta; }
    public String getCuerpoRespuesta() { return cuerpoRespuesta; }
    public void setCuerpoRespuesta(String cuerpoRespuesta) { this.cuerpoRespuesta = cuerpoRespuesta; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getExpiraEn() { return expiraEn; }
}
