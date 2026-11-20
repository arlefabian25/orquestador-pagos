package com.ecomerce.pagos.infraestructura.salida.persistencia.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;

import java.time.Instant;
import java.util.UUID;

/** Inbox de notificaciones. La inserción se hace con SQL nativo (ON CONFLICT DO NOTHING). */
@Entity
@Table(name = "eventos_proveedor")
public class EventoProveedorEntity {

    @Id
    private UUID id;

    @Column(name = "proveedor", nullable = false)
    private String proveedor;

    @Column(name = "id_evento_proveedor", nullable = false)
    private String idEventoProveedor;

    @Column(name = "intento_pago_id")
    private UUID intentoPagoId;

    @Column(name = "tipo_evento", nullable = false)
    private String tipoEvento;

    @ColumnTransformer(write = "?::jsonb")
    @Column(name = "contenido", nullable = false, columnDefinition = "jsonb")
    private String contenido;

    @Column(name = "estado_procesamiento", nullable = false)
    private String estadoProcesamiento;

    @Column(name = "intentos", nullable = false)
    private int intentos;

    @Column(name = "proximo_intento_en", nullable = false)
    private Instant proximoIntentoEn;

    @Column(name = "ocurrido_en_proveedor")
    private Instant ocurridoEnProveedor;

    @Column(name = "ultimo_error")
    private String ultimoError;

    protected EventoProveedorEntity() { }

    public UUID getId() { return id; }
    public String getProveedor() { return proveedor; }
    public String getIdEventoProveedor() { return idEventoProveedor; }
    public UUID getIntentoPagoId() { return intentoPagoId; }
    public String getTipoEvento() { return tipoEvento; }
    public String getContenido() { return contenido; }
    public String getEstadoProcesamiento() { return estadoProcesamiento; }
    public void setEstadoProcesamiento(String estadoProcesamiento) { this.estadoProcesamiento = estadoProcesamiento; }
    public int getIntentos() { return intentos; }
    public void setIntentos(int intentos) { this.intentos = intentos; }
    public Instant getProximoIntentoEn() { return proximoIntentoEn; }
    public void setProximoIntentoEn(Instant proximoIntentoEn) { this.proximoIntentoEn = proximoIntentoEn; }
    public Instant getOcurridoEnProveedor() { return ocurridoEnProveedor; }
    public String getUltimoError() { return ultimoError; }
    public void setUltimoError(String ultimoError) { this.ultimoError = ultimoError; }
}
