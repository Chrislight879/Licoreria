package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.services.BodegaService;
import java.util.List;

public class BodegaController {
    private BodegaService bodegaService;

    public BodegaController() {
        this.bodegaService = new BodegaService();
    }

    /**
     * Obtener todos los productos
     */
    public List<Producto> getAllProductos() {
        try {
            return bodegaService.getAllProductos();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> getProductosStockBajo() {
        try {
            return bodegaService.getProductosStockBajo();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos con stock bajo: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar productos
     */
    public List<Producto> searchProductos(String searchTerm) {
        try {
            return bodegaService.searchProductos(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener productos para reabastecer
     */
    public List<Producto> getProductosParaReabastecer() {
        try {
            return bodegaService.getProductosParaReabastecer();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos para reabastecer: " + e.getMessage(), e);
        }
    }

    /**
     * Actualizar stock en bodega
     */
    public boolean actualizarStockBodega(int productoId, double nuevaCantidad) {
        try {
            return bodegaService.actualizarStockBodega(productoId, nuevaCantidad);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar stock: " + e.getMessage(), e);
        }
    }

    /**
     * Crear solicitud de compra a proveedor
     */
    public boolean crearSolicitudCompra(int productoId, double cantidadSolicitada, String observaciones) {
        try {
            return bodegaService.crearSolicitudCompra(productoId, cantidadSolicitada, observaciones);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al crear solicitud de compra: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener peticiones pendientes de vendedores
     */
    public List<PeticionStock> getPeticionesPendientes() {
        try {
            return bodegaService.getPeticionesPendientes();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones pendientes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener peticiones aprobadas para despacho
     */
    public List<PeticionStock> getPeticionesAprobadas() {
        try {
            return bodegaService.getPeticionesAprobadas();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones aprobadas: " + e.getMessage(), e);
        }
    }

    /**
     * Despachar petición a vendedor
     */
    public boolean despacharPeticion(int peticionId) {
        try {
            return bodegaService.despacharPeticion(peticionId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al despachar petición: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas de bodega
     */
    public String getEstadisticasBodega() {
        try {
            return bodegaService.getEstadisticasBodega();
        } catch (Exception e) {
            return "Error al calcular estadísticas: " + e.getMessage();
        }
    }

    /**
     * Obtener alertas de stock crítico
     */
    public List<Producto> getAlertasStockCritico() {
        try {
            List<Producto> productos = bodegaService.getAllProductos();
            return productos.stream()
                    .filter(p -> p.getStockBodega() <= p.getCantidadMinimaBodega() * 0.5)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener alertas de stock crítico: " + e.getMessage(), e);
        }
    }
}