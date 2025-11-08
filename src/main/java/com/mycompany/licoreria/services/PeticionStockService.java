package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.PeticionStockDAO;
import com.mycompany.licoreria.models.PeticionStock;
import java.util.List;

public class PeticionStockService {
    private PeticionStockDAO peticionStockDAO;

    public PeticionStockService() {
        this.peticionStockDAO = new PeticionStockDAO();
    }

    /**
     * Obtener todas las peticiones
     */
    public List<PeticionStock> getAllPeticiones() {
        return peticionStockDAO.getAllPeticiones();
    }

    /**
     * Obtener peticiones por estado
     */
    public List<PeticionStock> getPeticionesByEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            throw new IllegalArgumentException("El estado no puede estar vacío");
        }
        return peticionStockDAO.getPeticionesByEstado(estado.trim());
    }

    /**
     * Buscar peticiones
     */
    public List<PeticionStock> searchPeticiones(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllPeticiones();
        }
        return peticionStockDAO.searchPeticiones(searchTerm.trim());
    }

    /**
     * Aprobar petición
     */
    public boolean aprobarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        // Verificar que la petición existe y está pendiente
        List<PeticionStock> peticiones = peticionStockDAO.getPeticionesByEstado("pendiente");
        boolean peticionValida = peticiones.stream()
                .anyMatch(p -> p.getPeticionId() == peticionId);

        if (!peticionValida) {
            throw new IllegalArgumentException("La petición no existe o no está pendiente");
        }

        return peticionStockDAO.aprobarPeticion(peticionId, usuarioAprobadorId, observaciones);
    }

    /**
     * Rechazar petición
     */
    public boolean rechazarPeticion(int peticionId, int usuarioAprobadorId, String observaciones) {
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        // Verificar que la petición existe y está pendiente
        List<PeticionStock> peticiones = peticionStockDAO.getPeticionesByEstado("pendiente");
        boolean peticionValida = peticiones.stream()
                .anyMatch(p -> p.getPeticionId() == peticionId);

        if (!peticionValida) {
            throw new IllegalArgumentException("La petición no existe o no está pendiente");
        }

        return peticionStockDAO.rechazarPeticion(peticionId, usuarioAprobadorId, observaciones);
    }

    /**
     * Despachar petición (completar el proceso)
     */
    public boolean despacharPeticion(int peticionId) {
        // Obtener información de la petición
        List<PeticionStock> peticionesAprobadas = peticionStockDAO.getPeticionesByEstado("aprobada");
        PeticionStock peticion = peticionesAprobadas.stream()
                .filter(p -> p.getPeticionId() == peticionId)
                .findFirst()
                .orElse(null);

        if (peticion == null) {
            throw new IllegalArgumentException("La petición no existe o no está aprobada");
        }

        // Verificar stock disponible
        if (peticion.getStockBodega() < peticion.getCantidadSolicitada()) {
            throw new IllegalArgumentException("Stock insuficiente en bodega. Disponible: " +
                    peticion.getStockBodega() + ", Solicitado: " + peticion.getCantidadSolicitada());
        }

        // Actualizar stocks
        boolean stockBodegaActualizado = peticionStockDAO.actualizarStockBodega(
                peticion.getProductoId(), peticion.getCantidadSolicitada());

        boolean stockVendedorActualizado = peticionStockDAO.actualizarStockVendedor(
                peticion.getProductoId(), peticion.getCantidadSolicitada());

        if (stockBodegaActualizado && stockVendedorActualizado) {
            return peticionStockDAO.despacharPeticion(peticionId);
        } else {
            throw new RuntimeException("Error al actualizar los stocks");
        }
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Object[]> getInventarioBodega() {
        return peticionStockDAO.getInventarioBodega();
    }

    /**
     * Obtener estadísticas de peticiones
     */
    public String getEstadisticasPeticiones() {
        List<PeticionStock> todas = getAllPeticiones();
        long pendientes = todas.stream().filter(p -> "pendiente".equals(p.getEstado())).count();
        long aprobadas = todas.stream().filter(p -> "aprobada".equals(p.getEstado())).count();
        long rechazadas = todas.stream().filter(p -> "rechazada".equals(p.getEstado())).count();
        long despachadas = todas.stream().filter(p -> "despachada".equals(p.getEstado())).count();

        return String.format("Total: %d | Pendientes: %d | Aprobadas: %d | Rechazadas: %d | Despachadas: %d",
                todas.size(), pendientes, aprobadas, rechazadas, despachadas);
    }
}