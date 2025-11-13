package com.mycompany.licoreria;

import com.mycompany.licoreria.formularios.*;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.HashMap;
import java.util.Map;


public class Licoreria {
    private static JFrame frame;
    private static JDesktopPane desktopPane;
    private static JMenuBar menuBar;
    private static Map<String, JMenu> menus;
    private static JLabel statusBar;

    // Colores modernos para la interfaz
    private static final Color COLOR_PRIMARIO = new Color(41, 128, 185);
    private static final Color COLOR_SECUNDARIO = new Color(52, 152, 219);
    private static final Color COLOR_ACENTO = new Color(231, 76, 60);
    private static final Color COLOR_FONDO = new Color(245, 245, 245);
    private static final Color COLOR_TEXTO_OSCURO = new Color(44, 62, 80);
    private static final Color COLOR_EXITO = new Color(39, 174, 96);
    private static final Color COLOR_ADVERTENCIA = new Color(243, 156, 18);

    public static void main(String[] args) {
        // Establecer el look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Personalizar algunos colores de UI
            UIManager.put("Panel.background", COLOR_FONDO);
            UIManager.put("OptionPane.background", COLOR_FONDO);
            UIManager.put("OptionPane.messageForeground", COLOR_TEXTO_OSCURO);

        } catch (Exception e) {
            System.err.println("Error al establecer el look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        // Crear ventana principal con diseño moderno
        frame = new JFrame("Sistema de Gestión - Licorería");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        // Configurar icono de la aplicación
        try {
            // Puedes agregar un icono aquí si tienes uno
            // frame.setIconImage(new ImageIcon("icon.png").getImage());
        } catch (Exception e) {
            System.err.println("Error al cargar icono: " + e.getMessage());
        }

        // Obtener dimensiones de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int)(screenSize.width * 0.9), (int)(screenSize.height * 0.9));
        frame.setLocationRelativeTo(null);

        // Crear desktop pane con fondo moderno
        desktopPane = new JDesktopPane() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                // Fondo con gradiente sutil
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(250, 250, 250),
                        getWidth(), getHeight(), new Color(240, 240, 240)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                // Patrón sutil de puntos
                g2d.setColor(new Color(220, 220, 220, 30));
                for (int i = 0; i < getWidth(); i += 20) {
                    for (int j = 0; j < getHeight(); j += 20) {
                        g2d.fillOval(i, j, 2, 2);
                    }
                }
            }
        };
        desktopPane.setBackground(COLOR_FONDO);

        // Crear barra de estado mejorada
        statusBar = new JLabel(" No logueado - Seleccione 'Iniciar Sesión' para comenzar");
        statusBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        statusBar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        statusBar.setOpaque(true);
        statusBar.setBackground(new Color(240, 240, 240));
        statusBar.setForeground(Color.RED);

        frame.setLayout(new BorderLayout());
        frame.add(desktopPane, BorderLayout.CENTER);
        frame.add(statusBar, BorderLayout.SOUTH);

        // Inicializar menú (inicialmente deshabilitado)
        inicializarMenu();
        actualizarMenuPorRol();

        // Mostrar ventana
        frame.setVisible(true);

        // Auto-abrir el login al iniciar
        abrirFormularioLogin();
    }

    private static void setupDesktopPaneBackground() {
        // Panel principal con diseño moderno
        JPanel panelBienvenida = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fondo con gradiente
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(52, 152, 219, 10),
                        getWidth(), getHeight(), new Color(41, 128, 185, 5)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panelBienvenida.setLayout(new BorderLayout());
        panelBienvenida.setOpaque(false);

        // Panel de contenido centrado
        JPanel contenidoPanel = new JPanel();
        contenidoPanel.setLayout(new BoxLayout(contenidoPanel, BoxLayout.Y_AXIS));
        contenidoPanel.setOpaque(false);
        contenidoPanel.setBorder(BorderFactory.createEmptyBorder(40, 20, 40, 20));

        // Título principal
        JLabel lblTitulo = new JLabel("Sistema de Gestión - Licorería");
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitulo.setForeground(COLOR_TEXTO_OSCURO);
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));

        // Subtítulo
        JLabel lblSubtitulo = new JLabel("Bienvenido al sistema integral de gestión");
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lblSubtitulo.setForeground(new Color(100, 100, 100));
        lblSubtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));

        // Panel de módulos con diseño de tarjetas
        JPanel modulosPanel = new JPanel();
        modulosPanel.setLayout(new GridLayout(0, 2, 20, 20));
        modulosPanel.setOpaque(false);
        modulosPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        modulosPanel.setMaximumSize(new Dimension(800, 400));

        // Crear tarjetas de módulos
        String[][] modulos = {
                {"🔐", "Sistema de Login", "Autenticación segura"},
                {"👥", "Gestión de Usuarios", "Administración completa"},
                {"📊", "Panel de Administración", "Control total del sistema"},
                {"📋", "Historial del Sistema", "Registro de actividades"},
                {"📦", "Módulo de Bodega", "Gestión de inventario"},
                {"🛒", "Punto de Venta", "Sistema de ventas completo"},
                {"📮", "Solicitar Stock", "Peticiones a bodega"},
                {"📈", "Peticiones de Stock", "Gestión de solicitudes"}
        };

        for (String[] modulo : modulos) {
            modulosPanel.add(crearTarjetaModulo(modulo[0], modulo[1], modulo[2]));
        }

        // Mensaje inferior
        JLabel lblMensaje = new JLabel("Seleccione una opción del menú para comenzar");
        lblMensaje.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        lblMensaje.setForeground(new Color(150, 150, 150));
        lblMensaje.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblMensaje.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));

        // Ensamblar componentes
        contenidoPanel.add(lblTitulo);
        contenidoPanel.add(lblSubtitulo);
        contenidoPanel.add(modulosPanel);
        contenidoPanel.add(lblMensaje);

        panelBienvenida.add(contenidoPanel, BorderLayout.CENTER);
        panelBienvenida.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());
        desktopPane.add(panelBienvenida, Integer.valueOf(0));
    }

    private static JPanel crearTarjetaModulo(String icono, String titulo, String descripcion) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(new BorderLayout());
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        tarjeta.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // Agregar sombra sutil
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 4, 2),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                        BorderFactory.createEmptyBorder(15, 15, 15, 15)
                )
        ));

        // Efecto hover mejorado
        tarjeta.addMouseListener(new MouseAdapter() {
            private final Color colorNormal = Color.WHITE;
            private final Color colorHover = new Color(245, 248, 250);

            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setBackground(colorHover);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 4, 2),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(COLOR_SECUNDARIO, 2),
                                BorderFactory.createEmptyBorder(15, 15, 15, 15)
                        )
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tarjeta.setBackground(colorNormal);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(2, 2, 4, 2),
                        BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                                BorderFactory.createEmptyBorder(15, 15, 15, 15)
                        )
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                // Efecto de click
                tarjeta.setBackground(new Color(235, 245, 255));
                Timer timer = new Timer(150, event -> {
                    tarjeta.setBackground(colorHover);
                });
                timer.setRepeats(false);
                timer.start();
            }
        });

        // Icono y título
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        headerPanel.setBackground(Color.WHITE);

        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI", Font.PLAIN, 20));

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblTitulo.setForeground(COLOR_TEXTO_OSCURO);

        headerPanel.add(lblIcono);
        headerPanel.add(lblTitulo);

        // Descripción
        JLabel lblDesc = new JLabel("<html><div style='text-align: left; color: #666; font-size: 11px; margin-top: 8px;'>" + descripcion + "</div></html>");

        tarjeta.add(headerPanel, BorderLayout.NORTH);
        tarjeta.add(lblDesc, BorderLayout.CENTER);

        return tarjeta;
    }

    /**
     * INICIALIZAR ESTRUCTURA DEL MENÚ MEJORADO
     */
    private static void inicializarMenu() {
        menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(2, 0, 2, 0)
        ));

        menus = new HashMap<>();

        // Menú Sistema (siempre visible)
        JMenu menuSistema = crearMenu("Sistema", 'S');
        menus.put("SISTEMA", menuSistema);

        JMenuItem menuItemLogin = crearMenuItem("Iniciar Sesión", 'I', "F1", e -> abrirFormularioLogin());
        JMenuItem menuItemDashboard = crearMenuItem("Dashboard Principal", 'D', "F2", e -> mostrarDashboard());
        JMenuItem menuItemSalir = crearMenuItem("Salir", 'S', "Alt+F4", e -> confirmarSalida());

        menuSistema.add(menuItemLogin);
        menuSistema.add(menuItemDashboard);
        menuSistema.addSeparator();
        menuSistema.add(menuItemSalir);

        // Menú Administración (solo admin)
        JMenu menuAdministracion = crearMenu("Administración", 'A');
        menus.put("ADMINISTRACION", menuAdministracion);

        menuAdministracion.add(crearMenuItem("Panel de Administración", 'P', "Ctrl+A", e -> abrirPanelAdministracion()));
        menuAdministracion.addSeparator();
        menuAdministracion.add(crearMenuItem("Gestión de Usuarios", 'U', "Ctrl+U", e -> abrirFormularioUsuarios()));
        menuAdministracion.add(crearMenuItem("Historial del Sistema", 'H', "Ctrl+H", e -> abrirFormularioHistorial()));
        menuAdministracion.add(crearMenuItem("Peticiones de Stock", 'S', "Ctrl+P", e -> abrirFormularioPeticiones()));

        // Menú Bodega (admin y bodega)
        JMenu menuBodega = crearMenu("Bodega", 'B');
        menus.put("BODEGA", menuBodega);

        menuBodega.add(crearMenuItem("Módulo de Bodega", 'M', "Ctrl+B", e -> abrirModuloBodega()));
        menuBodega.addSeparator();
        menuBodega.add(crearMenuItem("Solicitar a Proveedores", 'P', "Ctrl+Shift+P", e -> abrirBodegaPedirProductos()));
        menuBodega.add(crearMenuItem("Ver Peticiones de Vendedores", 'V', "Ctrl+Shift+V", e -> abrirBodegaVerPeticiones()));

        // Menú Vendedor (admin y vendedor)
        JMenu menuVendedor = crearMenu("Vendedor", 'V');
        menus.put("VENTAS", menuVendedor);

        menuVendedor.add(crearMenuItem("Punto de Venta", 'P', "F9", e -> abrirVenderForm()));
        menuVendedor.add(crearMenuItem("Solicitar Stock a Bodega", 'S', "F10", e -> abrirVendedorPedirForm()));
        menuVendedor.addSeparator();
        menuVendedor.add(crearMenuItem("Módulo Completo de Vendedor", 'M', "F11", e -> abrirVendedorMainForm()));

        // Menú Ayuda (siempre visible)
        JMenu menuAyuda = crearMenu("Ayuda", 'y');
        menuAyuda.add(crearMenuItem("Acerca de...", 'A', "F1", e -> mostrarAcercaDe()));
        menuAyuda.add(crearMenuItem("Manual de Usuario", 'M', "F2", e -> mostrarMensajeEnDesarrollo("Manual de Usuario")));
        menus.put("AYUDA", menuAyuda);

        // Configurar atajos de teclado
        setupKeyboardShortcuts();
    }

    /**
     * CONFIGURAR ACCESOS RÁPIDOS DE TECLADO
     */
    private static void setupKeyboardShortcuts() {
        // Crear mapa de accesos directos
        InputMap inputMap = menuBar.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = menuBar.getActionMap();

        // F1 - Ayuda
        inputMap.put(KeyStroke.getKeyStroke("F1"), "ayuda");
        actionMap.put("ayuda", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarAcercaDe();
            }
        });

        // F2 - Dashboard
        inputMap.put(KeyStroke.getKeyStroke("F2"), "dashboard");
        actionMap.put("dashboard", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mostrarDashboard();
            }
        });

        // Alt+F4 - Salir
        inputMap.put(KeyStroke.getKeyStroke("alt F4"), "salir");
        actionMap.put("salir", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                confirmarSalida();
            }
        });
    }

    /**
     * CREAR MENÚ CON ESTILO MEJORADO
     */
    private static JMenu crearMenu(String texto, char mnemonic) {
        JMenu menu = new JMenu(texto);
        menu.setMnemonic(mnemonic);
        menu.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        menu.setForeground(COLOR_TEXTO_OSCURO);
        return menu;
    }

    /**
     * CREAR ITEM DE MENÚ CON ESTILO MEJORADO
     */
    private static JMenuItem crearMenuItem(String texto, char mnemonic, String accelerator, ActionListener action) {
        JMenuItem menuItem = new JMenuItem(texto);
        menuItem.setMnemonic(mnemonic);
        if (accelerator != null) {
            // Aquí podrías configurar aceleradores si lo deseas
        }
        menuItem.addActionListener(action);
        menuItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        menuItem.setIconTextGap(10);
        return menuItem;
    }

    /**
     * ACTUALIZAR MENÚ SEGÚN ROL DEL USUARIO
     */
    public static void actualizarMenuPorRol() {
        // Limpiar barra de menú
        menuBar.removeAll();

        // Siempre agregar menú Sistema
        menuBar.add(menus.get("SISTEMA"));

        // Verificar permisos para cada módulo
        if (SessionManager.tienePermiso("ADMINISTRACION")) {
            menuBar.add(menus.get("ADMINISTRACION"));
        }

        if (SessionManager.tienePermiso("BODEGA")) {
            menuBar.add(menus.get("BODEGA"));
        }

        if (SessionManager.tienePermiso("VENTAS")) {
            menuBar.add(menus.get("VENTAS"));
        }

        // Siempre agregar menú Ayuda
        menuBar.add(Box.createHorizontalGlue()); // Empuja el último menú a la derecha
        menuBar.add(menus.get("AYUDA"));

        // Actualizar barra de menú
        frame.setJMenuBar(menuBar);
        frame.revalidate();
        frame.repaint();

        // Actualizar barra de estado
        actualizarBarraEstado();
    }

    /**
     * ACTUALIZAR BARRA DE ESTADO CON INFORMACIÓN DEL USUARIO
     */
    private static void actualizarBarraEstado() {
        if (SessionManager.haySesionActiva()) {
            String usuarioInfo = String.format(" 👤 %s | 🎯 %s | ⏰ %s | 📅 %s",
                    SessionManager.getCurrentUser().getUsername(),
                    SessionManager.getRolActual(),
                    SessionManager.getDuracionSesion(),
                    java.time.LocalDate.now().toString()
            );

            statusBar.setText(usuarioInfo);
            statusBar.setForeground(COLOR_EXITO);
            statusBar.setBackground(new Color(220, 245, 220));

            // Agregar icono según el rol
            String iconoRol = SessionManager.esAdministrador() ? "👑" :
                    SessionManager.esBodega() ? "📦" : "🛒";
            statusBar.setText(iconoRol + " " + usuarioInfo);

        } else {
            statusBar.setText(" 🔒 No logueado - Presione F1 para Iniciar Sesión");
            statusBar.setForeground(COLOR_ACENTO);
            statusBar.setBackground(new Color(255, 235, 235));
        }
    }

    /**
     * MÉTODO LLAMADO DESPUÉS DEL LOGIN EXITOSO
     */
    public static void onLoginExitoso() {
        actualizarMenuPorRol();
        mostrarDashboard();

        // Mostrar mensaje de bienvenida mejorado
        String mensajeBienvenida = String.format(
                "<html><div style='text-align: center;'>" +
                        "<div style='background: linear-gradient(135deg, #E8F6F3, #D1F2EB); padding: 20px; border-radius: 10px;'>" +
                        "<h2 style='color: #2E4053; margin: 0;'>¡Bienvenido %s!</h2>" +
                        "<div style='background: white; padding: 15px; border-radius: 8px; margin: 15px 0; border-left: 4px solid %s;'>" +
                        "<p style='margin: 5px 0;'><b>🎯 Rol:</b> %s</p>" +
                        "<p style='margin: 5px 0;'><b>📋 Módulos disponibles:</b></p>%s" +
                        "</div>" +
                        "<p style='color: #566573; font-size: 12px;'>Sistema de Gestión - Licorería</p>" +
                        "</div>" +
                        "</div></html>",
                SessionManager.getCurrentUser().getUsername(),
                SessionManager.esAdministrador() ? "#3498DB" :
                        SessionManager.esBodega() ? "#E67E22" : "#2ECC71",
                SessionManager.getRolActual(),
                obtenerModulosDisponibles()
        );

        JOptionPane.showMessageDialog(frame, mensajeBienvenida, "✅ Login Exitoso", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * OBTENER LISTA DE MÓDULOS DISPONIBLES SEGÚN ROL
     */
    private static String obtenerModulosDisponibles() {
        StringBuilder modulos = new StringBuilder("<ul style='text-align: left; margin: 5px 0; padding-left: 20px;'>");

        if (SessionManager.esAdministrador()) {
            modulos.append("<li>👥 Administración Completa</li>");
            modulos.append("<li>📦 Módulo de Bodega</li>");
            modulos.append("<li>🛒 Módulo de Vendedor</li>");
        } else if (SessionManager.esBodega()) {
            modulos.append("<li>📦 Módulo de Bodega</li>");
        } else if (SessionManager.esVendedor()) {
            modulos.append("<li>🛒 Punto de Venta</li>");
            modulos.append("<li>📮 Solicitar Stock</li>");
        }

        modulos.append("</ul>");
        return modulos.toString();
    }

    /**
     * MÉTODO LLAMADO AL CERRAR SESIÓN
     */
    public static void onLogout() {
        // Cerrar todos los formularios abiertos
        for (JInternalFrame frame : desktopPane.getAllFrames()) {
            frame.dispose();
        }

        actualizarMenuPorRol();
        mostrarDashboard();

        JOptionPane.showMessageDialog(frame,
                "<html><div style='text-align: center;'>" +
                        "<h3 style='color: #566573;'>Sesión cerrada</h3>" +
                        "<p>La sesión se ha cerrado correctamente</p>" +
                        "</div></html>",
                "🔒 Cierre de Sesión",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // ==========================================================================
    // MÉTODOS PARA ABRIR FORMULARIOS
    // ==========================================================================

    private static void abrirFormularioLogin() {
        mostrarFormulario(new login());
    }

    private static void abrirPanelAdministracion() {
        if (!SessionManager.tienePermiso("ADMINISTRACION")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new AdminMainForm());
    }

    private static void abrirFormularioUsuarios() {
        if (!SessionManager.tienePermiso("ADMINISTRACION")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new AdminCrearUsuarios());
    }

    private static void abrirFormularioHistorial() {
        if (!SessionManager.tienePermiso("ADMINISTRACION")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new AdminHistoriall());
    }

    private static void abrirFormularioPeticiones() {
        if (!SessionManager.tienePermiso("ADMINISTRACION")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new AdminVerPeticiones());
    }

    private static void abrirModuloBodega() {
        if (!SessionManager.tienePermiso("BODEGA")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new BodegaMainForm());
    }

    private static void abrirBodegaPedirProductos() {
        if (!SessionManager.tienePermiso("BODEGA")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new BodegaPedirProductos());
    }

    private static void abrirBodegaVerPeticiones() {
        if (!SessionManager.tienePermiso("BODEGA")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new BodegaVerPeticiones());
    }

    private static void abrirVenderForm() {
        if (!SessionManager.tienePermiso("VENTAS")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new VenderForm());
    }

    private static void abrirVendedorPedirForm() {
        if (!SessionManager.tienePermiso("VENTAS")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new VendedorPedirForm());
    }

    private static void abrirVendedorMainForm() {
        if (!SessionManager.tienePermiso("VENTAS")) {
            mostrarErrorPermisos();
            return;
        }
        mostrarFormulario(new VendedorMainForm());
    }

    /**
     * MOSTRAR ERROR POR FALTA DE PERMISOS (mejorado)
     */
    private static void mostrarErrorPermisos() {
        JOptionPane.showMessageDialog(frame,
                "<html><div style='text-align: center;'>" +
                        "<div style='background: #FDEDEC; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                        "<h3 style='color: #C0392B; margin: 0;'>❌ Acceso Denegado</h3>" +
                        "<p style='margin: 10px 0;'>No tiene permisos para acceder a este módulo.</p>" +
                        "<p style='margin: 5px 0;'><b>Rol actual:</b> " + SessionManager.getRolActual() + "</p>" +
                        "</div>" +
                        "</div></html>",
                "Permisos Insuficientes",
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * MÉTODO GENÉRICO PARA MOSTRAR FORMULARIOS
     */
    public static void mostrarFormulario(javax.swing.JInternalFrame formulario) {
        // Verificar si ya está abierto
        for (javax.swing.JInternalFrame window : desktopPane.getAllFrames()) {
            if (window.getClass().equals(formulario.getClass())) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    window.toFront();
                    return;
                } catch (Exception e) {
                    System.err.println("Error al seleccionar formulario existente: " + e.getMessage());
                }
            }
        }

        // Si no está abierto, crear nuevo
        try {
            formulario.setVisible(true);
            desktopPane.add(formulario);
            centrarFormulario(formulario);
            formulario.setSelected(true);
            formulario.moveToFront();
        } catch (Exception e) {
            System.err.println("Error al mostrar formulario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * CENTRAR FORMULARIO EN EL DESKTOP
     */
    public static void centrarFormulario(javax.swing.JInternalFrame formulario) {
        try {
            Dimension desktopSize = desktopPane.getSize();
            Dimension formSize = formulario.getSize();

            // Ajustar tamaño si es muy grande
            if (formSize.width > desktopSize.width * 0.9 || formSize.height > desktopSize.height * 0.9) {
                formulario.setSize(
                        (int)(desktopSize.width * 0.8),
                        (int)(desktopSize.height * 0.8)
                );
                formSize = formulario.getSize();
            }

            formulario.setLocation(
                    (desktopSize.width - formSize.width) / 2,
                    (desktopSize.height - formSize.height) / 2
            );
        } catch (Exception e) {
            System.err.println("Error al centrar formulario: " + e.getMessage());
        }
    }

    /**
     * MOSTRAR DASHBOARD PRINCIPAL
     */
    private static void mostrarDashboard() {
        desktopPane.removeAll();
        setupDesktopPaneBackground();
        desktopPane.revalidate();
        desktopPane.repaint();
    }

    /**
     * CONFIRMAR SALIDA DEL SISTEMA (mejorado)
     */
    private static void confirmarSalida() {
        int confirmacion = JOptionPane.showConfirmDialog(
                frame,
                "<html><div style='text-align: center;'>" +
                        "<div style='background: #FEF9E7; padding: 15px; border-radius: 8px; border-left: 4px solid #F39C12;'>" +
                        "<h3 style='color: #D35400; margin: 0;'>¿Está seguro que desea salir del sistema?</h3>" +
                        "<p style='margin: 10px 0;'>Todos los formularios abiertos se cerrarán.</p>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar salida",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    /**
     * MENSAJE PARA MÓDULOS EN DESARROLLO (mejorado)
     */
    private static void mostrarMensajeEnDesarrollo(String modulo) {
        JOptionPane.showMessageDialog(
                frame,
                "<html><div style='text-align: center;'>" +
                        "<div style='background: #F4F6F6; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<h3 style='color: #2C3E50; margin: 0;'>🚧 Módulo en Desarrollo</h3>" +
                        "<p style='margin: 10px 0;'>El módulo <b>'" + modulo + "'</b> está actualmente en desarrollo.</p>" +
                        "<p style='margin: 5px 0;'>Estará disponible en próximas actualizaciones del sistema.</p>" +
                        "</div>" +
                        "</div></html>",
                "Módulo en desarrollo",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * ACERCA DEL SISTEMA (mejorado)
     */
    private static void mostrarAcercaDe() {
        JOptionPane.showMessageDialog(
                frame,
                "<html><div style='text-align: center; max-width: 500px;'>" +
                        "<div style='background: linear-gradient(135deg, #E8F4FD, #D6EAF8); padding: 20px; border-radius: 10px;'>" +
                        "<h2 style='color: #2C3E50; margin: 0 0 15px 0;'>Sistema de Gestión - Licorería</h2>" +
                        "<div style='background: white; padding: 15px; border-radius: 8px; margin: 10px 0;'>" +
                        "<p style='margin: 5px 0;'><b>🎯 Versión:</b> 3.0</p>" +
                        "<p style='margin: 5px 0;'><b>👨‍💻 Desarrollado por:</b> Emerson</p>" +
                        "<p style='margin: 5px 0;'><b>📅 Fecha:</b> Noviembre 2024</p>" +
                        "</div>" +
                        "<h3 style='color: #2C3E50; margin: 15px 0 10px 0;'>🚀 Módulos Implementados:</h3>" +
                        "<div style='background: white; padding: 15px; border-radius: 8px; text-align: left;'>" +
                        "<p style='margin: 3px 0;'>🔐 <b>Sistema de Login</b> - Autenticación segura</p>" +
                        "<p style='margin: 3px 0;'>👥 <b>Gestión de Usuarios</b> - CRUD completo con roles</p>" +
                        "<p style='margin: 3px 0;'>📊 <b>Panel de Administración</b> - Interfaz MDI completa</p>" +
                        "<p style='margin: 3px 0;'>📋 <b>Historial del Sistema</b> - Registro de actividades</p>" +
                        "<p style='margin: 3px 0;'>📦 <b>Módulo de Bodega</b> - Gestión completa de inventario</p>" +
                        "<p style='margin: 3px 0;'>🛒 <b>Punto de Venta</b> - Sistema de ventas completo</p>" +
                        "<p style='margin: 3px 0;'>📮 <b>Solicitar Stock</b> - Peticiones a bodega</p>" +
                        "<p style='margin: 3px 0;'>📈 <b>Peticiones de Stock</b> - Gestión de solicitudes</p>" +
                        "<p style='margin: 3px 0;'>🔄 <b>Módulo de Vendedor</b> - Interfaz completa para vendedores</p>" +
                        "</div>" +
                        "<p style='color: #7F8C8D; font-size: 11px; margin: 15px 0 0 0;'>Sistema desarrollado con Java Swing y MySQL</p>" +
                        "</div>" +
                        "</div></html>",
                "Acerca del Sistema",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * SISTEMA DE NOTIFICACIONES TEMPORALES
     */
    public static void mostrarNotificacion(String mensaje, String tipo) {
        JPanel notificacion = new JPanel(new BorderLayout());
        notificacion.setBackground(tipo.equals("exito") ? COLOR_EXITO :
                tipo.equals("error") ? COLOR_ACENTO : COLOR_ADVERTENCIA);
        notificacion.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel lblMensaje = new JLabel(mensaje);
        lblMensaje.setForeground(Color.WHITE);
        lblMensaje.setFont(new Font("Segoe UI", Font.BOLD, 12));

        notificacion.add(lblMensaje, BorderLayout.CENTER);

        // Posicionar en la esquina superior derecha
        notificacion.setBounds(
                desktopPane.getWidth() - 300,
                10,
                280,
                50
        );

        desktopPane.add(notificacion, Integer.valueOf(Integer.MAX_VALUE));
        desktopPane.revalidate();
        desktopPane.repaint();

        // Auto-ocultar después de 3 segundos
        Timer timer = new Timer(3000, e -> {
            desktopPane.remove(notificacion);
            desktopPane.revalidate();
            desktopPane.repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    // ==========================================================================
    // GETTERS PÚBLICOS
    // ==========================================================================

    /**
     * OBTENER FRAME PRINCIPAL
     */
    public static JFrame getMainFrame() {
        return frame;
    }

    /**
     * OBTENER DESKTOP PANE
     */
    public static JDesktopPane getDesktopPane() {
        return desktopPane;
    }
}