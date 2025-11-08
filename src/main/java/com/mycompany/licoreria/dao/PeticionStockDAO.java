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
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PeticionStock peticion = mapResultSetToPeticion(rs);
                peticiones.add(peticion);
            }
        } catch (SQLException e) {
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

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PeticionStock peticion = mapResultSetToPeticion(rs);
                peticiones.add(peticion);
            }
        } catch (SQLException e) {
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
                "observaciones = CONCAT(IFNULL(observaciones, ''), ?) " +
                "WHERE peticion_id = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioAprobadorId);
            stmt.setString(2, " | Aprobado: " + observaciones);
            stmt.setInt(3, peticionId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
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
                "observaciones = CONCAT(IFNULL(observaciones, ''), ?) " +
                "WHERE peticion_id = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioAprobadorId);
            stmt.setString(2, " | Rechazado: " + observaciones);
            stmt.setInt(3, peticionId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
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
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
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
            e.printStackTrace();
        }
        return inventario;
    }

    /**
     * Actualizar stock en bodega después de despachar
     */
    public boolean actualizarStockBodega(int productoId, double cantidad) {
        String sql = "UPDATE InventarioBodega SET " +
                "cantidad_disponible = cantidad_disponible - ?, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP " +
                "WHERE producto_id = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidad);
            stmt.setInt(2, productoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar stock del vendedor después de recibir
     */
    public boolean actualizarStockVendedor(int productoId, double cantidad) {
        String sql = "UPDATE InventarioVendedor SET " +
                "cantidad_disponible = cantidad_disponible + ?, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP " +
                "WHERE producto_id = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidad);
            stmt.setInt(2, productoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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