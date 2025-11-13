package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

import static javax.swing.BorderFactory.createEmptyBorder;

public class AdminMainForm extends JInternalFrame {
    // Paleta de colores azules mejorada
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); // SteelBlue - azul principal
    private final Color SECONDARY_COLOR = new Color(50, 90, 140); // Azul oscuro para sidebar
    private final Color ACCENT_COLOR = new Color(30, 144, 255); // DodgerBlue - azul brillante
    private final Color BACKGROUND_COLOR = new Color(30, 40, 60); // Azul oscuro para fondo
    private final Color CARD_BACKGROUND = new Color(40, 55, 80); // Azul medio para tarjetas
    private final Color BORDER_COLOR = new Color(100, 130, 180); // Borde azul
    private final Color TEXT_WHITE = Color.WHITE; // TODOS LOS TEXTOS EN BLANCO
    private final Color SUCCESS_COLOR = new Color(86, 202, 133); // Verde azulado para éxitos
    private final Color WARNING_COLOR = new Color(255, 193, 87); // Amarillo dorado para advertencias
    private final Color DANGER_COLOR = new Color(255, 118, 117); // Rojo coral para peligros

    private JPanel sidebarPanel;
    private JPanel contentPanel;
    private JLabel lblWelcome;
    private JLabel lblUserRole;

    // Cards de estadísticas
    private JPanel statsPanel;
    private JLabel lblTotalUsers, lblActiveUsers, lblPendingRequests, lblLowStock;

    public AdminMainForm() {
        initComponents();
        setupModernDesign();
        loadDashboardData();
    }

    private void initComponents() {
        setTitle("Panel de Administración - Sistema Licorería");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1200, 700);
        setLayout(new BorderLayout());

        // Crear layout principal
        createMainLayout();

        // Centrar en el desktop
        centrarEnDesktop();
    }

    private void createMainLayout() {
        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Content area (Sidebar + Main Content)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(3);
        splitPane.setBorder(createEmptyBorder());
        splitPane.setBackground(BACKGROUND_COLOR);

        // Sidebar
        sidebarPanel = createSidebarPanel();
        splitPane.setLeftComponent(sidebarPanel);

        // Content Panel
        contentPanel = createContentPanel();
        splitPane.setRightComponent(contentPanel);

        mainPanel.add(splitPane, BorderLayout.CENTER);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                createEmptyBorder(15, 25, 15, 25)
        ));

        // Logo y título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JLabel iconLabel = new JLabel("🏢");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        iconLabel.setForeground(TEXT_WHITE);

        JLabel titleLabel = new JLabel("Panel de Administración");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_WHITE);

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(10));
        titlePanel.add(titleLabel);

        // Información del usuario
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(0, 0, 0, 0));

        lblWelcome = new JLabel("Bienvenido: " + SessionManager.getCurrentUser().getUsername());
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcome.setForeground(TEXT_WHITE);

        lblUserRole = new JLabel("Rol: " + SessionManager.getCurrentUser().getRolTitulo());
        lblUserRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUserRole.setForeground(new Color(200, 220, 255));

        JButton btnLogout = new ModernButton("🚪 Cerrar Sesión", DANGER_COLOR);
        btnLogout.setPreferredSize(new Dimension(140, 35));
        btnLogout.addActionListener(e -> confirmarLogout());

        userPanel.add(lblWelcome);
        userPanel.add(Box.createHorizontalStrut(10));
        userPanel.add(lblUserRole);
        userPanel.add(Box.createHorizontalStrut(20));
        userPanel.add(btnLogout);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(SECONDARY_COLOR);
        sidebar.setBorder(createEmptyBorder(20, 0, 20, 0));

        // Título del sidebar
        JLabel sidebarTitle = new JLabel("NAVEGACIÓN");
        sidebarTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        sidebarTitle.setForeground(new Color(180, 200, 255));
        sidebarTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarTitle.setBorder(createEmptyBorder(0, 0, 20, 0));

        sidebar.add(sidebarTitle);

        // Botones del sidebar
        String[] menuItems = {
                "📊 Dashboard Principal",
                "👥 Gestión de Usuarios",
                "📦 Gestión de Productos",
                "📋 Historial del Sistema",
                "📈 Peticiones de Stock",
                "📊 Reportes y Estadísticas",
                "⚙️ Configuración del Sistema"
        };

        String[] icons = {"📊", "👥", "📦", "📋", "📈", "📊", "⚙️"};

        for (int i = 0; i < menuItems.length; i++) {
            JButton menuButton = createMenuButton(menuItems[i], icons[i]);
            menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            final int index = i;
            menuButton.addActionListener(e -> handleMenuSelection(index));
            sidebar.add(menuButton);
            sidebar.add(Box.createVerticalStrut(8));
        }

        // Espacio flexible
        sidebar.add(Box.createVerticalGlue());

        // Información del sistema
        JPanel systemInfoPanel = new JPanel();
        systemInfoPanel.setLayout(new BoxLayout(systemInfoPanel, BoxLayout.Y_AXIS));
        systemInfoPanel.setBackground(new Color(35, 65, 100));
        systemInfoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                createEmptyBorder(10, 15, 10, 15)
        ));
        systemInfoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel systemTitle = new JLabel("Estado del Sistema");
        systemTitle.setFont(new Font("Segoe UI", Font.BOLD, 11));
        systemTitle.setForeground(TEXT_WHITE);
        systemTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel statusLabel = new JLabel("✅ Todo funcionando");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        statusLabel.setForeground(SUCCESS_COLOR);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        systemInfoPanel.add(systemTitle);
        systemInfoPanel.add(Box.createVerticalStrut(5));
        systemInfoPanel.add(statusLabel);

        sidebar.add(systemInfoPanel);

        return sidebar;
    }

    private JButton createMenuButton(String text, String icon) {
        JButton button = new JButton("<html><div style='text-align: left;'>" + icon + " " + text + "</div></html>");
        button.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        button.setForeground(TEXT_WHITE);
        button.setBackground(new Color(0, 0, 0, 0));
        button.setBorder(createEmptyBorder(12, 20, 12, 20));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(230, 45));

        // Efecto hover
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(ACCENT_COLOR);
                button.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(ACCENT_COLOR, 1),
                        createEmptyBorder(11, 19, 11, 19)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(0, 0, 0, 0));
                button.setBorder(createEmptyBorder(12, 20, 12, 20));
            }
        });

        return button;
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(new Color(0, 0, 0, 0));

        // Panel de estadísticas
        statsPanel = createStatsPanel();
        content.add(statsPanel, BorderLayout.NORTH);

        // Panel principal del dashboard
        JPanel dashboardPanel = createDashboardPanel();
        content.add(dashboardPanel, BorderLayout.CENTER);

        return content;
    }

    private JPanel createStatsPanel() {
        JPanel stats = new JPanel(new GridLayout(1, 4, 15, 0));
        stats.setBackground(new Color(0, 0, 0, 0));
        stats.setBorder(createEmptyBorder(20, 20, 20, 20));

        // Cards de estadísticas
        StatCard usersCard = new StatCard("👥 Total Usuarios", "0", "Usuarios activos en el sistema", PRIMARY_COLOR);
        StatCard activeCard = new StatCard("✅ Usuarios Activos", "0", "Sesiones activas ahora", SUCCESS_COLOR);
        StatCard requestsCard = new StatCard("📋 Peticiones Pendientes", "0", "Esperando aprobación", WARNING_COLOR);
        StatCard stockCard = new StatCard("📦 Stock Bajo", "0", "Productos con stock crítico", DANGER_COLOR);

        stats.add(usersCard);
        stats.add(activeCard);
        stats.add(requestsCard);
        stats.add(stockCard);

        // Referencias para actualizar
        lblTotalUsers = ((JLabel)usersCard.getComponent(1));
        lblActiveUsers = ((JLabel)activeCard.getComponent(1));
        lblPendingRequests = ((JLabel)requestsCard.getComponent(1));
        lblLowStock = ((JLabel)stockCard.getComponent(1));

        return stats;
    }

    private JPanel createDashboardPanel() {
        JPanel dashboard = new JPanel(new BorderLayout());
        dashboard.setBackground(new Color(0, 0, 0, 0));
        dashboard.setBorder(createEmptyBorder(0, 20, 20, 20));

        // Título del dashboard
        JLabel dashboardTitle = new JLabel("Dashboard Principal");
        dashboardTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        dashboardTitle.setForeground(TEXT_WHITE);
        dashboardTitle.setBorder(createEmptyBorder(0, 0, 15, 0));

        dashboard.add(dashboardTitle, BorderLayout.NORTH);

        // Contenido del dashboard en pestañas
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Personalizar el color de las pestañas
        tabbedPane.setBackground(CARD_BACKGROUND);
        tabbedPane.setForeground(TEXT_WHITE);

        // Pestaña de Resumen
        tabbedPane.addTab("📊 Resumen General", createSummaryTab());

        // Pestaña de Actividad Reciente
        tabbedPane.addTab("🕒 Actividad Reciente", createActivityTab());

        // Pestaña de Alertas
        tabbedPane.addTab("⚠️ Alertas del Sistema", createAlertsTab());

        dashboard.add(tabbedPane, BorderLayout.CENTER);

        return dashboard;
    }

    private JPanel createSummaryTab() {
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBackground(CARD_BACKGROUND);
        summaryPanel.setBorder(createEmptyBorder(20, 20, 20, 20));

        JTextArea summaryText = new JTextArea();
        summaryText.setEditable(false);
        summaryText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        summaryText.setLineWrap(true);
        summaryText.setWrapStyleWord(true);
        summaryText.setBackground(CARD_BACKGROUND);
        summaryText.setForeground(TEXT_WHITE);
        summaryText.setCaretColor(TEXT_WHITE);
        summaryText.setText(
                "Resumen General del Sistema:\n\n" +
                        "• Sistema de gestión de licorería funcionando correctamente\n" +
                        "• Base de datos conectada y sincronizada\n" +
                        "• Módulos principales activos y operativos\n" +
                        "• Última actualización: " + new java.util.Date() + "\n\n" +
                        "Próximas actividades programadas:\n" +
                        "✓ Revisión de inventario semanal\n" +
                        "✓ Backup automático de base de datos\n" +
                        "✓ Actualización de precios y promociones"
        );

        JScrollPane scrollPane = new JScrollPane(summaryText);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        summaryPanel.add(scrollPane, BorderLayout.CENTER);

        return summaryPanel;
    }

    private JPanel createActivityTab() {
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBackground(CARD_BACKGROUND);

        // Datos de ejemplo para actividad reciente
        String[] columnNames = {"Fecha/Hora", "Usuario", "Actividad", "Módulo"};
        Object[][] data = {
                {"2024-01-15 10:30", "admin", "Inicio de sesión", "Sistema"},
                {"2024-01-15 10:25", "vendedor1", "Venta procesada", "Punto de Venta"},
                {"2024-01-15 10:20", "bodega1", "Stock actualizado", "Bodega"},
                {"2024-01-15 10:15", "admin", "Usuario creado", "Administración"},
                {"2024-01-15 10:10", "vendedor2", "Petición de stock", "Vendedor"}
        };

        JTable activityTable = new JTable(data, columnNames);
        activityTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        activityTable.setRowHeight(30);
        activityTable.setBackground(new Color(50, 65, 95));
        activityTable.setForeground(TEXT_WHITE);
        activityTable.setGridColor(BORDER_COLOR);
        activityTable.setSelectionBackground(ACCENT_COLOR);
        activityTable.setSelectionForeground(TEXT_WHITE);

        // Personalizar header de la tabla
        activityTable.getTableHeader().setBackground(PRIMARY_COLOR);
        activityTable.getTableHeader().setForeground(TEXT_WHITE);
        activityTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(activityTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        activityPanel.add(scrollPane, BorderLayout.CENTER);

        return activityPanel;
    }

    private JPanel createAlertsTab() {
        JPanel alertsPanel = new JPanel(new BorderLayout());
        alertsPanel.setBackground(CARD_BACKGROUND);
        alertsPanel.setBorder(createEmptyBorder(20, 20, 20, 20));

        JTextArea alertsText = new JTextArea();
        alertsText.setEditable(false);
        alertsText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        alertsText.setLineWrap(true);
        alertsText.setWrapStyleWord(true);
        alertsText.setBackground(CARD_BACKGROUND);
        alertsText.setForeground(TEXT_WHITE);
        alertsText.setCaretColor(TEXT_WHITE);
        alertsText.setText(
                "⚠️ ALERTAS DEL SISTEMA:\n\n" +
                        "🔴 CRÍTICAS:\n" +
                        "• No hay alertas críticas en este momento\n\n" +
                        "🟡 ADVERTENCIAS:\n" +
                        "• 5 productos con stock bajo\n" +
                        "• 3 peticiones de stock pendientes\n\n" +
                        "🟢 INFORMATIVAS:\n" +
                        "• Sistema funcionando correctamente\n" +
                        "• Todos los módulos operativos\n" +
                        "• Conexión a BD estable"
        );

        JScrollPane scrollPane = new JScrollPane(alertsText);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);
        alertsPanel.add(scrollPane, BorderLayout.CENTER);

        return alertsPanel;
    }

    private void handleMenuSelection(int index) {
        // Limpiar contenido actual
        contentPanel.removeAll();

        switch (index) {
            case 0: // Dashboard
                contentPanel.add(createStatsPanel(), BorderLayout.NORTH);
                contentPanel.add(createDashboardPanel(), BorderLayout.CENTER);
                break;
            case 1: // Gestión de Usuarios
                abrirGestionUsuarios();
                break;
            case 2: // Gestión de Productos
                mostrarMensajeEnDesarrollo("Gestión de Productos");
                break;
            case 3: // Historial del Sistema
                abrirHistorialSistema();
                break;
            case 4: // Peticiones de Stock
                abrirPeticionesStock();
                break;
            case 5: // Reportes
                mostrarMensajeEnDesarrollo("Reportes y Estadísticas");
                break;
            case 6: // Configuración
                mostrarMensajeEnDesarrollo("Configuración del Sistema");
                break;
        }

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void abrirGestionUsuarios() {
        // Usar el método de la clase principal para abrir el formulario
        com.mycompany.licoreria.Licoreria.mostrarFormulario(new AdminCrearUsuarios());
    }

    private void abrirHistorialSistema() {
        // Usar el método de la clase principal para abrir el formulario
        com.mycompany.licoreria.Licoreria.mostrarFormulario(new AdminHistoriall());
    }

    private void abrirPeticionesStock() {
        // Usar el método de la clase principal para abrir el formulario
        com.mycompany.licoreria.Licoreria.mostrarFormulario(new AdminVerPeticiones());
    }

    private void loadDashboardData() {
        // Simular carga de datos
        Timer timer = new Timer(1000, e -> {
            lblTotalUsers.setText("12");
            lblActiveUsers.setText("3");
            lblPendingRequests.setText("5");
            lblLowStock.setText("7");
        });
        timer.setRepeats(false);
        timer.start();
    }

    private void confirmarLogout() {
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>🚪 Cerrar Sesión</div>" +
                        "<div style='color: #ECF0F1;'>¿Está seguro que desea cerrar la sesión actual?</div>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar Cierre de Sesión",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.cerrarSesion();
            com.mycompany.licoreria.Licoreria.onLogout();
            dispose();
        }
    }

    private void setupModernDesign() {
        // Agregar borde azul sutil
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                createEmptyBorder(2, 2, 2, 2)
        ));
    }

    private void centrarEnDesktop() {
        try {
            com.mycompany.licoreria.Licoreria.centrarFormulario(this);
        } catch (Exception e) {
            // Si falla el centrado, continuar sin él
        }
    }

    private void mostrarMensajeEnDesarrollo(String modulo) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>🚧 Módulo en Desarrollo</div>" +
                        "<div style='color: #ECF0F1;'>El módulo <b>'" + modulo + "'</b> está actualmente en desarrollo.</div>" +
                        "<div style='color: #BDC3C7; font-size: 12px; margin-top: 8px;'>Estará disponible en próximas actualizaciones del sistema.</div>" +
                        "</div>" +
                        "</div></html>",
                "Módulo en desarrollo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Clase para las cards de estadísticas
    class StatCard extends JPanel {
        public StatCard(String title, String value, String description, Color color) {
            setLayout(new BorderLayout());
            setBackground(CARD_BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    createEmptyBorder(20, 20, 20, 20)
            ));

            // Header con icono y título
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(CARD_BACKGROUND);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            titleLabel.setForeground(TEXT_WHITE);

            headerPanel.add(titleLabel, BorderLayout.CENTER);

            // Valor
            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
            valueLabel.setForeground(color);
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Descripción
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            descLabel.setForeground(new Color(180, 200, 255));
            descLabel.setHorizontalAlignment(SwingConstants.CENTER);

            add(headerPanel, BorderLayout.NORTH);
            add(valueLabel, BorderLayout.CENTER);
            add(descLabel, BorderLayout.SOUTH);

            // Efecto hover
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(color, 2),
                            createEmptyBorder(19, 19, 19, 19)
                    ));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            createEmptyBorder(20, 20, 20, 20)
                    ));
                }
            });
        }
    }

    class ModernButton extends JButton {
        private Color originalColor;

        public ModernButton(String text, Color color) {
            super(text);
            this.originalColor = color;

            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBackground(color);
            setForeground(TEXT_WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(createEmptyBorder(8, 15, 8, 15));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor.darker());
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor);
                    }
                }
            });
        }
    }

    // Clase para el fondo con gradiente
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Gradiente azul oscuro moderno
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 40, 60),
                    getWidth(), getHeight(), new Color(50, 70, 100)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Elementos decorativos sutiles
            g2d.setColor(new Color(255, 255, 255, 10));
            g2d.fillOval(-50, -50, 150, 150);
            g2d.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);
        }
    }
}