package com.ecomerce.pagos.infraestructura.salida.persistencia.mapeador;

import com.ecomerce.pagos.dominio.modelo.EstadoIntento;
import com.ecomerce.pagos.dominio.modelo.IntentoPago;
import com.ecomerce.pagos.dominio.modelo.Monto;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.IntentoPagoEntity;

public final class IntentoPagoMapeador {

    private IntentoPagoMapeador() { }

    public static IntentoPago aDominio(IntentoPagoEntity e) {
        return IntentoPago.reconstruir(e.getId(), e.getOrdenId(), e.getNumeroIntento(), e.getProveedor(),
                e.getIdPagoProveedor(), new Monto(e.getMontoCentavos(), e.getMoneda()),
                EstadoIntento.valueOf(e.getEstado()), e.getUrlRedireccion(), e.getIntentosConciliacion(),
                e.getProximaConciliacionEn(), e.getIdCorrelacion(), e.getVersion() == null ? 0 : e.getVersion());
    }

    public static void copiar(IntentoPago i, IntentoPagoEntity e) {
        e.setOrdenId(i.ordenId());
        e.setNumeroIntento(i.numeroIntento());
        e.setProveedor(i.proveedor());
        e.setIdPagoProveedor(i.idPagoProveedor());
        e.setMontoCentavos(i.monto().centavos());
        e.setMoneda(i.monto().moneda());
        e.setEstado(i.estado().name());
        e.setUrlRedireccion(i.urlRedireccion());
        e.setIntentosConciliacion(i.intentosConciliacion());
        e.setProximaConciliacionEn(i.proximaConciliacionEn());
        e.setIdCorrelacion(i.idCorrelacion());
    }
}
