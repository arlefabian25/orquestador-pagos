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
@Table(name = "ordenes")
public class OrdenEntity {

    @Id
    private UUID id;

    @Column(name = "comprador_id", nullable = false)
    private String compradorId;

    @Column(name = "monto_centavos", nullable = false)
    private long montoCentavos;

    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "moneda", nullable = false, length = 3)
    private String moneda;

    @Column(name = "estado", nullable = false)
    private String estado;

    @Column(name = "expira_en", nullable = false)
    private Instant expiraEn;

    @Version
    @Column(name = "version")
    private Long version;

    protected OrdenEntity() { }

    public OrdenEntity(UUID id) {
        this.id = id;
    }

    public UUID getId() { return id; }
    public String getCompradorId() { return compradorId; }
    public void setCompradorId(String compradorId) { this.compradorId = compradorId; }
    public long getMontoCentavos() { return montoCentavos; }
    public void setMontoCentavos(long montoCentavos) { this.montoCentavos = montoCentavos; }
    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Instant getExpiraEn() { return expiraEn; }
    public void setExpiraEn(Instant expiraEn) { this.expiraEn = expiraEn; }
    public Long getVersion() { return version; }
}
