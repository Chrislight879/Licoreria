package com.mycompany.licoreria.utils;

import com.mycompany.licoreria.models.UsuarioLogin;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SessionManager {
    private static UsuarioLogin currentUser;
    private static LocalDateTime loginTime;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Iniciar sesión
     */
    public static void iniciarSesion(UsuarioLogin usuario) {
        currentUser = usuario;
        loginTime = LocalDateTime.now();

        System.out.println("Sesión iniciada: " + usuario.getUsername() +
                " - Rol: " + usuario.getRolTitulo() +
                " - Hora: " + loginTime.format(formatter));
    }

    /**
     * Cerrar sesión
     */
    public static void cerrarSesion() {
        if (currentUser != null) {
            System.out.println("Sesión cerrada: " + currentUser.getUsername() +
                    " - Duración: " + getDuracionSesion());
            currentUser = null;
            loginTime = null;
        }
    }

    /**
     * Obtener usuario actual
     */
    public static UsuarioLogin getCurrentUser() {
        return currentUser;
    }

    /**
     * Verificar si hay sesión activa
     */
    public static boolean haySesionActiva() {
        return currentUser != null;
    }

    /**
     * Obtener tiempo de sesión
     */
    public static String getDuracionSesion() {
        if (loginTime == null) return "No activa";

        LocalDateTime now = LocalDateTime.now();
        long seconds = java.time.Duration.between(loginTime, now).getSeconds();

        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        return String.format("%02d:%02d:%02d", hours, minutes, secs);
    }

    /**
     * Obtener información de la sesión
     */
    public static String getInfoSesion() {
        if (!haySesionActiva()) {
            return "No hay sesión activa";
        }

        return String.format("Usuario: %s | Rol: %s | Inicio: %s | Duración: %s",
                currentUser.getUsername(),
                currentUser.getRolTitulo(),
                loginTime.format(formatter),
                getDuracionSesion());
    }

    /**
     * Verificar permisos por módulo
     */
    public static boolean tienePermiso(String modulo) {
        if (!haySesionActiva()) return false;

        switch (modulo.toUpperCase()) {
            case "ADMINISTRACION":
                return currentUser.getRolId() == 1; // Solo Admin
            case "BODEGA":
                return currentUser.getRolId() == 3 || currentUser.getRolId() == 1; // Bodega o Admin
            case "VENTAS":
                return currentUser.getRolId() == 2 || currentUser.getRolId() == 1; // Vendedor o Admin
            case "INVENTARIO":
                return currentUser.getRolId() == 3 || currentUser.getRolId() == 1; // Bodega o Admin
            case "REPORTES":
                return currentUser.getRolId() == 1; // Solo Admin
            default:
                return false;
        }
    }

    /**
     * Obtener el rol actual como texto
     */
    public static String getRolActual() {
        if (!haySesionActiva()) return "No logueado";

        switch (currentUser.getRolId()) {
            case 1: return "Administrador";
            case 2: return "Vendedor";
            case 3: return "Encargado de Bodega";
            default: return "Usuario";
        }
    }

    /**
     * Verificar si es administrador
     */
    public static boolean esAdministrador() {
        return haySesionActiva() && currentUser.getRolId() == 1;
    }

    /**
     * Verificar si es bodega
     */
    public static boolean esBodega() {
        return haySesionActiva() && currentUser.getRolId() == 3;
    }

    /**
     * Verificar si es vendedor
     */
    public static boolean esVendedor() {
        return haySesionActiva() && currentUser.getRolId() == 2;
    }
}