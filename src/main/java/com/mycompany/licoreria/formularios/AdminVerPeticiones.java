package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.PeticionStockController;
import com.mycompany.licoreria.models.PeticionStock;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AdminVerPeticiones extends JInternalFrame {
    private PeticionStockController peticionController;

    // Componentes de la UI
    private JTable peticionesTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;
    private JComboBox<String> cmbEstadoFilter;
    private JButton btnSearch, btnClearFilters, btnAprobar, btnRechazar, btnDespachar, btnRefresh;
    private JLabel lblStats;

    // Paleta de colores azules mejorada
    private final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private final Color ACCENT_COLOR = new Color(30, 144, 255);
    private final Color BACKGROUND_COLOR = new Color(30, 40, 60);
    private final Color CARD_BACKGROUND = new Color(40, 55, 80);
    private final Color BORDER_COLOR = new Color(100, 130, 180);
    private final Color TEXT_WHITE = Color.WHITE;
    private final Color SUCCESS_COLOR = new Color(86, 202, 133);
    private final Color WARNING_COLOR = new Color(255, 193, 87);
    private final Color DANGER_COLOR = new Color(255, 118, 117);
    private final Color INFO_COLOR = new Color(52, 152, 219);

    public AdminVerPeticiones() {
        initComponents();
        setupModernDesign();
        peticionController = new PeticionStockController();
        loadPeticionesData();
    }

    private void initComponents() {
        setTitle("Gestión de Peticiones de Stock - Sistema Licorería");
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

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Panel de filtros y botones - AHORA MÁS VISIBLE
        mainPanel.add(createFiltersPanel(), BorderLayout.CENTER);

        // Tabla con scroll - REDUCIDO PARA QUE SE VEAN LOS BOTONES
        JPanel tableContainer = new JPanel(new BorderLayout());
        tableContainer.add(createTablePanel(), BorderLayout.CENTER);
        tableContainer.setPreferredSize(new Dimension(1200, 400)); // ALTURA REDUCIDA
        mainPanel.add(tableContainer, BorderLayout.SOUTH);

        add(mainPanel);

        // Centrar en el desktop
        centrarEnDesktop();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Título y estadísticas
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("📈 Peticiones de Stock");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);

        lblStats = new JLabel("Cargando estadísticas...");
        lblStats.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblStats.setForeground(new Color(180, 200, 255));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(lblStats, BorderLayout.EAST);

        // Botones de acción
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionPanel.setBackground(new Color(0, 0, 0, 0));

        btnRefresh = new ModernButton("🔄 Actualizar", ACCENT_COLOR);
        btnRefresh.setPreferredSize(new Dimension(120, 32));
        btnRefresh.addActionListener(e -> refreshData());

        actionPanel.add(btnRefresh);

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
                        "Filtros y Acciones",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15) // AUMENTADO PADDING
        ));

        // Panel principal de filtros
        JPanel mainFiltersPanel = new JPanel(new GridBagLayout());
        mainFiltersPanel.setBackground(CARD_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.weightx = 1.0;

        // Búsqueda general
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel lblSearch = new JLabel("Búsqueda:");
        lblSearch.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblSearch.setForeground(TEXT_WHITE);
        mainFiltersPanel.add(lblSearch, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 2;
        txtSearch = new ModernTextField("Buscar por producto, usuario o observaciones...");
        txtSearch.setPreferredSize(new Dimension(300, 32));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
        });
        mainFiltersPanel.add(txtSearch, gbc);

        // Filtro por estado
        gbc.gridx = 3; gbc.gridy = 0;
        JLabel lblEstado = new JLabel("Filtrar por Estado:");
        lblEstado.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblEstado.setForeground(TEXT_WHITE);
        mainFiltersPanel.add(lblEstado, gbc);

        gbc.gridx = 4; gbc.gridy = 0;
        cmbEstadoFilter = new ModernComboBox();
        cmbEstadoFilter.setPreferredSize(new Dimension(150, 32));
        cmbEstadoFilter.addItem("Todos los estados");
        cmbEstadoFilter.addItem("pendiente");
        cmbEstadoFilter.addItem("aprobada");
        cmbEstadoFilter.addItem("rechazada");
        cmbEstadoFilter.addItem("despachada");
        cmbEstadoFilter.addActionListener(e -> filterData());
        mainFiltersPanel.add(cmbEstadoFilter, gbc);

        // Botones de acción - AHORA MÁS SEPARADOS Y VISIBLES
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 5;
        gbc.insets = new Insets(15, 4, 4, 4); // MÁS ESPACIO ARRIBA

        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0)); // MÁS ESPACIO ENTRE BOTONES
        actionButtonsPanel.setBackground(CARD_BACKGROUND);

        btnAprobar = new ModernButton("✅ Aprobar Petición", SUCCESS_COLOR);
        btnAprobar.setPreferredSize(new Dimension(140, 35)); // BOTONES MÁS GRANDES
        btnAprobar.setEnabled(false);
        btnAprobar.addActionListener(e -> aprobarPeticion());

        btnRechazar = new ModernButton("❌ Rechazar Petición", DANGER_COLOR);
        btnRechazar.setPreferredSize(new Dimension(140, 35));
        btnRechazar.setEnabled(false);
        btnRechazar.addActionListener(e -> rechazarPeticion());

        btnDespachar = new ModernButton("🚚 Despachar Petición", INFO_COLOR);
        btnDespachar.setPreferredSize(new Dimension(140, 35));
        btnDespachar.setEnabled(false);
        btnDespachar.addActionListener(e -> despacharPeticion());

        btnClearFilters = new ModernButton("🧹 Limpiar Filtros", new Color(149, 165, 166));
        btnClearFilters.setPreferredSize(new Dimension(120, 35));
        btnClearFilters.addActionListener(e -> clearFilters());

        actionButtonsPanel.add(btnAprobar);
        actionButtonsPanel.add(btnRechazar);
        actionButtonsPanel.add(btnDespachar);
        actionButtonsPanel.add(Box.createHorizontalStrut(20)); // MÁS SEPARACIÓN
        actionButtonsPanel.add(btnClearFilters);

        mainFiltersPanel.add(actionButtonsPanel, gbc);

        // Panel de información sobre los botones
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 5;
        gbc.insets = new Insets(10, 4, 4, 4);

        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        infoPanel.setBackground(CARD_BACKGROUND);

        JLabel infoLabel = new JLabel("💡 Seleccione una petición para habilitar los botones de acción");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(180, 200, 255));

        infoPanel.add(infoLabel);
        mainFiltersPanel.add(infoPanel, gbc);

        filtersPanel.add(mainFiltersPanel, BorderLayout.CENTER);

        return filtersPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Lista de Peticiones",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // Modelo de tabla
        String[] columnNames = {
                "ID", "Producto", "Solicitante", "Cantidad", "Unidad",
                "Fecha Solicitud", "Estado", "Stock Bodega", "Observaciones"
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
                    case 3 -> Double.class;  // Cantidad
                    case 7 -> Double.class;  // Stock Bodega
                    default -> String.class;
                };
            }
        };

        peticionesTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        peticionesTable.setRowSorter(sorter);

        // Configurar tabla
        setupTable();

        // Scroll pane con información
        JScrollPane scrollPane = new JScrollPane(peticionesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setPreferredSize(new Dimension(1200, 350)); // ALTURA FIJA PARA QUE NO OCUPE TODO

        // Panel de información
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JLabel infoLabel = new JLabel("💡 Seleccione una petición para realizar acciones. Use los filtros para encontrar peticiones específicas.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(180, 200, 255));

        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        countLabel.setForeground(TEXT_WHITE);

        // Actualizar contador cuando cambie la tabla
        tableModel.addTableModelListener(e -> {
            countLabel.setText("Peticiones mostradas: " + tableModel.getRowCount());
        });

        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(countLabel, BorderLayout.EAST);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void setupTable() {
        peticionesTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        peticionesTable.setRowHeight(30);
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
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Anchos de columnas optimizados
        peticionesTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        peticionesTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Producto
        peticionesTable.getColumnModel().getColumn(2).setPreferredWidth(110); // Solicitante
        peticionesTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Cantidad
        peticionesTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Unidad
        peticionesTable.getColumnModel().getColumn(5).setPreferredWidth(110); // Fecha
        peticionesTable.getColumnModel().getColumn(6).setPreferredWidth(90);  // Estado
        peticionesTable.getColumnModel().getColumn(7).setPreferredWidth(90);  // Stock Bodega
        peticionesTable.getColumnModel().getColumn(8).setPreferredWidth(180); // Observaciones

        // Renderers personalizados
        peticionesTable.getColumnModel().getColumn(6).setCellRenderer(new EstadoPeticionRenderer());
        peticionesTable.getColumnModel().getColumn(7).setCellRenderer(new StockRenderer());

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
                return peticionController.getAllPeticiones();
            }

            @Override
            protected void done() {
                try {
                    List<PeticionStock> peticiones = get();
                    tableModel.setRowCount(0);

                    for (PeticionStock peticion : peticiones) {
                        Object[] row = {
                                peticion.getPeticionId(),
                                peticion.getProductoNombre() != null ? peticion.getProductoNombre() : "N/A",
                                peticion.getUsuarioSolicitanteNombre() != null ? peticion.getUsuarioSolicitanteNombre() : "N/A",
                                peticion.getCantidadSolicitada(),
                                peticion.getUnidadMedida() != null ? peticion.getUnidadMedida() : "unidad",
                                peticion.getFechaSolicitud() != null ? peticion.getFechaSolicitud().toString() : "N/A",
                                peticion.getEstado() != null ? peticion.getEstado() : "pendiente",
                                peticion.getStockBodega(),
                                peticion.getObservaciones() != null ? peticion.getObservaciones() : ""
                        };
                        tableModel.addRow(row);
                    }

                    updateStats();
                    showSuccess("Datos cargados: " + peticiones.size() + " peticiones");

                } catch (Exception e) {
                    showError("Error al cargar peticiones: " + e.getMessage());
                    e.printStackTrace();
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
            rf = RowFilter.andFilter(List.of(
                    rf,
                    RowFilter.regexFilter("(?i).*" + selectedEstado + ".*", 6) // Columna estado
            ));
        }

        sorter.setRowFilter(rf);
    }

    private void clearFilters() {
        txtSearch.setText("Buscar por producto, usuario o observaciones...");
        cmbEstadoFilter.setSelectedIndex(0);
        sorter.setRowFilter(null);
    }

    private void updateButtonStates() {
        int selectedRow = peticionesTable.getSelectedRow();
        if (selectedRow != -1) {
            int modelRow = peticionesTable.convertRowIndexToModel(selectedRow);
            String estado = (String) tableModel.getValueAt(modelRow, 6);

            // HABILITAR BOTONES SEGÚN EL ESTADO
            btnAprobar.setEnabled("pendiente".equals(estado));
            btnRechazar.setEnabled("pendiente".equals(estado));
            btnDespachar.setEnabled("aprobada".equals(estado));

            // CAMBIAR COLORES PARA INDICAR ESTADO
            if ("pendiente".equals(estado)) {
                btnAprobar.setBackground(SUCCESS_COLOR);
                btnRechazar.setBackground(DANGER_COLOR);
                btnDespachar.setBackground(new Color(149, 165, 166)); // Deshabilitado
            } else if ("aprobada".equals(estado)) {
                btnAprobar.setBackground(new Color(149, 165, 166)); // Deshabilitado
                btnRechazar.setBackground(new Color(149, 165, 166)); // Deshabilitado
                btnDespachar.setBackground(INFO_COLOR);
            } else {
                // Para estados rechazada o despachada, deshabilitar todos
                btnAprobar.setBackground(new Color(149, 165, 166));
                btnRechazar.setBackground(new Color(149, 165, 166));
                btnDespachar.setBackground(new Color(149, 165, 166));
            }

        } else {
            // Si no hay selección, deshabilitar todos
            btnAprobar.setEnabled(false);
            btnRechazar.setEnabled(false);
            btnDespachar.setEnabled(false);
            btnAprobar.setBackground(new Color(149, 165, 166));
            btnRechazar.setBackground(new Color(149, 165, 166));
            btnDespachar.setBackground(new Color(149, 165, 166));
        }
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
        double cantidad = (double) tableModel.getValueAt(modelRow, 3);

        // Verificar stock
        double stockBodega = (double) tableModel.getValueAt(modelRow, 7);
        if (cantidad > stockBodega) {
            showError("❌ Stock insuficiente en bodega\n\nDisponible: " + stockBodega + "\nSolicitado: " + cantidad);
            return;
        }

        String observaciones = JOptionPane.showInputDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #27AE60;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>✅ Aprobar Petición</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p><b>Producto:</b> " + producto + "</p>" +
                        "<p><b>Cantidad:</b> " + cantidad + "</p>" +
                        "<p><b>Stock disponible:</b> " + stockBodega + "</p>" +
                        "<p>Observaciones adicionales:</p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Observaciones de Aprobación",
                JOptionPane.QUESTION_MESSAGE);

        if (observaciones != null && !observaciones.trim().isEmpty()) {
            ejecutarAccionPeticion(peticionId, "aprobar", observaciones.trim());
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
        double cantidad = (double) tableModel.getValueAt(modelRow, 3);

        String observaciones = JOptionPane.showInputDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>❌ Rechazar Petición</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p><b>Producto:</b> " + producto + "</p>" +
                        "<p><b>Cantidad solicitada:</b> " + cantidad + "</p>" +
                        "<p>Motivo del rechazo:</p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Motivo de Rechazo",
                JOptionPane.QUESTION_MESSAGE);

        if (observaciones != null && !observaciones.trim().isEmpty()) {
            ejecutarAccionPeticion(peticionId, "rechazar", observaciones.trim());
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
        double stockBodega = (double) tableModel.getValueAt(modelRow, 7);

        // Verificar stock nuevamente antes de despachar
        if (cantidad > stockBodega) {
            showError("❌ Stock insuficiente para despachar\n\nDisponible: " + stockBodega + "\nNecesario: " + cantidad);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>🚚 Confirmar Despacho</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p><b>Producto:</b> " + producto + "</p>" +
                        "<p><b>Cantidad a despachar:</b> " + cantidad + "</p>" +
                        "<p><b>Stock disponible:</b> " + stockBodega + "</p>" +
                        "<p><small>✅ Esta acción transferirá el stock de bodega al vendedor</small></p>" +
                        "<p><small>📦 Stock restante en bodega: " + (stockBodega - cantidad) + "</small></p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar Despacho",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            ejecutarAccionPeticion(peticionId, "despachar", "Despachado automáticamente desde el sistema");
        }
    }

    private void ejecutarAccionPeticion(int peticionId, String accion, String observaciones) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    switch (accion) {
                        case "aprobar":
                            return peticionController.aprobarPeticion(peticionId, 1, observaciones);
                        case "rechazar":
                            return peticionController.rechazarPeticion(peticionId, 1, observaciones);
                        case "despachar":
                            return peticionController.despacharPeticion(peticionId);
                        default:
                            return false;
                    }
                } catch (Exception e) {
                    throw new RuntimeException("Error en " + accion + ": " + e.getMessage());
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showSuccess("✅ Petición " + accion + "da exitosamente");
                        loadPeticionesData(); // RECARGAR DATOS
                    } else {
                        showError("❌ Error al " + accion + " la petición");
                    }
                } catch (Exception e) {
                    showError("❌ Error: " + e.getMessage());
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

    private void updateStats() {
        try {
            String stats = peticionController.getEstadisticasPeticiones();
            lblStats.setText(stats);
        } catch (Exception e) {
            lblStats.setText("Error cargando estadísticas");
        }
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

    // Métodos de utilidad para mensajes
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
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
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
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            setRenderer(new ModernComboBoxRenderer());
        }
    }

    class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

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
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

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

    class EstadoPeticionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            String estado = (String) value;
            switch (estado) {
                case "pendiente":
                    label.setBackground(new Color(255, 193, 87, 50));
                    label.setForeground(WARNING_COLOR);
                    break;
                case "aprobada":
                    label.setBackground(new Color(86, 202, 133, 50));
                    label.setForeground(SUCCESS_COLOR);
                    break;
                case "rechazada":
                    label.setBackground(new Color(255, 118, 117, 50));
                    label.setForeground(DANGER_COLOR);
                    break;
                case "despachada":
                    label.setBackground(new Color(52, 152, 219, 50));
                    label.setForeground(INFO_COLOR);
                    break;
                default:
                    label.setBackground(new Color(149, 165, 166, 50));
                    label.setForeground(new Color(127, 140, 141));
            }

            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(label.getForeground(), 1),
                    BorderFactory.createEmptyBorder(3, 7, 3, 7)
            ));

            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }

            return label;
        }
    }

    class StockRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setBackground(new Color(50, 65, 95));
            label.setForeground(TEXT_WHITE);

            if (value instanceof Double) {
                double stock = (Double) value;
                double cantidadSolicitada = (Double) table.getValueAt(row, 3);

                if (stock < cantidadSolicitada) {
                    label.setForeground(DANGER_COLOR);
                } else if (stock < cantidadSolicitada * 2) {
                    label.setForeground(WARNING_COLOR);
                } else {
                    label.setForeground(SUCCESS_COLOR);
                }

                label.setText(String.format("%.2f", stock));
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