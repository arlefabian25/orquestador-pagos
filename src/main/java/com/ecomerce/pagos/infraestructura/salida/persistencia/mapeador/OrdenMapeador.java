package com.ecomerce.pagos.infraestructura.salida.persistencia.mapeador;

import com.ecomerce.pagos.dominio.modelo.EstadoOrden;
import com.ecomerce.pagos.dominio.modelo.Monto;
import com.ecomerce.pagos.dominio.modelo.Orden;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.OrdenEntity;

public final class OrdenMapeador {

    private OrdenMapeador() { }

    public static Orden aDominio(OrdenEntity e) {
        return Orden.reconstruir(e.getId(), e.getCompradorId(), new Monto(e.getMontoCentavos(), e.getMoneda()),
                EstadoOrden.valueOf(e.getEstado()), e.getExpiraEn(), e.getVersion() == null ? 0 : e.getVersion());
    }

    /** Copia el estado del dominio sobre la entidad (nueva o gestionada por JPA). */
    public static void copiar(Orden o, OrdenEntity e) {
        e.setCompradorId(o.compradorId());
        e.setMontoCentavos(o.monto().centavos());
        e.setMoneda(o.monto().moneda());
        e.setEstado(o.estado().name());
        e.setExpiraEn(o.expiraEn());
    }
}
