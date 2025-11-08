package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.PeticionVendedor;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.services.PeticionVendedorService;
import java.util.List;

public class PeticionVendedorController {
    private PeticionVendedorService peticionVendedorService;

    public PeticionVendedorController() {
        this.peticionVendedorService = new PeticionVendedorService();
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Producto> getInventarioBodega() {
        try {
            return peticionVendedorService.getInventarioBodega();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener inventario de bodega: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener inventario del vendedor
     */
    public List<Producto> getInventarioVendedor() {
        try {
            return peticionVendedorService.getInventarioVendedor();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener inventario del vendedor: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar productos en bodega
     */
    public List<Producto> buscarProductosBodega(String searchTerm) {
        try {
            return peticionVendedorService.buscarProductosBodega(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar productos: " + e.getMessage(), e);
        }
    }

    /**
     * Crear petición de stock
     */
    public boolean crearPeticion(int productoId, int usuarioId, double cantidadSolicitada, String observaciones) {
        try {
            return peticionVendedorService.crearPeticion(productoId, usuarioId, cantidadSolicitada, observaciones);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al crear petición: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener peticiones del vendedor
     */
    public List<PeticionVendedor> getPeticionesPorVendedor(int usuarioId) {
        try {
            return peticionVendedorService.getPeticionesPorVendedor(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar peticiones
     */
    public List<PeticionVendedor> buscarPeticiones(int usuarioId, String searchTerm) {
        try {
            return peticionVendedorService.buscarPeticiones(usuarioId, searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar peticiones: " + e.getMessage(), e);
        }
    }

    /**
     * Eliminar petición
     */
    public boolean eliminarPeticion(int peticionId) {
        try {
            return peticionVendedorService.eliminarPeticion(peticionId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar petición: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> getProductosStockBajoVendedor(int usuarioId) {
        try {
            return peticionVendedorService.getProductosStockBajoVendedor(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos con stock bajo: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas
     */
    public String getEstadisticasPeticiones(int usuarioId) {
        try {
            return peticionVendedorService.getEstadisticasPeticiones(usuarioId);
        } catch (Exception e) {
            return "Error al calcular estadísticas: " + e.getMessage();
        }
    }

    /**
     * Validar petición
     */
    public boolean validarPeticionProducto(int productoId, double cantidad) {
        try {
            return peticionVendedorService.validarPeticionProducto(productoId, cantidad);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtener producto por ID
     */
    public Producto getProductoPorId(int productoId) {
        try {
            return peticionVendedorService.getProductoPorId(productoId);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Obtener peticiones pendientes
     */
    public List<PeticionVendedor> getPeticionesPendientes(int usuarioId) {
        try {
            List<PeticionVendedor> peticiones = getPeticionesPorVendedor(usuarioId);
            return peticiones.stream()
                    .filter(p -> "pendiente".equals(p.getEstado()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones pendientes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener peticiones aprobadas
     */
    public List<PeticionVendedor> getPeticionesAprobadas(int usuarioId) {
        try {
            List<PeticionVendedor> peticiones = getPeticionesPorVendedor(usuarioId);
            return peticiones.stream()
                    .filter(p -> "aprobada".equals(p.getEstado()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones aprobadas: " + e.getMessage(), e);
        }
    }
}