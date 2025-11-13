package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.Licoreria;
import com.mycompany.licoreria.controllers.VentaRapidaController;
import com.mycompany.licoreria.controllers.PeticionVendedorController;
import com.mycompany.licoreria.controllers.VentaController;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.PeticionVendedor;
import com.mycompany.licoreria.models.Venta;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.List;

public class VendedorMainForm extends JInternalFrame {
    private VentaRapidaController ventaRapidaController;
    private PeticionVendedorController peticionController;
    private VentaController ventaController;

    // Componentes UI
    private JTabbedPane tabbedPane;
    private JLabel lblStatsVentas, lblStatsPeticiones, lblStatsStock;
    private JTable tableVentasHoy, tablePeticionesActivas, tableStockBajo;
    private DefaultTableModel modelVentas, modelPeticiones, modelStock;

    // Paleta de colores azules mejorada con mejor contraste
    private final Color PRIMARY_COLOR = new Color(70, 130, 180); // SteelBlue - azul principal
    private final Color SECONDARY_COLOR = new Color(100, 149, 237); // CornflowerBlue - azul claro
    private final Color ACCENT_COLOR = new Color(30, 144, 255); // DodgerBlue - azul brillante
    private final Color BACKGROUND_COLOR = new Color(30, 40, 60); // Azul oscuro para fondo
    private final Color CARD_BACKGROUND = new Color(40, 55, 80); // Azul medio para tarjetas
    private final Color BORDER_COLOR = new Color(100, 130, 180); // Borde azul
    private final Color TEXT_WHITE = Color.WHITE; // TODOS LOS TEXTOS EN BLANCO
    private final Color SUCCESS_COLOR = new Color(86, 202, 133); // Verde azulado para éxitos
    private final Color WARNING_COLOR = new Color(255, 193, 87); // Amarillo dorado para advertencias
    private final Color DANGER_COLOR = new Color(255, 118, 117); // Rojo coral para peligros

    public VendedorMainForm() {
        initComponents();
        ventaRapidaController = new VentaRapidaController();
        peticionController = new PeticionVendedorController();
        ventaController = new VentaController();

        cargarDashboard();
        iniciarActualizacionAutomatica();
    }

    private void initComponents() {
        setTitle("👨‍💼 Módulo Completo de Vendedor");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1200, 800);
        setLayout(new BorderLayout());

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Información del usuario
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userPanel.setBackground(new Color(0, 0, 0, 0));

        JLabel userIcon = new JLabel("👤");
        userIcon.setFont(new Font("Segoe UI", Font.PLAIN, 24));
        userIcon.setForeground(TEXT_WHITE);

        JLabel userInfo = new JLabel("<html><b style='color: white;'>" +
                SessionManager.getCurrentUser().getUsername() + "</b><br>" +
                SessionManager.getCurrentUser().getRolTitulo() + "</html>");
        userInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userInfo.setForeground(TEXT_WHITE);

        userPanel.add(userIcon);
        userPanel.add(userInfo);

        // Título
        JLabel titleLabel = new JLabel("PANEL PRINCIPAL - VENDEDOR");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Panel de estadísticas rápidas
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBackground(new Color(0, 0, 0, 0));

        lblStatsVentas = createStatCard("0", "Ventas Hoy", "💰", new Color(70, 130, 180));
        lblStatsPeticiones = createStatCard("0", "Peticiones Activas", "📨", new Color(65, 105, 225));
        lblStatsStock = createStatCard("0", "Stock Bajo", "⚠️", new Color(255, 165, 0));

        statsPanel.add(lblStatsVentas);
        statsPanel.add(lblStatsPeticiones);
        statsPanel.add(lblStatsStock);

        headerPanel.add(userPanel, BorderLayout.WEST);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(statsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(new Color(0, 0, 0, 0));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tabbedPane.setBackground(CARD_BACKGROUND);
        tabbedPane.setForeground(TEXT_WHITE);

        // Personalizar el color de las pestañas
        tabbedPane.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                if (isSelected) {
                    g.setColor(ACCENT_COLOR);
                } else {
                    g.setColor(CARD_BACKGROUND);
                }
                g.fillRect(x, y, w, h);
            }

            @Override
            protected void paintContentBorder(Graphics g, int tabPlacement, int selectedIndex) {
                // No pintar borde del contenido
            }
        });

        // Pestaña de Dashboard
        tabbedPane.addTab("📊 Dashboard", createDashboardTab());

        // Pestaña de Ventas Rápidas
        tabbedPane.addTab("🛒 Venta Rápida", createVentaRapidaTab());

        // Pestaña de Solicitudes
        tabbedPane.addTab("📦 Solicitar Stock", createSolicitudesTab());

        // Pestaña de Historial
        tabbedPane.addTab("📋 Mi Historial", createHistorialTab());

        centerPanel.add(tabbedPane, BorderLayout.CENTER);

        return centerPanel;
    }

    private JPanel createDashboardTab() {
        JPanel dashboardPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        dashboardPanel.setBackground(new Color(0, 0, 0, 0));
        dashboardPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Ventas de hoy
        dashboardPanel.add(createDashboardCard("💰 Ventas del Día", createVentasHoyPanel(), SECONDARY_COLOR));

        // Peticiones activas
        dashboardPanel.add(createDashboardCard("📨 Mis Solicitudes", createPeticionesPanel(), PRIMARY_COLOR));

        // Stock bajo
        dashboardPanel.add(createDashboardCard("⚠️ Stock Bajo", createStockBajoPanel(), WARNING_COLOR));

        // Acciones rápidas
        dashboardPanel.add(createDashboardCard("🚀 Acciones Rápidas", createAccionesRapidasPanel(), ACCENT_COLOR));

        return dashboardPanel;
    }

    private JPanel createVentaRapidaTab() {
        JPanel ventaPanel = new JPanel(new BorderLayout());
        ventaPanel.setBackground(CARD_BACKGROUND);
        ventaPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("VENTA RÁPIDA - PUNTO DE VENTA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panel de acceso al módulo de ventas
        JPanel accessPanel = new JPanel(new GridBagLayout());
        accessPanel.setBackground(CARD_BACKGROUND);

        JLabel accessLabel = new JLabel("<html><div style='text-align: center; color: white;'>" +
                "<h3 style='color: white;'>Módulo de Venta Rápida</h3>" +
                "<p style='color: #E0E0E0;'>" +
                "Acceda al sistema completo de punto de venta con todas las funcionalidades:</p>" +
                "<ul style='text-align: left; color: #E0E0E0;'>" +
                "<li>🛒 Carrito de compras interactivo</li>" +
                "<li>🔍 Búsqueda en tiempo real</li>" +
                "<li>💰 Cálculo automático de totales</li>" +
                "<li>📦 Control de stock en tiempo real</li>" +
                "<li>🧾 Generación de facturas</li>" +
                "</ul>" +
                "</div></html>");

        JButton btnAbrirVenta = new ModernButton("🎯 Abrir Punto de Venta", ACCENT_COLOR);
        btnAbrirVenta.setPreferredSize(new Dimension(200, 50));
        btnAbrirVenta.addActionListener(e -> abrirPuntoVenta());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        accessPanel.add(accessLabel, gbc);

        gbc.gridy = 1;
        accessPanel.add(btnAbrirVenta, gbc);

        ventaPanel.add(titleLabel, BorderLayout.NORTH);
        ventaPanel.add(accessPanel, BorderLayout.CENTER);

        return ventaPanel;
    }

    private JPanel createSolicitudesTab() {
        JPanel solicitudesPanel = new JPanel(new BorderLayout());
        solicitudesPanel.setBackground(CARD_BACKGROUND);
        solicitudesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("SOLICITUDES DE STOCK - BODEGA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panel de acceso al módulo de solicitudes
        JPanel accessPanel = new JPanel(new GridBagLayout());
        accessPanel.setBackground(CARD_BACKGROUND);

        JLabel accessLabel = new JLabel("<html><div style='text-align: center; color: white;'>" +
                "<h3 style='color: white;'>Módulo de Solicitudes</h3>" +
                "<p style='color: #E0E0E0;'>" +
                "Gestión completa de solicitudes de stock a bodega:</p>" +
                "<ul style='text-align: left; color: #E0E0E0;'>" +
                "<li>📦 Visualización de stock en bodega</li>" +
                "<li>📨 Envío de solicitudes</li>" +
                "<li>📊 Seguimiento de estado</li>" +
                "<li>❌ Cancelación de solicitudes pendientes</li>" +
                "<li>🔔 Notificaciones de cambios</li>" +
                "</ul>" +
                "</div></html>");

        JButton btnAbrirSolicitudes = new ModernButton("📨 Abrir Solicitudes", ACCENT_COLOR);
        btnAbrirSolicitudes.setPreferredSize(new Dimension(200, 50));
        btnAbrirSolicitudes.addActionListener(e -> abrirSolicitudes());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);
        accessPanel.add(accessLabel, gbc);

        gbc.gridy = 1;
        accessPanel.add(btnAbrirSolicitudes, gbc);

        solicitudesPanel.add(titleLabel, BorderLayout.NORTH);
        solicitudesPanel.add(accessPanel, BorderLayout.CENTER);

        return solicitudesPanel;
    }

    private JPanel createHistorialTab() {
        JPanel historialPanel = new JPanel(new BorderLayout());
        historialPanel.setBackground(CARD_BACKGROUND);
        historialPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("MI HISTORIAL - VENTAS Y SOLICITUDES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Panel de resumen de historial
        JPanel resumenPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        resumenPanel.setBackground(CARD_BACKGROUND);

        // Últimas ventas
        JPanel ventasPanel = createHistorialCard("💰 Últimas Ventas", createUltimasVentasPanel(), SECONDARY_COLOR);

        // Todas las solicitudes
        JPanel peticionesPanel = createHistorialCard("📨 Todas mis Solicitudes", createTodasPeticionesPanel(), PRIMARY_COLOR);

        resumenPanel.add(ventasPanel);
        resumenPanel.add(peticionesPanel);

        historialPanel.add(titleLabel, BorderLayout.NORTH);
        historialPanel.add(resumenPanel, BorderLayout.CENTER);

        return historialPanel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(new Color(0, 0, 0, 0));
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));

        JLabel statusLabel = new JLabel("Sistema de Vendedor - Conectado");
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        statusLabel.setForeground(TEXT_WHITE);

        JLabel updateLabel = new JLabel("Actualizado: " + new java.util.Date());
        updateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        updateLabel.setForeground(TEXT_WHITE);

        footerPanel.add(statusLabel, BorderLayout.WEST);
        footerPanel.add(updateLabel, BorderLayout.EAST);

        return footerPanel;
    }

    // Métodos para crear componentes del dashboard
    private JLabel createStatCard(String value, String title, String icon, Color color) {
        JLabel card = new JLabel("<html><div style='text-align: center; background: " +
                String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue()) +
                "; padding: 15px; border-radius: 10px; color: white;'>" +
                "<div style='font-size: 24px; margin-bottom: 5px;'>" + icon + "</div>" +
                "<div style='font-size: 18px; font-weight: bold;'>" + value + "</div>" +
                "<div style='font-size: 11px;'>" + title + "</div>" +
                "</div></html>");
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return card;
    }

    private JPanel createDashboardCard(String title, JComponent content, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createVentasHoyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);

        modelVentas = new DefaultTableModel(new Object[]{"Hora", "Cliente", "Total", "Items"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableVentasHoy = new JTable(modelVentas);
        tableVentasHoy.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tableVentasHoy.setRowHeight(25);
        tableVentasHoy.setBackground(new Color(50, 65, 95));
        tableVentasHoy.setForeground(TEXT_WHITE);
        tableVentasHoy.setGridColor(BORDER_COLOR);
        tableVentasHoy.setSelectionBackground(ACCENT_COLOR);
        tableVentasHoy.setSelectionForeground(TEXT_WHITE);

        // Personalizar header de la tabla
        tableVentasHoy.getTableHeader().setBackground(PRIMARY_COLOR);
        tableVentasHoy.getTableHeader().setForeground(TEXT_WHITE);
        tableVentasHoy.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        JScrollPane scrollPane = new JScrollPane(tableVentasHoy);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPeticionesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);

        modelPeticiones = new DefaultTableModel(new Object[]{"Producto", "Cantidad", "Estado", "Fecha"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablePeticionesActivas = new JTable(modelPeticiones);
        tablePeticionesActivas.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tablePeticionesActivas.setRowHeight(25);
        tablePeticionesActivas.setBackground(new Color(50, 65, 95));
        tablePeticionesActivas.setForeground(TEXT_WHITE);
        tablePeticionesActivas.setGridColor(BORDER_COLOR);
        tablePeticionesActivas.setSelectionBackground(ACCENT_COLOR);
        tablePeticionesActivas.setSelectionForeground(TEXT_WHITE);
        tablePeticionesActivas.setDefaultRenderer(Object.class, (TableCellRenderer) new EstadoPeticionRenderer());

        // Personalizar header de la tabla
        tablePeticionesActivas.getTableHeader().setBackground(PRIMARY_COLOR);
        tablePeticionesActivas.getTableHeader().setForeground(TEXT_WHITE);
        tablePeticionesActivas.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        JScrollPane scrollPane = new JScrollPane(tablePeticionesActivas);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStockBajoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);

        modelStock = new DefaultTableModel(new Object[]{"Producto", "Stock Actual", "Mínimo", "Estado"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableStockBajo = new JTable(modelStock);
        tableStockBajo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tableStockBajo.setRowHeight(25);
        tableStockBajo.setBackground(new Color(50, 65, 95));
        tableStockBajo.setForeground(TEXT_WHITE);
        tableStockBajo.setGridColor(BORDER_COLOR);
        tableStockBajo.setSelectionBackground(ACCENT_COLOR);
        tableStockBajo.setSelectionForeground(TEXT_WHITE);
        tableStockBajo.setDefaultRenderer(Object.class, (TableCellRenderer) new StockBajoRenderer());

        // Personalizar header de la tabla
        tableStockBajo.getTableHeader().setBackground(PRIMARY_COLOR);
        tableStockBajo.getTableHeader().setForeground(TEXT_WHITE);
        tableStockBajo.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        JScrollPane scrollPane = new JScrollPane(tableStockBajo);
        scrollPane.setPreferredSize(new Dimension(0, 120));
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAccionesRapidasPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 5, 5));
        panel.setBackground(CARD_BACKGROUND);

        JButton btnVentaRapida = new QuickActionButton("🛒 Nueva Venta", ACCENT_COLOR);
        btnVentaRapida.addActionListener(e -> abrirPuntoVenta());

        JButton btnSolicitarStock = new QuickActionButton("📦 Solicitar Stock", SECONDARY_COLOR);
        btnSolicitarStock.addActionListener(e -> abrirSolicitudes());

        JButton btnActualizar = new QuickActionButton("🔄 Actualizar Datos", PRIMARY_COLOR);
        btnActualizar.addActionListener(e -> cargarDashboard());

        panel.add(btnVentaRapida);
        panel.add(btnSolicitarStock);
        panel.add(btnActualizar);

        return panel;
    }

    private JPanel createHistorialCard(String title, JComponent content, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_BACKGROUND);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        titleLabel.setForeground(TEXT_WHITE);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(content, BorderLayout.CENTER);

        return card;
    }

    private JPanel createUltimasVentasPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Fecha", "Cliente", "Total"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.setBackground(new Color(50, 65, 95));
        table.setForeground(TEXT_WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(TEXT_WHITE);

        // Personalizar header de la tabla
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(TEXT_WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Cargar últimas ventas
        try {
            List<Venta> ventas = ventaController.getHistorialVentas(
                    SessionManager.getCurrentUser().getUsuarioId()
            );

            for (Venta v : ventas) {
                model.addRow(new Object[]{
                        v.getFechaVenta(),
                        v.getCliente(),
                        "$" + v.getTotal()
                });
            }
        } catch (Exception e) {
            // Ignorar errores
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTodasPeticionesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"Producto", "Cantidad", "Estado", "Fecha"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        table.setRowHeight(25);
        table.setBackground(new Color(50, 65, 95));
        table.setForeground(TEXT_WHITE);
        table.setGridColor(BORDER_COLOR);
        table.setSelectionBackground(ACCENT_COLOR);
        table.setSelectionForeground(TEXT_WHITE);
        table.setDefaultRenderer(Object.class, (TableCellRenderer) new EstadoPeticionRenderer());

        // Personalizar header de la tabla
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(TEXT_WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 11));

        // Cargar todas las peticiones
        try {
            List<PeticionVendedor> peticiones = peticionController.getPeticionesPorVendedor(
                    SessionManager.getCurrentUser().getUsuarioId()
            );

            for (PeticionVendedor p : peticiones) {
                model.addRow(new Object[]{
                        p.getProductoNombre(),
                        p.getCantidadSolicitada(),
                        p.getEstado(),
                        p.getFechaSolicitud()
                });
            }
        } catch (Exception e) {
            // Ignorar errores
        }

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    // Métodos de negocio (sin cambios)
    private void cargarDashboard() {
        cargarEstadisticas();
        cargarVentasHoy();
        cargarPeticionesActivas();
        cargarStockBajo();
    }

    private void cargarEstadisticas() {
        try {
            // Ventas hoy
            List<Object[]> ventasHoy = ventaRapidaController.getVentasDelDia();
            lblStatsVentas.setText(createStatCard(String.valueOf(ventasHoy.size()), "Ventas Hoy", "💰", new Color(70, 130, 180)).getText());

            // Peticiones activas
            List<PeticionVendedor> peticiones = peticionController.getPeticionesPendientes(
                    SessionManager.getCurrentUser().getUsuarioId()
            );
            lblStatsPeticiones.setText(createStatCard(String.valueOf(peticiones.size()), "Peticiones Activas", "📨", new Color(65, 105, 225)).getText());

            // Stock bajo
            List<Producto> stockBajo = peticionController.getProductosStockBajoVendedor(
                    SessionManager.getCurrentUser().getUsuarioId()
            );
            lblStatsStock.setText(createStatCard(String.valueOf(stockBajo.size()), "Stock Bajo", "⚠️", new Color(255, 165, 0)).getText());

        } catch (Exception e) {
            showError("Error al cargar estadísticas: " + e.getMessage());
        }
    }

    private void cargarVentasHoy() {
        modelVentas.setRowCount(0);
        try {
            List<Object[]> ventas = ventaRapidaController.getVentasDelDia();
            for (Object[] venta : ventas) {
                modelVentas.addRow(new Object[]{
                        venta[1], // Fecha
                        venta[2], // Cliente
                        venta[3], // Total
                        venta[5]  // Items
                });
            }
        } catch (Exception e) {
            // Ignorar errores
        }
    }

    private void cargarPeticionesActivas() {
        modelPeticiones.setRowCount(0);
        try {
            List<PeticionVendedor> peticiones = peticionController.getPeticionesPendientes(
                    SessionManager.getCurrentUser().getUsuarioId()
            );

            for (PeticionVendedor p : peticiones) {
                modelPeticiones.addRow(new Object[]{
                        p.getProductoNombre(),
                        p.getCantidadSolicitada(),
                        p.getEstado(),
                        p.getFechaSolicitud()
                });
            }
        } catch (Exception e) {
            // Ignorar errores
        }
    }

    private void cargarStockBajo() {
        modelStock.setRowCount(0);
        try {
            List<Producto> productos = peticionController.getProductosStockBajoVendedor(
                    SessionManager.getCurrentUser().getUsuarioId()
            );

            for (Producto p : productos) {
                String estado = p.getStockVendedor() <= p.getCantidadMinimaVendedor() * 0.5 ? "CRÍTICO" : "BAJO";
                modelStock.addRow(new Object[]{
                        p.getNombre(),
                        p.getStockVendedor(),
                        p.getCantidadMinimaVendedor(),
                        estado
                });
            }
        } catch (Exception e) {
            // Ignorar errores
        }
    }

    private void iniciarActualizacionAutomatica() {
        Timer timer = new Timer(30000, e -> cargarDashboard()); // Actualizar cada 30 segundos
        timer.start();
    }

    private void abrirPuntoVenta() {
        VenderForm venderForm = new VenderForm();
        venderForm.setVisible(true);
        Licoreria.mostrarFormulario(venderForm);
    }

    private void abrirSolicitudes() {
        VendedorPedirForm pedirForm = new VendedorPedirForm();
        pedirForm.setVisible(true);
        com.mycompany.licoreria.Licoreria.mostrarFormulario(pedirForm);
    }

    // Clases de renderizado actualizadas
    class EstadoPeticionRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(TEXT_WHITE);

            if (column == 2) { // Columna de estado
                String estado = (String) value;
                switch (estado.toLowerCase()) {
                    case "pendiente":
                        c.setBackground(new Color(70, 100, 170)); // Azul medio
                        break;
                    case "aprobada":
                        c.setBackground(new Color(60, 140, 100)); // Verde medio
                        break;
                    case "rechazada":
                        c.setBackground(new Color(180, 80, 80)); // Rojo medio
                        break;
                    case "despachada":
                        c.setBackground(new Color(80, 120, 160)); // Azul grisáceo
                        break;
                    default:
                        c.setBackground(new Color(50, 65, 95));
                }
            } else {
                c.setBackground(new Color(50, 65, 95));
            }

            return c;
        }
    }

    class StockBajoRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(TEXT_WHITE);

            if (column == 3) { // Columna de estado
                String estado = (String) value;
                if ("CRÍTICO".equals(estado)) {
                    c.setBackground(new Color(180, 80, 80)); // Rojo medio
                } else if ("BAJO".equals(estado)) {
                    c.setBackground(new Color(180, 140, 60)); // Amarillo oscuro
                }
            } else {
                c.setBackground(new Color(50, 65, 95));
            }

            return c;
        }
    }

    // Clases para componentes modernos
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
            setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor.darker());
                    }
                }
                public void mouseExited(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor);
                    }
                }
            });
        }
    }

    class QuickActionButton extends JButton {
        private Color originalColor;

        public QuickActionButton(String text, Color color) {
            super(text);
            this.originalColor = color;

            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBackground(color);
            setForeground(TEXT_WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor.darker());
                    }
                }
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
            // Círculos decorativos en esquinas
            g2d.fillOval(-50, -50, 150, 150);
            g2d.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);
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
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }
}