package com.mycompany.licoreria.models;

import java.sql.Timestamp;

public class HistoryLog {
    private int historyLogId;
    private int procesoId;
    private int usuarioId;
    private int productoId;
    private double cantidad;
    private String descripcion;
    private Timestamp fecha;
    private boolean activo;

    // Campos adicionales para mostrar información relacionada
    private String procesoNombre;
    private String usuarioNombre;
    private String productoNombre;

    public HistoryLog() {}

    public HistoryLog(int historyLogId, int procesoId, int usuarioId, int productoId,
                      double cantidad, String descripcion, Timestamp fecha, boolean activo) {
        this.historyLogId = historyLogId;
        this.procesoId = procesoId;
        this.usuarioId = usuarioId;
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.activo = activo;
    }

    // Getters y Setters
    public int getHistoryLogId() { return historyLogId; }
    public void setHistoryLogId(int historyLogId) { this.historyLogId = historyLogId; }

    public int getProcesoId() { return procesoId; }
    public void setProcesoId(int procesoId) { this.procesoId = procesoId; }

    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public Timestamp getFecha() { return fecha; }
    public void setFecha(Timestamp fecha) { this.fecha = fecha; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getProcesoNombre() { return procesoNombre; }
    public void setProcesoNombre(String procesoNombre) { this.procesoNombre = procesoNombre; }

    public String getUsuarioNombre() { return usuarioNombre; }
    public void setUsuarioNombre(String usuarioNombre) { this.usuarioNombre = usuarioNombre; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }
}