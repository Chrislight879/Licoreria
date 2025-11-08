package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.VentaDetalle;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.config.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDetalleDAO {
    private Connection connection;

    public VentaDetalleDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Obtener productos disponibles para venta rápida
     */
    public List<Producto> getProductosParaVentaRapida() {
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
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Buscar productos para venta rápida
     */
    public List<Producto> buscarProductosVentaRapida(String searchTerm) {
        List<Producto> productos = new ArrayList<>();
        String sql = "SELECT " +
                "p.producto_id, p.nombre, p.precio, p.unidad_medida, " +
                "iv.cantidad_disponible as stock_vendedor " +
                "FROM Productos p " +
                "INNER JOIN InventarioVendedor iv ON p.producto_id = iv.producto_id " +
                "WHERE p.activo = true AND iv.activo = true " +
                "AND (p.nombre LIKE ? OR p.descripcion LIKE ?) " +
                "AND iv.cantidad_disponible > 0 " +
                "ORDER BY p.nombre";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String likeTerm = "%" + searchTerm + "%";
            stmt.setString(1, likeTerm);
            stmt.setString(2, likeTerm);
            ResultSet rs = stmt.executeQuery();

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
            e.printStackTrace();
        }
        return productos;
    }

    /**
     * Obtener ventas del día actual
     */
    public List<Object[]> getVentasDelDia() {
        List<Object[]> ventas = new ArrayList<>();
        String sql = "SELECT " +
                "f.factura_id, f.fecha_factura, f.cliente, f.total, " +
                "u.username as vendedor, " +
                "COUNT(df.detalle_factura_id) as items " +
                "FROM Facturas f " +
                "INNER JOIN Usuarios u ON f.usuario_id = u.usuario_id " +
                "LEFT JOIN DetallesDeFacturas df ON f.factura_id = df.factura_id " +
                "WHERE DATE(f.fecha_factura) = CURDATE() AND f.activo = true " +
                "GROUP BY f.factura_id, f.fecha_factura, f.cliente, f.total, u.username " +
                "ORDER BY f.fecha_factura DESC";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Object[] venta = {
                        rs.getInt("factura_id"),
                        rs.getTimestamp("fecha_factura"),
                        rs.getString("cliente"),
                        rs.getBigDecimal("total"),
                        rs.getString("vendedor"),
                        rs.getInt("items")
                };
                ventas.add(venta);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ventas;
    }

    /**
     * Obtener detalles de una venta específica
     */
    public List<Object[]> getDetallesVenta(int facturaId) {
        List<Object[]> detalles = new ArrayList<>();
        String sql = "SELECT " +
                "p.nombre as producto, df.cantidad, df.precio_unitario, df.sub_total, " +
                "p.unidad_medida " +
                "FROM DetallesDeFacturas df " +
                "INNER JOIN Productos p ON df.producto_id = p.producto_id " +
                "WHERE df.factura_id = ? AND df.activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, facturaId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Object[] detalle = {
                        rs.getString("producto"),
                        rs.getDouble("cantidad"),
                        rs.getBigDecimal("precio_unitario"),
                        rs.getBigDecimal("sub_total"),
                        rs.getString("unidad_medida")
                };
                detalles.add(detalle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return detalles;
    }

    /**
     * Procesar venta rápida
     */
    public boolean procesarVentaRapida(String cliente, int usuarioId, List<VentaDetalle> detalles) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Calcular total
            BigDecimal total = detalles.stream()
                    .map(VentaDetalle::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            // Insertar factura
            String sqlFactura = "INSERT INTO Facturas (cliente, total, usuario_id) VALUES (?, ?, ?)";
            int facturaId;

            try (PreparedStatement stmt = conn.prepareStatement(sqlFactura, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setString(1, cliente);
                stmt.setBigDecimal(2, total);
                stmt.setInt(3, usuarioId);
                stmt.executeUpdate();

                ResultSet generatedKeys = stmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    facturaId = generatedKeys.getInt(1);
                } else {
                    throw new SQLException("No se pudo obtener el ID de la factura");
                }
            }

            // Insertar detalles y actualizar stock
            String sqlDetalle = "INSERT INTO DetallesDeFacturas (factura_id, producto_id, cantidad, precio_unitario, sub_total) VALUES (?, ?, ?, ?, ?)";
            String sqlStock = "UPDATE InventarioVendedor SET cantidad_disponible = cantidad_disponible - ? WHERE producto_id = ?";

            for (VentaDetalle detalle : detalles) {
                // Insertar detalle
                try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle)) {
                    stmtDetalle.setInt(1, facturaId);
                    stmtDetalle.setInt(2, detalle.getProductoId());
                    stmtDetalle.setDouble(3, detalle.getCantidad());
                    stmtDetalle.setBigDecimal(4, detalle.getPrecioUnitario());
                    stmtDetalle.setBigDecimal(5, detalle.getSubTotal());
                    stmtDetalle.executeUpdate();
                }

                // Actualizar stock
                try (PreparedStatement stmtStock = conn.prepareStatement(sqlStock)) {
                    stmtStock.setDouble(1, detalle.getCantidad());
                    stmtStock.setInt(2, detalle.getProductoId());
                    stmtStock.executeUpdate();
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Eliminar venta (anulación)
     */
    public boolean anularVenta(int facturaId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Obtener detalles para restaurar stock
            String sqlSelectDetalles = "SELECT producto_id, cantidad FROM DetallesDeFacturas WHERE factura_id = ? AND activo = true";
            List<Object[]> detalles = new ArrayList<>();

            try (PreparedStatement stmt = conn.prepareStatement(sqlSelectDetalles)) {
                stmt.setInt(1, facturaId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Object[] detalle = {
                            rs.getInt("producto_id"),
                            rs.getDouble("cantidad")
                    };
                    detalles.add(detalle);
                }
            }

            // Restaurar stock
            String sqlRestoreStock = "UPDATE InventarioVendedor SET cantidad_disponible = cantidad_disponible + ? WHERE producto_id = ?";
            for (Object[] detalle : detalles) {
                try (PreparedStatement stmt = conn.prepareStatement(sqlRestoreStock)) {
                    stmt.setDouble(1, (Double) detalle[1]);
                    stmt.setInt(2, (Integer) detalle[0]);
                    stmt.executeUpdate();
                }
            }

            // Anular factura y detalles
            String sqlAnularFactura = "UPDATE Facturas SET activo = false WHERE factura_id = ?";
            String sqlAnularDetalles = "UPDATE DetallesDeFacturas SET activo = false WHERE factura_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sqlAnularDetalles)) {
                stmt.setInt(1, facturaId);
                stmt.executeUpdate();
            }

            try (PreparedStatement stmt = conn.prepareStatement(sqlAnularFactura)) {
                stmt.setInt(1, facturaId);
                stmt.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Obtener estadísticas del día
     */
    public Object[] getEstadisticasDelDia() {
        String sql = "SELECT " +
                "COUNT(*) as total_ventas, " +
                "COALESCE(SUM(total), 0) as total_ingresos, " +
                "COALESCE(AVG(total), 0) as promedio_venta " +
                "FROM Facturas " +
                "WHERE DATE(fecha_factura) = CURDATE() AND activo = true";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return new Object[]{
                        rs.getInt("total_ventas"),
                        rs.getBigDecimal("total_ingresos"),
                        rs.getBigDecimal("promedio_venta")
                };
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Object[]{0, BigDecimal.ZERO, BigDecimal.ZERO};
    }
}