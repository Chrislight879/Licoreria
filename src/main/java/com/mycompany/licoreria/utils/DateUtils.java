package com.mycompany.licoreria.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;
import java.util.Calendar;

public class DateUtils {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    /**
     * Convertir java.util.Date a java.sql.Date
     */
    public static Date toSqlDate(java.util.Date utilDate) {
        if (utilDate == null) return null;
        return new Date(utilDate.getTime());
    }

    /**
     * Convertir string a java.sql.Date
     */
    public static Date stringToSqlDate(String dateString) {
        try {
            java.util.Date utilDate = DATE_FORMAT.parse(dateString);
            return new Date(utilDate.getTime());
        } catch (ParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use: yyyy-MM-dd");
        }
    }

    /**
     * Formatear fecha para mostrar
     */
    public static String formatDateForDisplay(Date sqlDate) {
        if (sqlDate == null) return "";
        return DISPLAY_FORMAT.format(sqlDate);
    }

    /**
     * Obtener fecha de hoy
     */
    public static Date getToday() {
        return new Date(System.currentTimeMillis());
    }

    /**
     * Obtener fecha de hace N días
     */
    public static Date getDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        return new Date(calendar.getTimeInMillis());
    }

    /**
     * Obtener primer día del mes actual
     */
    public static Date getFirstDayOfMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return new Date(calendar.getTimeInMillis());
    }
}