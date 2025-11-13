package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.Venta;
import com.mycompany.licoreria.models.DetalleVenta;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    private Connection connection;

    public VentaDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener productos disponibles para vender (con stock en vendedor)
     */
    public List<Producto> getProductosParaVenta() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.precio, p.unidad_medida, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND iv.activo = true AND iv.cantidad_disponible > 0 " +
                "ORDER BY p.nombre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setProductoId(rs.getInt("producto_id"));
                producto.setNombre(rs.getString("nombre"));
                producto.setPrecio(rs.getBigDecimal("precio"));
                producto.setUnidadMedida(rs.getString("unidad_medida"));
                producto.setStockVendedor(rs.getDouble("stock_vendedor"));
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos para venta: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Crear una nueva venta - CON usuario_id
     */
    public int crearVenta(Venta venta) {
        String sql = "INSERT INTO Facturas (total, cliente, usuario_id) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setBigDecimal(1, venta.getTotal());
            stmt.setString(2, venta.getCliente());
            stmt.setInt(3, venta.getUsuarioId());

            int affectedRows = stmt.executeUpdate();
            if (affectedRows > 0) {
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
            return -1;
        } catch (SQLException e) {
            System.err.println("Error al crear venta: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * Procesar venta completa con transacción
     */
    public boolean procesarVentaCompleta(Venta venta, List<DetalleVenta> detalles) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Crear factura
            String sqlFactura = "INSERT INTO Facturas (total, cliente, usuario_id) VALUES (?, ?, ?)";
            int facturaId;

            try (PreparedStatement stmt = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setBigDecimal(1, venta.getTotal());
                stmt.setString(2, venta.getCliente());
                stmt.setInt(3, venta.getUsuarioId());
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    facturaId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la factura");
                }
            }

            // 2. Insertar detalles y actualizar stock
            String sqlDetalle = "INSERT INTO DetallesDeFacturas (factura_id, producto_id, cantidad, precio_unitario, sub_total) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE InventarioVendedor SET cantidad_disponible = cantidad_disponible - ? WHERE producto_id = ? AND activo = true AND cantidad_disponible >= ?";

            for (DetalleVenta detalle : detalles) {
                // Insertar detalle
                try (PreparedStatement stmt = conn.prepareStatement(sqlDetalle)) {
                    stmt.setInt(1, facturaId);
                    stmt.setInt(2, detalle.getProductoId());
                    stmt.setDouble(3, detalle.getCantidad());
                    stmt.setBigDecimal(4, detalle.getPrecioUnitario());
                    stmt.setBigDecimal(5, detalle.getSubTotal());
                    stmt.executeUpdate();
                }

                // Actualizar stock con validación
                try (PreparedStatement stmt = conn.prepareStatement(sqlStock)) {
                    stmt.setDouble(1, detalle.getCantidad());
                    stmt.setInt(2, detalle.getProductoId());
                    stmt.setDouble(3, detalle.getCantidad());

                    int affectedRows = stmt.executeUpdate();
                    if (affectedRows == 0) {
                        throw new SQLException("Stock insuficiente para producto ID: " + detalle.getProductoId());
                    }
                }
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
            System.err.println("Error en transacción de venta: " + e.getMessage());
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
     * Agregar detalle a una venta
     */
    public boolean agregarDetalleVenta(int ventaId, DetalleVenta detalle) {
        String sql = "INSERT INTO DetallesDeFacturas (factura_id, producto_id, cantidad, precio_unitario, sub_total) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);
            stmt.setInt(2, detalle.getProductoId());
            stmt.setDouble(3, detalle.getCantidad());
            stmt.setBigDecimal(4, detalle.getPrecioUnitario());
            stmt.setBigDecimal(5, detalle.getSubTotal());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al agregar detalle de venta: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Actualizar stock del vendedor después de una venta
     */
    public boolean actualizarStockVendedor(int productoId, double cantidadVendida) {
        String sql = "UPDATE InventarioVendedor SET " +
                "cantidad_disponible = cantidad_disponible - ?, " +
                "fecha_actualizacion = CURRENT_TIMESTAMP " +
                "WHERE producto_id = ? AND activo = true AND cantidad_disponible >= ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, cantidadVendida);
            stmt.setInt(2, productoId);
            stmt.setDouble(3, cantidadVendida);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error al actualizar stock de vendedor: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Crear petición de stock a bodega
     */
    public boolean crearPeticionStock(int productoId, int usuarioId, double cantidadSolicitada, String observaciones) {
        String sql = "INSERT INTO PeticionesStock (producto_id, usuario_solicitante_id, cantidad_solicitada, observaciones) " +
                "VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, productoId);
            stmt.setInt(2, usuarioId);
            stmt.setDouble(3, cantidadSolicitada);
            stmt.setString(4, observaciones);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error al crear petición de stock: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener historial de ventas del vendedor
     */
    public List<Venta> getVentasPorVendedor(int usuarioId) {
        List<Venta> ventas = new ArrayList<>();
        String sql = "SELECT f.factura_id, f.fecha_factura, f.total, f.cliente, f.usuario_id " +
                "FROM Facturas f " +
                "WHERE f.activo = true AND f.usuario_id = ? " +
                "ORDER BY f.fecha_factura DESC " +
                "LIMIT 50";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Venta venta = new Venta();
                    venta.setVentaId(rs.getInt("factura_id"));
                    venta.setFechaVenta(rs.getTimestamp("fecha_factura"));
                    venta.setTotal(rs.getBigDecimal("total"));
                    venta.setCliente(rs.getString("cliente"));
                    venta.setUsuarioId(rs.getInt("usuario_id"));
                    ventas.add(venta);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener ventas por vendedor: " + e.getMessage());
            e.printStackTrace();
        }
        return ventas;
    }

    /**
     * Obtener detalles de una venta
     */
    public List<DetalleVenta> getDetallesVenta(int ventaId) {
        List<DetalleVenta> detalles = new ArrayList<>();
        String sql = "SELECT df.detalle_factura_id, df.producto_id, df.cantidad, df.precio_unitario, df.sub_total, " +
                "p.nombre as producto_nombre, p.unidad_medida " +
                "FROM DetallesDeFacturas df " +
                "INNER JOIN Productos p ON df.producto_id = p.producto_id " +
                "WHERE df.factura_id = ? AND df.activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ventaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    DetalleVenta detalle = new DetalleVenta();
                    detalle.setDetalleVentaId(rs.getInt("detalle_factura_id"));
                    detalle.setProductoId(rs.getInt("producto_id"));
                    detalle.setCantidad(rs.getDouble("cantidad"));
                    detalle.setPrecioUnitario(rs.getBigDecimal("precio_unitario"));
                    detalle.setSubTotal(rs.getBigDecimal("sub_total"));
                    detalle.setProductoNombre(rs.getString("producto_nombre"));
                    detalle.setUnidadMedida(rs.getString("unidad_medida"));
                    detalles.add(detalle);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener detalles de venta: " + e.getMessage());
            e.printStackTrace();
        }
        return detalles;
    }

    /**
     * Obtener productos con stock bajo para el vendedor
     */
    public List<Producto> getProductosStockBajoVendedor() {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.unidad_medida, " +
                "iv.cantidad_disponible as stock_vendedor, " +
                "iv.cantidad_minima as cantidad_minima_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND iv.activo = true " +
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
                productos.add(producto);
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener productos con stock bajo: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Buscar productos por nombre
     */
    public List<Producto> buscarProductos(String searchTerm) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.precio, p.unidad_medida, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND iv.activo = true " +
                "AND p.nombre LIKE ? AND iv.cantidad_disponible > 0 " +
                "ORDER BY p.nombre";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Producto producto = new Producto();
                    producto.setProductoId(rs.getInt("producto_id"));
                    producto.setNombre(rs.getString("nombre"));
                    producto.setPrecio(rs.getBigDecimal("precio"));
                    producto.setUnidadMedida(rs.getString("unidad_medida"));
                    producto.setStockVendedor(rs.getDouble("stock_vendedor"));
                    productos.add(producto);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al buscar productos: " + e.getMessage());
            e.printStackTrace();
        }
        return productos;
    }
}