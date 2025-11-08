package com.mycompany.licoreria.services;

import com.mycompany.licoreria.dao.UserDAO;
import com.mycompany.licoreria.models.User;
import com.mycompany.licoreria.utils.PasswordUtils;
import java.util.List;

public class UserService {
    private UserDAO userDAO;

    public UserService() {
        this.userDAO = new UserDAO();
    }

    public boolean createUser(String username, String password, int rolId) {
        // Validaciones básicas
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }

        if (password == null || password.length() < 4) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 4 caracteres");
        }

        // Verificar si el usuario ya existe
        if (userDAO.usernameExists(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe");
        }

        // Encriptar la contraseña
        String hashedPassword = PasswordUtils.hashPassword(password);

        User user = new User();
        user.setUsername(username.trim());
        user.setPassword(hashedPassword);
        user.setRolId(rolId);
        user.setActivo(true);

        return userDAO.createUser(user);
    }

    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    public List<User> searchUsers(String searchTerm) {
        return userDAO.searchUsers(searchTerm);
    }

    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }

    public List<String> getAvailableRoles() {
        return userDAO.getAvailableRoles();
    }

    public boolean usernameExists(String username) {
        return userDAO.usernameExists(username);
    }
}