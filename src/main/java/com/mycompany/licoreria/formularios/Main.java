package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.LoginController;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 *
 * @author christopher
 */
public class Main extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Main.class.getName());
    private javax.swing.JDesktopPane desktopPane;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JLabel lblStatus;
    private LoginController loginController;

    /**
     * Creates new form Main
     */
    public Main() {
        initComponents();
        loginController = new LoginController();
        initializeApplication();
    }

    private void initializeApplication() {
        setupLookAndFeel();
        setupDesktopPane();
        createMenu();
        setupWindowListener();
        showLoginForm();
        setTitle("Sistema de Gestión - Licorería El Bolo Feliz - Por favor inicie sesión");
    }

    private void setupLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            logger.warning("No se pudo establecer el look and feel del sistema: " + e.getMessage());
        }
    }

    private void setupDesktopPane() {
        desktopPane = new javax.swing.JDesktopPane();
        setupDesktopBackground();
        getContentPane().add(desktopPane, BorderLayout.CENTER);

        // Panel de estado
        javax.swing.JPanel statusPanel = new javax.swing.JPanel(new BorderLayout());
        statusPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblStatus = new javax.swing.JLabel(" Bienvenido - Por favor inicie sesión ");
        lblStatus.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.PLAIN, 12));
        statusPanel.add(lblStatus, BorderLayout.WEST);

        javax.swing.JLabel lblVersion = new javax.swing.JLabel("Versión 1.0 - Licorería El Bolo Feliz ");
        lblVersion.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.PLAIN, 12));
        statusPanel.add(lblVersion, BorderLayout.EAST);

        getContentPane().add(statusPanel, BorderLayout.SOUTH);
    }

    private void setupDesktopBackground() {
        javax.swing.JLabel lblBackground = new javax.swing.JLabel(
                "<html><div style='text-align: center; padding: 50px;'>" +
                        "<h1 style='color: #2E4053; font-size: 36px;'>Licorería El Bolo Feliz</h1>" +
                        "<h2 style='color: #566573; font-size: 24px;'>Sistema de Gestión Integral</h2>" +
                        "<br>" +
                        "<div style='background: #F8F9F9; padding: 30px; border-radius: 15px; margin: 20px auto; width: 80%;'>" +
                        "<h3 style='color: #2E4053;'>Módulos Disponibles:</h3>" +
                        "<ul style='text-align: left; color: #566573; font-size: 16px;'>" +
                        "<li><b>🔐 Sistema de Autenticación</b> - Login seguro por roles</li>" +
                        "<li><b>👥 Gestión de Usuarios</b> - Administración completa</li>" +
                        "<li><b>📦 Módulo de Bodega</b> - Control de inventario</li>" +
                        "<li><b>📋 Peticiones de Stock</b> - Solicitudes de vendedores</li>" +
                        "<li><b>📊 Historial del Sistema</b> - Registro de actividades</li>" +
                        "<li><b>📈 Panel de Administración</b> - Vista MDI completa</li>" +
                        "</ul>" +
                        "</div>" +
                        "<p style='color: #85929E; font-size: 14px;'>Inicie sesión para acceder al sistema</p>" +
                        "</div></html>",
                javax.swing.JLabel.CENTER
        );
        lblBackground.setHorizontalAlignment(javax.swing.JLabel.CENTER);
        lblBackground.setVerticalAlignment(javax.swing.JLabel.CENTER);
        desktopPane.add(lblBackground, javax.swing.JLayeredPane.DEFAULT_LAYER);
    }

    private void createMenu() {
        menuBar = new javax.swing.JMenuBar();

        // Menú Sistema
        javax.swing.JMenu menuSistema = new javax.swing.JMenu("Sistema");
        menuSistema.setMnemonic('S');

        javax.swing.JMenuItem menuItemLogin = new javax.swing.JMenuItem("Iniciar Sesión");
        menuItemLogin.setMnemonic('I');
        menuItemLogin.addActionListener(e -> showLoginForm());

        javax.swing.JMenuItem menuItemLogout = new javax.swing.JMenuItem("Cerrar Sesión");
        menuItemLogout.setMnemonic('C');
        menuItemLogout.addActionListener(e -> logout());

        javax.swing.JMenuItem menuItemSalir = new javax.swing.JMenuItem("Salir");
        menuItemSalir.setMnemonic('S');
        menuItemSalir.addActionListener(e -> confirmExit());

        menuSistema.add(menuItemLogin);
        menuSistema.add(menuItemLogout);
        menuSistema.addSeparator();
        menuSistema.add(menuItemSalir);

        // Menú Administración (solo para usuarios autorizados)
        javax.swing.JMenu menuAdministracion = new javax.swing.JMenu("Administración");
        menuAdministracion.setMnemonic('A');

        javax.swing.JMenuItem menuItemPanelAdmin = new javax.swing.JMenuItem("Panel de Administración");
        menuItemPanelAdmin.setMnemonic('P');
        menuItemPanelAdmin.addActionListener(e -> openAdminPanel());

        javax.swing.JMenuItem menuItemUsuarios = new javax.swing.JMenuItem("Gestión de Usuarios");
        menuItemUsuarios.setMnemonic('U');
        menuItemUsuarios.addActionListener(e -> openUserManagement());

        javax.swing.JMenuItem menuItemHistorial = new javax.swing.JMenuItem("Historial del Sistema");
        menuItemHistorial.setMnemonic('H');
        menuItemHistorial.addActionListener(e -> openSystemHistory());

        menuAdministracion.add(menuItemPanelAdmin);
        menuAdministracion.addSeparator();
        menuAdministracion.add(menuItemUsuarios);
        menuAdministracion.add(menuItemHistorial);

        // Menú Bodega
        javax.swing.JMenu menuBodega = new javax.swing.JMenu("Bodega");
        menuBodega.setMnemonic('B');

        javax.swing.JMenuItem menuItemModuloBodega = new javax.swing.JMenuItem("Módulo de Bodega");
        menuItemModuloBodega.setMnemonic('M');
        menuItemModuloBodega.addActionListener(e -> openBodegaModule());

        javax.swing.JMenuItem menuItemPeticiones = new javax.swing.JMenuItem("Peticiones de Stock");
        menuItemPeticiones.setMnemonic('P');
        menuItemPeticiones.addActionListener(e -> openStockRequests());

        menuBodega.add(menuItemModuloBodega);
        menuBodega.add(menuItemPeticiones);

        // Menú Ayuda
        javax.swing.JMenu menuAyuda = new javax.swing.JMenu("Ayuda");
        menuAyuda.setMnemonic('y');

        javax.swing.JMenuItem menuItemAcercaDe = new javax.swing.JMenuItem("Acerca de...");
        menuItemAcercaDe.setMnemonic('A');
        menuItemAcercaDe.addActionListener(e -> showAbout());

        javax.swing.JMenuItem menuItemInfoSistema = new javax.swing.JMenuItem("Información del Sistema");
        menuItemInfoSistema.setMnemonic('I');
        menuItemInfoSistema.addActionListener(e -> showSystemInfo());

        menuAyuda.add(menuItemAcercaDe);
        menuAyuda.add(menuItemInfoSistema);

        // Agregar menús a la barra
        menuBar.add(menuSistema);
        menuBar.add(menuAdministracion);
        menuBar.add(menuBodega);
        menuBar.add(menuAyuda);

        setJMenuBar(menuBar);
        updateMenuAccess();
    }

    private void updateMenuAccess() {
        boolean isLoggedIn = SessionManager.haySesionActiva();
        boolean isAdmin = loginController.usuarioEsAdministrador();
        boolean isBodega = loginController.usuarioEsBodega();

        // Habilitar/deshabilitar menús según permisos
        javax.swing.JMenu menuAdministracion = menuBar.getMenu(1); // Índice 1 = Administración
        javax.swing.JMenu menuBodega = menuBar.getMenu(2); // Índice 2 = Bodega

        if (menuAdministracion != null) {
            menuAdministracion.setEnabled(isLoggedIn && (isAdmin || isBodega));
        }

        if (menuBodega != null) {
            menuBodega.setEnabled(isLoggedIn && (isAdmin || isBodega));
        }

        // Actualizar estado
        if (isLoggedIn) {
            lblStatus.setText(" Usuario: " + loginController.getUsernameUsuarioLogueado() +
                    " | Rol: " + loginController.getRolUsuarioLogueado() +
                    " | Sesión: " + SessionManager.getDuracionSesion());
            setTitle("Sistema de Gestión - Licorería El Bolo Feliz - Usuario: " +
                    loginController.getUsernameUsuarioLogueado());
        } else {
            lblStatus.setText(" No hay sesión activa - Por favor inicie sesión ");
            setTitle("Sistema de Gestión - Licorería El Bolo Feliz - Por favor inicie sesión");
        }
    }

    private void setupWindowListener() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }

    private void showLoginForm() {
        // Verificar si ya está abierto
        for (javax.swing.JInternalFrame frame : desktopPane.getAllFrames()) {
            if (frame instanceof login) {
                try {
                    frame.setSelected(true);
                    frame.moveToFront();
                    return;
                } catch (Exception ex) {
                    logger.warning("Error al traer formulario de login al frente: " + ex.getMessage());
                }
            }
        }

        // Crear nuevo formulario de login
        login loginForm = new login();
        loginForm.setVisible(true);
        desktopPane.add(loginForm);
        centerInternalFrame(loginForm);

        try {
            loginForm.setSelected(true);
        } catch (java.beans.PropertyVetoException ex) {
            logger.warning("Error al seleccionar formulario de login: " + ex.getMessage());
        }
    }

    private void logout() {
        if (!SessionManager.haySesionActiva()) {
            JOptionPane.showMessageDialog(this,
                    "No hay ninguna sesión activa",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center;'>" +
                        "<h3>¿Cerrar sesión?</h3>" +
                        "<p>¿Está seguro que desea cerrar la sesión de</p>" +
                        "<p><b>" + loginController.getUsernameUsuarioLogueado() + "</b>?</p>" +
                        "</div></html>",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Cerrar todos los formularios internos
            closeAllInternalFrames();

            // Cerrar sesión
            SessionManager.cerrarSesion();
            loginController.cerrarSesion();

            // Actualizar interfaz
            updateMenuAccess();
            setupDesktopBackground();

            JOptionPane.showMessageDialog(this,
                    "Sesión cerrada exitosamente",
                    "Sesión cerrada",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void closeAllInternalFrames() {
        for (javax.swing.JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }
        desktopPane.removeAll();
        desktopPane.revalidate();
        desktopPane.repaint();
    }

    private void openAdminPanel() {
        if (!validateAccess("ADMINISTRACION")) return;

        AdminMainForm adminForm = new AdminMainForm();
        adminForm.setVisible(true);
        desktopPane.add(adminForm);
        centerInternalFrame(adminForm);
    }

    private void openUserManagement() {
        if (!validateAccess("ADMINISTRACION")) return;

        AdminCrearUsuarios userForm = new AdminCrearUsuarios();
        userForm.setVisible(true);
        desktopPane.add(userForm);
        centerInternalFrame(userForm);
    }

    private void openSystemHistory() {
        if (!validateAccess("ADMINISTRACION")) return;

        AdminHistoriall historyForm = new AdminHistoriall();
        historyForm.setVisible(true);
        desktopPane.add(historyForm);
        centerInternalFrame(historyForm);
    }

    private void openBodegaModule() {
        if (!validateAccess("BODEGA")) return;

        BodegaMainForm bodegaForm = new BodegaMainForm();
        bodegaForm.setVisible(true);
        desktopPane.add(bodegaForm);
        centerInternalFrame(bodegaForm);
    }

    private void openStockRequests() {
        if (!validateAccess("BODEGA")) return;

        AdminVerPeticiones requestsForm = new AdminVerPeticiones();
        requestsForm.setVisible(true);
        desktopPane.add(requestsForm);
        centerInternalFrame(requestsForm);
    }

    private boolean validateAccess(String modulo) {
        if (!SessionManager.haySesionActiva()) {
            JOptionPane.showMessageDialog(this,
                    "Debe iniciar sesión para acceder a este módulo",
                    "Acceso denegado",
                    JOptionPane.WARNING_MESSAGE);
            showLoginForm();
            return false;
        }

        if (!SessionManager.tienePermiso(modulo)) {
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align: center;'>" +
                            "<h3>Permiso denegado</h3>" +
                            "<p>Su rol actual <b>(" + loginController.getRolUsuarioLogueado() + ")</b></p>" +
                            "<p>no tiene permisos para acceder al módulo <b>" + modulo + "</b></p>" +
                            "</div></html>",
                    "Permiso insuficiente",
                    JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private void centerInternalFrame(javax.swing.JInternalFrame frame) {
        Dimension desktopSize = desktopPane.getSize();
        Dimension frameSize = frame.getSize();

        // Ajustar tamaño si es muy grande
        if (frameSize.width > desktopSize.width * 0.8 || frameSize.height > desktopSize.height * 0.8) {
            frame.setSize(
                    (int)(desktopSize.width * 0.8),
                    (int)(desktopSize.height * 0.8)
            );
            frameSize = frame.getSize();
        }

        frame.setLocation(
                (desktopSize.width - frameSize.width) / 2,
                (desktopSize.height - frameSize.height) / 2
        );
    }

    private void confirmExit() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center;'>" +
                        "<h3>¿Salir del sistema?</h3>" +
                        "<p>¿Está seguro que desea salir del</p>" +
                        "<p><b>Sistema de Gestión - Licorería El Bolo Feliz</b>?</p>" +
                        "</div></html>",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            // Cerrar sesión si está activa
            if (SessionManager.haySesionActiva()) {
                SessionManager.cerrarSesion();
            }
            System.exit(0);
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center;'>" +
                        "<h2 style='color: #2E4053;'>Licorería El Bolo Feliz</h2>" +
                        "<h3>Sistema de Gestión Integral</h3>" +
                        "<div style='background: #F8F9F9; padding: 15px; border-radius: 8px; margin: 10px;'>" +
                        "<p><b>Versión:</b> 1.0</p>" +
                        "<p><b>Desarrollado por:</b> Christopher</p>" +
                        "<p><b>Fecha:</b> Noviembre 2024</p>" +
                        "</div>" +
                        "<h4>Características:</h4>" +
                        "<ul style='text-align: left;'>" +
                        "<li>Gestión multi-usuario con roles</li>" +
                        "<li>Control completo de inventario</li>" +
                        "<li>Sistema de peticiones de stock</li>" +
                        "<li>Historial detallado del sistema</li>" +
                        "<li>Interfaz MDI profesional</li>" +
                        "</ul>" +
                        "<p style='color: #85929E; font-size: 12px;'>© 2024 Todos los derechos reservados</p>" +
                        "</div></html>",
                "Acerca del Sistema",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showSystemInfo() {
        String javaVersion = System.getProperty("java.version");
        String osName = System.getProperty("os.name");
        String osVersion = System.getProperty("os.version");
        String userHome = System.getProperty("user.home");

        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center;'>" +
                        "<h3>Información del Sistema</h3>" +
                        "<div style='background: #F8F9F9; padding: 15px; border-radius: 8px; margin: 10px;'>" +
                        "<p><b>Java Version:</b> " + javaVersion + "</p>" +
                        "<p><b>Sistema Operativo:</b> " + osName + " " + osVersion + "</p>" +
                        "<p><b>Directorio Usuario:</b> " + userHome + "</p>" +
                        "<p><b>Memoria Total:</b> " + Runtime.getRuntime().totalMemory() / (1024 * 1024) + " MB</p>" +
                        "<p><b>Memoria Libre:</b> " + Runtime.getRuntime().freeMemory() / (1024 * 1024) + " MB</p>" +
                        "</div>" +
                        (SessionManager.haySesionActiva() ?
                                "<div style='background: #E8F6F3; padding: 10px; border-radius: 5px; margin: 5px;'>" +
                                        "<p><b>Información de Sesión:</b></p>" +
                                        "<p>" + SessionManager.getInfoSesion() + "</p>" +
                                        "</div>" :
                                "<p style='color: #85929E;'>No hay sesión activa</p>") +
                        "</div></html>",
                "Información del Sistema",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Sistema de Gestión - Licorería El Bolo Feliz");
        setPreferredSize(new java.awt.Dimension(1200, 800));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1199, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 847, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            Main mainFrame = new Main();

            // Configurar tamaño y posición
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            mainFrame.setSize((int)(screenSize.width * 0.9), (int)(screenSize.height * 0.9));
            mainFrame.setLocationRelativeTo(null);

            mainFrame.setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    // Método público para actualizar la interfaz desde otros formularios
    public void actualizarEstadoSesion() {
        updateMenuAccess();
    }
}