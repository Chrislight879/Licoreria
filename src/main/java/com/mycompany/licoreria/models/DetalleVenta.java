package com.mycompany.licoreria.models;

import java.math.BigDecimal;

public class DetalleVenta {
    private int detalleVentaId;
    private int ventaId;
    private int productoId;
    private double cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;

    // Campos adicionales
    private String productoNombre;
    private String unidadMedida;

    public DetalleVenta() {}

    public DetalleVenta(int productoId, double cantidad, BigDecimal precioUnitario) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    // Getters y Setters
    public int getDetalleVentaId() { return detalleVentaId; }
    public void setDetalleVentaId(int detalleVentaId) { this.detalleVentaId = detalleVentaId; }

    public int getVentaId() { return ventaId; }
    public void setVentaId(int ventaId) { this.ventaId = ventaId; }

    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
        calcularSubTotal();
    }

    public BigDecimal getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
        calcularSubTotal();
    }

    public BigDecimal getSubTotal() { return subTotal; }
    public void setSubTotal(BigDecimal subTotal) { this.subTotal = subTotal; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    private void calcularSubTotal() {
        if (precioUnitario != null) {
            this.subTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }
}