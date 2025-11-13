package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.BodegaController;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class BodegaMainForm extends JInternalFrame {
    private BodegaController bodegaController;

    // Componentes de la UI
    private JTable productosTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JTextField txtSearch;
    private JButton btnRefresh, btnSolicitarProveedor, btnActualizarStock;
    private JLabel lblStats;

    // Cards de estadísticas
    private JLabel lblTotalProductos, lblStockBajo, lblStockCritico, lblValorInventario;

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
    private final Color INFO_COLOR = new Color(155, 89, 182); // Púrpura para información

    public BodegaMainForm() {
        initComponents();
        setupModernDesign();
        bodegaController = new BodegaController();
        loadProductosData();
        loadAlertasStock();
    }

    private void initComponents() {
        setTitle("Módulo de Bodega - Sistema Licorería");
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

        // Estadísticas más compactas
        mainPanel.add(createStatsPanel(), BorderLayout.CENTER);

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

        // Título y información del usuario
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("📦 Módulo de Bodega");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18)); // Tamaño reducido
        titleLabel.setForeground(TEXT_WHITE);

        JLabel userLabel = new JLabel("Usuario: " + SessionManager.getCurrentUser().getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Fuente más pequeña
        userLabel.setForeground(new Color(180, 200, 255));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(userLabel, BorderLayout.EAST);

        // Barra de búsqueda y botones
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0)); // Espacio reducido
        searchPanel.setBackground(new Color(0, 0, 0, 0));

        txtSearch = new ModernTextField("Buscar productos...");
        txtSearch.setPreferredSize(new Dimension(200, 32)); // Altura reducida
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0)); // Espacio reducido
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        btnRefresh = new ModernButton("🔄 Actualizar", ACCENT_COLOR);
        btnRefresh.setPreferredSize(new Dimension(100, 32)); // Botón más compacto
        btnRefresh.addActionListener(e -> refreshData());

        btnSolicitarProveedor = new ModernButton("📞 Solicitar", INFO_COLOR);
        btnSolicitarProveedor.setPreferredSize(new Dimension(100, 32));
        btnSolicitarProveedor.addActionListener(e -> abrirSolicitudProveedor());

        btnActualizarStock = new ModernButton("✏️ Stock", SUCCESS_COLOR);
        btnActualizarStock.setPreferredSize(new Dimension(90, 32));
        btnActualizarStock.addActionListener(e -> actualizarStock());

        buttonPanel.add(btnRefresh);
        buttonPanel.add(btnSolicitarProveedor);
        buttonPanel.add(btnActualizarStock);

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(buttonPanel, BorderLayout.EAST);

        headerPanel.add(titlePanel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 10, 0)); // Espacio reducido
        statsPanel.setBackground(new Color(0, 0, 0, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0)); // Padding reducido

        // Cards de estadísticas más compactas
        StatCard totalCard = new StatCard("📊 Total", "0", "Productos en inventario", PRIMARY_COLOR);
        StatCard bajoCard = new StatCard("⚠️ Bajo", "0", "Stock bajo", WARNING_COLOR);
        StatCard criticoCard = new StatCard("🔴 Crítico", "0", "Sin stock", DANGER_COLOR);
        StatCard valorCard = new StatCard("💰 Valor", "$0", "Valor inventario", SUCCESS_COLOR);

        // Referencias para actualizar
        lblTotalProductos = ((JLabel)totalCard.getComponent(1));
        lblStockBajo = ((JLabel)bajoCard.getComponent(1));
        lblStockCritico = ((JLabel)criticoCard.getComponent(1));
        lblValorInventario = ((JLabel)valorCard.getComponent(1));

        statsPanel.add(totalCard);
        statsPanel.add(bajoCard);
        statsPanel.add(criticoCard);
        statsPanel.add(valorCard);

        return statsPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Inventario de Productos",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 13), // Fuente reducida
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(12, 12, 12, 12) // Padding reducido
        ));

        // Crear tabla de productos
        String[] columnNames = {
                "ID", "Producto", "Proveedor", "Costo", "Precio",
                "Stock Bodega", "Mínimo", "Stock Vendedor", "Unidad", "Estado"
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
                    case 3, 4 -> Double.class; // Costo, Precio
                    case 5, 6, 7 -> Double.class; // Stocks
                    default -> String.class;
                };
            }
        };

        productosTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        productosTable.setRowSorter(sorter);

        // Configurar tabla más compacta
        setupTable();

        JScrollPane scrollPane = new JScrollPane(productosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Scroll más suave

        // Panel de información más compacto
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0)); // Padding reducido

        JLabel infoLabel = new JLabel("💡 Seleccione un producto para actualizar stock. Use la búsqueda para filtrar.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Fuente más pequeña
        infoLabel.setForeground(new Color(180, 200, 255));

        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Fuente más pequeña
        countLabel.setForeground(TEXT_WHITE);

        // Actualizar contador
        tableModel.addTableModelListener(e -> {
            countLabel.setText("Productos: " + tableModel.getRowCount());
        });

        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(countLabel, BorderLayout.EAST);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void setupTable() {
        productosTable.setFont(new Font("Segoe UI", Font.PLAIN, 11)); // Fuente reducida
        productosTable.setRowHeight(30); // Altura de fila reducida
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.setIntercellSpacing(new Dimension(0, 0));
        productosTable.setShowGrid(false);
        productosTable.setAutoCreateRowSorter(true);
        productosTable.setBackground(new Color(50, 65, 95));
        productosTable.setForeground(TEXT_WHITE);
        productosTable.setGridColor(BORDER_COLOR);
        productosTable.setSelectionBackground(ACCENT_COLOR);
        productosTable.setSelectionForeground(TEXT_WHITE);

        // Header personalizado más compacto
        JTableHeader header = productosTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fuente reducida
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Altura reducida

        // Anchos de columnas optimizados
        productosTable.getColumnModel().getColumn(0).setPreferredWidth(50);  // ID
        productosTable.getColumnModel().getColumn(1).setPreferredWidth(140); // Producto
        productosTable.getColumnModel().getColumn(2).setPreferredWidth(110); // Proveedor
        productosTable.getColumnModel().getColumn(3).setPreferredWidth(70);  // Costo
        productosTable.getColumnModel().getColumn(4).setPreferredWidth(70);  // Precio
        productosTable.getColumnModel().getColumn(5).setPreferredWidth(90);  // Stock Bodega
        productosTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Mínimo
        productosTable.getColumnModel().getColumn(7).setPreferredWidth(90);  // Stock Vendedor
        productosTable.getColumnModel().getColumn(8).setPreferredWidth(70);  // Unidad
        productosTable.getColumnModel().getColumn(9).setPreferredWidth(80);  // Estado

        // Renderers personalizados
        productosTable.getColumnModel().getColumn(5).setCellRenderer(new StockBodegaRenderer());
        productosTable.getColumnModel().getColumn(9).setCellRenderer(new EstadoProductoRenderer());
    }

    private void loadProductosData() {
        SwingWorker<List<Producto>, Void> worker = new SwingWorker<List<Producto>, Void>() {
            @Override
            protected List<Producto> doInBackground() throws Exception {
                return bodegaController.getAllProductos();
            }

            @Override
            protected void done() {
                try {
                    List<Producto> productos = get();
                    tableModel.setRowCount(0);

                    int stockBajo = 0;
                    int stockCritico = 0;
                    double valorTotal = 0;

                    for (Producto producto : productos) {
                        // Calcular estadísticas
                        if (producto.getStockBodega() <= producto.getCantidadMinimaBodega()) {
                            stockBajo++;
                        }
                        if (producto.getStockBodega() == 0) {
                            stockCritico++;
                        }
                        valorTotal += producto.getStockBodega() * producto.getCosto().doubleValue();

                        Object[] row = {
                                producto.getProductoId(),
                                producto.getNombre(),
                                producto.getProveedorNombre(),
                                producto.getCosto(),
                                producto.getPrecio(),
                                producto.getStockBodega(),
                                producto.getCantidadMinimaBodega(),
                                producto.getStockVendedor(),
                                producto.getUnidadMedida(),
                                producto.getStockBodega() > producto.getCantidadMinimaBodega() ? "Normal" : "Bajo"
                        };
                        tableModel.addRow(row);
                    }

                    // Actualizar estadísticas
                    updateStats(productos.size(), stockBajo, stockCritico, valorTotal);
                    showSuccess("Inventario cargado: " + productos.size() + " productos");

                } catch (Exception e) {
                    showError("Error al cargar productos: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void loadAlertasStock() {
        SwingWorker<List<Producto>, Void> worker = new SwingWorker<List<Producto>, Void>() {
            @Override
            protected List<Producto> doInBackground() throws Exception {
                return bodegaController.getAlertasStockCritico();
            }

            @Override
            protected void done() {
                try {
                    List<Producto> alertas = get();
                    // Aquí se podrían actualizar las alertas en tiempo real
                } catch (Exception e) {
                    // Manejar error silenciosamente
                }
            }
        };

        worker.execute();
    }

    private void filterData() {
        String searchText = txtSearch.getText().toLowerCase();
        RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i).*" + searchText + ".*");
        sorter.setRowFilter(rf);
    }

    private void refreshData() {
        btnRefresh.setText("🔄 Cargando...");
        btnRefresh.setEnabled(false);

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                loadProductosData();
                loadAlertasStock();
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

    private void abrirSolicitudProveedor() {
        BodegaPedirProductos solicitudForm = new BodegaPedirProductos();
        mostrarFormularioEnDesktop(solicitudForm);
    }

    private void actualizarStock() {
        int selectedRow = productosTable.getSelectedRow();
        if (selectedRow == -1) {
            showError("Debe seleccionar un producto para actualizar su stock");
            return;
        }

        int modelRow = productosTable.convertRowIndexToModel(selectedRow);
        int productoId = (int) tableModel.getValueAt(modelRow, 0);
        String productoNombre = (String) tableModel.getValueAt(modelRow, 1);
        double stockActual = (double) tableModel.getValueAt(modelRow, 5);

        String nuevoStockStr = JOptionPane.showInputDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>✏️ Actualizar Stock</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p><b>Producto:</b> " + productoNombre + "</p>" +
                        "<p><b>Stock actual:</b> " + stockActual + "</p>" +
                        "<p>Ingrese el nuevo stock:</p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Actualizar Stock",
                JOptionPane.QUESTION_MESSAGE);

        if (nuevoStockStr != null && !nuevoStockStr.trim().isEmpty()) {
            try {
                double nuevoStock = Double.parseDouble(nuevoStockStr.trim());
                if (nuevoStock < 0) {
                    showError("El stock no puede ser negativo");
                    return;
                }

                boolean success = bodegaController.actualizarStockBodega(productoId, nuevoStock);
                if (success) {
                    showSuccess("Stock actualizado exitosamente");
                    refreshData();
                } else {
                    showError("Error al actualizar el stock");
                }

            } catch (NumberFormatException e) {
                showError("Ingrese un valor numérico válido");
            }
        }
    }

    private void updateStats(int total, int stockBajo, int stockCritico, double valorTotal) {
        lblTotalProductos.setText(String.valueOf(total));
        lblStockBajo.setText(String.valueOf(stockBajo));
        lblStockCritico.setText(String.valueOf(stockCritico));
        lblValorInventario.setText(String.format("$%,.2f", valorTotal));
    }

    private void mostrarFormularioEnDesktop(JInternalFrame formulario) {
        formulario.setVisible(true);
        com.mycompany.licoreria.Licoreria.getDesktopPane().add(formulario);
        com.mycompany.licoreria.Licoreria.centrarFormulario(formulario);

        try {
            formulario.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
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
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12)); // Padding reducido

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
            label.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Fuente reducida
            label.setBackground(new Color(50, 65, 95));
            label.setForeground(TEXT_WHITE);

            if (value instanceof Double) {
                double stock = (Double) value;
                double minimo = (Double) table.getValueAt(row, 6); // Columna mínimo

                if (stock == 0) {
                    label.setForeground(DANGER_COLOR);
                    label.setText("🔴 " + String.format("%.2f", stock));
                } else if (stock <= minimo) {
                    label.setForeground(WARNING_COLOR);
                    label.setText("🟡 " + String.format("%.2f", stock));
                } else if (stock <= minimo * 2) {
                    label.setForeground(ACCENT_COLOR);
                    label.setText("🔵 " + String.format("%.2f", stock));
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

    class EstadoProductoRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10)); // Fuente reducida
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8)); // Padding reducido

            String estado = (String) value;
            if ("Normal".equals(estado)) {
                label.setBackground(new Color(86, 202, 133, 50));
                label.setForeground(SUCCESS_COLOR);
            } else {
                label.setBackground(new Color(255, 193, 87, 50));
                label.setForeground(WARNING_COLOR);
            }

            label.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(label.getForeground(), 1),
                    BorderFactory.createEmptyBorder(3, 7, 3, 7) // Padding reducido
            ));

            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }

            return label;
        }
    }

    class StatCard extends JPanel {
        public StatCard(String title, String value, String description, Color color) {
            setLayout(new BorderLayout());
            setBackground(CARD_BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(15, 15, 15, 15) // Padding reducido
            ));

            // Header con icono y título
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(CARD_BACKGROUND);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 12)); // Fuente reducida
            titleLabel.setForeground(TEXT_WHITE);

            headerPanel.add(titleLabel, BorderLayout.CENTER);

            // Valor
            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24)); // Tamaño reducido
            valueLabel.setForeground(color);
            valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

            // Descripción
            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10)); // Fuente reducida
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
                            BorderFactory.createEmptyBorder(14, 14, 14, 14) // Padding reducido
                    ));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(15, 15, 15, 15)
                    ));
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