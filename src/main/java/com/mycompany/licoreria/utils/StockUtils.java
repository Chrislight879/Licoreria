package com.mycompany.licoreria.utils;

import java.text.DecimalFormat;

public class StockUtils {

    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0.00");

    /**
     * Formatear cantidad para mostrar
     */
    public static String formatCantidad(double cantidad) {
        return DECIMAL_FORMAT.format(cantidad);
    }

    /**
     * Verificar si hay stock suficiente
     */
    public static boolean hayStockSuficiente(double stockDisponible, double cantidadSolicitada) {
        return stockDisponible >= cantidadSolicitada;
    }

    /**
     * Calcular porcentaje de stock
     */
    public static double calcularPorcentajeStock(double stockActual, double stockMinimo) {
        if (stockMinimo <= 0) return 100.0;
        return (stockActual / stockMinimo) * 100;
    }

    /**
     * Obtener nivel de stock como texto
     */
    public static String getNivelStock(double stockActual, double stockMinimo) {
        double porcentaje = calcularPorcentajeStock(stockActual, stockMinimo);

        if (porcentaje >= 150) return "ALTO";
        else if (porcentaje >= 100) return "NORMAL";
        else if (porcentaje >= 50) return "BAJO";
        else return "CRÍTICO";
    }

    /**
     * Obtener color para nivel de stock
     */
    public static java.awt.Color getColorNivelStock(double stockActual, double stockMinimo) {
        double porcentaje = calcularPorcentajeStock(stockActual, stockMinimo);

        if (porcentaje >= 100) return new java.awt.Color(0, 128, 0); // Verde
        else if (porcentaje >= 50) return new java.awt.Color(255, 165, 0); // Naranja
        else return new java.awt.Color(255, 0, 0); // Rojo
    }

    /**
     * Validar cantidad solicitada
     */
    public static boolean validarCantidad(double cantidad) {
        return cantidad > 0 && cantidad <= 10000; // Límite razonable
    }
}