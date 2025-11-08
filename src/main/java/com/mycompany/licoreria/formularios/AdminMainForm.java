package com.mycompany.licoreria.formularios; // mas o menos

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JDesktopPane;
import javax.swing.JOptionPane;

public class AdminMainForm extends javax.swing.JInternalFrame {
    private JDesktopPane desktopPane;

    public AdminMainForm() {
        initComponents();
        initializeDesktop();
        setTitle("Panel de Administración - Licorería");
        setMaximizable(true);
        setClosable(true);
        setResizable(true);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    private void initializeDesktop() {
        desktopPane = new JDesktopPane();
        getContentPane().add(desktopPane, BorderLayout.CENTER);
    }

    private void abrirGestionUsuarios() {
        try {
            AdminCrearUsuarios usuariosForm = new AdminCrearUsuarios();
            usuariosForm.setVisible(true);
            desktopPane.add(usuariosForm);

            // Centrar el formulario
            usuariosForm.setLocation(
                    (desktopPane.getWidth() - usuariosForm.getWidth()) / 2,
                    (desktopPane.getHeight() - usuariosForm.getHeight()) / 2
            );

            usuariosForm.toFront();
            try {
                usuariosForm.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {}

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir gestión de usuarios: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirHistorial() {
        try {
            AdminHistoriall historialForm = new AdminHistoriall();
            historialForm.setVisible(true);
            desktopPane.add(historialForm);

            // Centrar el formulario
            historialForm.setLocation(
                    (desktopPane.getWidth() - historialForm.getWidth()) / 2,
                    (desktopPane.getHeight() - historialForm.getHeight()) / 2
            );

            historialForm.toFront();
            try {
                historialForm.setSelected(true);
            } catch (java.beans.PropertyVetoException e) {}

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir historial: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void abrirGestionPeticiones() {
        try {
            // Aquí puedes crear o abrir el formulario de peticiones cuando lo tengas
            JOptionPane.showMessageDialog(this,
                    "Funcionalidad de Gestión de Peticiones en desarrollo",
                    "Información",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al abrir gestión de peticiones: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarDashboard() {
        try {
            // Crear un panel de dashboard simple
            javax.swing.JPanel dashboardPanel = new javax.swing.JPanel();
            dashboardPanel.setLayout(new BorderLayout());

            // Título
            javax.swing.JLabel tituloLabel = new javax.swing.JLabel("PANEL DE ADMINISTRACIÓN - LICORERÍA", javax.swing.JLabel.CENTER);
            tituloLabel.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.BOLD, 24));
            tituloLabel.setForeground(new java.awt.Color(0, 51, 102));
            dashboardPanel.add(tituloLabel, BorderLayout.NORTH);

            // Panel de información
            javax.swing.JPanel infoPanel = new javax.swing.JPanel();
            infoPanel.setLayout(new java.awt.GridLayout(3, 1, 10, 10));
            infoPanel.setBorder(javax.swing.BorderFactory.createEmptyBorder(50, 50, 50, 50));

            // Información del sistema
            javax.swing.JLabel info1 = new javax.swing.JLabel("• Gestión completa de usuarios y roles", javax.swing.JLabel.LEFT);
            javax.swing.JLabel info2 = new javax.swing.JLabel("• Historial detallado del sistema", javax.swing.JLabel.LEFT);
            javax.swing.JLabel info3 = new javax.swing.JLabel("• Control de peticiones de stock", javax.swing.JLabel.LEFT);

            info1.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.PLAIN, 16));
            info2.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.PLAIN, 16));
            info3.setFont(new java.awt.Font("Liberation Sans", java.awt.Font.PLAIN, 16));

            infoPanel.add(info1);
            infoPanel.add(info2);
            infoPanel.add(info3);

            dashboardPanel.add(infoPanel, BorderLayout.CENTER);

            // Agregar al desktop pane
            javax.swing.JInternalFrame dashboardFrame = new javax.swing.JInternalFrame("Dashboard", false, false, false, false);
            dashboardFrame.getContentPane().add(dashboardPanel);
            dashboardFrame.setSize(600, 400);
            dashboardFrame.setVisible(true);
            desktopPane.add(dashboardFrame);

            // Centrar
            dashboardFrame.setLocation(
                    (desktopPane.getWidth() - dashboardFrame.getWidth()) / 2,
                    (desktopPane.getHeight() - dashboardFrame.getHeight()) / 2
            );

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al mostrar dashboard: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cerrarTodosLosFormularios() {
        javax.swing.JInternalFrame[] frames = desktopPane.getAllFrames();
        for (javax.swing.JInternalFrame frame : frames) {
            frame.dispose();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuBar1 = new javax.swing.JMenuBar();
        menuSistema = new javax.swing.JMenu();
        menuItemDashboard = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        menuItemCerrar = new javax.swing.JMenuItem();
        menuAdministracion = new javax.swing.JMenu();
        menuItemPeticiones = new javax.swing.JMenuItem();
        menuItemHistorial = new javax.swing.JMenuItem();
        menuItemUsuarios = new javax.swing.JMenuItem();
        menuAyuda = new javax.swing.JMenu();
        menuItemAcercaDe = new javax.swing.JMenuItem();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Panel de Administración");
        setPreferredSize(new java.awt.Dimension(1200, 700));

        menuSistema.setText("Sistema");

        menuItemDashboard.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_D, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItemDashboard.setIcon(new javax.swing.ImageIcon("/home/emerson/IdeaProjects/Licoreria/src/main/java/com/mycompany/licoreria/assets/dashboard.png")); // NOI18N
        menuItemDashboard.setText("Dashboard");
        menuItemDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemDashboardActionPerformed(evt);
            }
        });
        menuSistema.add(menuItemDashboard);
        menuSistema.add(jSeparator1);

        menuItemCerrar.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Q, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItemCerrar.setIcon(new javax.swing.ImageIcon("/home/emerson/IdeaProjects/Licoreria/src/main/java/com/mycompany/licoreria/assets/exit.png")); // NOI18N
        menuItemCerrar.setText("Cerrar");
        menuItemCerrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemCerrarActionPerformed(evt);
            }
        });
        menuSistema.add(menuItemCerrar);

        jMenuBar1.add(menuSistema);

        menuAdministracion.setText("Administración");

        menuItemPeticiones.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_P, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItemPeticiones.setIcon(new javax.swing.ImageIcon("/home/emerson/IdeaProjects/Licoreria/src/main/java/com/mycompany/licoreria/assets/requests.png")); // NOI18N
        menuItemPeticiones.setText("Gestión de Peticiones");
        menuItemPeticiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemPeticionesActionPerformed(evt);
            }
        });
        menuAdministracion.add(menuItemPeticiones);

        menuItemHistorial.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_H, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItemHistorial.setIcon(new javax.swing.ImageIcon("/home/emerson/IdeaProjects/Licoreria/src/main/java/com/mycompany/licoreria/assets/history.png")); // NOI18N
        menuItemHistorial.setText("Historial del Sistema");
        menuItemHistorial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemHistorialActionPerformed(evt);
            }
        });
        menuAdministracion.add(menuItemHistorial);

        menuItemUsuarios.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_U, java.awt.event.InputEvent.CTRL_DOWN_MASK));
        menuItemUsuarios.setIcon(new javax.swing.ImageIcon("/home/emerson/IdeaProjects/Licoreria/src/main/java/com/mycompany/licoreria/assets/users.png")); // NOI18N
        menuItemUsuarios.setText("Gestión de Usuarios");
        menuItemUsuarios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemUsuariosActionPerformed(evt);
            }
        });
        menuAdministracion.add(menuItemUsuarios);

        jMenuBar1.add(menuAdministracion);

        menuAyuda.setText("Ayuda");

        menuItemAcercaDe.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        menuItemAcercaDe.setIcon(new javax.swing.ImageIcon("/home/emerson/IdeaProjects/Licoreria/src/main/java/com/mycompany/licoreria/assets/about.png")); // NOI18N
        menuItemAcercaDe.setText("Acerca de...");
        menuItemAcercaDe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                menuItemAcercaDeActionPerformed(evt);
            }
        });
        menuAyuda.add(menuItemAcercaDe);

        jMenuBar1.add(menuAyuda);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 1184, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 651, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void menuItemUsuariosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemUsuariosActionPerformed
        abrirGestionUsuarios();
    }//GEN-LAST:event_menuItemUsuariosActionPerformed

    private void menuItemHistorialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemHistorialActionPerformed
        abrirHistorial();
    }//GEN-LAST:event_menuItemHistorialActionPerformed

    private void menuItemPeticionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemPeticionesActionPerformed
        abrirGestionPeticiones();
    }//GEN-LAST:event_menuItemPeticionesActionPerformed

    private void menuItemDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemDashboardActionPerformed
        cerrarTodosLosFormularios();
        mostrarDashboard();
    }//GEN-LAST:event_menuItemDashboardActionPerformed

    private void menuItemCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemCerrarActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de que desea cerrar el panel de administración?",
                "Confirmar cierre",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_menuItemCerrarActionPerformed

    private void menuItemAcercaDeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_menuItemAcercaDeActionPerformed
        JOptionPane.showMessageDialog(this,
                "Sistema de Gestión - Licorería\n" +
                        "Versión 1.0\n" +
                        "Desarrollado por: Christopher\n" +
                        "© 2024 Todos los derechos reservados\n\n" +
                        "Funcionalidades:\n" +
                        "• Gestión de usuarios y roles\n" +
                        "• Historial del sistema\n" +
                        "• Control de peticiones\n" +
                        "• Sistema de inventario",
                "Acerca del Sistema",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_menuItemAcercaDeActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JMenu menuAdministracion;
    private javax.swing.JMenu menuAyuda;
    private javax.swing.JMenuItem menuItemAcercaDe;
    private javax.swing.JMenuItem menuItemCerrar;
    private javax.swing.JMenuItem menuItemDashboard;
    private javax.swing.JMenuItem menuItemHistorial;
    private javax.swing.JMenuItem menuItemPeticiones;
    private javax.swing.JMenuItem menuItemUsuarios;
    private javax.swing.JMenu menuSistema;
    // End of variables declaration//GEN-END:variables
}