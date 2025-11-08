package com.mycompany.licoreria.models;

import java.sql.Timestamp;
import java.util.List;

public class PeticionVendedor {
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

    // Campos adicionales para mostrar información
    private String productoNombre;
    private String usuarioSolicitanteNombre;
    private String usuarioAprobadorNombre;
    private String unidadMedida;
    private double stockBodega;
    private double stockVendedor;

    public PeticionVendedor() {}

    public PeticionVendedor(int productoId, int usuarioSolicitanteId, double cantidadSolicitada, String observaciones) {
        this.productoId = productoId;
        this.usuarioSolicitanteId = usuarioSolicitanteId;
        this.cantidadSolicitada = cantidadSolicitada;
        this.observaciones = observaciones;
        this.estado = "pendiente";
        this.fechaSolicitud = new Timestamp(System.currentTimeMillis());
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

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public double getStockBodega() { return stockBodega; }
    public void setStockBodega(double stockBodega) { this.stockBodega = stockBodega; }

    public double getStockVendedor() { return stockVendedor; }
    public void setStockVendedor(double stockVendedor) { this.stockVendedor = stockVendedor; }
}