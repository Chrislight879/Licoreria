package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.PeticionBodegaDAO;
import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.models.Producto;
import java.util.List;

public class PeticionBodegaService {
    private PeticionBodegaDAO peticionBodegaDAO;

    public PeticionBodegaService() {
        this.peticionBodegaDAO = new PeticionBodegaDAO();
    }

    /**
     * Obtener peticiones pendientes
     */
    public List<PeticionStock> getPeticionesPendientes() {
        return peticionBodegaDAO.getPeticionesPendientes();
    }

    /**
     * Obtener peticiones aceptadas/despachadas
     */
    public List<PeticionStock> getPeticionesAceptadas() {
        return peticionBodegaDAO.getPeticionesAceptadas();
    }

    /**
     * Obtener inventario de bodega completo
     */
    public List<Producto> getInventarioBodegaCompleto() {
        return peticionBodegaDAO.getInventarioBodegaCompleto();
    }

    /**
     * Buscar peticiones pendientes
     */
    public List<PeticionStock> searchPeticionesPendientes(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getPeticionesPendientes();
        }
        return peticionBodegaDAO.searchPeticionesPendientes(searchTerm.trim());
    }

    /**
     * Buscar peticiones aceptadas
     */
    public List<PeticionStock> searchPeticionesAceptadas(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getPeticionesAceptadas();
        }
        return peticionBodegaDAO.searchPeticionesAceptadas(searchTerm.trim());
    }

    /**
     * Buscar en inventario de bodega
     */
    public List<Producto> searchInventarioBodega(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getInventarioBodegaCompleto();
        }
        return peticionBodegaDAO.searchInventarioBodega(searchTerm.trim());
    }

    /**
     * Aprobar petición
     */
    public boolean aprobarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        // Verificar que la petición existe y está pendiente
        List<PeticionStock> peticionesPendientes = getPeticionesPendientes();
        PeticionStock peticion = peticionesPendientes.stream()
                .filter(p -> p.getPeticionId() == peticionId)
                .findFirst()
                .orElse(null);

        if (peticion == null) {
            throw new IllegalArgumentException("La petición no existe o no está pendiente");
        }

        // Verificar stock disponible
        if (peticion.getStockBodega() < peticion.getCantidadSolicitada()) {
            throw new IllegalArgumentException("Stock insuficiente en bodega. Disponible: " +
                    peticion.getStockBodega() + ", Solicitado: " + peticion.getCantidadSolicitada());
        }

        return peticionBodegaDAO.aprobarPeticion(peticionId, usuarioAprobadorId, observaciones);
    }

    /**
     * Rechazar petición
     */
    public boolean rechazarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        // Verificar que la petición existe y está pendiente
        List<PeticionStock> peticionesPendientes = getPeticionesPendientes();
        boolean peticionValida = peticionesPendientes.stream()
                .anyMatch(p -> p.getPeticionId() == peticionId);

        if (!peticionValida) {
            throw new IllegalArgumentException("La petición no existe o no está pendiente");
        }

        return peticionBodegaDAO.rechazarPeticion(peticionId, usuarioAprobadorId, observaciones);
    }

    /**
     * Despachar petición
     */
    public boolean despacharPeticion(int peticionId) {
        // Verificar que la petición existe y está aprobada
        List<PeticionStock> peticionesAprobadas = getPeticionesAceptadas();
        PeticionStock peticion = peticionesAprobadas.stream()
                .filter(p -> p.getPeticionId() == peticionId && "aprobada".equals(p.getEstado()))
                .findFirst()
                .orElse(null);

        if (peticion == null) {
            throw new IllegalArgumentException("La petición no existe o no está aprobada");
        }

        return peticionBodegaDAO.despacharPeticion(peticionId);
    }

    /**
     * Obtener estadísticas de peticiones
     */
    public String getEstadisticasPeticiones() {
        return peticionBodegaDAO.getEstadisticasPeticiones();
    }

    /**
     * Obtener peticiones críticas (stock bajo)
     */
    public List<PeticionStock> getPeticionesCriticas() {
        List<PeticionStock> peticionesPendientes = getPeticionesPendientes();
        return peticionesPendientes.stream()
                .filter(p -> p.getStockBodega() < p.getCantidadSolicitada())
                .toList();
    }

    /**
     * Verificar si hay peticiones urgentes
     */
    public boolean hayPeticionesUrgentes() {
        return !getPeticionesCriticas().isEmpty();
    }

    /**
     * Obtener productos con múltiples peticiones pendientes
     */
    public List<Producto> getProductosConMultiplesPeticiones() {
        List<Producto> inventario = getInventarioBodegaCompleto();
        return inventario.stream()
                .filter(p -> {
                    // Buscar peticiones pendientes para este producto
                    List<PeticionStock> peticionesProducto = getPeticionesPendientes().stream()
                            .filter(ps -> ps.getProductoId() == p.getProductoId())
                            .toList();
                    return peticionesProducto.size() > 1;
                })
                .toList();
    }
}