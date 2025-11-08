package com.mycompany.licoreria.dao;

import com.mycompany.licoreria.models.UsuarioLogin;
import com.mycompany.licoreria.config.DatabaseConnection;
import com.mycompany.licoreria.utils.PasswordUtils;
import java.sql.*;

public class LoginDAO {
    private Connection connection;

    public LoginDAO() {
        this.connection = DatabaseConnection.getConnection();
    }

    /**
     * Validar credenciales de usuario
     */
    public UsuarioLogin validarCredenciales(String username, String password) {
        String sql = "SELECT u.usuario_id, u.username, u.password, u.rol_id, u.activo, r.titulo as rol_titulo " +
                "FROM Usuarios u " +
                "INNER JOIN Roles r ON u.rol_id = r.rol_id " +
                "WHERE u.username = ? AND u.activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String hashedPasswordFromDB = rs.getString("password");
                String hashedInputPassword = PasswordUtils.hashPassword(password);

                // Verificar contraseña
                if (hashedInputPassword.equals(hashedPasswordFromDB)) {
                    UsuarioLogin usuario = new UsuarioLogin();
                    usuario.setUsuarioId(rs.getInt("usuario_id"));
                    usuario.setUsername(rs.getString("username"));
                    usuario.setPassword(hashedPasswordFromDB); // No es necesario enviar esto
                    usuario.setRolId(rs.getInt("rol_id"));
                    usuario.setRolTitulo(rs.getString("rol_titulo"));
                    usuario.setActivo(rs.getBoolean("activo"));
                    return usuario;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Verificar si el usuario existe
     */
    public boolean usuarioExiste(String username) {
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

    /**
     * Registrar intento de login en el historial
     */
    public boolean registrarIntentoLogin(int usuarioId, boolean exitoso, String observaciones) {
        String sql = "INSERT INTO HistoryLogs (proceso_id, usuario_id, descripcion) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // proceso_id = 1 para login (debes tener este proceso en tu BD)
            stmt.setInt(1, 1);
            stmt.setInt(2, usuarioId);
            String descripcion = exitoso ?
                    "Login exitoso - " + observaciones :
                    "Intento de login fallido - " + observaciones;
            stmt.setString(3, descripcion);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtener información del usuario por ID
     */
    public UsuarioLogin getUsuarioById(int usuarioId) {
        String sql = "SELECT u.usuario_id, u.username, u.password, u.rol_id, u.activo, r.titulo as rol_titulo " +
                "FROM Usuarios u " +
                "INNER JOIN Roles r ON u.rol_id = r.rol_id " +
                "WHERE u.usuario_id = ? AND u.activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, usuarioId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UsuarioLogin usuario = new UsuarioLogin();
                usuario.setUsuarioId(rs.getInt("usuario_id"));
                usuario.setUsername(rs.getString("username"));
                usuario.setPassword(rs.getString("password"));
                usuario.setRolId(rs.getInt("rol_id"));
                usuario.setRolTitulo(rs.getString("rol_titulo"));
                usuario.setActivo(rs.getBoolean("activo"));
                return usuario;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Cambiar contraseña de usuario
     */
    public boolean cambiarPassword(int usuarioId, String nuevaPassword) {
        String sql = "UPDATE Usuarios SET password = ? WHERE usuario_id = ? AND activo = true";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            String hashedPassword = PasswordUtils.hashPassword(nuevaPassword);
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, usuarioId);

            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}