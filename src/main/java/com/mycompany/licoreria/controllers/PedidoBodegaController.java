package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.PedidoBodega;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.services.PedidoBodegaService;
import java.util.List;

public class PedidoBodegaController {
    private PedidoBodegaService pedidoBodegaService;

    public PedidoBodegaController() {
        this.pedidoBodegaService = new PedidoBodegaService();
    }

    /**
     * Obtener inventario de bodega
     */
    public List<Producto> getInventarioBodega() {
        try {
            return pedidoBodegaService.getInventarioBodega();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener inventario de bodega: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar productos en bodega
     */
    public List<Producto> searchInventarioBodega(String searchTerm) {
        try {
            return pedidoBodegaService.searchInventarioBodega(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar en inventario: " + e.getMessage(), e);
        }
    }

    /**
     * Crear pedido a bodega
     */
    public boolean crearPedidoBodega(int productoId, int usuarioSolicitanteId,
                                     double cantidadSolicitada, String observaciones) {
        try {
            return pedidoBodegaService.crearPedidoBodega(productoId, usuarioSolicitanteId,
                    cantidadSolicitada, observaciones);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al crear pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener pedidos del usuario
     */
    public List<PedidoBodega> getPedidosPorUsuario(int usuarioId) {
        try {
            return pedidoBodegaService.getPedidosPorUsuario(usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedidos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener todos los pedidos
     */
    public List<PedidoBodega> getAllPedidos() {
        try {
            return pedidoBodegaService.getAllPedidos();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener todos los pedidos: " + e.getMessage(), e);
        }
    }

    /**
     * Eliminar pedido (cancelar)
     */
    public boolean eliminarPedido(int pedidoId, int usuarioId) {
        try {
            return pedidoBodegaService.eliminarPedido(pedidoId, usuarioId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar pedido: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar pedidos
     */
    public List<PedidoBodega> searchPedidos(String searchTerm, int usuarioId) {
        try {
            return pedidoBodegaService.searchPedidos(searchTerm, usuarioId);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar pedidos: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener estadísticas de pedidos
     */
    public String getEstadisticasPedidos(int usuarioId) {
        try {
            return pedidoBodegaService.getEstadisticasPedidos(usuarioId);
        } catch (Exception e) {
            return "Error al cargar estadísticas: " + e.getMessage();
        }
    }

    /**
     * Verificar si se puede solicitar producto
     */
    public boolean puedeSolicitarProducto(int productoId, double cantidadSolicitada) {
        try {
            return pedidoBodegaService.puedeSolicitarProducto(productoId, cantidadSolicitada);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Obtener productos con stock disponible
     */
    public List<Producto> getProductosConStockDisponible() {
        try {
            return pedidoBodegaService.getProductosConStockDisponible();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener productos con stock: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener pedidos pendientes del usuario
     */
    public List<PedidoBodega> getPedidosPendientes(int usuarioId) {
        try {
            List<PedidoBodega> pedidos = getPedidosPorUsuario(usuarioId);
            return pedidos.stream()
                    .filter(p -> "pendiente".equals(p.getEstado()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedidos pendientes: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener pedidos enviados del usuario
     */
    public List<PedidoBodega> getPedidosEnviados(int usuarioId) {
        try {
            List<PedidoBodega> pedidos = getPedidosPorUsuario(usuarioId);
            return pedidos.stream()
                    .filter(p -> !"pendiente".equals(p.getEstado()))
                    .toList();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener pedidos enviados: " + e.getMessage(), e);
        }
    }
}