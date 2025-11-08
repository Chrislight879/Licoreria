package com.mycompany.licoreria.models;

import java.math.BigDecimal;

public class VentaDetalle {
    private int productoId;
    private String productoNombre;
    private double cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subTotal;
    private String unidadMedida;
    private double stockDisponible;

    public VentaDetalle() {}

    public VentaDetalle(int productoId, String productoNombre, double cantidad,
                        BigDecimal precioUnitario, String unidadMedida, double stockDisponible) {
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.unidadMedida = unidadMedida;
        this.stockDisponible = stockDisponible;
        calcularSubTotal();
    }

    // Getters y Setters
    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public String getProductoNombre() { return productoNombre; }
    public void setProductoNombre(String productoNombre) { this.productoNombre = productoNombre; }

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

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public double getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(double stockDisponible) { this.stockDisponible = stockDisponible; }

    private void calcularSubTotal() {
        if (precioUnitario != null) {
            this.subTotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        }
    }

    public boolean tieneStockSuficiente() {
        return cantidad <= stockDisponible;
    }
}