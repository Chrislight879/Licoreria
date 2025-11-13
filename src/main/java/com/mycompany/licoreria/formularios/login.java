package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.LoginController;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class login extends JInternalFrame {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnCancel;
    private JCheckBox chkShowPassword;
    private LoginController loginController;

    // Nueva paleta de colores azules
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); // SteelBlue - azul principal
    private final Color SECONDARY_COLOR = new Color(100, 149, 237); // CornflowerBlue - azul claro
    private final Color ACCENT_COLOR = new Color(30, 144, 255); // DodgerBlue - azul brillante
    private final Color BACKGROUND_COLOR = new Color(240, 248, 255); // AliceBlue - fondo azul claro
    private final Color TEXT_COLOR = Color.WHITE; // TODOS LOS TEXTOS EN BLANCO
    private final Color BORDER_COLOR = new Color(176, 196, 222); // LightSteelBlue - borde azul
    private final Color PLACEHOLDER_COLOR = new Color(220, 220, 255); // Placeholder en blanco azulado claro
    private final Color SUCCESS_COLOR = new Color(173, 216, 230); // LightBlue - azul suave
    private final Color ERROR_COLOR = new Color(135, 206, 250); // LightSkyBlue - azul claro

    public login() {
        initComponents();
        setupModernDesign();
        loginController = new LoginController();
    }

    private void initComponents() {
        setTitle("Iniciar Sesión - Sistema Licorería");
        setClosable(true);
        setResizable(false);
        setMaximizable(false);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        // REDUCIDO el tamaño total del formulario
        setSize(400, 500);
        setLayout(new BorderLayout());

        // Panel principal con diseño azul
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Header REDUCIDO
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Form panel con diseño azul
        JPanel formPanel = createFormPanel();
        mainPanel.add(formPanel, BorderLayout.CENTER);

        // Footer REDUCIDO
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Centrar en el desktop
        centrarEnDesktop();

        // Agregar listener para cerrar sesión cuando se cierre el formulario
        addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                // Si no hay sesión activa y se cierra el login, mostrar mensaje
                if (!SessionManager.haySesionActiva()) {
                    mostrarMensajeDespedida();
                }
            }
        });
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        // REDUCIDO el padding del header
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));

        // Panel de contenido del header
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(0, 0, 0, 0));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icono de la aplicación REDUCIDO
        JLabel iconLabel = new JLabel("🔐");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36)); // REDUCIDO de 48 a 36
        iconLabel.setForeground(Color.WHITE);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // REDUCIDO

        // Título principal REDUCIDO
        JLabel titleLabel = new JLabel("Bienvenido");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // REDUCIDO de 32 a 24
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Subtítulo REDUCIDO
        JLabel subtitleLabel = new JLabel("Inicie sesión en su cuenta");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12)); // REDUCIDO de 14 a 12
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setBorder(BorderFactory.createEmptyBorder(3, 0, 0, 0)); // REDUCIDO

        contentPanel.add(iconLabel);
        contentPanel.add(titleLabel);
        contentPanel.add(subtitleLabel);

        headerPanel.add(contentPanel, BorderLayout.CENTER);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(new Color(30, 60, 90)); // Fondo azul oscuro para mejor contraste
        // REDUCIDO el padding del form panel
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 1, 0, 1, new Color(100, 130, 180)),
                BorderFactory.createEmptyBorder(25, 30, 20, 30) // REDUCIDO
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6); // REDUCIDO

        // Usuario
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblUser = new JLabel("Usuario");
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUser.setForeground(Color.WHITE);
        formPanel.add(lblUser, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 6, 12, 6); // REDUCIDO
        txtUsername = new JTextField();
        txtUsername.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 255), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12) // REDUCIDO
        ));
        txtUsername.setBackground(new Color(50, 80, 120));
        txtUsername.setForeground(Color.WHITE);
        txtUsername.setCaretColor(Color.WHITE);
        txtUsername.setOpaque(true);

        // Placeholder mejorado
        txtUsername.setText("Ingrese su usuario");
        txtUsername.setForeground(new Color(200, 220, 255));
        txtUsername.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtUsername.getText().equals("Ingrese su usuario")) {
                    txtUsername.setText("");
                    txtUsername.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtUsername.getText().isEmpty()) {
                    txtUsername.setText("Ingrese su usuario");
                    txtUsername.setForeground(new Color(200, 220, 255));
                }
            }
        });

        formPanel.add(txtUsername, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        gbc.insets = new Insets(6, 6, 6, 6); // REDUCIDO
        JLabel lblPassword = new JLabel("Contraseña");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassword.setForeground(Color.WHITE);
        formPanel.add(lblPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 6, 8, 6); // REDUCIDO
        txtPassword = new JPasswordField();
        txtPassword.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 255), 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12) // REDUCIDO
        ));
        txtPassword.setBackground(new Color(50, 80, 120));
        txtPassword.setForeground(Color.WHITE);
        txtPassword.setCaretColor(Color.WHITE);
        txtPassword.setOpaque(true);
        txtPassword.setEchoChar('•');

        // Placeholder para contraseña
        txtPassword.setEchoChar((char) 0);
        txtPassword.setText("Ingrese su contraseña");
        txtPassword.setForeground(new Color(200, 220, 255));
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (new String(txtPassword.getPassword()).equals("Ingrese su contraseña")) {
                    txtPassword.setText("");
                    txtPassword.setEchoChar('•');
                    txtPassword.setForeground(Color.WHITE);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtPassword.getPassword().length == 0) {
                    txtPassword.setEchoChar((char) 0);
                    txtPassword.setText("Ingrese su contraseña");
                    txtPassword.setForeground(new Color(200, 220, 255));
                }
            }
        });

        formPanel.add(txtPassword, gbc);

        // Panel para mostrar contraseña
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(0, 6, 20, 6); // REDUCIDO
        JPanel checkPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        checkPanel.setBackground(new Color(30, 60, 90));

        chkShowPassword = new JCheckBox(" Mostrar contraseña");
        chkShowPassword.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        chkShowPassword.setBackground(new Color(30, 60, 90));
        chkShowPassword.setForeground(Color.WHITE);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.addActionListener(e -> togglePasswordVisibility());
        checkPanel.add(chkShowPassword);

        formPanel.add(checkPanel, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 6, 6, 6); // REDUCIDO

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0)); // REDUCIDO espacio
        buttonPanel.setBackground(new Color(30, 60, 90));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0)); // REDUCIDO

        btnLogin = new ModernButton("Iniciar Sesión", ACCENT_COLOR);
        btnLogin.addActionListener(e -> realizarLogin());

        btnCancel = new ModernButton("Cancelar", new Color(120, 140, 160));
        btnCancel.addActionListener(e -> dispose());

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnCancel);

        formPanel.add(buttonPanel, gbc);

        // Eventos de teclado
        setupKeyboardEvents();

        return formPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBackground(new Color(0, 0, 0, 0));
        // REDUCIDO el padding del footer
        footerPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(0, 0, 0, 0));
        contentPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel versionLabel = new JLabel("Sistema de Gestión Licorería v3.0");
        versionLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // REDUCIDO
        versionLabel.setForeground(Color.WHITE);
        versionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel copyrightLabel = new JLabel("© 2024 Todos los derechos reservados");
        copyrightLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9)); // REDUCIDO
        copyrightLabel.setForeground(Color.WHITE);
        copyrightLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        copyrightLabel.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0)); // REDUCIDO

        contentPanel.add(versionLabel);
        contentPanel.add(copyrightLabel);

        footerPanel.add(contentPanel, BorderLayout.CENTER);

        return footerPanel;
    }

    private void setupModernDesign() {
        // Agregar borde azul sutil
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(176, 196, 222, 150), 1),
                BorderFactory.createEmptyBorder(4, 4, 4, 4) // REDUCIDO
        ));

        // Hacer la ventana no redimensionable para mantener el diseño
        setResizable(false);
    }

    private void setupKeyboardEvents() {
        // Enter para login
        txtUsername.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        });

        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    realizarLogin();
                }
            }
        });

        // Atajos de teclado
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        actionMap.put("cancel", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Ctrl+L para focus en login
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_L, InputEvent.CTRL_DOWN_MASK), "focusUser");
        actionMap.put("focusUser", new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                txtUsername.requestFocus();
                txtUsername.selectAll();
            }
        });
    }

    private void realizarLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        // Validar placeholders
        if (username.equals("Ingrese su usuario") || username.isEmpty()) {
            showError("Debe ingresar un nombre de usuario");
            txtUsername.requestFocus();
            txtUsername.selectAll();
            return;
        }

        if (password.equals("Ingrese su contraseña") || password.isEmpty()) {
            showError("Debe ingresar una contraseña");
            txtPassword.requestFocus();
            txtPassword.selectAll();
            return;
        }

        // Mostrar loading
        btnLogin.setText("Conectando...");
        btnLogin.setEnabled(false);
        btnCancel.setEnabled(false);
        chkShowPassword.setEnabled(false);

        // Animación de carga
        startLoadingAnimation();

        // Ejecutar en hilo separado para no bloquear la UI
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    Thread.sleep(1000); // Simular latencia de red
                    return loginController.iniciarSesion(username, password);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void done() {
                stopLoadingAnimation();
                try {
                    boolean success = get();
                    if (success) {
                        showSuccess("¡Bienvenido " + username + "!");
                        SessionManager.iniciarSesion(loginController.getUsuarioLogueado());

                        // ACTUALIZACIÓN: LLAMAR AL MÉTODO DE LA CLASE PRINCIPAL
                        com.mycompany.licoreria.Licoreria.onLoginExitoso();

                        dispose();
                    } else {
                        showError("Credenciales incorrectas. Verifique su usuario y contraseña.");
                        txtPassword.setText("");
                        txtPassword.requestFocus();
                    }
                } catch (Exception e) {
                    showError("Error de conexión: " + e.getMessage());
                } finally {
                    btnLogin.setText("Iniciar Sesión");
                    btnLogin.setEnabled(true);
                    btnCancel.setEnabled(true);
                    chkShowPassword.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void togglePasswordVisibility() {
        if (chkShowPassword.isSelected()) {
            txtPassword.setEchoChar((char) 0);
        } else {
            // Solo cambiar a bullets si no es el placeholder
            if (!new String(txtPassword.getPassword()).equals("Ingrese su contraseña")) {
                txtPassword.setEchoChar('•');
            }
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>❌ Error</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Error de Autenticación",
                JOptionPane.ERROR_MESSAGE
        );
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #27AE60;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>✅ Éxito</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Inicio de Sesión Exitoso",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void mostrarMensajeDespedida() {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>👋 Hasta Pronto</div>" +
                        "<div style='color: #ECF0F1;'>Puede volver a abrir el login desde el menú Sistema</div>" +
                        "</div>" +
                        "</div></html>",
                "Sesión Cerrada",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void centrarEnDesktop() {
        try {
            com.mycompany.licoreria.Licoreria.centrarFormulario(this);
        } catch (Exception e) {
            // Si falla el centrado, continuar sin él
        }
    }

    private void startLoadingAnimation() {
        // Cambiar cursor a waiting
        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        // Agregar efecto visual de carga
        btnLogin.setBackground(ACCENT_COLOR.darker());
    }

    private void stopLoadingAnimation() {
        // Restaurar cursor normal
        setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // Restaurar color del botón
        btnLogin.setBackground(ACCENT_COLOR);
    }

    // Clases internas para componentes modernos con tema azul
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Gradiente azul moderno
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(70, 130, 180),
                    getWidth(), getHeight(), new Color(100, 149, 237)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Elementos decorativos sutiles
            g2d.setColor(new Color(255, 255, 255, 20));
            // Círculos decorativos en esquinas
            g2d.fillOval(-50, -50, 150, 150);
            g2d.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);

            // Líneas decorativas
            g2d.setColor(new Color(255, 255, 255, 30));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
        }
    }

    class ModernButton extends JButton {
        private Color originalColor;

        public ModernButton(String text, Color color) {
            super(text);
            this.originalColor = color;

            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setBackground(color);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18)); // REDUCIDO
            setOpaque(true);

            // Efecto hover mejorado con tema azul
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor.darker());
                        setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(originalColor.darker().darker(), 1),
                                BorderFactory.createEmptyBorder(11, 17, 11, 17) // REDUCIDO
                        ));
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor);
                        setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18)); // REDUCIDO
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor.darker().darker());
                    }
                }
            });
        }
    }
}