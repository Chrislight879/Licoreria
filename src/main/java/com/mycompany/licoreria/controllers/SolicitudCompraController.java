package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.SolicitudCompra;
import com.mycompany.licoreria.services.SolicitudCompraService;
import java.util.List;

public class SolicitudCompraController {
    private SolicitudCompraService solicitudCompraService;

    public SolicitudCompraController() {
        this.solicitudCompraService = new SolicitudCompraService();
    }

    /**
     * Obtener todas las solicitudes de compra
     */
    public List<SolicitudCompra> getAllSolicitudes() {
        try {
            return solicitudCompraService.getAllSolicitudes();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener solicitudes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener solicitudes por estado
     */
    public List<SolicitudCompra> getSolicitudesByEstado(String estado) {
        try {
            return solicitudCompraService.getSolicitudesByEstado(estado);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener solicitudes por estado: " + e.getMessage(), e);
        }
    }

    /**
     * Aprobar solicitud de compra
     */
    public boolean aprobarSolicitud(int solicitudId) {
        try {
            return solicitudCompraService.aprobarSolicitud(solicitudId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al aprobar solicitud: " + e.getMessage(), e);
        }
    }

    /**
     * Rechazar solicitud de compra
     */
    public boolean rechazarSolicitud(int solicitudId) {
        try {
            return solicitudCompraService.rechazarSolicitud(solicitudId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al rechazar solicitud: " + e.getMessage(), e);
        }
    }

    /**
     * Completar solicitud de compra (despachar) - SUMA stock a bodega
     */
    public boolean completarSolicitud(int solicitudId) {
        try {
            return solicitudCompraService.completarSolicitud(solicitudId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al completar solicitud: " + e.getMessage(), e);
        }
    }

    /**
     * Crear nueva solicitud de compra
     */
    public boolean crearSolicitudCompra(int productoId, int usuarioSolicitanteId,
                                        double cantidadSolicitada, String observaciones) {
        try {
            return solicitudCompraService.crearSolicitudCompra(productoId, usuarioSolicitanteId,
                    cantidadSolicitada, observaciones);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al crear solicitud: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas de solicitudes
     */
    public String getEstadisticasSolicitudes() {
        try {
            return solicitudCompraService.getEstadisticasSolicitudes();
        } catch (Exception e) {
            return "Error al calcular estadísticas: " + e.getMessage();
        }
    }

    /**
     * Obtener solicitudes pendientes
     */
    public List<SolicitudCompra> getSolicitudesPendientes() {
        return getSolicitudesByEstado("pendiente");
    }

    /**
     * Obtener solicitudes aprobadas
     */
    public List<SolicitudCompra> getSolicitudesAprobadas() {
        return getSolicitudesByEstado("aprobada");
    }

    /**
     * Obtener solicitudes completadas
     */
    public List<SolicitudCompra> getSolicitudesCompletadas() {
        return getSolicitudesByEstado("completada");
    }
}