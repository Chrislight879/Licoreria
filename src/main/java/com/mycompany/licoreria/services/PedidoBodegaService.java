package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.PedidoBodegaDAO;
import com.mycompany.licoreria.models.PedidoBodega;
import com.mycompany.licoreria.models.Producto;
import java.util.List;

public class PedidoBodegaService {
    private PedidoBodegaDAO pedidoBodegaDAO;

    public PedidoBodegaService() {
        this.pedidoBodegaDAO = new PedidoBodegaDAO();
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Producto> getInventarioBodega() {
        return pedidoBodegaDAO.getInventarioBodega();
    }

    /**
     * Buscar productos en bodega
     */
    public List<Producto> searchInventarioBodega(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getInventarioBodega();
        }
        return pedidoBodegaDAO.searchInventarioBodega(searchTerm.trim());
    }

    /**
     * Crear pedido a bodega
     */
    public boolean crearPedidoBodega(int productoId, int usuarioSolicitanteId,
                                     double cantidadSolicitada, String observaciones) {
        // Validaciones
        if (cantidadSolicitada <= 0) {
            throw new IllegalArgumentException("La cantidad solicitada debe ser mayor a 0");
        }

        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException("Las observaciones no pueden estar vacías");
        }

        // Verificar que el producto existe en bodega
        List<Producto> inventario = getInventarioBodega();
        boolean productoExiste = inventario.stream()
                .anyMatch(p -> p.getProductoId() == productoId);

        if (!productoExiste) {
            throw new IllegalArgumentException("El producto no existe en el inventario de bodega");
        }

        return pedidoBodegaDAO.crearPedidoBodega(productoId, usuarioSolicitanteId,
                cantidadSolicitada, observaciones.trim());
    }

    /**
     * Obtener pedidos del usuario
     */
    public List<PedidoBodega> getPedidosPorUsuario(int usuarioId) {
        return pedidoBodegaDAO.getPedidosPorUsuario(usuarioId);
    }

    /**
     * Obtener todos los pedidos
     */
    public List<PedidoBodega> getAllPedidos() {
        return pedidoBodegaDAO.getAllPedidos();
    }

    /**
     * Eliminar pedido (cancelar)
     */
    public boolean eliminarPedido(int pedidoId, int usuarioId) {
        // Verificar que el pedido existe y pertenece al usuario
        List<PedidoBodega> pedidos = getPedidosPorUsuario(usuarioId);
        boolean pedidoValido = pedidos.stream()
                .anyMatch(p -> p.getPedidoId() == pedidoId && "pendiente".equals(p.getEstado()));

        if (!pedidoValido) {
            throw new IllegalArgumentException("El pedido no existe o no se puede cancelar");
        }

        return pedidoBodegaDAO.eliminarPedido(pedidoId, usuarioId);
    }

    /**
     * Buscar pedidos
     */
    public List<PedidoBodega> searchPedidos(String searchTerm, int usuarioId) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return getPedidosPorUsuario(usuarioId);
        }
        return pedidoBodegaDAO.searchPedidos(searchTerm.trim(), usuarioId);
    }

    /**
     * Obtener estadísticas de pedidos
     */
    public String getEstadisticasPedidos(int usuarioId) {
        return pedidoBodegaDAO.getEstadisticasPedidos(usuarioId);
    }

    /**
     * Verificar si se puede solicitar producto
     */
    public boolean puedeSolicitarProducto(int productoId, double cantidadSolicitada) {
        List<Producto> inventario = getInventarioBodega();
        Producto producto = inventario.stream()
                .filter(p -> p.getProductoId() == productoId)
                .findFirst()
                .orElse(null);

        if (producto == null) {
            return false;
        }

        // Verificar que hay stock suficiente en bodega
        return producto.getStockBodega() >= cantidadSolicitada;
    }

    /**
     * Obtener productos con stock disponible
     */
    public List<Producto> getProductosConStockDisponible() {
        List<Producto> inventario = getInventarioBodega();
        return inventario.stream()
                .filter(p -> p.getStockBodega() > 0)
                .toList();
    }
}