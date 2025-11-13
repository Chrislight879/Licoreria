package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.PeticionVendedorDAO;
import com.mycompany.licoreria.models.PeticionVendedor;
import com.mycompany.licoreria.models.Producto;
import java.util.List;

public class PeticionVendedorService {
    private PeticionVendedorDAO peticionVendedorDAO;

    public PeticionVendedorService() {
        this.peticionVendedorDAO = new PeticionVendedorDAO();
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Producto> getInventarioBodega() {
        return peticionVendedorDAO.getInventarioBodega();
    }

    /**
     * Obtener inventario del vendedor
     */
    public List<Producto> getInventarioVendedor() {
        return peticionVendedorDAO.getInventarioVendedor();
    }

    /**
     * Buscar productos en bodega
     */
    public List<Producto> buscarProductosBodega(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getInventarioBodega();
        }
        return peticionVendedorDAO.buscarProductosBodega(searchTerm.trim());
    }

    /**
     * Crear petición de stock
     */
    public boolean crearPeticion(int productoId, int usuarioId, double cantidadSolicitada, String observaciones) {
        if (cantidadSolicitada <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a 0");
        }

        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        // Verificar que el producto existe en bodega
        List<Producto> productosBodega = getInventarioBodega();
        boolean productoExiste = productosBodega.stream()
                .anyMatch(p -> p.getProductoId() == productoId);

        if (!productoExiste) {
            throw new IllegalArgumentException("El producto no existe en bodega");
        }

        // Verificar stock disponible en bodega
        Producto producto = productosBodega.stream()
                .filter(p -> p.getProductoId() == productoId)
                .findFirst()
                .orElse(null);

        if (producto != null && cantidadSolicitada > producto.getStockBodega()) {
            throw new IllegalArgumentException("Stock insuficiente en bodega. Disponible: " +
                    producto.getStockBodega() + ", Solicitado: " + cantidadSolicitada);
        }

        PeticionVendedor peticion = new PeticionVendedor(productoId, usuarioId, cantidadSolicitada, observaciones);
        return peticionVendedorDAO.crearPeticion(peticion);
    }

    /**
     * Obtener peticiones del vendedor
     */
    public List<PeticionVendedor> getPeticionesPorVendedor(int usuarioId) {
        return peticionVendedorDAO.getPeticionesPorVendedor(usuarioId);
    }

    /**
     * Buscar peticiones
     */
    public List<PeticionVendedor> buscarPeticiones(int usuarioId, String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getPeticionesPorVendedor(usuarioId);
        }
        return peticionVendedorDAO.buscarPeticiones(usuarioId, searchTerm.trim());
    }

    /**
     * Eliminar petición - CORREGIDO (sin usuario hardcodeado)
     */
    public boolean eliminarPeticion(int peticionId, int usuarioId) {
        // Verificar que la petición existe y está pendiente
        List<PeticionVendedor> peticiones = getPeticionesPorVendedor(usuarioId);
        boolean peticionValida = peticiones.stream()
                .anyMatch(p -> p.getPeticionId() == peticionId && "pendiente".equals(p.getEstado()));

        if (!peticionValida) {
            throw new IllegalArgumentException("La petición no existe o no se puede eliminar (ya fue procesada)");
        }

        return peticionVendedorDAO.eliminarPeticion(peticionId);
    }

    /**
     * Obtener productos con stock bajo para el vendedor
     */
    public List<Producto> getProductosStockBajoVendedor(int usuarioId) {
        return peticionVendedorDAO.getProductosStockBajoVendedor(usuarioId);
    }

    /**
     * Obtener estadísticas de peticiones
     */
    public String getEstadisticasPeticiones(int usuarioId) {
        List<PeticionVendedor> peticiones = getPeticionesPorVendedor(usuarioId);
        int totalPeticiones = peticiones.size();
        long pendientes = peticiones.stream().filter(p -> "pendiente".equals(p.getEstado())).count();
        long aprobadas = peticiones.stream().filter(p -> "aprobada".equals(p.getEstado())).count();
        long rechazadas = peticiones.stream().filter(p -> "rechazada".equals(p.getEstado())).count();
        long despachadas = peticiones.stream().filter(p -> "despachada".equals(p.getEstado())).count();

        return String.format("Total: %d | Pendientes: %d | Aprobadas: %d | Rechazadas: %d | Despachadas: %d",
                totalPeticiones, pendientes, aprobadas, rechazadas, despachadas);
    }

    /**
     * Validar si se puede crear petición para un producto
     */
    public boolean validarPeticionProducto(int productoId, double cantidad) {
        List<Producto> productosBodega = getInventarioBodega();
        return productosBodega.stream()
                .filter(p -> p.getProductoId() == productoId)
                .anyMatch(p -> p.getStockBodega() >= cantidad);
    }

    /**
     * Obtener producto por ID
     */
    public Producto getProductoPorId(int productoId) {
        List<Producto> productos = getInventarioBodega();
        return productos.stream()
                .filter(p -> p.getProductoId() == productoId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtener petición por ID
     */
    public PeticionVendedor getPeticionPorId(int peticionId, int usuarioId) {
        List<PeticionVendedor> peticiones = getPeticionesPorVendedor(usuarioId);
        return peticiones.stream()
                .filter(p -> p.getPeticionId() == peticionId)
                .findFirst()
                .orElse(null);
    }

    public boolean eliminarPeticion(int peticionId) {
        return false;
    }
}