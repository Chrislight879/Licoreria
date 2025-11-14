package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.SolicitudCompraDAO;
import com.mycompany.licoreria.models.SolicitudCompra;
import java.util.List;

public class SolicitudCompraService {
    private SolicitudCompraDAO solicitudCompraDAO;

    public SolicitudCompraService() {
        this.solicitudCompraDAO = new SolicitudCompraDAO();
    }

    /**
     * Obtener todas las solicitudes de compra
     */
    public List<SolicitudCompra> getAllSolicitudes() {
        return solicitudCompraDAO.getAllSolicitudes();
    }

    /**
     * Obtener solicitudes por estado
     */
    public List<SolicitudCompra> getSolicitudesByEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        return solicitudCompraDAO.getSolicitudesByEstado(estado.trim());
    }

    /**
     * Aprobar solicitud de compra
     */
    public boolean aprobarSolicitud(int solicitudId) {
        // Verificar que la solicitud existe y está pendiente
        List<SolicitudCompra> solicitudesPendientes = getSolicitudesByEstado("pendiente");
        boolean solicitudValida = solicitudesPendientes.stream()
                .anyMatch(s -> s.getSolicitudId() == solicitudId);

        if (!solicitudValida) {
            throw new IllegalArgumentException("La solicitud no existe o no está pendiente");
        }

        return solicitudCompraDAO.aprobarSolicitud(solicitudId);
    }

    /**
     * Rechazar solicitud de compra
     */
    public boolean rechazarSolicitud(int solicitudId) {
        // Verificar que la solicitud existe y está pendiente
        List<SolicitudCompra> solicitudesPendientes = getSolicitudesByEstado("pendiente");
        boolean solicitudValida = solicitudesPendientes.stream()
                .anyMatch(s -> s.getSolicitudId() == solicitudId);

        if (!solicitudValida) {
            throw new IllegalArgumentException("La solicitud no existe o no está pendiente");
        }

        return solicitudCompraDAO.rechazarSolicitud(solicitudId);
    }

    /**
     * Completar solicitud de compra (despachar) - SUMA stock a bodega
     */
    public boolean completarSolicitud(int solicitudId) {
        // Verificar que la solicitud existe y está aprobada
        List<SolicitudCompra> solicitudesAprobadas = getSolicitudesByEstado("aprobada");
        boolean solicitudValida = solicitudesAprobadas.stream()
                .anyMatch(s -> s.getSolicitudId() == solicitudId);

        if (!solicitudValida) {
            throw new IllegalArgumentException("La solicitud no existe o no está aprobada");
        }

        return solicitudCompraDAO.completarSolicitud(solicitudId);
    }

    /**
     * Crear nueva solicitud de compra
     */
    public boolean crearSolicitudCompra(int productoId, int usuarioSolicitanteId,
                                        double cantidadSolicitada, String observaciones) {
        if (cantidadSolicitada <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a 0");
        }

        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        return solicitudCompraDAO.crearSolicitudCompra(productoId, usuarioSolicitanteId,
                cantidadSolicitada, observaciones.trim());
    }

    /**
     * Obtener estadísticas de solicitudes
     */
    public String getEstadisticasSolicitudes() {
        List<SolicitudCompra> todas = getAllSolicitudes();
        long pendientes = todas.stream().filter(s -> "pendiente".equals(s.getEstado())).count();
        long aprobadas = todas.stream().filter(s -> "aprobada".equals(s.getEstado())).count();
        long rechazadas = todas.stream().filter(s -> "rechazada".equals(s.getEstado())).count();
        long completadas = todas.stream().filter(s -> "completada".equals(s.getEstado())).count();

        return String.format("Total: %d | Pendientes: %d | Aprobadas: %d | Rechazadas: %d | Completadas: %d",
                todas.size(), pendientes, aprobadas, rechazadas, completadas);
    }
}