package com.ecomerce.pagos.infraestructura.salida.persistencia.adaptador;

import com.ecomerce.pagos.aplicacion.modelo.EventoProveedor;
import com.ecomerce.pagos.aplicacion.puerto.salida.EventoProveedorRepositorio;
import com.ecomerce.pagos.infraestructura.salida.persistencia.entidad.EventoProveedorEntity;
import com.ecomerce.pagos.infraestructura.salida.persistencia.jpa.EventoProveedorJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public class EventoProveedorRepositorioJpa implements EventoProveedorRepositorio {

    private static final int MAX_LARGO_ERROR = 500;

    private final EventoProveedorJpaRepository jpa;

    public EventoProveedorRepositorioJpa(EventoProveedorJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public boolean registrarSiNoExiste(EventoProveedor e) {
        int filas = jpa.insertarSiNoExiste(e.id(), e.proveedor(), e.idEventoProveedor(),
                e.intentoPagoId() == null ? "" : e.intentoPagoId().toString(),
                e.tipoEvento(), e.contenido(),
                e.ocurridoEnProveedor() == null ? "" : e.ocurridoEnProveedor().toString());
        return filas == 1;
    }

    @Override
    public List<EventoProveedor> tomarPendientes(Instant ahora, int limite) {
        return jpa.tomarPendientes(ahora, limite).stream()
                .map(e -> new EventoProveedor(e.getId(), e.getProveedor(), e.getIdEventoProveedor(),
                        e.getIntentoPagoId(), e.getTipoEvento(), e.getContenido(), e.getIntentos(),
                        e.getOcurridoEnProveedor()))
                .toList();
    }

    @Override
    public void marcarProcesado(UUID id) {
        cambiarEstado(id, "PROCESSED", null);
    }

    @Override
    public void marcarIgnorado(UUID id, String motivo) {
        cambiarEstado(id, "IGNORED", motivo);
    }

    @Override
    public void programarReintento(UUID id, Instant proximoIntento, String error) {
        EventoProveedorEntity e = buscar(id);
        e.setEstadoProcesamiento("RETRY");
        e.setIntentos(e.getIntentos() + 1);
        e.setProximoIntentoEn(proximoIntento);
        e.setUltimoError(recortar(error));
    }

    @Override
    public void marcarFallido(UUID id, String error) {
        EventoProveedorEntity e = buscar(id);
        e.setEstadoProcesamiento("FAILED");
        e.setIntentos(e.getIntentos() + 1);
        e.setUltimoError(recortar(error));
    }

    private void cambiarEstado(UUID id, String estado, String error) {
        EventoProveedorEntity e = buscar(id);
        e.setEstadoProcesamiento(estado);
        e.setUltimoError(recortar(error));
    }

    private EventoProveedorEntity buscar(UUID id) {
        return jpa.findById(id).orElseThrow(() -> new IllegalStateException("Evento no encontrado: " + id));
    }

    private static String recortar(String texto) {
        return texto == null || texto.length() <= MAX_LARGO_ERROR ? texto : texto.substring(0, MAX_LARGO_ERROR);
    }
}
