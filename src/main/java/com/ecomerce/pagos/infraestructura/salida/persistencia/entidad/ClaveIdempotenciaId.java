package com.ecomerce.pagos.infraestructura.salida.persistencia.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;
import java.util.Objects;

/** PK compuesta (cliente_api_id, clave). */
@Embeddable
public class ClaveIdempotenciaId implements Serializable {

    @Column(name = "cliente_api_id")
    private String clienteApiId;

    @Column(name = "clave")
    private String clave;

    protected ClaveIdempotenciaId() { }

    public ClaveIdempotenciaId(String clienteApiId, String clave) {
        this.clienteApiId = clienteApiId;
        this.clave = clave;
    }

    public String getClienteApiId() { return clienteApiId; }
    public String getClave() { return clave; }

    @Override
    public boolean equals(Object o) {
        return o instanceof ClaveIdempotenciaId otro
                && Objects.equals(clienteApiId, otro.clienteApiId)
                && Objects.equals(clave, otro.clave);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clienteApiId, clave);
    }
}
