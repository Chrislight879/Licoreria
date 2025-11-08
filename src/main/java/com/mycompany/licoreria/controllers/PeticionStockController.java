package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.services.PeticionStockService;
import java.util.List;

public class PeticionStockController {
    private PeticionStockService peticionStockService;

    public PeticionStockController() {
        this.peticionStockService = new PeticionStockService();
    }

    /**
     * Obtener todas las peticiones
     */
    public List<PeticionStock> getAllPeticiones() {
        try {
            return peticionStockService.getAllPeticiones();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener peticiones por estado
     */
    public List<PeticionStock> getPeticionesByEstado(String estado) {
        try {
            return peticionStockService.getPeticionesByEstado(estado);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones por estado: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar peticiones
     */
    public List<PeticionStock> searchPeticiones(String searchTerm) {
        try {
            return peticionStockService.searchPeticiones(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar peticiones: " + e.getMessage(), e);
        }
    }

    /**
     * Aprobar petición
     */
    public boolean aprobarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        try {
            return peticionStockService.aprobarPeticion(peticionId, usuarioAprobadorId, observaciones);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al aprobar petición: " + e.getMessage(), e);
        }
    }

    /**
     * Rechazar petición
     */
    public boolean rechazarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        try {
            return peticionStockService.rechazarPeticion(peticionId, usuarioAprobadorId, observaciones);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al rechazar petición: " + e.getMessage(), e);
        }
    }

    /**
     * Despachar petición
     */
    public boolean despacharPeticion(int peticionId) {
        try {
            return peticionStockService.despacharPeticion(peticionId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al despachar petición: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Object[]> getInventarioBodega() {
        try {
            return peticionStockService.getInventarioBodega();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener inventario de bodega: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas
     */
    public String getEstadisticasPeticiones() {
        try {
            return peticionStockService.getEstadisticasPeticiones();
        } catch (Exception e) {
            return "Error al calcular estadísticas: " + e.getMessage();
        }
    }

    /**
     * Obtener peticiones pendientes (para tabla de espera)
     */
    public List<PeticionStock> getPeticionesPendientes() {
        return getPeticionesByEstado("pendiente");
    }

    /**
     * Obtener peticiones aprobadas (para tabla de aprobadas)
     */
    public List<PeticionStock> getPeticionesAprobadas() {
        return getPeticionesByEstado("aprobada");
    }
}