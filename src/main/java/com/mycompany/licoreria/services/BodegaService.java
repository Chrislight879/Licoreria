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
     * Despachar petición a vendedor
     */
    public boolean despacharPeticion(int peticionId) {
        // Verificar stock disponible antes de despachar
        List<PeticionStock> peticionesAprobadas = getPeticionesAprobadas();
        PeticionStock peticion = peticionesAprobadas.stream()
                .filter(p -> p.getPeticionId() == peticionId)
                .findFirst()
                .orElse(null);

        if (peticion == null) {
            throw new IllegalArgumentException("La petición no existe o no está aprobada");
        }

        // Verificar stock en bodega
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
            throw new RuntimeException("Error al actualizar los stocks durante el despacho");
        }
    }

    /**
     * Obtener estadísticas de bodega
     */
    public String getEstadisticasBodega() {
        List<Producto> productos = getAllProductos();
        long totalProductos = productos.size();
        long stockBajo = productos.stream()
                .filter(p -> p.getStockBodega() <= p.getCantidadMinimaBodega())
                .count();
        long sinStock = productos.stream()
                .filter(p -> p.getStockBodega() == 0)
                .count();

        double valorTotalInventario = productos.stream()
                .mapToDouble(p -> p.getStockBodega() * p.getCosto().doubleValue())
                .sum();

        return String.format("Productos: %d | Stock Bajo: %d | Sin Stock: %d | Valor Inventario: $%,.2f",
                totalProductos, stockBajo, sinStock, valorTotalInventario);
    }
}