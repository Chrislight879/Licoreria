package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.VentaDetalle;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.services.VentaRapidaService;
import java.math.BigDecimal;
import java.util.List;

public class VentaRapidaController {
    private VentaRapidaService ventaRapidaService;

    public VentaRapidaController() {
        this.ventaRapidaService = new VentaRapidaService();
    }

    /**
     * Obtener productos para venta - CORREGIDO
     */
    public List<Producto> getProductosParaVenta() {
        try {
            return ventaRapidaService.getProductosParaVentaRapida();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar productos - CORREGIDO
     */
    public List<Producto> buscarProductos(String searchTerm) {
        try {
            return ventaRapidaService.buscarProductosVentaRapida(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos: " + e.getMessage(), e);
        }
    }

    /**
     * Procesar venta rápida
     */
    public boolean procesarVentaRapida(String cliente, int usuarioId, List<VentaDetalle> detalles) {
        try {
            return ventaRapidaService.procesarVentaRapida(cliente, usuarioId, detalles);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar venta: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener ventas del día
     */
    public List<Object[]> getVentasDelDia() {
        try {
            return ventaRapidaService.getVentasDelDia();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener ventas del día: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener detalles de venta
     */
    public List<Object[]> getDetallesVenta(int facturaId) {
        try {
            return ventaRapidaService.getDetallesVenta(facturaId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener detalles de venta: " + e.getMessage(), e);
        }
    }

    /**
     * Anular venta
     */
    public boolean anularVenta(int facturaId) {
        try {
            return ventaRapidaService.anularVenta(facturaId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al anular venta: " + e.getMessage(), e);
        }
    }

    /**
     * Calcular total de venta
     */
    public BigDecimal calcularTotalVenta(List<VentaDetalle> detalles) {
        try {
            return ventaRapidaService.calcularTotalVenta(detalles);
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular total: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas del día
     */
    public String getEstadisticasDelDia() {
        try {
            return ventaRapidaService.getEstadisticasDelDiaFormateadas();
        } catch (Exception e) {
            return "Error al cargar estadísticas: " + e.getMessage();
        }
    }

    /**
     * Validar producto para venta
     */
    public boolean validarProductoParaVenta(int productoId, double cantidad) {
        try {
            return ventaRapidaService.validarProductoParaVenta(productoId, cantidad);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Crear detalle de venta
     */
    public VentaDetalle crearDetalleVenta(Producto producto, double cantidad) {
        try {
            return ventaRapidaService.crearDetalleVenta(producto, cantidad);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al crear detalle de venta: " + e.getMessage(), e);
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

    /**
     * Generar número de factura sugerido
     */
    public String generarNumeroFacturaSugerido() {
        try {
            List<Object[]> ventas = getVentasDelDia();
            int numero = ventas.size() + 1;
            return "F-" + String.format("%04d", numero);
        } catch (Exception e) {
            return "F-0001";
        }
    }
}