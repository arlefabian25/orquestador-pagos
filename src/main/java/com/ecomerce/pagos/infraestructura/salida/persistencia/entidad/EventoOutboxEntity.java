package com.ecomerce.pagos.infraestructura.salida.persistencia.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "eventos_outbox")
public class EventoOutboxEntity {

    @Id
    private UUID id;

    @Column(name = "agregado_id", nullable = false)
    private UUID agregadoId;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "contenido", nullable = false, columnDefinition = "jsonb")
    private String contenido;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "cabeceras", columnDefinition = "jsonb")
    private String cabeceras;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "proximo_intento_en", nullable = false)
    private Instant proximoIntentoEn;

    @Column(name = "creado_en", nullable = false)
    private Instant creadoEn;

    @Column(name = "publicado_en")
    private Instant publicadoEn;

    protected EventoOutboxEntity() { }

    public EventoOutboxEntity(UUID id, UUID agregadoId, String tipoEvento, String contenido, String cabeceras,
                              Instant ahora) {
        this.id = id;
        this.agregadoId = agregadoId;
        this.tipoEvento = tipoEvento;
        this.contenido = contenido;
        this.cabeceras = cabeceras;
        this.estado = "PENDING";
        this.intentos = 0;
        this.proximoIntentoEn = ahora;
        this.creadoEn = ahora;
    }

    public UUID getId() { return id; }
    public UUID getAgregadoId() { return agregadoId; }
    public String getTipoEvento() { return tipoEvento; }
    public String getContenido() { return contenido; }
    public String getCabeceras() { return cabeceras; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }
    public Instant getProximoIntentoEn() { return proximoIntentoEn; }
    public void setProximoIntentoEn(Instant proximoIntentoEn) { this.proximoIntentoEn = proximoIntentoEn; }
    public Instant getCreadoEn() { return creadoEn; }
    public Instant getPublicadoEn() { return publicadoEn; }
    public void setPublicadoEn(Instant publicadoEn) { this.publicadoEn = publicadoEn; }
}
