package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.HistoryLog;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryLogDAO {
    private Connection connection;

    public HistoryLogDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener todos los registros del historial con información relacionada
     */
    public List<HistoryLog> getAllHistoryLogs() {
        List<HistoryLog> historyLogs = new ArrayList<>();
        String sql = "SELECT " +
                "hl.history_log_id, hl.proceso_id, hl.usuario_id, hl.producto_id, " +
                "hl.cantidad, hl.descripcion, hl.fecha, hl.activo, " +
                "p.nombre as proceso_nombre, " +
                "u.username as usuario_nombre, " +
                "pr.nombre as producto_nombre " +
                "FROM HistoryLogs hl " +
                "LEFT JOIN Procesos p ON hl.proceso_id = p.proceso_id " +
                "LEFT JOIN Usuarios u ON hl.usuario_id = u.usuario_id " +
                "LEFT JOIN Productos pr ON hl.producto_id = pr.producto_id " +
                "WHERE hl.activo = true " +
                "ORDER BY hl.fecha DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                HistoryLog log = mapResultSetToHistoryLog(rs);
                historyLogs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyLogs;
    }

    /**
     * Buscar registros del historial por término de búsqueda
     */
    public List<HistoryLog> searchHistoryLogs(String searchTerm) {
        List<HistoryLog> historyLogs = new ArrayList<>();
        String sql = "SELECT " +
                "hl.history_log_id, hl.proceso_id, hl.usuario_id, hl.producto_id, " +
                "hl.cantidad, hl.descripcion, hl.fecha, hl.activo, " +
                "p.nombre as proceso_nombre, " +
                "u.username as usuario_nombre, " +
                "pr.nombre as producto_nombre " +
                "FROM HistoryLogs hl " +
                "LEFT JOIN Procesos p ON hl.proceso_id = p.proceso_id " +
                "LEFT JOIN Usuarios u ON hl.usuario_id = u.usuario_id " +
                "LEFT JOIN Productos pr ON hl.producto_id = pr.producto_id " +
                "WHERE hl.activo = true AND (" +
                "hl.descripcion LIKE ? OR " +
                "p.nombre LIKE ? OR " +
                "u.username LIKE ? OR " +
                "pr.nombre LIKE ?) " +
                "ORDER BY hl.fecha DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                HistoryLog log = mapResultSetToHistoryLog(rs);
                historyLogs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyLogs;
    }

    /**
     * Filtrar registros por proceso
     */
    public List<HistoryLog> filterByProceso(int procesoId) {
        List<HistoryLog> historyLogs = new ArrayList<>();
        String sql = "SELECT " +
                "hl.history_log_id, hl.proceso_id, hl.usuario_id, hl.producto_id, " +
                "hl.cantidad, hl.descripcion, hl.fecha, hl.activo, " +
                "p.nombre as proceso_nombre, " +
                "u.username as usuario_nombre, " +
                "pr.nombre as producto_nombre " +
                "FROM HistoryLogs hl " +
                "LEFT JOIN Procesos p ON hl.proceso_id = p.proceso_id " +
                "LEFT JOIN Usuarios u ON hl.usuario_id = u.usuario_id " +
                "LEFT JOIN Productos pr ON hl.producto_id = pr.producto_id " +
                "WHERE hl.activo = true AND hl.proceso_id = ? " +
                "ORDER BY hl.fecha DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, procesoId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HistoryLog log = mapResultSetToHistoryLog(rs);
                historyLogs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyLogs;
    }

    /**
     * Filtrar registros por fecha
     */
    public List<HistoryLog> filterByDateRange(Date startDate, Date endDate) {
        List<HistoryLog> historyLogs = new ArrayList<>();
        String sql = "SELECT " +
                "hl.history_log_id, hl.proceso_id, hl.usuario_id, hl.producto_id, " +
                "hl.cantidad, hl.descripcion, hl.fecha, hl.activo, " +
                "p.nombre as proceso_nombre, " +
                "u.username as usuario_nombre, " +
                "pr.nombre as producto_nombre " +
                "FROM HistoryLogs hl " +
                "LEFT JOIN Procesos p ON hl.proceso_id = p.proceso_id " +
                "LEFT JOIN Usuarios u ON hl.usuario_id = u.usuario_id " +
                "LEFT JOIN Productos pr ON hl.producto_id = pr.producto_id " +
                "WHERE hl.activo = true AND DATE(hl.fecha) BETWEEN ? AND ? " +
                "ORDER BY hl.fecha DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDate(1, startDate);
            stmt.setDate(2, endDate);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                HistoryLog log = mapResultSetToHistoryLog(rs);
                historyLogs.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return historyLogs;
    }

    /**
     * Crear un nuevo registro en el historial
     */
    public boolean createHistoryLog(HistoryLog historyLog) {
        String sql = "INSERT INTO HistoryLogs (proceso_id, usuario_id, producto_id, cantidad, descripcion) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, historyLog.getProcesoId());
            stmt.setInt(2, historyLog.getUsuarioId());
            stmt.setInt(3, historyLog.getProductoId());
            stmt.setDouble(4, historyLog.getCantidad());
            stmt.setString(5, historyLog.getDescripcion());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener procesos disponibles
     */
    public List<String> getAvailableProcesos() {
        List<String> procesos = new ArrayList<>();
        String sql = "SELECT proceso_id, nombre FROM Procesos WHERE activo = true ORDER BY nombre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                procesos.add(rs.getInt("proceso_id") + " - " + rs.getString("nombre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return procesos;
    }

    /**
     * Mapear ResultSet a objeto HistoryLog
     */
    private HistoryLog mapResultSetToHistoryLog(ResultSet rs) throws SQLException {
        HistoryLog log = new HistoryLog();
        log.setHistoryLogId(rs.getInt("history_log_id"));
        log.setProcesoId(rs.getInt("proceso_id"));
        log.setUsuarioId(rs.getInt("usuario_id"));
        log.setProductoId(rs.getInt("producto_id"));
        log.setCantidad(rs.getDouble("cantidad"));
        log.setDescripcion(rs.getString("descripcion"));
        log.setFecha(rs.getTimestamp("fecha"));
        log.setActivo(rs.getBoolean("activo"));

        // Campos relacionados
        log.setProcesoNombre(rs.getString("proceso_nombre"));
        log.setUsuarioNombre(rs.getString("usuario_nombre"));
        log.setProductoNombre(rs.getString("producto_nombre"));

        return log;
    }
}