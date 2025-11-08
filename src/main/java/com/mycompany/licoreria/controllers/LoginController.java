package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.UsuarioLogin;
import com.mycompany.licoreria.services.LoginService;

public class LoginController {
    private LoginService loginService;
    private static UsuarioLogin usuarioLogueado;

    public LoginController() {
        this.loginService = new LoginService();
    }

    /**
     * Iniciar sesión
     */
    public boolean iniciarSesion(String username, String password) {
        try {
            UsuarioLogin usuario = loginService.autenticarUsuario(username, password);
            if (usuario != null) {
                usuarioLogueado = usuario;
                return true;
            }
            return false;
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error del sistema al iniciar sesión: " + e.getMessage(), e);
        }
    }

    /**
     * Cerrar sesión
     */
    public void cerrarSesion() {
        usuarioLogueado = null;
    }

    /**
     * Obtener usuario logueado
     */
    public UsuarioLogin getUsuarioLogueado() {
        return usuarioLogueado;
    }

    /**
     * Verificar si hay un usuario logueado
     */
    public boolean hayUsuarioLogueado() {
        return usuarioLogueado != null;
    }

    /**
     * Verificar permisos de administrador
     */
    public boolean usuarioEsAdministrador() {
        return loginService.esAdministrador(usuarioLogueado);
    }

    /**
     * Verificar permisos de bodega
     */
    public boolean usuarioEsBodega() {
        return loginService.esBodega(usuarioLogueado);
    }

    /**
     * Verificar permisos de vendedor
     */
    public boolean usuarioEsVendedor() {
        return loginService.esVendedor(usuarioLogueado);
    }

    /**
     * Cambiar contraseña del usuario actual
     */
    public boolean cambiarPassword(String passwordActual, String nuevaPassword, String confirmacionPassword) {
        try {
            if (usuarioLogueado == null) {
                throw new IllegalStateException("No hay usuario logueado");
            }

            // Verificar contraseña actual
            if (!loginService.autenticarUsuario(usuarioLogueado.getUsername(), passwordActual).isActivo()) {
                throw new IllegalArgumentException("La contraseña actual es incorrecta");
            }

            return loginService.cambiarPassword(usuarioLogueado.getUsuarioId(), nuevaPassword, confirmacionPassword);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar validaciones específicas
        } catch (Exception e) {
            throw new RuntimeException("Error al cambiar contraseña: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener nombre del rol del usuario logueado
     */
    public String getRolUsuarioLogueado() {
        return usuarioLogueado != null ? usuarioLogueado.getRolTitulo() : "No logueado";
    }

    /**
     * Obtener username del usuario logueado
     */
    public String getUsernameUsuarioLogueado() {
        return usuarioLogueado != null ? usuarioLogueado.getUsername() : "Invitado";
    }

    /**
     * Verificar si el usuario puede acceder al módulo de administración
     */
    public boolean puedeAccederAdministracion() {
        return usuarioEsAdministrador() || usuarioEsBodega();
    }

    /**
     * Verificar si el usuario puede acceder al módulo de bodega
     */
    public boolean puedeAccederBodega() {
        return usuarioEsBodega() || usuarioEsAdministrador();
    }

    /**
     * Verificar si el usuario puede acceder al módulo de ventas
     */
    public boolean puedeAccederVentas() {
        return usuarioEsVendedor() || usuarioEsAdministrador();
    }
}