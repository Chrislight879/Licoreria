package com.mycompany.licoreria.models;

import java.math.BigDecimal;

public class Producto {
    private int productoId;
    private int proveedorId;
    private String nombre;
    private BigDecimal costo;
    private BigDecimal precio;
    private String descripcion;
    private String unidadMedida;
    private boolean activo;

    // Campos adicionales para mostrar información relacionada
    private String proveedorNombre;
    private double stockBodega;
    private double stockVendedor;
    private double cantidadMinimaBodega;
    private double cantidadMinimaVendedor;

    public Producto() {}

    public Producto(int productoId, String nombre, BigDecimal costo, BigDecimal precio,
                    String descripcion, String unidadMedida) {
        this.productoId = productoId;
        this.nombre = nombre;
        this.costo = costo;
        this.precio = precio;
        this.descripcion = descripcion;
        this.unidadMedida = unidadMedida;
    }

    // Getters y Setters
    public int getProductoId() { return productoId; }
    public void setProductoId(int productoId) { this.productoId = productoId; }

    public int getProveedorId() { return proveedorId; }
    public void setProveedorId(int proveedorId) { this.proveedorId = proveedorId; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public BigDecimal getCosto() { return costo; }
    public void setCosto(BigDecimal costo) { this.costo = costo; }

    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getProveedorNombre() { return proveedorNombre; }
    public void setProveedorNombre(String proveedorNombre) { this.proveedorNombre = proveedorNombre; }

    public double getStockBodega() { return stockBodega; }
    public void setStockBodega(double stockBodega) { this.stockBodega = stockBodega; }

    public double getStockVendedor() { return stockVendedor; }
    public void setStockVendedor(double stockVendedor) { this.stockVendedor = stockVendedor; }

    public double getCantidadMinimaBodega() { return cantidadMinimaBodega; }
    public void setCantidadMinimaBodega(double cantidadMinimaBodega) { this.cantidadMinimaBodega = cantidadMinimaBodega; }

    public double getCantidadMinimaVendedor() { return cantidadMinimaVendedor; }
    public void setCantidadMinimaVendedor(double cantidadMinimaVendedor) { this.cantidadMinimaVendedor = cantidadMinimaVendedor; }
}