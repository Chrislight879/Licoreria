package com.mycompany.licoreria.models;

import java.sql.Timestamp;

public class PeticionStock {
    private int peticionId;
    private int productoId;
    private int usuarioSolicitanteId;
    private double cantidadSolicitada;
    private Timestamp fechaSolicitud;
    private Timestamp fechaAprobacion;
    private Timestamp fechaDespacho;
    private int usuarioAprobadorId;
    private String estado;
    private String observaciones;
    private boolean activo;

    // Campos adicionales para mostrar información relacionada
    private String productoNombre;
    private String usuarioSolicitanteNombre;
    private String usuarioAprobadorNombre;
    private double stockBodega;
    private double stockVendedor;

    public PeticionStock() {}

    public PeticionStock(int peticionId, int productoId, int usuarioSolicitanteId,
                         double cantidadSolicitada, Timestamp fechaSolicitud,
                         String estado, String observaciones) {
        this.peticionId = peticionId;
        this.productoId = productoId;
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.cantidadSolicitada = cantidadSolicitada;
        this.fechaSolicitud = fechaSolicitud;
        this.estado = estado;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public int getPeticionId() { return peticionId; }
    public void setPeticionId(int peticionId) { this.peticionId = peticionId; }

    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public int getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public void setUsuarioSolicitanteId(int usuarioSolicitanteId) { this.usuarioSolicitanteId = usuarioSolicitanteId; }

    public double getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(double cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }

    public Timestamp getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Timestamp fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public Timestamp getFechaAprobacion() { return fechaAprobacion; }
    public void setFechaAprobacion(Timestamp fechaAprobacion) { this.fechaAprobacion = fechaAprobacion; }

    public Timestamp getFechaDespacho() { return fechaDespacho; }
    public void setFechaDespacho(Timestamp fechaDespacho) { this.fechaDespacho = fechaDespacho; }

    public int getUsuarioAprobadorId() { return usuarioAprobadorId; }
    public void setUsuarioAprobadorId(int usuarioAprobadorId) { this.usuarioAprobadorId = usuarioAprobadorId; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public String getUsuarioSolicitanteNombre() { return usuarioSolicitanteNombre; }
    public void setUsuarioSolicitanteNombre(String usuarioSolicitanteNombre) { this.usuarioSolicitanteNombre = usuarioSolicitanteNombre; }

    public String getUsuarioAprobadorNombre() { return usuarioAprobadorNombre; }
    public void setUsuarioAprobadorNombre(String usuarioAprobadorNombre) { this.usuarioAprobadorNombre = usuarioAprobadorNombre; }

    public double getStockBodega() { return stockBodega; }
    public void setStockBodega(double stockBodega) { this.stockBodega = stockBodega; }

    public double getStockVendedor() { return stockVendedor; }
    public void setStockVendedor(double stockVendedor) { this.stockVendedor = stockVendedor; }

    public String getUnidadMedida() {
        return "";
    }
}