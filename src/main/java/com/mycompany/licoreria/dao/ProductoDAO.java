package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductoDAO {
    private Connection connection;

    public ProductoDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener todos los productos con información de stock
     */
    public List<Producto> getAllProductos() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.proveedor_id, p.nombre, p.costo, p.precio, " +
                "p.descripcion, p.unidad_medida, p.activo, " +
                "pr.nombre as proveedor_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_minima as cantidad_minima_vendedor " +
                "FROM Productos p " +
                "LEFT JOIN Proveedores pr ON p.proveedor_id = pr.proveedor_id " +
                "LEFT JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true " +
                "ORDER BY p.nombre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = mapResultSetToProducto(rs);
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Obtener productos con stock bajo en bodega
     */
    public List<Producto> getProductosStockBajoBodega() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.proveedor_id, p.nombre, p.costo, p.precio, " +
                "p.descripcion, p.unidad_medida, p.activo, " +
                "pr.nombre as proveedor_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_minima as cantidad_minima_vendedor " +
                "FROM Productos p " +
                "LEFT JOIN Proveedores pr ON p.proveedor_id = pr.proveedor_id " +
                "LEFT JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND ib.cantidad_disponible <= ib.cantidad_minima " +
                "ORDER BY ib.cantidad_disponible ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = mapResultSetToProducto(rs);
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Buscar productos por nombre
     */
    public List<Producto> searchProductos(String searchTerm) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.proveedor_id, p.nombre, p.costo, p.precio, " +
                "p.descripcion, p.unidad_medida, p.activo, " +
                "pr.nombre as proveedor_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_minima as cantidad_minima_vendedor " +
                "FROM Productos p " +
                "LEFT JOIN Proveedores pr ON p.proveedor_id = pr.proveedor_id " +
                "LEFT JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND p.nombre LIKE ? " +
                "ORDER BY p.nombre";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Producto producto = mapResultSetToProducto(rs);
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Actualizar stock en bodega
     */
    public boolean actualizarStockBodega(int productoId, double nuevaCantidad) {
        String sql = "UPDATE InventarioBodega SET " +
                "cantidad_disponible = ?, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP " +
                "WHERE producto_id = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, nuevaCantidad);
            stmt.setInt(2, productoId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crear solicitud de compra a proveedor
     */
    public boolean crearSolicitudCompra(int productoId, double cantidadSolicitada, String observaciones) {
        String sql = "INSERT INTO Compras (proveedor_id, total, observaciones) " +
                "SELECT p.proveedor_id, (p.costo * ?), ? " +
                "FROM Productos p WHERE p.producto_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setDouble(1, cantidadSolicitada);
            stmt.setString(2, observaciones);
            stmt.setInt(3, productoId);

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                // Obtener el ID de la compra creada
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int compraId = generatedKeys.getInt(1);

                    // Crear detalle de compra
                    return crearDetalleCompra(compraId, productoId, cantidadSolicitada);
                }
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean crearDetalleCompra(int compraId, int productoId, double cantidad) {
        String sql = "INSERT INTO DetallesDeCompras (compra_id, producto_id, cantidad, costo_unitario, sub_total) " +
                "SELECT ?, ?, ?, p.costo, (p.costo * ?) " +
                "FROM Productos p WHERE p.producto_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, compraId);
            stmt.setInt(2, productoId);
            stmt.setDouble(3, cantidad);
            stmt.setDouble(4, cantidad);
            stmt.setInt(5, productoId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener productos que necesitan reabastecimiento
     */
    public List<Producto> getProductosParaReabastecer() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.proveedor_id, p.nombre, p.costo, p.precio, " +
                "p.descripcion, p.unidad_medida, p.activo, " +
                "pr.nombre as proveedor_nombre, " +
                "ib.cantidad_disponible as stock_bodega, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "ib.cantidad_minima as cantidad_minima_bodega, " +
                "iv.cantidad_minima as cantidad_minima_vendedor " +
                "FROM Productos p " +
                "LEFT JOIN Proveedores pr ON p.proveedor_id = pr.proveedor_id " +
                "LEFT JOIN InventarioBodega ib ON p.producto_id = ib.producto_id " +
                "LEFT JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND " +
                "(ib.cantidad_disponible <= ib.cantidad_minima * 1.5 OR ib.cantidad_disponible IS NULL) " +
                "ORDER BY ib.cantidad_disponible ASC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = mapResultSetToProducto(rs);
                productos.add(producto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Mapear ResultSet a objeto Producto
     */
    private Producto mapResultSetToProducto(ResultSet rs) throws SQLException {
        Producto producto = new Producto();
        producto.setProductoId(rs.getInt("producto_id"));
        producto.setProveedorId(rs.getInt("proveedor_id"));
        producto.setNombre(rs.getString("nombre"));
        producto.setCosto(rs.getBigDecimal("costo"));
        producto.setPrecio(rs.getBigDecimal("precio"));
        producto.setDescripcion(rs.getString("descripcion"));
        producto.setUnidadMedida(rs.getString("unidad_medida"));
        producto.setActivo(rs.getBoolean("activo"));

        // Campos relacionados
        producto.setProveedorNombre(rs.getString("proveedor_nombre"));
        producto.setStockBodega(rs.getDouble("stock_bodega"));
        producto.setStockVendedor(rs.getDouble("stock_vendedor"));
        producto.setCantidadMinimaBodega(rs.getDouble("cantidad_minima_bodega"));
        producto.setCantidadMinimaVendedor(rs.getDouble("cantidad_minima_vendedor"));

        return producto;
    }
}