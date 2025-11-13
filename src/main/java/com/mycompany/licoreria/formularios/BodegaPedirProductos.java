package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.BodegaController;
import com.mycompany.licoreria.models.Producto;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BodegaPedirProductos extends JInternalFrame {
    private BodegaController bodegaController;

    // Componentes de la UI - CORREGIDO: lblCostoTotal declarado una sola vez
    private JTable productosTable;
    private DefaultTableModel tableModel;
    private JTextField txtSearch, txtCantidadSolicitada;
    private JTextArea txtObservaciones;
    private JComboBox<String> cmbProductos;
    private JButton btnSolicitar, btnLimpiar, btnVerReabastecer;
    private JLabel lblProductoSeleccionado, lblStockActual, lblCostoUnitario, lblCostoTotal;

    // Producto seleccionado
    private Producto productoSeleccionado;

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
    private final Color INFO_COLOR = new Color(155, 89, 182);

    public BodegaPedirProductos() {
        initComponents();
        setupModernDesign();
        bodegaController = new BodegaController();
        loadProductosParaReabastecer();
        loadProductosComboBox();
    }

    private void initComponents() {
        setTitle("Solicitar Productos a Proveedores - Sistema Licorería");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1000, 700);
        setLayout(new BorderLayout(10, 10));

        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header más compacto
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Content (Form + Table)
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setDividerLocation(350);
        splitPane.setDividerSize(3);
        splitPane.setBorder(BorderFactory.createEmptyBorder());

        // Form Panel
        splitPane.setTopComponent(createFormPanel());

        // Table Panel
        splitPane.setBottomComponent(createTablePanel());

        mainPanel.add(splitPane, BorderLayout.CENTER);

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

        // Título
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JLabel iconLabel = new JLabel("📞");
        iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 20));
        iconLabel.setForeground(TEXT_WHITE);

        JLabel titleLabel = new JLabel("Solicitar Productos a Proveedores");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_WHITE);

        titlePanel.add(iconLabel);
        titlePanel.add(Box.createHorizontalStrut(8));
        titlePanel.add(titleLabel);

        // Barra de búsqueda
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(new Color(0, 0, 0, 0));

        txtSearch = new ModernTextField("Buscar productos para reabastecer...");
        txtSearch.setPreferredSize(new Dimension(200, 32));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterTableData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterTableData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterTableData(); }
        });

        btnVerReabastecer = new ModernButton("🔄 Actualizar", INFO_COLOR);
        btnVerReabastecer.setPreferredSize(new Dimension(120, 32));
        btnVerReabastecer.addActionListener(e -> loadProductosParaReabastecer());

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnVerReabastecer, BorderLayout.EAST);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Formulario de Solicitud",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Panel principal del formulario
        JPanel mainFormPanel = new JPanel(new GridBagLayout());
        mainFormPanel.setBackground(CARD_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.weightx = 1.0;

        // Selección de producto
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        JLabel lblProducto = new JLabel("Producto:");
        lblProducto.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblProducto.setForeground(TEXT_WHITE);
        mainFormPanel.add(lblProducto, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridwidth = 3;
        cmbProductos = new ModernComboBox();
        cmbProductos.setPreferredSize(new Dimension(200, 32));
        cmbProductos.addActionListener(e -> onProductoSeleccionado());
        mainFormPanel.add(cmbProductos, gbc);

        // Información del producto seleccionado
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 4;
        JPanel infoPanel = createInfoProductoPanel();
        mainFormPanel.add(infoPanel, gbc);

        // Cantidad solicitada
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblCantidad = new JLabel("Cantidad:");
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCantidad.setForeground(TEXT_WHITE);
        mainFormPanel.add(lblCantidad, gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        txtCantidadSolicitada = new ModernTextField("Ingrese la cantidad");
        txtCantidadSolicitada.setPreferredSize(new Dimension(150, 32));
        txtCantidadSolicitada.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { calcularCostoTotal(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { calcularCostoTotal(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { calcularCostoTotal(); }
        });
        mainFormPanel.add(txtCantidadSolicitada, gbc);

        // Costo unitario y total - CORREGIDO: No redeclarar lblCostoTotal
        gbc.gridx = 2; gbc.gridy = 2;
        JLabel lblCostoUnit = new JLabel("Costo Unit:");
        lblCostoUnit.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCostoUnit.setForeground(TEXT_WHITE);
        mainFormPanel.add(lblCostoUnit, gbc);

        gbc.gridx = 3; gbc.gridy = 2;
        lblCostoUnitario = new JLabel("$0.00");
        lblCostoUnitario.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCostoUnitario.setForeground(SUCCESS_COLOR);
        mainFormPanel.add(lblCostoUnitario, gbc);

        gbc.gridx = 2; gbc.gridy = 3;
        JLabel lblCostoTotalLabel = new JLabel("Costo Total:"); // Cambiado el nombre
        lblCostoTotalLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCostoTotalLabel.setForeground(TEXT_WHITE);
        mainFormPanel.add(lblCostoTotalLabel, gbc);

        gbc.gridx = 3; gbc.gridy = 3;
        lblCostoTotal = new JLabel("$0.00"); // Usando la variable ya declarada
        lblCostoTotal.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblCostoTotal.setForeground(ACCENT_COLOR);
        mainFormPanel.add(lblCostoTotal, gbc);

        // Observaciones
        gbc.gridx = 0; gbc.gridy = 4;
        JLabel lblObservaciones = new JLabel("Observaciones:");
        lblObservaciones.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblObservaciones.setForeground(TEXT_WHITE);
        mainFormPanel.add(lblObservaciones, gbc);

        gbc.gridx = 1; gbc.gridy = 4; gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        txtObservaciones = new JTextArea(2, 20);
        txtObservaciones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        txtObservaciones.setLineWrap(true);
        txtObservaciones.setWrapStyleWord(true);
        txtObservaciones.setBackground(new Color(50, 65, 95));
        txtObservaciones.setForeground(TEXT_WHITE);
        txtObservaciones.setCaretColor(TEXT_WHITE);
        txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)
        ));
        JScrollPane scrollObservaciones = new JScrollPane(txtObservaciones);
        scrollObservaciones.getViewport().setBackground(new Color(50, 65, 95));
        mainFormPanel.add(scrollObservaciones, gbc);

        // Botones
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.0;
        gbc.insets = new Insets(15, 6, 6, 6);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        buttonPanel.setBackground(CARD_BACKGROUND);

        btnSolicitar = new ModernButton("📞 Enviar", SUCCESS_COLOR);
        btnSolicitar.setPreferredSize(new Dimension(120, 32));
        btnSolicitar.addActionListener(e -> enviarSolicitud());

        btnLimpiar = new ModernButton("🧹 Limpiar", new Color(149, 165, 166));
        btnLimpiar.setPreferredSize(new Dimension(100, 32));
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        buttonPanel.add(btnSolicitar);
        buttonPanel.add(btnLimpiar);

        mainFormPanel.add(buttonPanel, gbc);

        formPanel.add(mainFormPanel, BorderLayout.CENTER);

        return formPanel;
    }

    private JPanel createInfoProductoPanel() {
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 8, 4));
        infoPanel.setBackground(new Color(35, 65, 100));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR, 2),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        // Producto seleccionado
        JLabel lblProductoTitulo = new JLabel("Producto:");
        lblProductoTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblProductoTitulo.setForeground(TEXT_WHITE);

        lblProductoSeleccionado = new JLabel("Ninguno");
        lblProductoSeleccionado.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblProductoSeleccionado.setForeground(ACCENT_COLOR);

        // Stock actual
        JLabel lblStockTitulo = new JLabel("Stock Actual:");
        lblStockTitulo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStockTitulo.setForeground(TEXT_WHITE);

        lblStockActual = new JLabel("0 unidades");
        lblStockActual.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStockActual.setForeground(WARNING_COLOR);

        infoPanel.add(lblProductoTitulo);
        infoPanel.add(lblProductoSeleccionado);
        infoPanel.add(lblStockTitulo);
        infoPanel.add(lblStockActual);

        return infoPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Productos para Reabastecer",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        // Modelo de tabla
        String[] columnNames = {
                "ID", "Producto", "Proveedor", "Stock Actual", "Stock Mínimo",
                "Déficit", "Costo Unitario", "Prioridad"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return switch (columnIndex) {
                    case 0 -> Integer.class;
                    case 3, 4, 5 -> Double.class;
                    case 6 -> Double.class;
                    default -> String.class;
                };
            }
        };

        productosTable = new JTable(tableModel);

        // Configurar tabla más compacta
        setupTable();

        JScrollPane scrollPane = new JScrollPane(productosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Panel de información más compacto
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

        JLabel infoLabel = new JLabel("💡 Haga clic en un producto para cargarlo en el formulario");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(180, 200, 255));

        infoPanel.add(infoLabel, BorderLayout.CENTER);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void setupTable() {
        productosTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        productosTable.setRowHeight(30);
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.setIntercellSpacing(new Dimension(0, 0));
        productosTable.setShowGrid(false);
        productosTable.setBackground(new Color(50, 65, 95));
        productosTable.setForeground(TEXT_WHITE);
        productosTable.setGridColor(BORDER_COLOR);
        productosTable.setSelectionBackground(ACCENT_COLOR);
        productosTable.setSelectionForeground(TEXT_WHITE);

        // Header personalizado más compacto
        JTableHeader header = productosTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Anchos de columnas optimizados
        productosTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        productosTable.getColumnModel().getColumn(1).setPreferredWidth(140);
        productosTable.getColumnModel().getColumn(2).setPreferredWidth(110);
        productosTable.getColumnModel().getColumn(3).setPreferredWidth(90);
        productosTable.getColumnModel().getColumn(4).setPreferredWidth(90);
        productosTable.getColumnModel().getColumn(5).setPreferredWidth(70);
        productosTable.getColumnModel().getColumn(6).setPreferredWidth(90);
        productosTable.getColumnModel().getColumn(7).setPreferredWidth(80);

        // Renderers personalizados
        productosTable.getColumnModel().getColumn(5).setCellRenderer(new DeficitRenderer());
        productosTable.getColumnModel().getColumn(7).setCellRenderer(new PrioridadRenderer());

        // Listener para selección
        productosTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && productosTable.getSelectedRow() != -1) {
                cargarProductoDesdeTabla();
            }
        });
    }

    private void loadProductosParaReabastecer() {
        SwingWorker<List<Producto>, Void> worker = new SwingWorker<List<Producto>, Void>() {
            @Override
            protected List<Producto> doInBackground() throws Exception {
                return bodegaController.getProductosParaReabastecer();
            }

            @Override
            protected void done() {
                try {
                    List<Producto> productos = get();
                    tableModel.setRowCount(0);

                    for (Producto producto : productos) {
                        double deficit = producto.getCantidadMinimaBodega() - producto.getStockBodega();
                        String prioridad = calcularPrioridad(producto.getStockBodega(), producto.getCantidadMinimaBodega());

                        Object[] row = {
                                producto.getProductoId(),
                                producto.getNombre(),
                                producto.getProveedorNombre(),
                                producto.getStockBodega(),
                                producto.getCantidadMinimaBodega(),
                                Math.max(0, deficit),
                                producto.getCosto(),
                                prioridad
                        };
                        tableModel.addRow(row);
                    }

                    showSuccess("Se cargaron " + productos.size() + " productos que necesitan reabastecimiento");

                } catch (Exception e) {
                    showError("Error al cargar productos: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void loadProductosComboBox() {
        SwingWorker<List<Producto>, Void> worker = new SwingWorker<List<Producto>, Void>() {
            @Override
            protected List<Producto> doInBackground() throws Exception {
                return bodegaController.getAllProductos();
            }

            @Override
            protected void done() {
                try {
                    List<Producto> productos = get();
                    cmbProductos.removeAllItems();
                    cmbProductos.addItem("-- Seleccione un producto --");

                    for (Producto producto : productos) {
                        cmbProductos.addItem(producto.getProductoId() + " - " + producto.getNombre());
                    }

                } catch (Exception e) {
                    showError("Error al cargar productos: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void onProductoSeleccionado() {
        String selected = (String) cmbProductos.getSelectedItem();
        if (selected != null && !selected.equals("-- Seleccione un producto --")) {
            int productoId = Integer.parseInt(selected.split(" - ")[0]);
            cargarProductoPorId(productoId);
        } else {
            limpiarInformacionProducto();
        }
    }

    private void cargarProductoPorId(int productoId) {
        SwingWorker<Producto, Void> worker = new SwingWorker<Producto, Void>() {
            @Override
            protected Producto doInBackground() throws Exception {
                List<Producto> productos = bodegaController.getAllProductos();
                return productos.stream()
                        .filter(p -> p.getProductoId() == productoId)
                        .findFirst()
                        .orElse(null);
            }

            @Override
            protected void done() {
                try {
                    Producto producto = get();
                    if (producto != null) {
                        productoSeleccionado = producto;
                        actualizarInformacionProducto();
                    }
                } catch (Exception e) {
                    showError("Error al cargar producto: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void cargarProductoDesdeTabla() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow != -1) {
            int productoId = (int) tableModel.getValueAt(selectedRow, 0);
            cargarProductoPorId(productoId);

            // Seleccionar en el combobox
            for (int i = 0; i < cmbProductos.getItemCount(); i++) {
                if (cmbProductos.getItemAt(i).startsWith(productoId + " - ")) {
                    cmbProductos.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void actualizarInformacionProducto() {
        if (productoSeleccionado != null) {
            lblProductoSeleccionado.setText(productoSeleccionado.getNombre());
            lblStockActual.setText(String.format("%.2f %s",
                    productoSeleccionado.getStockBodega(),
                    productoSeleccionado.getUnidadMedida()));

            lblCostoUnitario.setText(String.format("$%.2f",
                    productoSeleccionado.getCosto().doubleValue()));

            // Actualizar colores según stock
            if (productoSeleccionado.getStockBodega() <= 0) {
                lblStockActual.setForeground(DANGER_COLOR);
            } else if (productoSeleccionado.getStockBodega() <= productoSeleccionado.getCantidadMinimaBodega()) {
                lblStockActual.setForeground(WARNING_COLOR);
            } else {
                lblStockActual.setForeground(SUCCESS_COLOR);
            }

            calcularCostoTotal();
        }
    }

    private void limpiarInformacionProducto() {
        productoSeleccionado = null;
        lblProductoSeleccionado.setText("Ninguno");
        lblStockActual.setText("0 unidades");
        lblCostoUnitario.setText("$0.00");
        lblCostoTotal.setText("$0.00");
        txtCantidadSolicitada.setText("Ingrese la cantidad");
        txtObservaciones.setText("");
    }

    private void calcularCostoTotal() {
        if (productoSeleccionado != null) {
            try {
                String cantidadStr = txtCantidadSolicitada.getText();
                if (!cantidadStr.equals("Ingrese la cantidad") && !cantidadStr.isEmpty()) {
                    double cantidad = Double.parseDouble(cantidadStr);
                    double costoTotal = cantidad * productoSeleccionado.getCosto().doubleValue();
                    lblCostoTotal.setText(String.format("$%.2f", costoTotal));
                } else {
                    lblCostoTotal.setText("$0.00");
                }
            } catch (NumberFormatException e) {
                lblCostoTotal.setText("$0.00");
            }
        }
    }

    private void filterTableData() {
        // Implementación simple de filtrado
        String searchText = txtSearch.getText().toLowerCase();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String producto = tableModel.getValueAt(i, 1).toString().toLowerCase();
            boolean visible = producto.contains(searchText);
            ((javax.swing.table.DefaultTableModel)tableModel).fireTableRowsUpdated(i, i);
        }
    }

    private void enviarSolicitud() {
        if (productoSeleccionado == null) {
            showError("Debe seleccionar un producto");
            return;
        }

        String cantidadStr = txtCantidadSolicitada.getText();
        if (cantidadStr.equals("Ingrese la cantidad") || cantidadStr.isEmpty()) {
            showError("Debe ingresar la cantidad a solicitar");
            txtCantidadSolicitada.requestFocus();
            return;
        }

        try {
            double cantidad = Double.parseDouble(cantidadStr);
            if (cantidad <= 0) {
                showError("La cantidad debe ser mayor a 0");
                return;
            }

            String observaciones = txtObservaciones.getText().trim();
            if (observaciones.isEmpty()) {
                observaciones = "Solicitud automática de reabastecimiento";
            }

            // Mostrar confirmación
            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                            "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                            "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>📞 Confirmar Solicitud</div>" +
                            "<div style='color: #ECF0F1; text-align: left;'>" +
                            "<p><b>Producto:</b> " + productoSeleccionado.getNombre() + "</p>" +
                            "<p><b>Cantidad:</b> " + cantidad + " " + productoSeleccionado.getUnidadMedida() + "</p>" +
                            "<p><b>Costo Total:</b> " + lblCostoTotal.getText() + "</p>" +
                            "<p><b>Proveedor:</b> " + productoSeleccionado.getProveedorNombre() + "</p>" +
                            "</div>" +
                            "</div>" +
                            "</div></html>",
                    "Confirmar Solicitud",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                btnSolicitar.setText("Enviando...");
                btnSolicitar.setEnabled(false);

                String finalObservaciones = observaciones;
                SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return bodegaController.crearSolicitudCompra(
                                productoSeleccionado.getProductoId(),
                                cantidad,
                                finalObservaciones
                        );
                    }

                    @Override
                    protected void done() {
                        try {
                            boolean success = get();
                            if (success) {
                                showSuccess("Solicitud enviada exitosamente al proveedor");
                                limpiarFormulario();
                                loadProductosParaReabastecer();
                            } else {
                                showError("Error al enviar la solicitud");
                            }
                        } catch (Exception e) {
                            showError("Error: " + e.getMessage());
                        } finally {
                            btnSolicitar.setText("📞 Enviar");
                            btnSolicitar.setEnabled(true);
                        }
                    }
                };

                worker.execute();
            }

        } catch (NumberFormatException e) {
            showError("Ingrese una cantidad válida");
            txtCantidadSolicitada.requestFocus();
        }
    }

    private void limpiarFormulario() {
        cmbProductos.setSelectedIndex(0);
        limpiarInformacionProducto();
        productosTable.clearSelection();
    }

    private String calcularPrioridad(double stockActual, double stockMinimo) {
        double porcentaje = (stockActual / stockMinimo) * 100;
        if (porcentaje <= 0) return "🔴 CRÍTICA";
        else if (porcentaje <= 50) return "🟡 ALTA";
        else if (porcentaje <= 100) return "🟠 MEDIA";
        else return "🟢 BAJA";
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
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));

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

    class DeficitRenderer extends DefaultTableCellRenderer {
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
                double deficit = (Double) value;
                if (deficit > 0) {
                    label.setForeground(DANGER_COLOR);
                    label.setText("+" + String.format("%.2f", deficit));
                } else {
                    label.setForeground(SUCCESS_COLOR);
                    label.setText(String.format("%.2f", deficit));
                }
            }

            if (isSelected) {
                label.setBackground(ACCENT_COLOR);
            }

            return label;
        }
    }

    class PrioridadRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            String prioridad = (String) value;
            if (prioridad.contains("CRÍTICA")) {
                label.setBackground(new Color(255, 118, 117, 50));
                label.setForeground(DANGER_COLOR);
            } else if (prioridad.contains("ALTA")) {
                label.setBackground(new Color(255, 193, 87, 50));
                label.setForeground(WARNING_COLOR);
            } else if (prioridad.contains("MEDIA")) {
                label.setBackground(new Color(255, 168, 87, 50));
                label.setForeground(new Color(255, 140, 0));
            } else {
                label.setBackground(new Color(86, 202, 133, 50));
                label.setForeground(SUCCESS_COLOR);
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