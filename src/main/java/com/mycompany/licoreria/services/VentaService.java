package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.VentaDAO;
import com.mycompany.licoreria.models.Venta;
import com.mycompany.licoreria.models.DetalleVenta;
import com.mycompany.licoreria.models.Producto;
import java.math.BigDecimal;
import java.util.List;

public class VentaService {
    private VentaDAO ventaDAO;

    public VentaService() {
        this.ventaDAO = new VentaDAO();
    }

    /**
     * Obtener productos disponibles para venta
     */
    public List<Producto> getProductosParaVenta() {
        return ventaDAO.getProductosParaVenta();
    }

    /**
     * Buscar productos
     */
    public List<Producto> buscarProductos(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getProductosParaVenta();
        }
        return ventaDAO.buscarProductos(searchTerm.trim());
    }

    /**
     * Procesar una venta
     */
    public boolean procesarVenta(Venta venta, List<DetalleVenta> detalles) {
        if (venta == null || detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("La venta y los detalles no pueden estar vacíos");
        }

        if (venta.getTotal() == null || venta.getTotal().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total de la venta debe ser mayor a 0");
        }

        // Validar stock para cada producto
        for (DetalleVenta detalle : detalles) {
            if (!validarStockSuficiente(detalle.getProductoId(), detalle.getCantidad())) {
                throw new IllegalArgumentException("Stock insuficiente para: " + detalle.getProductoNombre());
            }
        }

        // Crear venta
        int ventaId = ventaDAO.crearVenta(venta);
        if (ventaId == -1) {
            throw new RuntimeException("Error al crear la venta");
        }

        // Agregar detalles
        for (DetalleVenta detalle : detalles) {
            boolean success = ventaDAO.agregarDetalleVenta(ventaId, detalle);
            if (!success) {
                throw new RuntimeException("Error al agregar detalle de venta");
            }
        }

        return true;
    }

    /**
     * Validar stock suficiente
     */
    private boolean validarStockSuficiente(int productoId, double cantidad) {
        List<Producto> productos = ventaDAO.getProductosParaVenta();
        return productos.stream()
                .filter(p -> p.getProductoId() == productoId)
                .anyMatch(p -> p.getStockVendedor() >= cantidad);
    }

    /**
     * Crear petición de stock
     */
    public boolean crearPeticionStock(int productoId, int usuarioId, double cantidadSolicitada, String observaciones) {
        if (cantidadSolicitada <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a 0");
        }

        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        return ventaDAO.crearPeticionStock(productoId, usuarioId, cantidadSolicitada, observaciones.trim());
    }

    /**
     * Obtener historial de ventas del vendedor
     */
    public List<Venta> getHistorialVentas(int usuarioId) {
        return ventaDAO.getVentasPorVendedor(usuarioId);
    }

    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> getProductosStockBajo() {
        return ventaDAO.getProductosStockBajoVendedor();
    }

    /**
     * Obtener detalles de una venta
     */
    public List<DetalleVenta> getDetallesVenta(int ventaId) {
        return ventaDAO.getDetallesVenta(ventaId);
    }

    /**
     * Calcular total de una venta
     */
    public BigDecimal calcularTotalVenta(List<DetalleVenta> detalles) {
        return detalles.stream()
                .map(DetalleVenta::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtener estadísticas de ventas
     */
    public String getEstadisticasVentas(int usuarioId) {
        List<Venta> ventas = getHistorialVentas(usuarioId);
        int totalVentas = ventas.size();
        BigDecimal totalIngresos = ventas.stream()
                .map(Venta::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long productosStockBajo = getProductosStockBajo().size();

        return String.format("Ventas: %d | Ingresos: $%,.2f | Productos con stock bajo: %d",
                totalVentas, totalIngresos, productosStockBajo);
    }
}