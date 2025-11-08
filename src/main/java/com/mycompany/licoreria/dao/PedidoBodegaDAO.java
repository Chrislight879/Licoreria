package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.PedidoBodega;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoBodegaDAO {
    private Connection connection;

    public PedidoBodegaDAO() {
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
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
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
                producto.setStockVendedor(rs.getDouble("stock_vendedor"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Buscar productos en bodega
     */
    public List<Producto> searchInventarioBodega(String searchTerm) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND ib.activo = true AND p.nombre LIKE ? " +
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
                producto.setStockVendedor(rs.getDouble("stock_vendedor"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Crear pedido a bodega
     */
    public boolean crearPedidoBodega(int productoId, int usuarioSolicitanteId,
                                     double cantidadSolicitada, String observaciones) {
        String sql = "INSERT INTO PeticionesStock " +
                "(producto_id, usuario_solicitante_id, cantidad_solicitada, observaciones, estado) " +
                "VALUES (?, ?, ?, ?, 'pendiente')";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            stmt.setInt(2, usuarioSolicitanteId);
            stmt.setDouble(3, cantidadSolicitada);
            stmt.setString(4, observaciones);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener pedidos del usuario actual
     */
    public List<PedidoBodega> getPedidosPorUsuario(int usuarioId) {
        List<PedidoBodega> pedidos = new ArrayList<>();
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
                "WHERE ps.activo = true AND ps.usuario_solicitante_id = ? " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PedidoBodega pedido = mapResultSetToPedido(rs);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    /**
     * Obtener todos los pedidos (para administración)
     */
    public List<PedidoBodega> getAllPedidos() {
        List<PedidoBodega> pedidos = new ArrayList<>();
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
                "WHERE ps.activo = true " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                PedidoBodega pedido = mapResultSetToPedido(rs);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    /**
     * Eliminar pedido (cancelar)
     */
    public boolean eliminarPedido(int pedidoId, int usuarioId) {
        String sql = "UPDATE PeticionesStock SET activo = false " +
                "WHERE peticion_id = ? AND usuario_solicitante_id = ? AND estado = 'pendiente'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, pedidoId);
            stmt.setInt(2, usuarioId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Buscar pedidos por término
     */
    public List<PedidoBodega> searchPedidos(String searchTerm, int usuarioId) {
        List<PedidoBodega> pedidos = new ArrayList<>();
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
                "WHERE ps.activo = true AND ps.usuario_solicitante_id = ? AND " +
                "(p.nombre LIKE ? OR ps.observaciones LIKE ?) " +
                "ORDER BY ps.fecha_solicitud DESC";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(2, likeTerm);
            stmt.setString(3, likeTerm);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                PedidoBodega pedido = mapResultSetToPedido(rs);
                pedidos.add(pedido);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pedidos;
    }

    /**
     * Obtener estadísticas de pedidos
     */
    public String getEstadisticasPedidos(int usuarioId) {
        String sql = "SELECT " +
                "COUNT(*) as total, " +
                "SUM(CASE WHEN estado = 'pendiente' THEN 1 ELSE 0 END) as pendientes, " +
                "SUM(CASE WHEN estado = 'aprobada' THEN 1 ELSE 0 END) as aprobadas, " +
                "SUM(CASE WHEN estado = 'rechazada' THEN 1 ELSE 0 END) as rechazadas, " +
                "SUM(CASE WHEN estado = 'despachada' THEN 1 ELSE 0 END) as despachadas " +
                "FROM PeticionesStock " +
                "WHERE activo = true AND usuario_solicitante_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

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
     * Mapear ResultSet a objeto PedidoBodega
     */
    private PedidoBodega mapResultSetToPedido(ResultSet rs) throws SQLException {
        PedidoBodega pedido = new PedidoBodega();
        pedido.setPedidoId(rs.getInt("peticion_id"));
        pedido.setProductoId(rs.getInt("producto_id"));
        pedido.setUsuarioSolicitanteId(rs.getInt("usuario_solicitante_id"));
        pedido.setCantidadSolicitada(rs.getDouble("cantidad_solicitada"));
        pedido.setFechaSolicitud(rs.getTimestamp("fecha_solicitud"));
        pedido.setFechaAprobacion(rs.getTimestamp("fecha_aprobacion"));
        pedido.setFechaDespacho(rs.getTimestamp("fecha_despacho"));
        pedido.setUsuarioAprobadorId(rs.getInt("usuario_aprobador_id"));
        pedido.setEstado(rs.getString("estado"));
        pedido.setObservaciones(rs.getString("observaciones"));
        pedido.setActivo(rs.getBoolean("activo"));

        // Campos relacionados
        pedido.setProductoNombre(rs.getString("producto_nombre"));
        pedido.setUsuarioSolicitanteNombre(rs.getString("usuario_solicitante_nombre"));
        pedido.setUsuarioAprobadorNombre(rs.getString("usuario_aprobador_nombre"));
        pedido.setStockBodega(rs.getDouble("stock_bodega"));
        pedido.setStockVendedor(rs.getDouble("stock_vendedor"));
        pedido.setUnidadMedida(rs.getString("unidad_medida"));

        return pedido;
    }
}