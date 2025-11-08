package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.User;
import com.mycompany.licoreria.config.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    // Crear usuario
    public boolean createUser(User user) {
        String sql = "INSERT INTO Usuarios (rol_id, username, password, activo) VALUES (?, ?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, user.getRolId());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isActivo());

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Obtener todos los usuarios con información del rol
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.usuario_id, u.rol_id, u.username, u.activo, r.titulo as rol_titulo " +
                "FROM Usuarios u " +
                "INNER JOIN Roles r ON u.rol_id = r.rol_id " +
                "WHERE u.activo = true " +
                "ORDER BY u.usuario_id";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                User user = new User();
                user.setUsuarioId(rs.getInt("usuario_id"));
                user.setRolId(rs.getInt("rol_id"));
                user.setUsername(rs.getString("username"));
                user.setActivo(rs.getBoolean("activo"));
                user.setRolTitulo(rs.getString("rol_titulo"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Buscar usuarios por nombre
    public List<User> searchUsers(String searchTerm) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT u.usuario_id, u.rol_id, u.username, u.activo, r.titulo as rol_titulo " +
                "FROM Usuarios u " +
                "INNER JOIN Roles r ON u.rol_id = r.rol_id " +
                "WHERE u.username LIKE ? AND u.activo = true " +
                "ORDER BY u.usuario_id";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + searchTerm + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User user = new User();
                user.setUsuarioId(rs.getInt("usuario_id"));
                user.setRolId(rs.getInt("rol_id"));
                user.setUsername(rs.getString("username"));
                user.setActivo(rs.getBoolean("activo"));
                user.setRolTitulo(rs.getString("rol_titulo"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Eliminar usuario (marcar como inactivo)
    public boolean deleteUser(int userId) {
        String sql = "UPDATE Usuarios SET activo = false WHERE usuario_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Verificar si el username existe
    public boolean usernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM Usuarios WHERE username = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Obtener roles disponibles
    public List<String> getAvailableRoles() {
        List<String> roles = new ArrayList<>();
        String sql = "SELECT rol_id, titulo FROM Roles WHERE activo = true";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                roles.add(rs.getInt("rol_id") + " - " + rs.getString("titulo"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return roles;
    }
}