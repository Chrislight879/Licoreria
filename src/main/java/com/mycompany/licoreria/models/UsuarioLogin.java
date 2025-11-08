package com.mycompany.licoreria.models;

public class UsuarioLogin {
    private int usuarioId;
    private String username;
    private String password;
    private int rolId;
    private String rolTitulo;
    private boolean activo;

    public UsuarioLogin() {}

    public UsuarioLogin(int usuarioId, String username, String password, int rolId, String rolTitulo, boolean activo) {
        this.usuarioId = usuarioId;
        this.username = username;
        this.password = password;
        this.rolId = rolId;
        this.rolTitulo = rolTitulo;
        this.activo = activo;
    }

    // Getters y Setters
    public int getUsuarioId() { return usuarioId; }
    public void setUsuarioId(int usuarioId) { this.usuarioId = usuarioId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public int getRolId() { return rolId; }
    public void setRolId(int rolId) { this.rolId = rolId; }

    public String getRolTitulo() { return rolTitulo; }
    public void setRolTitulo(String rolTitulo) { this.rolTitulo = rolTitulo; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}