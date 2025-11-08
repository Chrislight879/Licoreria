package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.LoginController;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.JOptionPane;
import java.awt.event.KeyEvent;

public class login extends javax.swing.JInternalFrame {
    private LoginController loginController;

    public login() {
        initComponents();
        loginController = new LoginController();
        setupListeners();
        setTitle("Inicio de Sesión - Licorería El Bolo Feliz");
    }

    private void setupListeners() {
        // Enter en campo de password también ejecuta login
        txtPassword.addActionListener(e -> realizarLogin());

        // Enter en campo de username pasa al password
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    private void realizarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validaciones
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese su nombre de usuario",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Por favor ingrese su contraseña",
                    "Campo requerido",
                    JOptionPane.WARNING_MESSAGE);
            txtPassword.requestFocus();
            return;
        }

        try {
            boolean loginExitoso = loginController.iniciarSesion(username, password);

            if (loginExitoso) {
                // Configurar sesión
                SessionManager.iniciarSesion(loginController.getUsuarioLogueado());

                JOptionPane.showMessageDialog(this,
                        "<html><div style='text-align: center;'>" +
                                "<h3>¡Bienvenido " + loginController.getUsernameUsuarioLogueado() + "!</h3>" +
                                "<p>Rol: " + loginController.getRolUsuarioLogueado() + "</p>" +
                                "<p>Ha iniciado sesión exitosamente</p>" +
                                "</div></html>",
                        "Login Exitoso",
                        JOptionPane.INFORMATION_MESSAGE);

                // Cerrar formulario de login
                this.dispose();

                // Mostrar ventana principal según el rol
                mostrarVentanaPrincipal();

            } else {
                JOptionPane.showMessageDialog(this,
                        "<html><div style='text-align: center;'>" +
                                "<h3>Error de autenticación</h3>" +
                                "<p>Usuario o contraseña incorrectos</p>" +
                                "<p>Por favor verifique sus credenciales</p>" +
                                "</div></html>",
                        "Error de Login",
                        JOptionPane.ERROR_MESSAGE);

                // Limpiar campo de password
                txtPassword.setText("");
                txtPassword.requestFocus();
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error del sistema: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void mostrarVentanaPrincipal() {
        // Aquí decides qué ventana mostrar según el rol
        if (loginController.usuarioEsAdministrador()) {
            // Mostrar panel de administración
            AdminMainForm adminForm = new AdminMainForm();
            adminForm.setVisible(true);
            com.mycompany.licoreria.Licoreria.getDesktopPane().add(adminForm);
            com.mycompany.licoreria.Licoreria.centrarFormulario(adminForm);
        } else if (loginController.usuarioEsBodega()) {
            // Mostrar módulo de bodega
            BodegaMainForm bodegaForm = new BodegaMainForm();
            bodegaForm.setVisible(true);
            com.mycompany.licoreria.Licoreria.getDesktopPane().add(bodegaForm);
            com.mycompany.licoreria.Licoreria.centrarFormulario(bodegaForm);
        } else if (loginController.usuarioEsVendedor()) {
            // Mostrar módulo de ventas (puedes crear uno similar)
            JOptionPane.showMessageDialog(this,
                    "Módulo de ventas en desarrollo para vendedores",
                    "Módulo no disponible",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        // Actualizar interfaz principal para mostrar información del usuario
        actualizarInterfazPrincipal();
    }

    private void actualizarInterfazPrincipal() {
        // Puedes actualizar la interfaz principal para mostrar información del usuario
        // Por ejemplo, cambiar el título de la ventana principal
        com.mycompany.licoreria.Licoreria.getMainFrame().setTitle(
                "Sistema de Gestión - Licorería - Usuario: " +
                        loginController.getUsernameUsuarioLogueado() + " (" +
                        loginController.getRolUsuarioLogueado() + ")"
        );
    }

    private void limpiarCampos() {
        txtUsername.setText("");
        txtPassword.setText("");
        txtUsername.requestFocus();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        btnAceptar = new javax.swing.JButton();
        txtUsername = new javax.swing.JTextField();
        txtPassword = new javax.swing.JPasswordField();
        btnCancelar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 51, 153));
        jLabel1.setText("PASSWORD:");

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 48)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 51, 153));
        jLabel2.setText("LICORERIA EL BOLO FELIZ");

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 36)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 51, 153));
        jLabel3.setText("USERNAME:");

        btnAceptar.setBackground(new java.awt.Color(0, 153, 51));
        btnAceptar.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        btnAceptar.setForeground(new java.awt.Color(255, 255, 255));
        btnAceptar.setText("INICIAR SESIÓN");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        txtUsername.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        txtUsername.setToolTipText("Ingrese su nombre de usuario");

        txtPassword.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        txtPassword.setToolTipText("Ingrese su contraseña");

        btnCancelar.setBackground(new java.awt.Color(204, 0, 0));
        btnCancelar.setFont(new java.awt.Font("Liberation Sans", 1, 24)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setText("CANCELAR");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(32, 32, 32)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 63, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(txtUsername, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 604, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtPassword, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 604, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(101, 101, 101))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                        .addContainerGap(266, Short.MAX_VALUE)
                                        .addComponent(jLabel2)
                                        .addGap(213, 213, 213)))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap(265, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtUsername, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel1)
                                        .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(110, 110, 110)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(115, 115, 115))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                        .addGap(48, 48, 48)
                                        .addComponent(jLabel2)
                                        .addContainerGap(627, Short.MAX_VALUE)))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        realizarLogin();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro que desea cancelar el inicio de sesión?",
                "Confirmar cancelación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        realizarLogin();
    }//GEN-LAST:event_txtPasswordActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtUsername;
    // End of variables declaration//GEN-END:variables
}