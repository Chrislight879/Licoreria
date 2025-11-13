package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.PeticionBodegaController;
import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

import static javax.swing.BorderFactory.createEmptyBorder;

public class BodegaVerPeticiones extends JInternalFrame {
    private PeticionBodegaController peticionController;

    // Componentes de la UI
    private JTable peticionesTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;
    private JComboBox<String> cmbEstadoFilter;
    private JButton btnSearch, btnClearFilters, btnAprobar, btnRechazar, btnDespachar, btnRefresh;
    private JLabel lblStats;

    // Paneles
    private JTabbedPane tabbedPane;

    // Paleta de colores azules mejorada
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
    private final Color INFO_COLOR = new Color(155, 89, 182); // Púrpura para info

    public BodegaVerPeticiones() {
        initComponents();
        setupModernDesign();
        peticionController = new PeticionBodegaController();
        loadPeticionesData();
    }

    private void initComponents() {
        setTitle("Peticiones de Vendedores - Módulo Bodega");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1200, 800);
        setLayout(new BorderLayout(10, 10));

        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(createEmptyBorder(15, 15, 15, 15));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Filtros y acciones
        mainPanel.add(createFiltersPanel(), BorderLayout.CENTER);

        // Contenido principal
        mainPanel.add(createContentPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        // Centrar en el desktop
        centrarEnDesktop();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                createEmptyBorder(15, 20, 15, 20)
        ));

        // Título y información
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("📋 Peticiones de Vendedores");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_WHITE);

        JLabel userLabel = new JLabel("Bodeguero: " + SessionManager.getCurrentUser().getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(180, 200, 255));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(userLabel, BorderLayout.EAST);

        // Estadísticas
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setBackground(new Color(0, 0, 0, 0));

        lblStats = new JLabel("Cargando estadísticas...");
        lblStats.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblStats.setForeground(TEXT_WHITE);

        btnRefresh = new ModernButton("🔄 Actualizar", ACCENT_COLOR);
        btnRefresh.addActionListener(e -> refreshData());

        statsPanel.add(lblStats);
        statsPanel.add(btnRefresh);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBackground(CARD_BACKGROUND);
        filtersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Filtros y Acciones Rápidas",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                createEmptyBorder(20, 20, 20, 20)
        ));

        // Panel principal de filtros
        JPanel mainFiltersPanel = new JPanel(new GridBagLayout());
        mainFiltersPanel.setBackground(CARD_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weightx = 1.0;

        // Búsqueda
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel lblSearch = new JLabel("Búsqueda:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblSearch.setForeground(TEXT_WHITE);
        mainFiltersPanel.add(lblSearch, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        txtSearch = new ModernTextField("Buscar por producto, vendedor o observaciones...");
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
        });
        mainFiltersPanel.add(txtSearch, gbc);

        // Filtro por estado
        gbc.gridx = 3; gbc.gridy = 0;
        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblEstado.setForeground(TEXT_WHITE);
        mainFiltersPanel.add(lblEstado, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        cmbEstadoFilter = new ModernComboBox();
        cmbEstadoFilter.addItem("Todos los estados");
        cmbEstadoFilter.addItem("pendiente");
        cmbEstadoFilter.addItem("aprobada");
        cmbEstadoFilter.addItem("rechazada");
        cmbEstadoFilter.addItem("despachada");
        cmbEstadoFilter.addActionListener(e -> filterData());
        mainFiltersPanel.add(cmbEstadoFilter, gbc);

        // Botones de acción
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5;
        gbc.insets = new Insets(15, 5, 5, 5);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setBackground(CARD_BACKGROUND);

        btnAprobar = new ModernButton("✅ Aprobar Petición", SUCCESS_COLOR);
        btnAprobar.addActionListener(e -> aprobarPeticion());

        btnRechazar = new ModernButton("❌ Rechazar Petición", DANGER_COLOR);
        btnRechazar.addActionListener(e -> rechazarPeticion());

        btnDespachar = new ModernButton("🚚 Despachar Stock", INFO_COLOR);
        btnDespachar.addActionListener(e -> despacharPeticion());

        btnClearFilters = new ModernButton("🧹 Limpiar Filtros", new Color(149, 165, 166));
        btnClearFilters.addActionListener(e -> clearFilters());

        actionPanel.add(btnAprobar);
        actionPanel.add(btnRechazar);
        actionPanel.add(btnDespachar);
        actionPanel.add(Box.createHorizontalStrut(20));
        actionPanel.add(btnClearFilters);

        mainFiltersPanel.add(actionPanel, gbc);

        filtersPanel.add(mainFiltersPanel, BorderLayout.CENTER);

        return filtersPanel;
    }

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Gestión de Peticiones",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                createEmptyBorder(20, 20, 20, 20)
        ));

        // Crear pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CARD_BACKGROUND);
        tabbedPane.setForeground(TEXT_WHITE);

        // Pestañas principales
        tabbedPane.addTab("⏳ Peticiones Pendientes", createPendientesTab());
        tabbedPane.addTab("✅ Peticiones Aprobadas", createAprobadasTab());
        tabbedPane.addTab("📊 Todas las Peticiones", createTodasTab());
        tabbedPane.addTab("🚨 Peticiones Críticas", createCriticasTab());

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createPendientesTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);

        // Crear tabla para peticiones pendientes
        String[] columnNames = {
                "ID", "Producto", "Vendedor", "Cantidad", "Unidad",
                "Fecha Solicitud", "Stock Bodega", "Mínimo Vendedor", "Observaciones", "Acciones"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Solo la columna de acciones es editable
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class; // ID
                    case 3, 6, 7 -> Double.class; // Cantidad, Stock Bodega, Mínimo Vendedor
                    default -> String.class;
                };
            }
        };

        peticionesTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        peticionesTable.setRowSorter(sorter);

        // Configurar tabla
        setupTable();

        JScrollPane scrollPane = new JScrollPane(peticionesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        // Panel de información
        JPanel infoPanel = createInfoPanel();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createAprobadasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(
                "<html><div style='text-align: center; color: #BDC3C7;'>" +
                        "<h3>✅ Peticiones Aprobadas</h3>" +
                        "<p>Peticiones que han sido aprobadas y están listas para despacho</p>" +
                        "<p><small>Gestión de peticiones aprobadas pendientes de enviar a vendedores</small></p>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTodasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(
                "<html><div style='text-align: center; color: #BDC3C7;'>" +
                        "<h3>📊 Todas las Peticiones</h3>" +
                        "<p>Vista completa de todas las peticiones del sistema</p>" +
                        "<p><small>Historial completo de solicitudes de vendedores</small></p>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCriticasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(createEmptyBorder(20, 20, 20, 20));

        // Panel de alertas críticas
        JPanel alertasPanel = new JPanel();
        alertasPanel.setLayout(new BoxLayout(alertasPanel, BoxLayout.Y_AXIS));
        alertasPanel.setBackground(CARD_BACKGROUND);

        JLabel titulo = new JLabel("🚨 Peticiones Críticas - Atención Requerida");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(DANGER_COLOR);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        alertasPanel.add(titulo);
        alertasPanel.add(Box.createVerticalStrut(20));

        // Alertas de ejemplo (se cargarían dinámicamente)
        alertasPanel.add(createAlertaCritica(
                "🔴 Stock Insuficiente",
                "Ron Zacapa Centenario - Solicitado: 10 unidades - Stock: 5 unidades",
                "El vendedor Juan Pérez solicita más stock del disponible",
                DANGER_COLOR
        ));

        alertasPanel.add(Box.createVerticalStrut(10));

        alertasPanel.add(createAlertaCritica(
                "🟡 Múltiples Solicitudes",
                "Cerveza Artesanal IPA - 3 peticiones pendientes",
                "Varios vendedores han solicitado este producto",
                WARNING_COLOR
        ));

        alertasPanel.add(Box.createVerticalStrut(10));

        alertasPanel.add(createAlertaCritica(
                "🔵 Urgente - Stock Cero",
                "Vino Tinto Reserva - Stock: 0 unidades",
                "Producto agotado en bodega, peticiones en espera",
                INFO_COLOR
        ));

        JScrollPane scrollPane = new JScrollPane(alertasPanel);
        scrollPane.setBorder(createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlertaCritica(String titulo, String descripcion, String detalle, Color color) {
        JPanel alertaPanel = new JPanel(new BorderLayout());
        alertaPanel.setBackground(new Color(60, 75, 100));
        alertaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                createEmptyBorder(15, 15, 15, 15)
        ));
        alertaPanel.setMaximumSize(new Dimension(600, 100));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloLabel.setForeground(color);

        JLabel descLabel = new JLabel(descripcion);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_WHITE);

        JLabel detalleLabel = new JLabel(detalle);
        detalleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        detalleLabel.setForeground(new Color(180, 200, 255));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(new Color(60, 75, 100));
        textPanel.add(tituloLabel);
        textPanel.add(descLabel);
        textPanel.add(detalleLabel);

        JButton actionBtn = new ModernButton("Resolver", color);
        actionBtn.setPreferredSize(new Dimension(100, 35));

        alertaPanel.add(textPanel, BorderLayout.CENTER);
        alertaPanel.add(actionBtn, BorderLayout.EAST);

        return alertaPanel;
    }

    private void setupTable() {
        peticionesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        peticionesTable.setRowHeight(40);
        peticionesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peticionesTable.setIntercellSpacing(new Dimension(0, 0));
        peticionesTable.setShowGrid(false);
        peticionesTable.setAutoCreateRowSorter(true);
        peticionesTable.setBackground(new Color(50, 65, 95));
        peticionesTable.setForeground(TEXT_WHITE);
        peticionesTable.setGridColor(BORDER_COLOR);
        peticionesTable.setSelectionBackground(ACCENT_COLOR);
        peticionesTable.setSelectionForeground(TEXT_WHITE);

        // Header personalizado
        JTableHeader header = peticionesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Anchos de columnas
        peticionesTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        peticionesTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Producto
        peticionesTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Vendedor
        peticionesTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Cantidad
        peticionesTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Unidad
        peticionesTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Fecha
        peticionesTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Stock Bodega
        peticionesTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Mínimo Vendedor
        peticionesTable.getColumnModel().getColumn(8).setPreferredWidth(200); // Observaciones
        peticionesTable.getColumnModel().getColumn(9).setPreferredWidth(150); // Acciones

        // Renderers personalizados
        peticionesTable.getColumnModel().getColumn(6).setCellRenderer((TableCellRenderer) new StockBodegaRenderer());
        peticionesTable.getColumnModel().getColumn(9).setCellRenderer((TableCellRenderer) new AccionesRenderer());

        // Listener para selección
        peticionesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(createEmptyBorder(10, 0, 0, 0));

        JLabel infoLabel = new JLabel("💡 Seleccione una petición para aprobar, rechazar o despachar. Las peticiones críticas se muestran en rojo.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(180, 200, 255));

        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        countLabel.setForeground(TEXT_WHITE);

        // Actualizar contador
        tableModel.addTableModelListener(e -> {
            countLabel.setText("Peticiones mostradas: " + tableModel.getRowCount());
        });

        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(countLabel, BorderLayout.EAST);

        return infoPanel;
    }

    private void loadPeticionesData() {
        SwingWorker<List<PeticionStock>, Void> worker = new SwingWorker<List<PeticionStock>, Void>() {
            @Override
            protected List<PeticionStock> doInBackground() throws Exception {
                return peticionController.getPeticionesPendientes();
            }

            @Override
            protected void done() {
                try {
                    List<PeticionStock> peticiones = get();
                    tableModel.setRowCount(0);

                    for (PeticionStock peticion : peticiones) {
                        Object[] row = {
                                peticion.getPeticionId(),
                                peticion.getProductoNombre(),
                                peticion.getUsuarioSolicitanteNombre(),
                                peticion.getCantidadSolicitada(),
                                peticion.getUnidadMedida(),
                                peticion.getFechaSolicitud(),
                                peticion.getStockBodega(),
                                peticion.getStockVendedor(), // Usando como mínimo vendedor
                                peticion.getObservaciones(),
                                "Acciones" // Placeholder para botones de acción
                        };
                        tableModel.addRow(row);
                    }

                    updateStats(peticiones.size());
                    showSuccess("Peticiones cargadas: " + peticiones.size() + " pendientes");

                } catch (Exception e) {
                    showError("Error al cargar peticiones: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void filterData() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedEstado = (String) cmbEstadoFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i).*" + searchText + ".*");

        if (selectedEstado != null && !selectedEstado.equals("Todos los estados")) {
            // Para la tabla de pendientes, ya estamos filtrando por estado
            // Este filtro adicional se aplicaría si mostráramos todos los estados
        }

        sorter.setRowFilter(rf);
    }

    private void clearFilters() {
        txtSearch.setText("Buscar por producto, vendedor o observaciones...");
        cmbEstadoFilter.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }

    private void updateButtonStates() {
        int selectedRow = peticionesTable.getSelectedRow();
        btnAprobar.setEnabled(selectedRow != -1);
        btnRechazar.setEnabled(selectedRow != -1);

        // Para despachar, necesitamos verificar el estado (pero en pendientes todas son despachables)
        btnDespachar.setEnabled(selectedRow != -1);
    }

    private void aprobarPeticion() {
        int selectedRow = peticionesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Debe seleccionar una petición para aprobar");
            return;
        }

        int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
        int peticionId = (int) tableModel.getValueAt(modelRow, 0);
        String producto = (String) tableModel.getValueAt(modelRow, 1);
        String vendedor = (String) tableModel.getValueAt(modelRow, 2);
        double cantidad = (double) tableModel.getValueAt(modelRow, 3);
        double stockBodega = (double) tableModel.getValueAt(modelRow, 6);

        // Verificar stock
        if (cantidad > stockBodega) {
            int option = JOptionPane.showConfirmDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                            "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #F39C12;'>" +
                            "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>⚠️ Stock Insuficiente</div>" +
                            "<div style='color: #ECF0F1; text-align: left;'>" +
                            "<p>Producto: <b>" + producto + "</b></p>" +
                            "<p>Solicitado: <b>" + cantidad + "</b> | Disponible: <b>" + stockBodega + "</b></p>" +
                            "<p>¿Desea aprobar parcialmente con el stock disponible?</p>" +
                            "</div>" +
                            "</div>" +
                            "</div></html>",
                    "Stock Insuficiente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String observaciones = JOptionPane.showInputDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>Observaciones de Aprobación</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p>Producto: <b>" + producto + "</b></p>" +
                        "<p>Vendedor: <b>" + vendedor + "</b></p>" +
                        "<p>Cantidad: <b>" + cantidad + "</b></p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Observaciones de Aprobación",
                JOptionPane.QUESTION_MESSAGE);

        if (observaciones != null) {
            ejecutarAccionPeticion(peticionId, "aprobar", observaciones);
        }
    }

    private void rechazarPeticion() {
        int selectedRow = peticionesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Debe seleccionar una petición para rechazar");
            return;
        }

        int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
        int peticionId = (int) tableModel.getValueAt(modelRow, 0);
        String producto = (String) tableModel.getValueAt(modelRow, 1);
        String vendedor = (String) tableModel.getValueAt(modelRow, 2);

        String motivo = JOptionPane.showInputDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>Motivo del Rechazo</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p>Producto: <b>" + producto + "</b></p>" +
                        "<p>Vendedor: <b>" + vendedor + "</b></p>" +
                        "<p>¿Por qué rechaza esta petición?</p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Motivo de Rechazo",
                JOptionPane.QUESTION_MESSAGE);

        if (motivo != null) {
            ejecutarAccionPeticion(peticionId, "rechazar", motivo);
        }
    }

    private void despacharPeticion() {
        int selectedRow = peticionesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Debe seleccionar una petición para despachar");
            return;
        }

        int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
        int peticionId = (int) tableModel.getValueAt(modelRow, 0);
        String producto = (String) tableModel.getValueAt(modelRow, 1);
        double cantidad = (double) tableModel.getValueAt(modelRow, 3);

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #9B59B6;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>🚚 Confirmar Despacho</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p>Producto: <b>" + producto + "</b></p>" +
                        "<p>Cantidad: <b>" + cantidad + "</b></p>" +
                        "<p><small>Esta acción transferirá el stock de bodega al vendedor</small></p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar Despacho",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            ejecutarAccionPeticion(peticionId, "despachar", "Despachado por bodeguero");
        }
    }

    private void ejecutarAccionPeticion(int peticionId, String accion, String observaciones) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                int usuarioAprobadorId = SessionManager.getCurrentUser().getUsuarioId();

                switch (accion) {
                    case "aprobar":
                        return peticionController.aprobarPeticion(peticionId, usuarioAprobadorId, observaciones);
                    case "rechazar":
                        return peticionController.rechazarPeticion(peticionId, usuarioAprobadorId, observaciones);
                    case "despachar":
                        return peticionController.despacharPeticion(peticionId);
                    default:
                        return false;
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showSuccess("Petición " + accion + "da exitosamente");
                        loadPeticionesData(); // Recargar datos
                    } else {
                        showError("Error al " + accion + " la petición");
                    }
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void refreshData() {
        btnRefresh.setText("🔄 Cargando...");
        btnRefresh.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadPeticionesData();
                return null;
            }

            @Override
            protected void done() {
                btnRefresh.setText("🔄 Actualizar");
                btnRefresh.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void updateStats(int totalPendientes) {
        String stats = peticionController.getEstadisticasPeticiones();
        lblStats.setText("Pendientes: " + totalPendientes + " | " + stats);
    }

    private void setupModernDesign() {
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
    }

    private void centrarEnDesktop() {
        try {
            com.mycompany.licoreria.Licoreria.centrarFormulario(this);
        } catch (Exception e) {
            // Si falla el centrado, continuar sin él
        }
    }

    // Métodos de utilidad para mensajes (actualizados)
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

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #27AE60;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>✅ Éxito</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Clases internas para componentes modernos con tema azul
    class ModernTextField extends JTextField {
        private String placeholder;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    createEmptyBorder(10, 15, 10, 15)
            ));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setCaretColor(TEXT_WHITE);
            setOpaque(true);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(TEXT_WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(new Color(200, 220, 255));
                    }
                }
            });

            setText(placeholder);
            setForeground(new Color(200, 220, 255));
        }
    }

    class ModernComboBox extends JComboBox<String> {
        public ModernComboBox() {
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    createEmptyBorder(8, 12, 8, 12)
            ));
            setRenderer(new ModernComboBoxRenderer());
        }
    }

    class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            label.setBorder(createEmptyBorder(5, 10, 5, 10));

            if (isSelected) {
                label.setBackground(ACCENT_COLOR);
                label.setForeground(TEXT_WHITE);
            } else {
                label.setBackground(new Color(50, 65, 95));
                label.setForeground(TEXT_WHITE);
            }

            return label;
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
            setBorder(createEmptyBorder(10, 20, 10, 20));

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

    class StockBodegaRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setBackground(new Color(50, 65, 95));

            if (value instanceof Double) {
                double stock = (Double) value;
                double cantidadSolicitada = (Double) table.getValueAt(row, 3);

                if (stock < cantidadSolicitada) {
                    label.setForeground(DANGER_COLOR);
                    label.setText("🔴 " + String.format("%.2f", stock));
                } else if (stock < cantidadSolicitada * 1.5) {
                    label.setForeground(WARNING_COLOR);
                    label.setText("🟡 " + String.format("%.2f", stock));
                } else {
                    label.setForeground(SUCCESS_COLOR);
                    label.setText("🟢 " + String.format("%.2f", stock));
                }
            }

            if (isSelected) {
                label.setBackground(ACCENT_COLOR);
            }

            return label;
        }
    }

    class AccionesRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setBackground(isSelected ? ACCENT_COLOR : new Color(50, 65, 95));

            JButton btnAprobar = new JButton("✅");
            btnAprobar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnAprobar.setBackground(SUCCESS_COLOR);
            btnAprobar.setForeground(TEXT_WHITE);
            btnAprobar.setBorder(createEmptyBorder(5, 8, 5, 8));
            btnAprobar.setCursor(new Cursor(Cursor.HAND_CURSOR));

            JButton btnRechazar = new JButton("❌");
            btnRechazar.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btnRechazar.setBackground(DANGER_COLOR);
            btnRechazar.setForeground(TEXT_WHITE);
            btnRechazar.setBorder(createEmptyBorder(5, 8, 5, 8));
            btnRechazar.setCursor(new Cursor(Cursor.HAND_CURSOR));

            panel.add(btnAprobar);
            panel.add(btnRechazar);

            return panel;
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