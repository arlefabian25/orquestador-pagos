package com.ecomerce.pagos.infraestructura.salida.persistencia.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "intentos_pago")
public class IntentoPagoEntity {

    @Id
    private UUID id;

    @Column(name = "orden_id", nullable = false)
    private UUID ordenId;

    @Column(name = "numero_intento", nullable = false)
    private int numeroIntento;

    @Column(name = "proveedor", nullable = false)
    private String proveedor;

    @Column(name = "id_pago_proveedor")
    private String idPagoProveedor;

    @Column(name = "monto_centavos", nullable = false)
    private long montoCentavos;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "url_redireccion")
    private String urlRedireccion;

    @Column(name = "intentos_conciliacion", nullable = false)
    private int intentosConciliacion;

    @Column(name = "proxima_conciliacion_en")
    private Instant proximaConciliacionEn;

    @Column(name = "id_correlacion", nullable = false)
    private String idCorrelacion;

    /** Bloqueo optimista (sección 7.3). */
    @Version
    @Column(name = "version")
    private Long version;

    protected IntentoPagoEntity() { }

    public IntentoPagoEntity(UUID id) {
        this.id = id;
    }

    public UUID getId() { return id; }
    public UUID getOrdenId() { return ordenId; }
    public void setOrdenId(UUID ordenId) { this.ordenId = ordenId; }
    public int getNumeroIntento() { return numeroIntento; }
    public void setNumeroIntento(int numeroIntento) { this.numeroIntento = numeroIntento; }
    public String getProveedor() { return proveedor; }
    public void setProveedor(String proveedor) { this.proveedor = proveedor; }
    public String getIdPagoProveedor() { return idPagoProveedor; }
    public void setIdPagoProveedor(String idPagoProveedor) { this.idPagoProveedor = idPagoProveedor; }
    public long getMontoCentavos() { return montoCentavos; }
    public void setMontoCentavos(long montoCentavos) { this.montoCentavos = montoCentavos; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUrlRedireccion() { return urlRedireccion; }
    public void setUrlRedireccion(String urlRedireccion) { this.urlRedireccion = urlRedireccion; }
    public int getIntentosConciliacion() { return intentosConciliacion; }
    public void setIntentosConciliacion(int intentosConciliacion) { this.intentosConciliacion = intentosConciliacion; }
    public Instant getProximaConciliacionEn() { return proximaConciliacionEn; }
    public void setProximaConciliacionEn(Instant proximaConciliacionEn) { this.proximaConciliacionEn = proximaConciliacionEn; }
    public String getIdCorrelacion() { return idCorrelacion; }
    public void setIdCorrelacion(String idCorrelacion) { this.idCorrelacion = idCorrelacion; }
    public Long getVersion() { return version; }
}
