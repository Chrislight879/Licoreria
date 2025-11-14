package com.mycompany.licoreria.utils;

import com.mycompany.licoreria.models.UsuarioLogin;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SessionManager {
    private static UsuarioLogin currentUser;
    private static LocalDateTime loginTime;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Constantes para roles
    public static final int ROL_ADMIN = 1;
    public static final int ROL_VENDEDOR = 2;
    public static final int ROL_BODEGA = 3;

    // Constantes para módulos/vistas
    public static final String MODULO_ADMINISTRACION = "ADMINISTRACION";
    public static final String MODULO_BODEGA = "BODEGA";
    public static final String MODULO_VENTAS = "VENTAS";
    public static final String MODULO_INVENTARIO = "INVENTARIO";
    public static final String MODULO_REPORTES = "REPORTES";

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
     * Verificar permisos por módulo - MEJORADO
     */
    public static boolean tienePermiso(String modulo) {
        if (!haySesionActiva()) return false;

        // Si es administrador, solo puede acceder a módulos de administración
        if (currentUser.getRolId() == ROL_ADMIN) {
            return modulo.equalsIgnoreCase(MODULO_ADMINISTRACION) || 
                   modulo.equalsIgnoreCase(MODULO_REPORTES);
        }

        // Para otros roles
        switch (modulo.toUpperCase()) {
            case MODULO_BODEGA:
            case MODULO_INVENTARIO:
                return currentUser.getRolId() == ROL_BODEGA;
            case MODULO_VENTAS:
                return currentUser.getRolId() == ROL_VENDEDOR;
            case MODULO_ADMINISTRACION:
            case MODULO_REPORTES:
                return false; // Solo admin puede acceder
            default:
                return false;
        }
    }

    /**
     * Verificar acceso a vista específica - NUEVO MÉTODO
     */
    public static boolean puedeAccederAVista(String nombreVista) {
        if (!haySesionActiva()) return false;

        // Mapeo de vistas a módulos
        if (nombreVista.toLowerCase().contains("admin") || 
            nombreVista.toLowerCase().contains("administracion")) {
            return tienePermiso(MODULO_ADMINISTRACION);
        }
        else if (nombreVista.toLowerCase().contains("bodega") || 
                 nombreVista.toLowerCase().contains("inventario")) {
            return tienePermiso(MODULO_BODEGA);
        }
        else if (nombreVista.toLowerCase().contains("venta") || 
                 nombreVista.toLowerCase().contains("vendedor")) {
            return tienePermiso(MODULO_VENTAS);
        }
        else if (nombreVista.toLowerCase().contains("reporte")) {
            return tienePermiso(MODULO_REPORTES);
        }

        return false;
    }

    /**
     * Obtener el rol actual como texto
     */
    public static String getRolActual() {
        if (!haySesionActiva()) return "No logueado";

        switch (currentUser.getRolId()) {
            case ROL_ADMIN: return "Administrador";
            case ROL_VENDEDOR: return "Vendedor";
            case ROL_BODEGA: return "Encargado de Bodega";
            default: return "Usuario";
        }
    }

    /**
     * Verificar si es administrador
     */
    public static boolean esAdministrador() {
        return haySesionActiva() && currentUser.getRolId() == ROL_ADMIN;
    }

    /**
     * Verificar si es bodega
     */
    public static boolean esBodega() {
        return haySesionActiva() && currentUser.getRolId() == ROL_BODEGA;
    }

    /**
     * Verificar si es vendedor
     */
    public static boolean esVendedor() {
        return haySesionActiva() && currentUser.getRolId() == ROL_VENDEDOR;
    }

    /**
     * Redirigir a la vista principal según el rol - NUEVO MÉTODO
     */
    public static String getVistaPrincipal() {
        if (!haySesionActiva()) return "login";

        switch (currentUser.getRolId()) {
            case ROL_ADMIN:
                return "vistaAdministracion";
            case ROL_VENDEDOR:
                return "vistaVentas";
            case ROL_BODEGA:
                return "vistaBodega";
            default:
                return "login";
        }
    }
}