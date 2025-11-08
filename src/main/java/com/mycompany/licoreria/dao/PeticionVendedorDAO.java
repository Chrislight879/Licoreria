package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.PeticionVendedor;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PeticionVendedorDAO {
    private Connection connection;

    public PeticionVendedorDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener inventario de bodega disponible
     */
    public List<Producto> getInventarioBodega() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "ib.cantidad_minima as cantidad_minima_bodega " +
                "FROM Productos p " +
                "INNER JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "WHERE p.activo = true AND ib.activo = true " +
                "ORDER BY p.nombre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProductoId(rs.getInt("producto_id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setUnidadMedida(rs.getString("unidad_medida"));
                producto.setStockBodega(rs.getDouble("stock_bodega"));
                producto.setCantidadMinimaBodega(rs.getDouble("cantidad_minima_bodega"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Obtener inventario del vendedor
     */
    public List<Producto> getInventarioVendedor() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "iv.cantidad_minima as cantidad_minima_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND iv.activo = true " +
                "ORDER BY p.nombre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProductoId(rs.getInt("producto_id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setUnidadMedida(rs.getString("unidad_medida"));
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
     * Crear petición de stock
     */
    public boolean crearPeticion(PeticionVendedor peticion) {
        String sql = "INSERT INTO PeticionesStock (producto_id, usuario_solicitante_id, cantidad_solicitada, observaciones) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, peticion.getProductoId());
            stmt.setInt(2, peticion.getUsuarioSolicitanteId());
            stmt.setDouble(3, peticion.getCantidadSolicitada());
            stmt.setString(4, peticion.getObservaciones());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener peticiones del vendedor
     */
    public List<PeticionVendedor> getPeticionesPorVendedor(int usuarioId) {
        List<PeticionVendedor> peticiones = new ArrayList<>();
        String sql = "SELECT " +
                "ps.peticion_id, ps.producto_id, ps.usuario_solicitante_id, " +
                "ps.cantidad_solicitada, ps.fecha_solicitud, ps.fecha_aprobacion, " +
                "ps.fecha_despacho, ps.usuario_aprobador_id, ps.estado, " +
                "ps.observaciones, ps.activo, " +
                "p.nombre as producto_nombre, p.unidad_medida, " +
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
                "WHERE ps.usuario_solicitante_id = ? AND ps.activo = true " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PeticionVendedor peticion = mapResultSetToPeticion(rs);
                peticiones.add(peticion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return peticiones;
    }

    /**
     * Eliminar petición (marcar como inactiva)
     */
    public boolean eliminarPeticion(int peticionId) {
        String sql = "UPDATE PeticionesStock SET activo = false WHERE peticion_id = ? AND estado = 'pendiente'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, peticionId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Buscar productos en bodega
     */
    public List<Producto> buscarProductosBodega(String searchTerm) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "ib.cantidad_minima as cantidad_minima_bodega " +
                "FROM Productos p " +
                "INNER JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "WHERE p.activo = true AND ib.activo = true " +
                "AND p.nombre LIKE ? " +
                "ORDER BY p.nombre";

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
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Buscar peticiones
     */
    public List<PeticionVendedor> buscarPeticiones(int usuarioId, String searchTerm) {
        List<PeticionVendedor> peticiones = new ArrayList<>();
        String sql = "SELECT " +
                "ps.peticion_id, ps.producto_id, ps.usuario_solicitante_id, " +
                "ps.cantidad_solicitada, ps.fecha_solicitud, ps.fecha_aprobacion, " +
                "ps.fecha_despacho, ps.usuario_aprobador_id, ps.estado, " +
                "ps.observaciones, ps.activo, " +
                "p.nombre as producto_nombre, p.unidad_medida, " +
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
                "WHERE ps.usuario_solicitante_id = ? AND ps.activo = true " +
                "AND (p.nombre LIKE ? OR ps.observaciones LIKE ?) " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PeticionVendedor peticion = mapResultSetToPeticion(rs);
                peticiones.add(peticion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return peticiones;
    }

    /**
     * Obtener productos con stock bajo en vendedor
     */
    public List<Producto> getProductosStockBajoVendedor(int usuarioId) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "iv.cantidad_minima as cantidad_minima_vendedor, " +
                "ib.cantidad_disponible as stock_bodega " +
                "FROM Productos p " +
                "INNER JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "INNER JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "WHERE p.activo = true AND iv.activo = true AND ib.activo = true " +
                "AND iv.cantidad_disponible <= iv.cantidad_minima " +
                "ORDER BY iv.cantidad_disponible ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProductoId(rs.getInt("producto_id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setUnidadMedida(rs.getString("unidad_medida"));
                producto.setStockVendedor(rs.getDouble("stock_vendedor"));
                producto.setCantidadMinimaVendedor(rs.getDouble("cantidad_minima_vendedor"));
                producto.setStockBodega(rs.getDouble("stock_bodega"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Mapear ResultSet a PeticionVendedor
     */
    private PeticionVendedor mapResultSetToPeticion(ResultSet rs) throws SQLException {
        PeticionVendedor peticion = new PeticionVendedor();
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
        peticion.setUnidadMedida(rs.getString("unidad_medida"));
        peticion.setUsuarioSolicitanteNombre(rs.getString("usuario_solicitante_nombre"));
        peticion.setUsuarioAprobadorNombre(rs.getString("usuario_aprobador_nombre"));
        peticion.setStockBodega(rs.getDouble("stock_bodega"));
        peticion.setStockVendedor(rs.getDouble("stock_vendedor"));

        return peticion;
    }
}