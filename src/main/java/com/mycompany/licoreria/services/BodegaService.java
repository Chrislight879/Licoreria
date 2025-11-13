package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.ProductoDAO;
import com.mycompany.licoreria.dao.PeticionStockDAO;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.PeticionStock;
import java.util.List;

public class BodegaService {
    private ProductoDAO productoDAO;
    private PeticionStockDAO peticionStockDAO;

    public BodegaService() {
        this.productoDAO = new ProductoDAO();
        this.peticionStockDAO = new PeticionStockDAO();
    }

    /**
     * Obtener todos los productos
     */
    public List<Producto> getAllProductos() {
        return productoDAO.getAllProductos();
    }

    /**
     * Obtener productos con stock bajo
     */
    public List<Producto> getProductosStockBajo() {
        return productoDAO.getProductosStockBajoBodega();
    }

    /**
     * Buscar productos
     */
    public List<Producto> searchProductos(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getAllProductos();
        }
        return productoDAO.searchProductos(searchTerm.trim());
    }

    /**
     * Obtener productos para reabastecer
     */
    public List<Producto> getProductosParaReabastecer() {
        return productoDAO.getProductosParaReabastecer();
    }

    /**
     * Actualizar stock en bodega
     */
    public boolean actualizarStockBodega(int productoId, double nuevaCantidad) {
        if (nuevaCantidad < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        return productoDAO.actualizarStockBodega(productoId, nuevaCantidad);
    }

    /**
     * Crear solicitud de compra a proveedor
     */
    public boolean crearSolicitudCompra(int productoId, double cantidadSolicitada, String observaciones) {
        if (cantidadSolicitada <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a 0");
        }
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        return productoDAO.crearSolicitudCompra(productoId, cantidadSolicitada, observaciones.trim());
    }

    /**
     * Obtener peticiones de vendedores pendientes
     */
    public List<PeticionStock> getPeticionesPendientes() {
        return peticionStockDAO.getPeticionesByEstado("pendiente");
    }

    /**
     * Obtener peticiones aprobadas para despacho
     */
    public List<PeticionStock> getPeticionesAprobadas() {
        return peticionStockDAO.getPeticionesByEstado("aprobada");
    }

    /**
     * Despachar petición a vendedor - USANDO MÉTODO TRANSACCIONAL
     */
    public boolean despacharPeticion(int peticionId) {
        // Verificar que la petición existe y está aprobada
        PeticionStock peticion = peticionStockDAO.getPeticionById(peticionId);

        if (peticion == null) {
            throw new IllegalArgumentException("La petición no existe");
        }

        if (!"aprobada".equals(peticion.getEstado())) {
            throw new IllegalArgumentException("La petición no está aprobada. Estado actual: " + peticion.getEstado());
        }

        // Verificar stock en bodega
        if (peticion.getStockBodega() < peticion.getCantidadSolicitada()) {
            throw new IllegalArgumentException("Stock insuficiente en bodega. Disponible: " +
                    peticion.getStockBodega() + ", Solicitado: " + peticion.getCantidadSolicitada());
        }

        // Usar el método transaccional del DAO
        return peticionStockDAO.despacharPeticionCompleta(peticionId);
    }

    /**
     * Obtener estadísticas de bodega
     */
    public String getEstadisticasBodega() {
        List<Producto> productos = getAllProductos();
        if (productos.isEmpty()) {
            return "No hay productos en bodega";
        }

        long totalProductos = productos.size();
        long stockBajo = productos.stream()
                .filter(p -> p.getStockBodega() <= p.getCantidadMinimaBodega())
                .count();
        long sinStock = productos.stream()
                .filter(p -> p.getStockBodega() == 0)
                .count();

        double valorTotalInventario = productos.stream()
                .mapToDouble(p -> p.getStockBodega() * (p.getCosto() != null ? p.getCosto().doubleValue() : 0))
                .sum();

        return String.format("Productos: %d | Stock Bajo: %d | Sin Stock: %d | Valor Inventario: $%,.2f",
                totalProductos, stockBajo, sinStock, valorTotalInventario);
    }

    /**
     * Obtener petición por ID
     */
    public PeticionStock getPeticionById(int peticionId) {
        return peticionStockDAO.getPeticionById(peticionId);
    }

    /**
     * NUEVO MÉTODO: Crear petición de stock desde bodega
     */
    public boolean crearPeticionStockBodega(int productoId, int usuarioId, double cantidadSolicitada, String observaciones) {
        try {
            // Crear petición de stock especial para bodega
            String observacionesCompletas = "🚚 " + observaciones + " [SOLICITUD BODEGA]";

            // Usar el DAO de peticiones de stock para crear la petición
            return peticionStockDAO.crearPeticionBodega(productoId, usuarioId, cantidadSolicitada, observacionesCompletas);
        } catch (Exception e) {
            System.err.println("Error al crear petición de stock desde bodega: " + e.getMessage());
            return false;
        }
    }

    /**
     * NUEVO MÉTODO: Aprobar petición de bodega y actualizar stock
     */
    public boolean aprobarPeticionBodega(int peticionId, int usuarioAprobadorId) {
        try {
            // Obtener la petición
            PeticionStock peticion = peticionStockDAO.getPeticionById(peticionId);

            if (peticion == null) {
                throw new IllegalArgumentException("La petición no existe");
            }

            if (!"pendiente".equals(peticion.getEstado())) {
                throw new IllegalArgumentException("La petición no está pendiente");
            }

            // Aprobar la petición
            boolean aprobada = peticionStockDAO.aprobarPeticion(peticionId, usuarioAprobadorId, "Aprobada solicitud de bodega");

            if (aprobada) {
                // Actualizar el stock en bodega (SUMAR la cantidad)
                boolean stockActualizado = productoDAO.actualizarStockBodegaSumar(
                        peticion.getProductoId(),
                        peticion.getCantidadSolicitada() // Se SUMA al stock de bodega
                );

                if (stockActualizado) {
                    // Marcar como despachada
                    return peticionStockDAO.despacharPeticion(peticionId);
                } else {
                    throw new RuntimeException("Error al actualizar stock en bodega");
                }
            }

            return false;
        } catch (Exception e) {
            throw new RuntimeException("Error al aprobar petición de bodega: " + e.getMessage(), e);
        }
    }
}