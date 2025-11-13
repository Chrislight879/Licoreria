package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeticionStockDAO {
    private Connection connection;

    public PeticionStockDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener todas las peticiones con información relacionada
     */
    public List<PeticionStock> getAllPeticiones() {
        List<PeticionStock> peticiones = new ArrayList<>();
        String sql = "SELECT " +
                "ps.peticion_id, ps.producto_id, ps.usuario_solicitante_id, " +
                "ps.cantidad_solicitada, ps.fecha_solicitud, ps.fecha_aprobacion, " +
                "ps.fecha_despacho, ps.usuario_aprobador_id, ps.estado, " +
                "ps.observaciones, ps.activo, " +
                "p.nombre as producto_nombre, " +
                "us.username as usuario_solicitante_nombre, " +
                "ua.username as usuario_aprobador_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.activo = true " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PeticionStock peticion = mapResultSetToPeticion(rs);
                peticiones.add(peticion);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener todas las peticiones: " + e.getMessage());
            e.printStackTrace();
        }
        return peticiones;
    }

    /**
     * Obtener peticiones por estado
     */
    public List<PeticionStock> getPeticionesByEstado(String estado) {
        List<PeticionStock> peticiones = new ArrayList<>();
        String sql = "SELECT " +
                "ps.peticion_id, ps.producto_id, ps.usuario_solicitante_id, " +
                "ps.cantidad_solicitada, ps.fecha_solicitud, ps.fecha_aprobacion, " +
                "ps.fecha_despacho, ps.usuario_aprobador_id, ps.estado, " +
                "ps.observaciones, ps.activo, " +
                "p.nombre as producto_nombre, " +
                "us.username as usuario_solicitante_nombre, " +
                "ua.username as usuario_aprobador_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.activo = true AND ps.estado = ? " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, estado);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PeticionStock peticion = mapResultSetToPeticion(rs);
                    peticiones.add(peticion);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener peticiones por estado '" + estado + "': " + e.getMessage());
            e.printStackTrace();
        }
        return peticiones;
    }

    /**
     * Buscar peticiones por término
     */
    public List<PeticionStock> searchPeticiones(String searchTerm) {
        List<PeticionStock> peticiones = new ArrayList<>();
        String sql = "SELECT " +
                "ps.peticion_id, ps.producto_id, ps.usuario_solicitante_id, " +
                "ps.cantidad_solicitada, ps.fecha_solicitud, ps.fecha_aprobacion, " +
                "ps.fecha_despacho, ps.usuario_aprobador_id, ps.estado, " +
                "ps.observaciones, ps.activo, " +
                "p.nombre as producto_nombre, " +
                "us.username as usuario_solicitante_nombre, " +
                "ua.username as usuario_aprobador_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.activo = true AND (" +
                "p.nombre LIKE ? OR " +
                "us.username LIKE ? OR " +
                "ps.observaciones LIKE ?) " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    PeticionStock peticion = mapResultSetToPeticion(rs);
                    peticiones.add(peticion);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar peticiones con término '" + searchTerm + "': " + e.getMessage());
            e.printStackTrace();
        }
        return peticiones;
    }

    /**
     * Aprobar petición
     */
    public boolean aprobarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        String sql = "UPDATE PeticionesStock SET " +
                "estado = 'aprobada', " +
                "fecha_aprobacion = CURRENT_TIMESTAMP, " +
                "usuario_aprobador_id = ?, " +
                "observaciones = CONCAT(COALESCE(observaciones, ''), ?) " +
                "WHERE peticion_id = ? AND activo = true AND estado = 'pendiente'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioAprobadorId);
            stmt.setString(2, " | Aprobado: " + observaciones);
            stmt.setInt(3, peticionId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al aprobar petición " + peticionId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Rechazar petición
     */
    public boolean rechazarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        String sql = "UPDATE PeticionesStock SET " +
                "estado = 'rechazada', " +
                "fecha_aprobacion = CURRENT_TIMESTAMP, " +
                "usuario_aprobador_id = ?, " +
                "observaciones = CONCAT(COALESCE(observaciones, ''), ?) " +
                "WHERE peticion_id = ? AND activo = true AND estado = 'pendiente'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioAprobadorId);
            stmt.setString(2, " | Rechazado: " + observaciones);
            stmt.setInt(3, peticionId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al rechazar petición " + peticionId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Marcar petición como despachada
     */
    public boolean despacharPeticion(int peticionId) {
        String sql = "UPDATE PeticionesStock SET " +
                "estado = 'despachada', " +
                "fecha_despacho = CURRENT_TIMESTAMP " +
                "WHERE peticion_id = ? AND activo = true AND estado = 'aprobada'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, peticionId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al despachar petición " + peticionId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * NUEVO MÉTODO: Crear petición de stock desde bodega
     */
    public boolean crearPeticionBodega(int productoId, int usuarioSolicitanteId, double cantidadSolicitada, String observaciones) {
        String sql = "INSERT INTO PeticionesStock " +
                "(producto_id, usuario_solicitante_id, cantidad_solicitada, observaciones, estado) " +
                "VALUES (?, ?, ?, ?, 'pendiente')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            stmt.setInt(2, usuarioSolicitanteId);
            stmt.setDouble(3, cantidadSolicitada);
            stmt.setString(4, observaciones);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear petición de bodega: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Object[]> getInventarioBodega() {
        List<Object[]> inventario = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, ib.cantidad_disponible, " +
                "ib.cantidad_minima, p.unidad_medida " +
                "FROM InventarioBodega ib " +
                "INNER JOIN Productos p ON ib.producto_id = p.producto_id " +
                "WHERE ib.activo = true AND p.activo = true " +
                "ORDER BY p.nombre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] fila = {
                        rs.getInt("producto_id"),
                        rs.getString("nombre"),
                        rs.getDouble("cantidad_disponible"),
                        rs.getDouble("cantidad_minima"),
                        rs.getString("unidad_medida")
                };
                inventario.add(fila);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener inventario de bodega: " + e.getMessage());
            e.printStackTrace();
        }
        return inventario;
    }

    /**
     * Actualizar stock en bodega después de despachar - CORREGIDO
     * Ahora RESTA de bodega (correcto)
     */
    public boolean actualizarStockBodega(int productoId, double cantidad) {
        String sql = "UPDATE InventarioBodega SET " +
                "cantidad_disponible = cantidad_disponible - ?, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP " +
                "WHERE producto_id = ? AND activo = true AND cantidad_disponible >= ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidad);
            stmt.setInt(2, productoId);
            stmt.setDouble(3, cantidad); // Validar que hay stock suficiente

            int affectedRows = stmt.executeUpdate();
            if (affectedRows == 0) {
                System.err.println("Stock insuficiente en bodega para producto " + productoId +
                        ". Cantidad solicitada: " + cantidad);
                return false;
            }
            return true;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock de bodega para producto " + productoId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar stock del vendedor después de recibir - CORREGIDO
     * Ahora SUMA al vendedor (correcto)
     */
    public boolean actualizarStockVendedor(int productoId, double cantidad) {
        String sql = "UPDATE InventarioVendedor SET " +
                "cantidad_disponible = cantidad_disponible + ?, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP " +
                "WHERE producto_id = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidad);
            stmt.setInt(2, productoId);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock de vendedor para producto " + productoId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Despachar petición completa con transacción - NUEVO MÉTODO
     */
    public boolean despacharPeticionCompleta(int peticionId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Obtener información de la petición
            String sqlSelect = "SELECT producto_id, cantidad_solicitada FROM PeticionesStock " +
                    "WHERE peticion_id = ? AND estado = 'aprobada' AND activo = true";

            int productoId;
            double cantidad;

            try (PreparedStatement stmt = conn.prepareStatement(sqlSelect)) {
                stmt.setInt(1, peticionId);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        throw new SQLException("Petición no encontrada o no está aprobada");
                    }
                    productoId = rs.getInt("producto_id");
                    cantidad = rs.getDouble("cantidad_solicitada");
                }
            }

            // 2. Actualizar stock de bodega (restar)
            String sqlBodega = "UPDATE InventarioBodega SET " +
                    "cantidad_disponible = cantidad_disponible - ?, " +
                    "fecha_actualizacion = CURRENT_TIMESTAMP " +
                    "WHERE producto_id = ? AND activo = true AND cantidad_disponible >= ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlBodega)) {
                stmt.setDouble(1, cantidad);
                stmt.setInt(2, productoId);
                stmt.setDouble(3, cantidad);

                if (stmt.executeUpdate() == 0) {
                    throw new SQLException("Stock insuficiente en bodega");
                }
            }

            // 3. Actualizar stock de vendedor (sumar)
            String sqlVendedor = "UPDATE InventarioVendedor SET " +
                    "cantidad_disponible = cantidad_disponible + ?, " +
                    "fecha_actualizacion = CURRENT_TIMESTAMP " +
                    "WHERE producto_id = ? AND activo = true";

            try (PreparedStatement stmt = conn.prepareStatement(sqlVendedor)) {
                stmt.setDouble(1, cantidad);
                stmt.setInt(2, productoId);
                stmt.executeUpdate();
            }

            // 4. Marcar petición como despachada
            String sqlDespachar = "UPDATE PeticionesStock SET " +
                    "estado = 'despachada', " +
                    "fecha_despacho = CURRENT_TIMESTAMP " +
                    "WHERE peticion_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlDespachar)) {
                stmt.setInt(1, peticionId);
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
            System.err.println("Error en transacción de despacho para petición " + peticionId + ": " + e.getMessage());
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
     * Obtener petición por ID
     */
    public PeticionStock getPeticionById(int peticionId) {
        String sql = "SELECT " +
                "ps.peticion_id, ps.producto_id, ps.usuario_solicitante_id, " +
                "ps.cantidad_solicitada, ps.fecha_solicitud, ps.fecha_aprobacion, " +
                "ps.fecha_despacho, ps.usuario_aprobador_id, ps.estado, " +
                "ps.observaciones, ps.activo, " +
                "p.nombre as producto_nombre, " +
                "us.username as usuario_solicitante_nombre, " +
                "ua.username as usuario_aprobador_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.peticion_id = ? AND ps.activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, peticionId);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPeticion(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener petición " + peticionId + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Mapear ResultSet a objeto PeticionStock
     */
    private PeticionStock mapResultSetToPeticion(ResultSet rs) throws SQLException {
        PeticionStock peticion = new PeticionStock();
        peticion.setPeticionId(rs.getInt("peticion_id"));
        peticion.setProductoId(rs.getInt("producto_id"));
        peticion.setUsuarioSolicitanteId(rs.getInt("usuario_solicitante_id"));
        peticion.setCantidadSolicitada(rs.getDouble("cantidad_solicitada"));
        peticion.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        peticion.setFechaAprobacion(rs.getTimestamp("fecha_aprobacion"));
        peticion.setFechaDespacho(rs.getTimestamp("fecha_despacho"));
        peticion.setUsuarioAprobadorId(rs.getInt("usuario_aprobador_id"));
        peticion.setEstado(rs.getString("estado"));
        peticion.setObservaciones(rs.getString("observaciones"));
        peticion.setActivo(rs.getBoolean("activo"));

        // Campos relacionados
        peticion.setProductoNombre(rs.getString("producto_nombre"));
        peticion.setUsuarioSolicitanteNombre(rs.getString("usuario_solicitante_nombre"));
        peticion.setUsuarioAprobadorNombre(rs.getString("usuario_aprobador_nombre"));
        peticion.setStockBodega(rs.getDouble("stock_bodega"));
        peticion.setStockVendedor(rs.getDouble("stock_vendedor"));

        return peticion;
    }
}