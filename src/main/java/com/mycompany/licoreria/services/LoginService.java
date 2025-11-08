package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.LoginDAO;
import com.mycompany.licoreria.models.UsuarioLogin;

public class LoginService {
    private LoginDAO loginDAO;

    public LoginService() {
        this.loginDAO = new LoginDAO();
    }

    /**
     * Autenticar usuario
     */
    public UsuarioLogin autenticarUsuario(String username, String password) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }

        if (password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }

        // Validar credenciales
        UsuarioLogin usuario = loginDAO.validarCredenciales(username.trim(), password.trim());

        if (usuario != null) {
            // Registrar login exitoso
            loginDAO.registrarIntentoLogin(usuario.getUsuarioId(), true, "Acceso concedido");
            return usuario;
        } else {
            // Registrar intento fallido
            if (loginDAO.usuarioExiste(username.trim())) {
                // Usuario existe pero contraseña incorrecta
                // Podrías buscar el usuario por username para obtener el ID
                // Por simplicidad, no registramos el intento fallido sin ID de usuario
            }
            return null;
        }
    }

    /**
     * Verificar si el usuario tiene permisos de administrador
     */
    public boolean esAdministrador(UsuarioLogin usuario) {
        return usuario != null && usuario.getRolId() == 1; // 1 = Administrador
    }

    /**
     * Verificar si el usuario tiene permisos de bodega
     */
    public boolean esBodega(UsuarioLogin usuario) {
        return usuario != null && usuario.getRolId() == 3; // 3 = Bodega
    }

    /**
     * Verificar si el usuario tiene permisos de vendedor
     */
    public boolean esVendedor(UsuarioLogin usuario) {
        return usuario != null && usuario.getRolId() == 2; // 2 = Vendedor
    }

    /**
     * Cambiar contraseña
     */
    public boolean cambiarPassword(int usuarioId, String nuevaPassword, String confirmacionPassword) {
        if (nuevaPassword == null || nuevaPassword.trim().isEmpty()) {
            throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
        }

        if (nuevaPassword.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }

        if (!nuevaPassword.equals(confirmacionPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        return loginDAO.cambiarPassword(usuarioId, nuevaPassword.trim());
    }

    /**
     * Validar fortaleza de contraseña
     */
    public boolean esPasswordSegura(String password) {
        if (password == null || password.length() < 4) {
            return false;
        }
        // Puedes agregar más validaciones aquí (mayúsculas, números, etc.)
        return true;
    }

    /**
     * Obtener usuario por ID
     */
    public UsuarioLogin getUsuarioById(int usuarioId) {
        return loginDAO.getUsuarioById(usuarioId);
    }
}