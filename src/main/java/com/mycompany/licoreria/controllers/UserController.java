package com.mycompany.licoreria.controllers;

import com.mycompany.licoreria.models.User;
import com.mycompany.licoreria.services.UserService;
import java.util.List;

public class UserController {
    private UserService userService;

    public UserController() {
        this.userService = new UserService();
    }

    /**
     * Crear un nuevo usuario
     */
    public boolean createUser(String username, String password, String confirmPassword, int rolId) {
        try {
            return userService.createUser(username, password, rolId);
        } catch (IllegalArgumentException e) {
            throw e; // Re-lanzar para que la vista lo maneje
        } catch (Exception e) {
            throw new RuntimeException("Error del sistema al crear usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener todos los usuarios
     */
    public List<User> getAllUsers() {
        try {
            return userService.getAllUsers();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener usuarios: " + e.getMessage(), e);
        }
    }

    /**
     * Buscar usuarios por término
     */
    public List<User> searchUsers(String searchTerm) {
        try {
            return userService.searchUsers(searchTerm);
        } catch (Exception e) {
            throw new RuntimeException("Error al buscar usuarios: " + e.getMessage(), e);
        }
    }

    /**
     * Eliminar usuario (marcar como inactivo)
     */
    public boolean deleteUser(int userId) {
        try {
            return userService.deleteUser(userId);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar usuario: " + e.getMessage(), e);
        }
    }

    /**
     * Obtener roles disponibles
     */
    public List<String> getAvailableRoles() {
        try {
            return userService.getAvailableRoles();
        } catch (Exception e) {
            throw new RuntimeException("Error al obtener roles: " + e.getMessage(), e);
        }
    }

    /**
     * Verificar si el usuario existe
     */
    public boolean userExists(String username) {
        try {
            return userService.usernameExists(username);
        } catch (Exception e) {
            throw new RuntimeException("Error al verificar usuario: " + e.getMessage(), e);
        }
    }
}