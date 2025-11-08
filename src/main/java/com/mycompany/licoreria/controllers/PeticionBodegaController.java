package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.services.PeticionBodegaService;
import java.util.List;

public class PeticionBodegaController {
    private PeticionBodegaService peticionBodegaService;

    public PeticionBodegaController() {
        this.peticionBodegaService = new PeticionBodegaService();
    }

    /**
     * Obtener peticiones pendientes
     */
    public List<PeticionStock> getPeticionesPendientes() {
        try {
            return peticionBodegaService.getPeticionesPendientes();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones pendientes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener peticiones aceptadas
     */
    public List<PeticionStock> getPeticionesAceptadas() {
        try {
            return peticionBodegaService.getPeticionesAceptadas();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones aceptadas: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Producto> getInventarioBodegaCompleto() {
        try {
            return peticionBodegaService.getInventarioBodegaCompleto();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener inventario de bodega: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar peticiones pendientes
     */
    public List<PeticionStock> searchPeticionesPendientes(String searchTerm) {
        try {
            return peticionBodegaService.searchPeticionesPendientes(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar peticiones pendientes: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar peticiones aceptadas
     */
    public List<PeticionStock> searchPeticionesAceptadas(String searchTerm) {
        try {
            return peticionBodegaService.searchPeticionesAceptadas(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar peticiones aceptadas: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar en inventario de bodega
     */
    public List<Producto> searchInventarioBodega(String searchTerm) {
        try {
            return peticionBodegaService.searchInventarioBodega(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar en inventario: " + e.getMessage(), e);
        }
    }

    /**
     * Aprobar petición
     */
    public boolean aprobarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        try {
            return peticionBodegaService.aprobarPeticion(peticionId, usuarioAprobadorId, observaciones);
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
            return peticionBodegaService.rechazarPeticion(peticionId, usuarioAprobadorId, observaciones);
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
            return peticionBodegaService.despacharPeticion(peticionId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al despachar petición: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas de peticiones
     */
    public String getEstadisticasPeticiones() {
        try {
            return peticionBodegaService.getEstadisticasPeticiones();
        } catch (Exception e) {
            return "Error al cargar estadísticas: " + e.getMessage();
        }
    }

    /**
     * Obtener peticiones críticas
     */
    public List<PeticionStock> getPeticionesCriticas() {
        try {
            return peticionBodegaService.getPeticionesCriticas();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones críticas: " + e.getMessage(), e);
        }
    }

    /**
     * Verificar si hay peticiones urgentes
     */
    public boolean hayPeticionesUrgentes() {
        try {
            return peticionBodegaService.hayPeticionesUrgentes();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtener productos con múltiples peticiones
     */
    public List<Producto> getProductosConMultiplesPeticiones() {
        try {
            return peticionBodegaService.getProductosConMultiplesPeticiones();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos con múltiples peticiones: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener peticiones por producto
     */
    public List<PeticionStock> getPeticionesPorProducto(int productoId) {
        try {
            List<PeticionStock> peticionesPendientes = getPeticionesPendientes();
            return peticionesPendientes.stream()
                    .filter(p -> p.getProductoId() == productoId)
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener peticiones por producto: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener resumen de peticiones
     */
    public String getResumenPeticiones() {
        try {
            List<PeticionStock> pendientes = getPeticionesPendientes();
            List<PeticionStock> criticas = getPeticionesCriticas();

            return String.format("Pendientes: %d | Críticas: %d | Urgentes: %s",
                    pendientes.size(), criticas.size(), hayPeticionesUrgentes() ? "SÍ" : "NO");
        } catch (Exception e) {
            return "Error al generar resumen";
        }
    }
}