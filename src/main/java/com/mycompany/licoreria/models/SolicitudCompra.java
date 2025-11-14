package com.mycompany.licoreria.models;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class SolicitudCompra {
    private int solicitudId;
    private int productoId;
    private String productoNombre;
    private double cantidadSolicitada;
    private BigDecimal costoTotal;
    private Timestamp fechaSolicitud;
    private String observaciones;
    private String estado; // pendiente, aprobada, rechazada, completada
    private int usuarioSolicitanteId;
    private String usuarioSolicitanteNombre;
    private String proveedorNombre;
    private String unidadMedida;

    public SolicitudCompra() {}

    // Getters y Setters
    public int getSolicitudId() { return solicitudId; }
    public void setSolicitudId(int solicitudId) { this.solicitudId = solicitudId; }

    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public double getCantidadSolicitada() { return cantidadSolicitada; }
    public void setCantidadSolicitada(double cantidadSolicitada) { this.cantidadSolicitada = cantidadSolicitada; }

    public BigDecimal getCostoTotal() { return costoTotal; }
    public void setCostoTotal(BigDecimal costoTotal) { this.costoTotal = costoTotal; }

    public Timestamp getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Timestamp fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public int getUsuarioSolicitanteId() { return usuarioSolicitanteId; }
    public void setUsuarioSolicitanteId(int usuarioSolicitanteId) { this.usuarioSolicitanteId = usuarioSolicitanteId; }

    public String getUsuarioSolicitanteNombre() { return usuarioSolicitanteNombre; }
    public void setUsuarioSolicitanteNombre(String usuarioSolicitanteNombre) { this.usuarioSolicitanteNombre = usuarioSolicitanteNombre; }

    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
}