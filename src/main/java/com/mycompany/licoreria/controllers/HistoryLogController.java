package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.HistoryLog;
import com.mycompany.licoreria.services.HistoryLogService;
import java.sql.Date;
import java.util.List;

public class HistoryLogController {
    private HistoryLogService historyLogService;

    public HistoryLogController() {
        this.historyLogService = new HistoryLogService();
    }

    /**
     * Obtener todos los registros del historial
     */
    public List<HistoryLog> getAllHistoryLogs() {
        try {
            return historyLogService.getAllHistoryLogs();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener el historial: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar registros del historial
     */
    public List<HistoryLog> searchHistoryLogs(String searchTerm) {
        try {
            return historyLogService.searchHistoryLogs(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar en el historial: " + e.getMessage(), e);
        }
    }

    /**
     * Filtrar por proceso
     */
    public List<HistoryLog> filterByProceso(int procesoId) {
        try {
            return historyLogService.filterByProceso(procesoId);
        } catch (Exception e) {
            throw new RuntimeException("Error al filtrar por proceso: " + e.getMessage(), e);
        }
    }

    /**
     * Filtrar por rango de fechas
     */
    public List<HistoryLog> filterByDateRange(Date startDate, Date endDate) {
        try {
            return historyLogService.filterByDateRange(startDate, endDate);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al filtrar por fecha: " + e.getMessage(), e);
        }
    }

    /**
     * Crear nuevo registro de historial
     */
    public boolean createHistoryLog(int procesoId, int usuarioId, int productoId,
                                    double cantidad, String descripcion) {
        try {
            return historyLogService.createHistoryLog(procesoId, usuarioId, productoId, cantidad, descripcion);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al crear registro de historial: " + e.getMessage(), e);
        }
    }

    /**
     * Crear registro simplificado
     */
    public boolean logAction(int procesoId, int usuarioId, String descripcion) {
        try {
            return historyLogService.logAction(procesoId, usuarioId, descripcion);
        } catch (Exception e) {
            throw new RuntimeException("Error al registrar acción: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener procesos disponibles
     */
    public List<String> getAvailableProcesos() {
        try {
            return historyLogService.getAvailableProcesos();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener procesos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas básicas del historial
     */
    public String getEstadisticas() {
        try {
            List<HistoryLog> logs = historyLogService.getAllHistoryLogs();
            int totalRegistros = logs.size();

            if (totalRegistros == 0) {
                return "No hay registros en el historial";
            }

            // Contar registros por tipo de proceso
            long loginCount = logs.stream().filter(log ->
                    log.getProcesoNombre() != null && log.getProcesoNombre().toLowerCase().contains("login")).count();
            long ventaCount = logs.stream().filter(log ->
                    log.getProcesoNombre() != null && log.getProcesoNombre().toLowerCase().contains("venta")).count();

            return String.format("Total registros: %d | Logins: %d | Ventas: %d",
                    totalRegistros, loginCount, ventaCount);
        } catch (Exception e) {
            return "Error al calcular estadísticas: " + e.getMessage();
        }
    }
}