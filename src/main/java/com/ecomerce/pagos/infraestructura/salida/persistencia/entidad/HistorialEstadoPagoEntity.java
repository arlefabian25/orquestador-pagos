package com.ecomerce.pagos.infraestructura.salida.persistencia.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

/** Append-only: solo se insertan filas. */
@Entity
@Table(name = "historial_estados_pago")
public class HistorialEstadoPagoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "intento_pago_id", nullable = false)
    private UUID intentoPagoId;

    @Column(name = "estado_origen")
    private String estadoOrigen;

    @Column(name = "estado_destino", nullable = false)
    private String estadoDestino;

    @Column(name = "origen", nullable = false)
    private String origen;

    @Column(name = "motivo")
    private String motivo;

    @Column(name = "evento_proveedor_id")
    private UUID eventoProveedorId;

    @Column(name = "id_correlacion")
    private String idCorrelacion;

    @Column(name = "ocurrido_en", nullable = false)
    private Instant ocurridoEn;

    protected HistorialEstadoPagoEntity() { }

    public HistorialEstadoPagoEntity(UUID intentoPagoId, String estadoOrigen, String estadoDestino, String origen,
                                     String motivo, UUID eventoProveedorId, String idCorrelacion, Instant ocurridoEn) {
        this.intentoPagoId = intentoPagoId;
        this.estadoOrigen = estadoOrigen;
        this.estadoDestino = estadoDestino;
        this.origen = origen;
        this.motivo = motivo;
        this.eventoProveedorId = eventoProveedorId;
        this.idCorrelacion = idCorrelacion;
        this.ocurridoEn = ocurridoEn;
    }

    public Long getId() { return id; }
    public UUID getIntentoPagoId() { return intentoPagoId; }
    public String getEstadoOrigen() { return estadoOrigen; }
    public String getEstadoDestino() { return estadoDestino; }
    public String getOrigen() { return origen; }
    public String getMotivo() { return motivo; }
    public UUID getEventoProveedorId() { return eventoProveedorId; }
    public String getIdCorrelacion() { return idCorrelacion; }
    public Instant getOcurridoEn() { return ocurridoEn; }
}
