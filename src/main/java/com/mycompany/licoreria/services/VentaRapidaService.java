package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.VentaDetalleDAO;
import com.mycompany.licoreria.models.VentaDetalle;
import com.mycompany.licoreria.models.Producto;
import java.math.BigDecimal;
import java.util.List;

public class VentaRapidaService {
    private VentaDetalleDAO ventaDetalleDAO;

    public VentaRapidaService() {
        this.ventaDetalleDAO = new VentaDetalleDAO();
    }

    /**
     * Obtener productos para venta rápida
     */
    public List<Producto> getProductosParaVentaRapida() {
        return ventaDetalleDAO.getProductosParaVentaRapida();
    }

    /**
     * Buscar productos para venta rápida
     */
    public List<Producto> buscarProductosVentaRapida(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getProductosParaVentaRapida();
        }
        return ventaDetalleDAO.buscarProductosVentaRapida(searchTerm.trim());
    }

    /**
     * Procesar venta rápida
     */
    public boolean procesarVentaRapida(String cliente, int usuarioId, List<VentaDetalle> detalles) {
        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException("El carrito de venta no puede estar vacío");
        }

        if (cliente == null || cliente.trim().isEmpty()) {
            cliente = "CLIENTE GENERAL";
        }

        // Validar stock para cada producto
        for (VentaDetalle detalle : detalles) {
            if (!detalle.tieneStockSuficiente()) {
                throw new IllegalArgumentException("Stock insuficiente para: " + detalle.getProductoNombre() +
                        ". Disponible: " + detalle.getStockDisponible());
            }
        }

        // Validar que el total sea mayor a 0
        BigDecimal total = calcularTotalVenta(detalles);
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El total de la venta debe ser mayor a 0");
        }

        return ventaDetalleDAO.procesarVentaRapida(cliente.trim(), usuarioId, detalles);
    }

    /**
     * Obtener ventas del día
     */
    public List<Object[]> getVentasDelDia() {
        return ventaDetalleDAO.getVentasDelDia();
    }

    /**
     * Obtener detalles de una venta
     */
    public List<Object[]> getDetallesVenta(int facturaId) {
        return ventaDetalleDAO.getDetallesVenta(facturaId);
    }

    /**
     * Anular venta
     */
    public boolean anularVenta(int facturaId) {
        if (facturaId <= 0) {
            throw new IllegalArgumentException("ID de factura inválido");
        }
        return ventaDetalleDAO.anularVenta(facturaId);
    }

    /**
     * Calcular total de venta
     */
    public BigDecimal calcularTotalVenta(List<VentaDetalle> detalles) {
        return detalles.stream()
                .map(VentaDetalle::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Obtener estadísticas del día
     */
    public Object[] getEstadisticasDelDia() {
        return ventaDetalleDAO.getEstadisticasDelDia();
    }

    /**
     * Formatear estadísticas del día
     */
    public String getEstadisticasDelDiaFormateadas() {
        Object[] stats = getEstadisticasDelDia();
        int totalVentas = (Integer) stats[0];
        BigDecimal totalIngresos = (BigDecimal) stats[1];
        BigDecimal promedioVenta = (BigDecimal) stats[2];

        return String.format("Ventas Hoy: %d | Ingresos: $%,.2f | Promedio: $%,.2f",
                totalVentas, totalIngresos, promedioVenta);
    }

    /**
     * Validar producto para venta
     */
    public boolean validarProductoParaVenta(int productoId, double cantidad) {
        List<Producto> productos = getProductosParaVentaRapida();
        return productos.stream()
                .filter(p -> p.getProductoId() == productoId)
                .anyMatch(p -> p.getStockVendedor() >= cantidad);
    }

    /**
     * Crear detalle de venta desde producto
     */
    public VentaDetalle crearDetalleVenta(Producto producto, double cantidad) {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a 0");
        }

        if (cantidad > producto.getStockVendedor()) {
            throw new IllegalArgumentException("Stock insuficiente. Disponible: " + producto.getStockVendedor());
        }

        return new VentaDetalle(
                producto.getProductoId(),
                producto.getNombre(),
                cantidad,
                producto.getPrecio(),
                producto.getUnidadMedida(),
                producto.getStockVendedor()
        );
    }
}