package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.SolicitudCompra;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SolicitudCompraDAO {
    private Connection connection;

    public SolicitudCompraDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener todas las solicitudes de compra
     */
    public List<SolicitudCompra> getAllSolicitudes() {
        List<SolicitudCompra> solicitudes = new ArrayList<>();
        String sql = "SELECT " +
                "sc.solicitud_id, sc.producto_id, sc.cantidad_solicitada, " +
                "sc.costo_total, sc.fecha_solicitud, sc.observaciones, " +
                "sc.estado, sc.usuario_solicitante_id, " +
                "p.nombre as producto_nombre, " +
                "u.username as usuario_solicitante_nombre, " +
                "pr.nombre as proveedor_nombre, " +
                "p.unidad_medida " +
                "FROM SolicitudesCompra sc " +
                "LEFT JOIN Productos p ON sc.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios u ON sc.usuario_solicitante_id = u.usuario_id " +
                "LEFT JOIN Proveedores pr ON p.proveedor_id = pr.proveedor_id " +
                "ORDER BY sc.fecha_solicitud DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                SolicitudCompra solicitud = mapResultSetToSolicitud(rs);
                solicitudes.add(solicitud);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes de compra: " + e.getMessage());
            e.printStackTrace();
        }
        return solicitudes;
    }

    /**
     * Obtener solicitudes por estado
     */
    public List<SolicitudCompra> getSolicitudesByEstado(String estado) {
        List<SolicitudCompra> solicitudes = new ArrayList<>();
        String sql = "SELECT " +
                "sc.solicitud_id, sc.producto_id, sc.cantidad_solicitada, " +
                "sc.costo_total, sc.fecha_solicitud, sc.observaciones, " +
                "sc.estado, sc.usuario_solicitante_id, " +
                "p.nombre as producto_nombre, " +
                "u.username as usuario_solicitante_nombre, " +
                "pr.nombre as proveedor_nombre, " +
                "p.unidad_medida " +
                "FROM SolicitudesCompra sc " +
                "LEFT JOIN Productos p ON sc.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios u ON sc.usuario_solicitante_id = u.usuario_id " +
                "LEFT JOIN Proveedores pr ON p.proveedor_id = pr.proveedor_id " +
                "WHERE sc.estado = ? " +
                "ORDER BY sc.fecha_solicitud DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, estado);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                SolicitudCompra solicitud = mapResultSetToSolicitud(rs);
                solicitudes.add(solicitud);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener solicitudes por estado: " + e.getMessage());
            e.printStackTrace();
        }
        return solicitudes;
    }

    /**
     * Aprobar solicitud de compra
     */
    public boolean aprobarSolicitud(int solicitudId) {
        String sql = "UPDATE SolicitudesCompra SET estado = 'aprobada' WHERE solicitud_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, solicitudId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al aprobar solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rechazar solicitud de compra
     */
    public boolean rechazarSolicitud(int solicitudId) {
        String sql = "UPDATE SolicitudesCompra SET estado = 'rechazada' WHERE solicitud_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, solicitudId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al rechazar solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Completar solicitud de compra (despachar) - SUMA stock a bodega
     */
    public boolean completarSolicitud(int solicitudId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Obtener información de la solicitud
            String sqlSelect = "SELECT producto_id, cantidad_solicitada FROM SolicitudesCompra " +
                    "WHERE solicitud_id = ? AND estado = 'aprobada'";

            int productoId;
            double cantidad;

            try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
                stmt.setInt(1, solicitudId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Solicitud no encontrada o no está aprobada");
                    }
                    productoId = rs.getInt("producto_id");
                    cantidad = rs.getDouble("cantidad_solicitada");
                }
            }

            // 2. Actualizar stock de bodega (SUMAR la cantidad)
            String sqlStock = "UPDATE InventarioBodega SET " +
                    "cantidad_disponible = cantidad_disponible + ?, " +
                    "fecha_actualizacion = CURRENT_TIMESTAMP " +
                    "WHERE producto_id = ? AND activo = true";

            try (PreparedStatement stmt = conn.prepareStatement(sqlStock)) {
                stmt.setDouble(1, cantidad);
                stmt.setInt(2, productoId);
                stmt.executeUpdate();
            }

            // 3. Marcar solicitud como completada
            String sqlCompletar = "UPDATE SolicitudesCompra SET estado = 'completada' WHERE solicitud_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlCompletar)) {
                stmt.setInt(1, solicitudId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    System.err.println("Error al hacer rollback: " + ex.getMessage());
                }
            }
            System.err.println("Error al completar solicitud: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    System.err.println("Error al restaurar auto-commit: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Crear nueva solicitud de compra
     */
    public boolean crearSolicitudCompra(int productoId, int usuarioSolicitanteId,
                                        double cantidadSolicitada, String observaciones) {
        String sql = "INSERT INTO SolicitudesCompra " +
                "(producto_id, usuario_solicitante_id, cantidad_solicitada, observaciones, estado) " +
                "VALUES (?, ?, ?, ?, 'pendiente')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            stmt.setInt(2, usuarioSolicitanteId);
            stmt.setDouble(3, cantidadSolicitada);
            stmt.setString(4, observaciones);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear solicitud de compra: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mapear ResultSet a objeto SolicitudCompra
     */
    private SolicitudCompra mapResultSetToSolicitud(ResultSet rs) throws SQLException {
        SolicitudCompra solicitud = new SolicitudCompra();
        solicitud.setSolicitudId(rs.getInt("solicitud_id"));
        solicitud.setProductoId(rs.getInt("producto_id"));
        solicitud.setCantidadSolicitada(rs.getDouble("cantidad_solicitada"));
        solicitud.setCostoTotal(rs.getBigDecimal("costo_total"));
        solicitud.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        solicitud.setObservaciones(rs.getString("observaciones"));
        solicitud.setEstado(rs.getString("estado"));
        solicitud.setUsuarioSolicitanteId(rs.getInt("usuario_solicitante_id"));

        // Campos relacionados
        solicitud.setProductoNombre(rs.getString("producto_nombre"));
        solicitud.setUsuarioSolicitanteNombre(rs.getString("usuario_solicitante_nombre"));
        solicitud.setProveedorNombre(rs.getString("proveedor_nombre"));
        solicitud.setUnidadMedida(rs.getString("unidad_medida"));

        return solicitud;
    }
}