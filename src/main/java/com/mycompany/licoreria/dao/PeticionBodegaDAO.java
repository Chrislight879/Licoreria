package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeticionBodegaDAO {
    private Connection connection;

    public PeticionBodegaDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener peticiones pendientes con información detallada
     */
    public List<PeticionStock> getPeticionesPendientes() {
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
                "iv.cantidad_disponible as stock_vendedor, " +
                "p.unidad_medida " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.activo = true AND ps.estado = 'pendiente' " +
                "ORDER BY ps.fecha_solicitud ASC";

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
     * Obtener peticiones aceptadas/despachadas
     */
    public List<PeticionStock> getPeticionesAceptadas() {
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
                "iv.cantidad_disponible as stock_vendedor, " +
                "p.unidad_medida " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.activo = true AND ps.estado IN ('aprobada', 'despachada') " +
                "ORDER BY ps.fecha_aprobacion DESC";

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
     * Obtener inventario de bodega con información relevante
     */
    public List<Producto> getInventarioBodegaCompleto() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "iv.cantidad_minima as cantidad_minima_vendedor, " +
                "COUNT(ps.peticion_id) as peticiones_pendientes " +
                "FROM Productos p " +
                "INNER JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "LEFT JOIN PeticionesStock ps ON p.producto_id = ps.producto_id AND ps.estado = 'pendiente' AND ps.activo = true " +
                "WHERE p.activo = true AND ib.activo = true " +
                "GROUP BY p.producto_id, p.nombre, p.unidad_medida, ib.cantidad_disponible, " +
                "ib.cantidad_minima, iv.cantidad_disponible, iv.cantidad_minima " +
                "ORDER BY peticiones_pendientes DESC, ib.cantidad_disponible ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProductoId(rs.getInt("producto_id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setUnidadMedida(rs.getString("unidad_medida"));
                producto.setStockBodega(rs.getDouble("stock_bodega"));
                producto.setCantidadMinimaBodega(rs.getDouble("cantidad_minima_bodega"));
                producto.setStockVendedor(rs.getDouble("stock_vendedor"));
                producto.setCantidadMinimaVendedor(rs.getDouble("cantidad_minima_vendedor"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Buscar peticiones pendientes por término
     */
    public List<PeticionStock> searchPeticionesPendientes(String searchTerm) {
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
                "iv.cantidad_disponible as stock_vendedor, " +
                "p.unidad_medida " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.activo = true AND ps.estado = 'pendiente' AND " +
                "(p.nombre LIKE ? OR us.username LIKE ? OR ps.observaciones LIKE ?) " +
                "ORDER BY ps.fecha_solicitud ASC";

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
     * Buscar peticiones aceptadas por término
     */
    public List<PeticionStock> searchPeticionesAceptadas(String searchTerm) {
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
                "iv.cantidad_disponible as stock_vendedor, " +
                "p.unidad_medida " +
                "FROM PeticionesStock ps " +
                "LEFT JOIN Productos p ON ps.producto_id = p.producto_id " +
                "LEFT JOIN Usuarios us ON ps.usuario_solicitante_id = us.usuario_id " +
                "LEFT JOIN Usuarios ua ON ps.usuario_aprobador_id = ua.usuario_id " +
                "LEFT JOIN InventarioBodega ib ON ps.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON ps.producto_id = iv.producto_id " +
                "WHERE ps.activo = true AND ps.estado IN ('aprobada', 'despachada') AND " +
                "(p.nombre LIKE ? OR us.username LIKE ? OR ua.username LIKE ? OR ps.observaciones LIKE ?) " +
                "ORDER BY ps.fecha_aprobacion DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            stmt.setString(4, likeTerm);

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
     * Buscar en inventario de bodega
     */
    public List<Producto> searchInventarioBodega(String searchTerm) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "iv.cantidad_minima as cantidad_minima_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND ib.activo = true AND p.nombre LIKE ? " +
                "ORDER BY ib.cantidad_disponible ASC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProductoId(rs.getInt("producto_id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setUnidadMedida(rs.getString("unidad_medida"));
                producto.setStockBodega(rs.getDouble("stock_bodega"));
                producto.setCantidadMinimaBodega(rs.getDouble("cantidad_minima_bodega"));
                producto.setStockVendedor(rs.getDouble("stock_vendedor"));
                producto.setCantidadMinimaVendedor(rs.getDouble("cantidad_minima_vendedor"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
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
                "WHERE peticion_id = ? AND activo = true AND estado = 'pendiente'";

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
                "WHERE peticion_id = ? AND activo = true AND estado = 'pendiente'";

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
     * Despachar petición (marcar como completada)
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
     * Obtener estadísticas de peticiones
     */
    public String getEstadisticasPeticiones() {
        String sql = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN estado = 'pendiente' THEN 1 ELSE 0 END) as pendientes, " +
                "SUM(CASE WHEN estado = 'aprobada' THEN 1 ELSE 0 END) as aprobadas, " +
                "SUM(CASE WHEN estado = 'rechazada' THEN 1 ELSE 0 END) as rechazadas, " +
                "SUM(CASE WHEN estado = 'despachada' THEN 1 ELSE 0 END) as despachadas " +
                "FROM PeticionesStock " +
                "WHERE activo = true";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return String.format("Total: %d | Pendientes: %d | Aprobadas: %d | Rechazadas: %d | Despachadas: %d",
                        rs.getInt("total"), rs.getInt("pendientes"), rs.getInt("aprobadas"),
                        rs.getInt("rechazadas"), rs.getInt("despachadas"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "No se pudieron cargar las estadísticas";
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