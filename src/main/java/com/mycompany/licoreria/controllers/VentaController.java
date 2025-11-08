package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.Venta;
import com.mycompany.licoreria.models.DetalleVenta;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.services.VentaService;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class VentaController {
    private VentaService ventaService;

    public VentaController() {
        this.ventaService = new VentaService();
    }

    /**
     * Obtener productos para venta
     */
    public List<Producto> getProductosParaVenta() {
        try {
            return ventaService.getProductosParaVenta();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar productos
     */
    public List<Producto> buscarProductos(String searchTerm) {
        try {
            return ventaService.buscarProductos(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos: " + e.getMessage(), e);
        }
    }

    /**
     * Procesar venta
     */
    public boolean procesarVenta(String cliente, int usuarioId, List<DetalleVenta> detalles) {
        try {
            BigDecimal total = ventaService.calcularTotalVenta(detalles);

            Venta venta = new Venta();
            venta.setCliente(cliente != null ? cliente.trim() : "CLIENTE GENERAL");
            venta.setUsuarioId(usuarioId);
            venta.setTotal(total);
            venta.setFechaVenta(new Timestamp(System.currentTimeMillis()));

            return ventaService.procesarVenta(venta, detalles);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar venta: " + e.getMessage(), e);
        }
    }

    /**
     * Crear petición de stock
     */
    public boolean crearPeticionStock(int productoId, int usuarioId, double cantidadSolicitada, String observaciones) {
        try {
            return ventaService.crearPeticionStock(productoId, usuarioId, cantidadSolicitada, observaciones);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al crear petición: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener historial de ventas
     */
    public List<Venta> getHistorialVentas(int usuarioId) {
        try {
            return ventaService.getHistorialVentas(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener historial: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> getProductosStockBajo() {
        try {
            return ventaService.getProductosStockBajo();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos con stock bajo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener detalles de venta
     */
    public List<DetalleVenta> getDetallesVenta(int ventaId) {
        try {
            return ventaService.getDetallesVenta(ventaId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener detalles de venta: " + e.getMessage(), e);
        }
    }

    /**
     * Calcular total de venta
     */
    public BigDecimal calcularTotal(List<DetalleVenta> detalles) {
        try {
            return ventaService.calcularTotalVenta(detalles);
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular total: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas
     */
    public String getEstadisticas(int usuarioId) {
        try {
            return ventaService.getEstadisticasVentas(usuarioId);
        } catch (Exception e) {
            return "Error al calcular estadísticas: " + e.getMessage();
        }
    }

    /**
     * Validar stock disponible
     */
    public boolean validarStockDisponible(int productoId, double cantidad) {
        try {
            List<Producto> productos = getProductosParaVenta();
            return productos.stream()
                    .filter(p -> p.getProductoId() == productoId)
                    .anyMatch(p -> p.getStockVendedor() >= cantidad);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtener producto por ID
     */
    public Producto getProductoPorId(int productoId) {
        try {
            List<Producto> productos = getProductosParaVenta();
            return productos.stream()
                    .filter(p -> p.getProductoId() == productoId)
                    .findFirst()
                    .orElse(null);
        } catch (Exception e) {
            return null;
        }
    }
}