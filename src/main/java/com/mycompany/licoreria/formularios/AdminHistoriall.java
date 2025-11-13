package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.HistoryLogController;
import com.mycompany.licoreria.models.HistoryLog;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

public class AdminHistoriall extends JInternalFrame {
    private HistoryLogController historyController;

    // Componentes de la UI
    private JTable historyTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;
    private JComboBox<String> cmbProcesoFilter;
    private JButton btnSearch, btnClearFilters, btnExport, btnRefresh;
    private JLabel lblStats;

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

    public AdminHistoriall() {
        initComponents();
        setupModernDesign();
        historyController = new HistoryLogController();
        loadHistoryData();
        loadProcesosFilter();
    }

    private void initComponents() {
        setTitle("Historial del Sistema - Sistema Licorería");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1200, 700);
        setLayout(new BorderLayout(10, 10));

        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header más compacto
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Filtros más compactos
        mainPanel.add(createFiltersPanel(), BorderLayout.CENTER);

        // Tabla con scroll
        mainPanel.add(createTablePanel(), BorderLayout.SOUTH);

        add(mainPanel);

        // Centrar en el desktop
        centrarEnDesktop();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15) // Padding reducido
        ));

        // Título y estadísticas
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("📋 Historial del Sistema");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Tamaño reducido
        titleLabel.setForeground(TEXT_WHITE);

        lblStats = new JLabel("Cargando estadísticas...");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Fuente más pequeña
        lblStats.setForeground(new Color(180, 200, 255));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(lblStats, BorderLayout.EAST);

        // Botones de acción más compactos
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); // Espacio reducido
        actionPanel.setBackground(new Color(0, 0, 0, 0));

        btnRefresh = new ModernButton("🔄 Actualizar", ACCENT_COLOR);
        btnRefresh.setPreferredSize(new Dimension(120, 32)); // Botón más compacto
        btnRefresh.addActionListener(e -> refreshData());

        btnExport = new ModernButton("📊 Exportar", SUCCESS_COLOR);
        btnExport.setPreferredSize(new Dimension(110, 32)); // Botón más compacto
        btnExport.addActionListener(e -> exportData());

        actionPanel.add(btnRefresh);
        actionPanel.add(btnExport);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(actionPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBackground(CARD_BACKGROUND);
        filtersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Filtros y Búsqueda",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13), // Fuente reducida
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(12, 15, 12, 15) // Padding reducido
        ));

        // Panel principal de filtros
        JPanel mainFiltersPanel = new JPanel(new GridBagLayout());
        mainFiltersPanel.setBackground(CARD_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4); // Espaciado reducido
        gbc.weightx = 1.0;

        // Búsqueda general
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel lblSearch = new JLabel("Búsqueda:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fuente reducida
        lblSearch.setForeground(TEXT_WHITE);
        mainFiltersPanel.add(lblSearch, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        txtSearch = new ModernTextField("Buscar en descripción, usuario o producto...");
        txtSearch.setPreferredSize(new Dimension(300, 32)); // Altura reducida
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
        });
        mainFiltersPanel.add(txtSearch, gbc);

        // Filtro por proceso
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblProceso = new JLabel("Filtrar por Proceso:");
        lblProceso.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fuente reducida
        lblProceso.setForeground(TEXT_WHITE);
        mainFiltersPanel.add(lblProceso, gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        cmbProcesoFilter = new ModernComboBox();
        cmbProcesoFilter.setPreferredSize(new Dimension(200, 32)); // Altura reducida
        cmbProcesoFilter.addItem("Todos los procesos");
        cmbProcesoFilter.addActionListener(e -> filterData());
        mainFiltersPanel.add(cmbProcesoFilter, gbc);

        // Botones de filtros
        gbc.gridx = 2; gbc.gridy = 1; gbc.gridwidth = 2;
        gbc.insets = new Insets(4, 20, 4, 4);

        JPanel filterButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterButtonsPanel.setBackground(CARD_BACKGROUND);

        btnSearch = new ModernButton("🔍 Aplicar Filtros", ACCENT_COLOR);
        btnSearch.setPreferredSize(new Dimension(140, 32)); // Botón más compacto
        btnSearch.addActionListener(e -> applyFilters());

        btnClearFilters = new ModernButton("🧹 Limpiar", new Color(149, 165, 166));
        btnClearFilters.setPreferredSize(new Dimension(100, 32)); // Botón más compacto
        btnClearFilters.addActionListener(e -> clearFilters());

        filterButtonsPanel.add(btnSearch);
        filterButtonsPanel.add(btnClearFilters);

        mainFiltersPanel.add(filterButtonsPanel, gbc);

        filtersPanel.add(mainFiltersPanel, BorderLayout.CENTER);

        return filtersPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Registros del Historial",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13), // Fuente reducida
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(12, 12, 12, 12) // Padding reducido
        ));

        // Modelo de tabla
        String[] columnNames = {
                "ID", "Fecha/Hora", "Proceso", "Usuario",
                "Producto", "Cantidad", "Descripción", "Estado"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class; // ID
                    case 5 -> Double.class;  // Cantidad
                    default -> String.class;
                };
            }
        };

        historyTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        historyTable.setRowSorter(sorter);

        // Configuración de la tabla más compacta
        historyTable.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Fuente reducida
        historyTable.setRowHeight(30); // Altura de fila reducida
        historyTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        historyTable.setIntercellSpacing(new Dimension(0, 0));
        historyTable.setShowGrid(false);
        historyTable.setAutoCreateRowSorter(true);
        historyTable.setBackground(new Color(50, 65, 95));
        historyTable.setForeground(TEXT_WHITE);
        historyTable.setGridColor(BORDER_COLOR);
        historyTable.setSelectionBackground(ACCENT_COLOR);
        historyTable.setSelectionForeground(TEXT_WHITE);

        // Header personalizado más compacto
        JTableHeader header = historyTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fuente reducida
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Altura reducida

        // Renderers personalizados
        historyTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        historyTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Fecha/Hora
        historyTable.getColumnModel().getColumn(2).setPreferredWidth(110); // Proceso
        historyTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Usuario
        historyTable.getColumnModel().getColumn(4).setPreferredWidth(110); // Producto
        historyTable.getColumnModel().getColumn(5).setPreferredWidth(70);  // Cantidad
        historyTable.getColumnModel().getColumn(6).setPreferredWidth(180); // Descripción
        historyTable.getColumnModel().getColumn(7).setPreferredWidth(70);  // Estado

        // Renderer para estado
        historyTable.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());

        // Renderer para cantidad (negativos en rojo)
        historyTable.getColumnModel().getColumn(5).setCellRenderer(new QuantityRenderer());

        // Scroll pane con información
        JScrollPane scrollPane = new JScrollPane(historyTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll más suave

        // Panel de información más compacto
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); // Padding reducido

        JLabel infoLabel = new JLabel("💡 Haga clic en cualquier columna para ordenar. Use los filtros para refinar la búsqueda.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Fuente más pequeña
        infoLabel.setForeground(new Color(180, 200, 255));

        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Fuente más pequeña
        countLabel.setForeground(TEXT_WHITE);

        // Actualizar contador cuando cambie la tabla
        tableModel.addTableModelListener(e -> {
            countLabel.setText("Registros mostrados: " + tableModel.getRowCount());
        });

        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(countLabel, BorderLayout.EAST);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void loadHistoryData() {
        SwingWorker<List<HistoryLog>, Void> worker = new SwingWorker<List<HistoryLog>, Void>() {
            @Override
            protected List<HistoryLog> doInBackground() throws Exception {
                return historyController.getAllHistoryLogs();
            }

            @Override
            protected void done() {
                try {
                    List<HistoryLog> logs = get();
                    tableModel.setRowCount(0);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

                    for (HistoryLog log : logs) {
                        Object[] row = {
                                log.getHistoryLogId(),
                                dateFormat.format(log.getFecha()),
                                log.getProcesoNombre() != null ? log.getProcesoNombre() : "N/A",
                                log.getUsuarioNombre() != null ? log.getUsuarioNombre() : "Sistema",
                                log.getProductoNombre() != null ? log.getProductoNombre() : "N/A",
                                log.getCantidad(),
                                log.getDescripcion() != null ? log.getDescripcion() : "",
                                log.isActivo() ? "Activo" : "Inactivo"
                        };
                        tableModel.addRow(row);
                    }

                    updateStats(logs.size());
                    showSuccess("Datos cargados exitosamente: " + logs.size() + " registros");

                } catch (Exception e) {
                    showError("Error al cargar el historial: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void loadProcesosFilter() {
        SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                return historyController.getAvailableProcesos();
            }

            @Override
            protected void done() {
                try {
                    List<String> procesos = get();
                    for (String proceso : procesos) {
                        // Extraer solo el nombre del proceso si viene en formato "ID - Nombre"
                        String nombreProceso = proceso;
                        if (proceso.contains(" - ")) {
                            nombreProceso = proceso.split(" - ")[1];
                        }
                        cmbProcesoFilter.addItem(nombreProceso);
                    }
                } catch (Exception e) {
                    showError("Error al cargar procesos: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void filterData() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedProceso = (String) cmbProcesoFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i).*" + searchText + ".*");

        if (selectedProceso != null && !selectedProceso.equals("Todos los procesos")) {
            rf = RowFilter.andFilter(List.of(
                    rf,
                    RowFilter.regexFilter("(?i).*" + selectedProceso + ".*", 2) // Columna proceso
            ));
        }

        sorter.setRowFilter(rf);
    }

    private void applyFilters() {
        // En una implementación real, aquí aplicarías filtros adicionales como fechas
        filterData();
        showInfo("Filtros aplicados correctamente");
    }

    private void clearFilters() {
        txtSearch.setText("Buscar en descripción, usuario o producto...");
        cmbProcesoFilter.setSelectedIndex(0);
        sorter.setRowFilter(null);
        loadHistoryData();
    }

    private void refreshData() {
        btnRefresh.setText("🔄 Cargando...");
        btnRefresh.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadHistoryData();
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

    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Exportar Historial");
        fileChooser.setSelectedFile(new java.io.File("historial_sistema_" +
                new SimpleDateFormat("yyyyMMdd").format(new java.util.Date()) + ".csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            // Implementar exportación a CSV
            showInfo("Funcionalidad de exportación en desarrollo");
        }
    }

    private void updateStats(int totalRecords) {
        lblStats.setText("Total registros: " + totalRecords + " | Última actualización: " +
                new SimpleDateFormat("HH:mm:ss").format(new java.util.Date()));
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

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>ℹ️ Información</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Clases internas para componentes modernos con tema azul
    class ModernTextField extends JTextField {
        private String placeholder;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Fuente reducida
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12) // Padding reducido
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
            setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Fuente reducida
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12) // Padding reducido
            ));
            setRenderer(new ModernComboBoxRenderer());
        }
    }

    class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13)); // Fuente reducida
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // Padding reducido

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

            setFont(new Font("Segoe UI", Font.BOLD, 11)); // Fuente reducida
            setBackground(color);
            setForeground(TEXT_WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15)); // Padding reducido

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

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Fuente reducida
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // Padding reducido

            if ("Activo".equals(value)) {
                label.setBackground(new Color(86, 202, 133, 50));
                label.setForeground(SUCCESS_COLOR);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_COLOR, 1),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7) // Padding reducido
                ));
            } else {
                label.setBackground(new Color(255, 118, 117, 50));
                label.setForeground(DANGER_COLOR);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DANGER_COLOR, 1),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7) // Padding reducido
                ));
            }

            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }

            return label;
        }
    }

    class QuantityRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Fuente reducida
            label.setBackground(new Color(50, 65, 95));
            label.setForeground(TEXT_WHITE);

            if (value instanceof Double) {
                double cantidad = (Double) value;
                if (cantidad < 0) {
                    label.setForeground(DANGER_COLOR);
                    label.setText(String.format("%.2f", cantidad));
                } else if (cantidad > 0) {
                    label.setForeground(SUCCESS_COLOR);
                    label.setText("+" + String.format("%.2f", cantidad));
                } else {
                    label.setForeground(new Color(180, 200, 255));
                    label.setText("0.00");
                }
            }

            if (isSelected) {
                label.setBackground(ACCENT_COLOR);
            }

            return label;
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