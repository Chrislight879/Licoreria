package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.HistoryLogDAO;
import com.mycompany.licoreria.models.HistoryLog;
import java.sql.Date;
import java.util.List;

public class HistoryLogService {
    private HistoryLogDAO historyLogDAO;

    public HistoryLogService() {
        this.historyLogDAO = new HistoryLogDAO();
    }

    /**
     * Obtener todos los registros del historial
     */
    public List<HistoryLog> getAllHistoryLogs() {
        return historyLogDAO.getAllHistoryLogs();
    }

    /**
     * Buscar registros por término
     */
    public List<HistoryLog> searchHistoryLogs(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllHistoryLogs();
        }
        return historyLogDAO.searchHistoryLogs(searchTerm.trim());
    }

    /**
     * Filtrar por proceso
     */
    public List<HistoryLog> filterByProceso(int procesoId) {
        return historyLogDAO.filterByProceso(procesoId);
    }

    /**
     * Filtrar por rango de fechas
     */
    public List<HistoryLog> filterByDateRange(Date startDate, Date endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Las fechas no pueden ser nulas");
        }
        if (startDate.after(endDate)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return historyLogDAO.filterByDateRange(startDate, endDate);
    }

    /**
     * Crear nuevo registro de historial
     */
    public boolean createHistoryLog(int procesoId, int usuarioId, int productoId,
                                    double cantidad, String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción no puede estar vacía");
        }

        HistoryLog historyLog = new HistoryLog();
        historyLog.setProcesoId(procesoId);
        historyLog.setUsuarioId(usuarioId);
        historyLog.setProductoId(productoId);
        historyLog.setCantidad(cantidad);
        historyLog.setDescripcion(descripcion.trim());

        return historyLogDAO.createHistoryLog(historyLog);
    }

    /**
     * Obtener procesos disponibles
     */
    public List<String> getAvailableProcesos() {
        return historyLogDAO.getAvailableProcesos();
    }

    /**
     * Crear registro de historial simplificado (sin producto)
     */
    public boolean logAction(int procesoId, int usuarioId, String descripcion) {
        return createHistoryLog(procesoId, usuarioId, 0, 0.0, descripcion);
    }
}