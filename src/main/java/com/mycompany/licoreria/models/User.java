package com.mycompany.licoreria.models;

public class User {
    private int usuarioId;
    private int rolId;
    private String username;
    private String password;
    private boolean activo;
    private String rolTitulo; // Para mostrar el nombre del rol

    public User() {}

    public User(int usuarioId, int rolId, String username, String password, boolean activo) {
        this.usuarioId = usuarioId;
        this.rolId = rolId;
        this.username = username;
        this.password = password;
        this.activo = activo;
    }

    // Getters y Setters
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getRolTitulo() { return rolTitulo; }
    public void setRolTitulo(String rolTitulo) { this.rolTitulo = rolTitulo; }
}