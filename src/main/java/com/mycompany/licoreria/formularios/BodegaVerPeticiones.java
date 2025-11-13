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
    private JButton btnClearFilters, btnAprobar, btnRechazar, btnDespachar, btnRefresh;
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
    private final Color INFO_COLOR = new Color(155, 89, 182); // Púrpura para info

    public BodegaVerPeticiones() {
        initComponents();
        setupModernDesign();
        peticionController = new PeticionBodegaController();
        loadPeticionesData();
    }

    private void initComponents() {
        setTitle("📋 Peticiones de Vendedores - Bodega");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1100, 600); // Ventana más compacta
        setLayout(new BorderLayout(5, 5));

        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(5, 5));
        mainPanel.setBorder(createEmptyBorder(8, 8, 8, 8));

        // Header compacto
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Panel de filtros y botones - AHORA MÁS VISIBLE
        mainPanel.add(createFiltersPanel(), BorderLayout.CENTER);

        // Tabla compacta
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
                createEmptyBorder(6, 10, 6, 10)
        ));

        // Título compacto
        JLabel titleLabel = new JLabel("📦 GESTIÓN DE PETICIONES");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_WHITE);

        // Panel de estadísticas
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        statsPanel.setBackground(new Color(0, 0, 0, 0));

        lblStats = new JLabel("Cargando...");
        lblStats.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStats.setForeground(TEXT_WHITE);

        btnRefresh = new ModernButton("🔄", ACCENT_COLOR);
        btnRefresh.setToolTipText("Actualizar lista");
        btnRefresh.setPreferredSize(new Dimension(35, 25));
        btnRefresh.addActionListener(e -> refreshData());

        statsPanel.add(lblStats);
        statsPanel.add(btnRefresh);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(statsPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFiltersPanel() {
        JPanel filtersPanel = new JPanel(new BorderLayout());
        filtersPanel.setBackground(CARD_BACKGROUND);
        filtersPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                createEmptyBorder(8, 10, 8, 10)
        ));

        // Panel de búsqueda rápida
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        searchPanel.setBackground(CARD_BACKGROUND);

        JLabel lblSearch = new JLabel("Buscar:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblSearch.setForeground(TEXT_WHITE);

        txtSearch = new ModernTextField("producto, vendedor...");
        txtSearch.setPreferredSize(new Dimension(180, 28));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
        });

        JLabel lblEstado = new JLabel("Estado:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblEstado.setForeground(TEXT_WHITE);

        cmbEstadoFilter = new ModernComboBox();
        cmbEstadoFilter.setPreferredSize(new Dimension(120, 28));
        cmbEstadoFilter.addItem("Todos");
        cmbEstadoFilter.addItem("pendiente");
        cmbEstadoFilter.addItem("aprobada");
        cmbEstadoFilter.addItem("rechazada");
        cmbEstadoFilter.addItem("despachada");
        cmbEstadoFilter.addActionListener(e -> filterData());

        btnClearFilters = new ModernButton("🧹", new Color(149, 165, 166));
        btnClearFilters.setToolTipText("Limpiar filtros");
        btnClearFilters.setPreferredSize(new Dimension(35, 28));
        btnClearFilters.addActionListener(e -> clearFilters());

        searchPanel.add(lblSearch);
        searchPanel.add(txtSearch);
        searchPanel.add(Box.createHorizontalStrut(10));
        searchPanel.add(lblEstado);
        searchPanel.add(cmbEstadoFilter);
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(btnClearFilters);

        // Panel de botones de acción - AHORA MÁS PROMINENTE
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        actionPanel.setBackground(CARD_BACKGROUND);
        actionPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                "ACCIONES RÁPIDAS",
                0, 0,
                new Font("Segoe UI", Font.BOLD, 11),
                TEXT_WHITE
        ));

        btnAprobar = new ModernButton("✅ APROBAR", SUCCESS_COLOR);
        btnAprobar.setPreferredSize(new Dimension(110, 35));
        btnAprobar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnAprobar.setEnabled(false);
        btnAprobar.addActionListener(e -> aprobarPeticion());

        btnRechazar = new ModernButton("❌ RECHAZAR", DANGER_COLOR);
        btnRechazar.setPreferredSize(new Dimension(110, 35));
        btnRechazar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnRechazar.setEnabled(false);
        btnRechazar.addActionListener(e -> rechazarPeticion());

        btnDespachar = new ModernButton("🚚 DESPACHAR", INFO_COLOR);
        btnDespachar.setPreferredSize(new Dimension(110, 35));
        btnDespachar.setFont(new Font("Segoe UI", Font.BOLD, 11));
        btnDespachar.setEnabled(false);
        btnDespachar.addActionListener(e -> despacharPeticion());

        actionPanel.add(btnAprobar);
        actionPanel.add(btnRechazar);
        actionPanel.add(btnDespachar);

        // Panel principal que combina búsqueda y acciones
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(CARD_BACKGROUND);
        mainPanel.add(searchPanel, BorderLayout.WEST);
        mainPanel.add(actionPanel, BorderLayout.CENTER);

        filtersPanel.add(mainPanel, BorderLayout.CENTER);

        return filtersPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(SECONDARY_COLOR, 1),
                createEmptyBorder(8, 8, 8, 8)
        ));

        // Columnas MÁS COMPACTAS
        String[] columnNames = {
                "ID", "Producto", "Vendedor", "Cant", "Unid", "Fecha", "Stock", "Estado"
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
                    case 3, 6 -> Double.class; // Cantidad, Stock
                    default -> String.class;
                };
            }
        };

        peticionesTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        peticionesTable.setRowSorter(sorter);

        // Configurar tabla MÁS COMPACTA
        setupCompactTable();

        JScrollPane scrollPane = new JScrollPane(peticionesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);

        // Información compacta
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(createEmptyBorder(4, 0, 0, 0));

        JLabel infoLabel = new JLabel("💡 Seleccione una petición para gestionarla");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 9));
        infoLabel.setForeground(new Color(180, 200, 255));

        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 9));
        countLabel.setForeground(TEXT_WHITE);

        tableModel.addTableModelListener(e -> {
            countLabel.setText("Total: " + tableModel.getRowCount());
        });

        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(countLabel, BorderLayout.EAST);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void setupCompactTable() {
        peticionesTable.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        peticionesTable.setRowHeight(24); // FILAS MÁS COMPACTAS
        peticionesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        peticionesTable.setIntercellSpacing(new Dimension(0, 0));
        peticionesTable.setShowGrid(false);
        peticionesTable.setAutoCreateRowSorter(true);
        peticionesTable.setBackground(new Color(50, 65, 95));
        peticionesTable.setForeground(TEXT_WHITE);
        peticionesTable.setGridColor(BORDER_COLOR);
        peticionesTable.setSelectionBackground(ACCENT_COLOR);
        peticionesTable.setSelectionForeground(TEXT_WHITE);

        // Header compacto
        JTableHeader header = peticionesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 10));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 28));

        // Anchos de columnas MÁS COMPACTOS
        peticionesTable.getColumnModel().getColumn(0).setPreferredWidth(40);   // ID
        peticionesTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Producto
        peticionesTable.getColumnModel().getColumn(2).setPreferredWidth(90);   // Vendedor
        peticionesTable.getColumnModel().getColumn(3).setPreferredWidth(50);   // Cantidad
        peticionesTable.getColumnModel().getColumn(4).setPreferredWidth(45);   // Unidad
        peticionesTable.getColumnModel().getColumn(5).setPreferredWidth(80);   // Fecha
        peticionesTable.getColumnModel().getColumn(6).setPreferredWidth(60);   // Stock
        peticionesTable.getColumnModel().getColumn(7).setPreferredWidth(70);   // Estado

        // Renderers personalizados
        peticionesTable.getColumnModel().getColumn(6).setCellRenderer(new StockBodegaRenderer());
        peticionesTable.getColumnModel().getColumn(7).setCellRenderer(new EstadoRenderer());

        // Listener para selección
        peticionesTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateButtonStates();
            }
        });
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
                                formatFecha(peticion.getFechaSolicitud()),
                                peticion.getStockBodega(),
                                peticion.getEstado()
                        };
                        tableModel.addRow(row);
                    }

                    updateStats(peticiones.size());
                    showSuccess("Peticiones cargadas: " + peticiones.size());

                } catch (Exception e) {
                    showError("Error al cargar peticiones: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        worker.execute();
    }

    private String formatFecha(java.sql.Timestamp fecha) {
        if (fecha == null) return "";
        return new java.text.SimpleDateFormat("dd/MM HH:mm").format(fecha);
    }

    private void filterData() {
        String searchText = txtSearch.getText().toLowerCase();
        String selectedEstado = (String) cmbEstadoFilter.getSelectedItem();

        RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i).*" + searchText + ".*");

        if (selectedEstado != null && !selectedEstado.equals("Todos")) {
            RowFilter<DefaultTableModel, Object> estadoFilter = RowFilter.regexFilter("(?i)^" + selectedEstado + "$", 7);
            rf = RowFilter.andFilter(java.util.List.of(rf, estadoFilter));
        }

        sorter.setRowFilter(rf);
    }

    private void clearFilters() {
        txtSearch.setText("producto, vendedor...");
        cmbEstadoFilter.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }

    private void updateButtonStates() {
        int selectedRow = peticionesTable.getSelectedRow();
        boolean tieneSeleccion = selectedRow != -1;

        if (tieneSeleccion) {
            int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
            Object estadoObj = tableModel.getValueAt(modelRow, 7);
            String estado = estadoObj != null ? estadoObj.toString() : "";

            // Actualizar botones según estado
            btnAprobar.setEnabled("pendiente".equals(estado));
            btnRechazar.setEnabled("pendiente".equals(estado));
            btnDespachar.setEnabled("aprobada".equals(estado));

        } else {
            btnAprobar.setEnabled(false);
            btnRechazar.setEnabled(false);
            btnDespachar.setEnabled(false);
        }
    }

    private void aprobarPeticion() {
        int selectedRow = peticionesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleccione una petición para aprobar");
            return;
        }

        int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
        int peticionId = (int) tableModel.getValueAt(modelRow, 0);
        String producto = (String) tableModel.getValueAt(modelRow, 1);
        double cantidad = (double) tableModel.getValueAt(modelRow, 3);
        double stockBodega = (double) tableModel.getValueAt(modelRow, 6);

        // Verificar stock
        if (cantidad > stockBodega) {
            int option = JOptionPane.showConfirmDialog(this,
                    "<html>Stock insuficiente!<br>¿Aprobar parcialmente con " + stockBodega + " unidades?",
                    "Stock Insuficiente",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (option != JOptionPane.YES_OPTION) {
                return;
            }
        }

        String observaciones = JOptionPane.showInputDialog(this,
                "Observaciones para la aprobación:",
                "Aprobar Petición",
                JOptionPane.QUESTION_MESSAGE);

        if (observaciones != null && !observaciones.trim().isEmpty()) {
            ejecutarAccionPeticion(peticionId, "aprobar", observaciones);
        }
    }

    private void rechazarPeticion() {
        int selectedRow = peticionesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleccione una petición para rechazar");
            return;
        }

        int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
        int peticionId = (int) tableModel.getValueAt(modelRow, 0);

        String motivo = JOptionPane.showInputDialog(this,
                "Motivo del rechazo:",
                "Rechazar Petición",
                JOptionPane.QUESTION_MESSAGE);

        if (motivo != null && !motivo.trim().isEmpty()) {
            ejecutarAccionPeticion(peticionId, "rechazar", motivo);
        }
    }

    private void despacharPeticion() {
        int selectedRow = peticionesTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Seleccione una petición para despachar");
            return;
        }

        int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
        int peticionId = (int) tableModel.getValueAt(modelRow, 0);
        String producto = (String) tableModel.getValueAt(modelRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html>¿Despachar <b>" + producto + "</b>?",
                "Confirmar Despacho",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            ejecutarAccionPeticion(peticionId, "despachar", "Despachado por sistema");
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
                        showSuccess("Acción completada exitosamente");
                        refreshData();
                    } else {
                        showError("Error al procesar la acción");
                    }
                } catch (Exception e) {
                    showError("Error: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void refreshData() {
        btnRefresh.setText("⏳");
        btnRefresh.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadPeticionesData();
                return null;
            }

            @Override
            protected void done() {
                btnRefresh.setText("🔄");
                btnRefresh.setEnabled(true);
            }
        };

        worker.execute();
    }

    private void updateStats(int totalPendientes) {
        lblStats.setText("Pendientes: " + totalPendientes);
    }

    private void setupModernDesign() {
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)
        ));
    }

    private void centrarEnDesktop() {
        try {
            com.mycompany.licoreria.Licoreria.centrarFormulario(this);
        } catch (Exception e) {
            // Si falla el centrado, continuar sin él
        }
    }

    // Métodos de utilidad para mensajes
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    // Clases internas para componentes modernos
    class ModernTextField extends JTextField {
        private String placeholder;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    createEmptyBorder(4, 8, 4, 8)
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
            setFont(new Font("Segoe UI", Font.PLAIN, 11));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    createEmptyBorder(4, 8, 4, 8)
            ));
            setRenderer(new ModernComboBoxRenderer());
        }
    }

    class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            label.setBorder(createEmptyBorder(2, 6, 2, 6));

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

            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBackground(color);
            setForeground(TEXT_WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(createEmptyBorder(6, 10, 6, 10));

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
            label.setFont(new Font("Segoe UI", Font.BOLD, 9));

            if (value instanceof Double) {
                double stock = (Double) value;
                double cantidadSolicitada = (Double) table.getValueAt(row, 3);

                if (stock < cantidadSolicitada) {
                    label.setForeground(DANGER_COLOR);
                    label.setText("🔴" + stock);
                } else if (stock < cantidadSolicitada * 1.5) {
                    label.setForeground(WARNING_COLOR);
                    label.setText("🟡" + stock);
                } else {
                    label.setForeground(SUCCESS_COLOR);
                    label.setText("🟢" + stock);
                }
            }

            if (isSelected) {
                label.setBackground(ACCENT_COLOR);
            }

            return label;
        }
    }

    class EstadoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 9));

            String estado = value != null ? value.toString() : "";
            switch (estado) {
                case "pendiente":
                    label.setForeground(WARNING_COLOR);
                    label.setText("⏳ Pendiente");
                    break;
                case "aprobada":
                    label.setForeground(SUCCESS_COLOR);
                    label.setText("✅ Aprobada");
                    break;
                case "rechazada":
                    label.setForeground(DANGER_COLOR);
                    label.setText("❌ Rechazada");
                    break;
                case "despachada":
                    label.setForeground(INFO_COLOR);
                    label.setText("🚚 Despachada");
                    break;
                default:
                    label.setForeground(TEXT_WHITE);
                    label.setText(estado);
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

            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 40, 60),
                    getWidth(), getHeight(), new Color(50, 70, 100)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}