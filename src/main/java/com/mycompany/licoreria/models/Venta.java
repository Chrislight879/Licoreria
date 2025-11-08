package com.mycompany.licoreria.models;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

public class Venta {
    private int ventaId;
    private Timestamp fechaVenta;
    private BigDecimal total;
    private String cliente;
    private int usuarioId;
    private boolean activo;

    // Campos adicionales
    private String usuarioNombre;
    private List<DetalleVenta> detalles;

    public Venta() {}

    public Venta(int ventaId, Timestamp fechaVenta, BigDecimal total, String cliente, int usuarioId) {
        this.ventaId = ventaId;
        this.fechaVenta = fechaVenta;
        this.total = total;
        this.cliente = cliente;
        this.usuarioId = usuarioId;
    }

    // Getters y Setters
    public int getVentaId() { return ventaId; }
    public void setVentaId(int ventaId) { this.ventaId = ventaId; }

    public Timestamp getFechaVenta() { return fechaVenta; }
    public void setFechaVenta(Timestamp fechaVenta) { this.fechaVenta = fechaVenta; }

    public BigDecimal getTotal() { return total; }
    public void setTotal(BigDecimal total) { this.total = total; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public List<DetalleVenta> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleVenta> detalles) { this.detalles = detalles; }
}