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
    private JButton btnSearch, btnRefresh, btnSolicitarProveedor, btnActualizarStock;
    private JLabel lblStats;

    // Cards de estadísticas
    private JLabel lblTotalProductos, lblStockBajo, lblStockCritico, lblValorInventario;

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

        setSize(1200, 800);
        setLayout(new BorderLayout(10, 10));

        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Estadísticas
        mainPanel.add(createStatsPanel(), BorderLayout.CENTER);

        // Contenido principal (pestañas)
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
                BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));

        // Título y información del usuario
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(new Color(0, 0, 0, 0));

        JLabel titleLabel = new JLabel("📦 Módulo de Bodega");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_WHITE);

        JLabel userLabel = new JLabel("Usuario: " + SessionManager.getCurrentUser().getUsername());
        userLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userLabel.setForeground(new Color(180, 200, 255));

        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(userLabel, BorderLayout.EAST);

        // Barra de búsqueda y botones
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setBackground(new Color(0, 0, 0, 0));

        txtSearch = new ModernTextField("Buscar productos...");
        txtSearch.setPreferredSize(new Dimension(250, 35));
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filterData(); }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(new Color(0, 0, 0, 0));

        btnRefresh = new ModernButton("🔄 Actualizar", ACCENT_COLOR);
        btnRefresh.addActionListener(e -> refreshData());

        btnSolicitarProveedor = new ModernButton("📞 Solicitar a Proveedor", INFO_COLOR);
        btnSolicitarProveedor.addActionListener(e -> abrirSolicitudProveedor());

        btnActualizarStock = new ModernButton("✏️ Actualizar Stock", SUCCESS_COLOR);
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
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 15, 0));
        statsPanel.setBackground(new Color(0, 0, 0, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Cards de estadísticas
        StatCard totalCard = new StatCard("📊 Total Productos", "0", "Productos en inventario", PRIMARY_COLOR);
        StatCard bajoCard = new StatCard("⚠️ Stock Bajo", "0", "Productos con stock bajo", WARNING_COLOR);
        StatCard criticoCard = new StatCard("🔴 Stock Crítico", "0", "Productos sin stock", DANGER_COLOR);
        StatCard valorCard = new StatCard("💰 Valor Inventario", "$0", "Valor total del inventario", SUCCESS_COLOR);

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

    private JPanel createContentPanel() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(CARD_BACKGROUND);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Gestión de Inventario",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        // Crear pestañas
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabbedPane.setBackground(CARD_BACKGROUND);
        tabbedPane.setForeground(TEXT_WHITE);

        // Pestaña de inventario completo
        tabbedPane.addTab("📦 Inventario Completo", createInventarioTab());

        // Pestaña de stock bajo
        tabbedPane.addTab("⚠️ Stock Bajo", createStockBajoTab());

        // Pestaña de reabastecimiento
        tabbedPane.addTab("🔄 Para Reabastecer", createReabastecerTab());

        // Pestaña de alertas
        tabbedPane.addTab("🚨 Alertas Críticas", createAlertasTab());

        contentPanel.add(tabbedPane, BorderLayout.CENTER);

        return contentPanel;
    }

    private JPanel createInventarioTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);

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

        // Configurar tabla
        setupTable();

        JScrollPane scrollPane = new JScrollPane(productosTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        // Panel de información
        JPanel infoPanel = createInfoPanel();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createStockBajoTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(
                "<html><div style='text-align: center; color: #BDC3C7;'>" +
                        "<h3>⚠️ Productos con Stock Bajo</h3>" +
                        "<p>Vista especializada para productos que requieren atención inmediata</p>" +
                        "<p><small>Productos con stock por debajo del mínimo establecido</small></p>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createReabastecerTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel label = new JLabel(
                "<html><div style='text-align: center; color: #BDC3C7;'>" +
                        "<h3>🔄 Productos para Reabastecer</h3>" +
                        "<p>Lista de productos que necesitan reabastecimiento</p>" +
                        "<p><small>Productos con stock cercano al mínimo o con alta rotación</small></p>" +
                        "</div></html>",
                SwingConstants.CENTER
        );
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        panel.add(label, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlertasTab() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de alertas
        JPanel alertasPanel = new JPanel();
        alertasPanel.setLayout(new BoxLayout(alertasPanel, BoxLayout.Y_AXIS));
        alertasPanel.setBackground(CARD_BACKGROUND);

        // Aquí se cargarán las alertas dinámicamente
        JLabel titulo = new JLabel("🚨 Alertas Críticas del Sistema");
        titulo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        titulo.setForeground(DANGER_COLOR);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        alertasPanel.add(titulo);
        alertasPanel.add(Box.createVerticalStrut(20));

        // Alertas de ejemplo
        alertasPanel.add(createAlertaItem("🔴 Stock Crítico", "Ron Zacapa - Stock: 2 unidades", DANGER_COLOR));
        alertasPanel.add(Box.createVerticalStrut(10));
        alertasPanel.add(createAlertaItem("🟡 Peticiones Pendientes", "5 peticiones de vendedores esperando", WARNING_COLOR));
        alertasPanel.add(Box.createVerticalStrut(10));
        alertasPanel.add(createAlertaItem("🔵 Reabastecimiento", "Cerveza Artesanal necesita reposición", INFO_COLOR));

        JScrollPane scrollPane = new JScrollPane(alertasPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(CARD_BACKGROUND);

        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAlertaItem(String titulo, String descripcion, Color color) {
        JPanel alertaPanel = new JPanel(new BorderLayout());
        alertaPanel.setBackground(CARD_BACKGROUND);
        alertaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        alertaPanel.setMaximumSize(new Dimension(500, 80));

        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tituloLabel.setForeground(color);

        JLabel descLabel = new JLabel(descripcion);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        descLabel.setForeground(TEXT_WHITE);

        JButton actionBtn = new ModernButton("Ver Detalles", color);
        actionBtn.setPreferredSize(new Dimension(100, 30));

        alertaPanel.add(tituloLabel, BorderLayout.NORTH);
        alertaPanel.add(descLabel, BorderLayout.CENTER);
        alertaPanel.add(actionBtn, BorderLayout.EAST);

        return alertaPanel;
    }

    private void setupTable() {
        productosTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        productosTable.setRowHeight(35);
        productosTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productosTable.setIntercellSpacing(new Dimension(0, 0));
        productosTable.setShowGrid(false);
        productosTable.setAutoCreateRowSorter(true);
        productosTable.setBackground(new Color(50, 65, 95));
        productosTable.setForeground(TEXT_WHITE);
        productosTable.setGridColor(BORDER_COLOR);
        productosTable.setSelectionBackground(ACCENT_COLOR);
        productosTable.setSelectionForeground(TEXT_WHITE);

        // Header personalizado
        JTableHeader header = productosTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Anchos de columnas
        productosTable.getColumnModel().getColumn(0).setPreferredWidth(60);  // ID
        productosTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Producto
        productosTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Proveedor
        productosTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Costo
        productosTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Precio
        productosTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Stock Bodega
        productosTable.getColumnModel().getColumn(6).setPreferredWidth(80);  // Mínimo
        productosTable.getColumnModel().getColumn(7).setPreferredWidth(100); // Stock Vendedor
        productosTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Unidad
        productosTable.getColumnModel().getColumn(9).setPreferredWidth(100); // Estado

        // Renderers personalizados
        productosTable.getColumnModel().getColumn(5).setCellRenderer((TableCellRenderer) new StockBodegaRenderer());
        productosTable.getColumnModel().getColumn(9).setCellRenderer((TableCellRenderer) new EstadoProductoRenderer());
    }

    private JPanel createInfoPanel() {
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel infoLabel = new JLabel("💡 Use los filtros para encontrar productos específicos. Haga clic en un producto para ver detalles.");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(180, 200, 255));

        JLabel countLabel = new JLabel();
        countLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        countLabel.setForeground(TEXT_WHITE);

        // Actualizar contador
        tableModel.addTableModelListener(e -> {
            countLabel.setText("Productos mostrados: " + tableModel.getRowCount());
        });

        infoPanel.add(infoLabel, BorderLayout.WEST);
        infoPanel.add(countLabel, BorderLayout.EAST);

        return infoPanel;
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
            setFont(new Font("Segoe UI", Font.PLAIN, 14));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(10, 15, 10, 15)
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

            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setBackground(color);
            setForeground(TEXT_WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

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
            label.setFont(new Font("Segoe UI", Font.BOLD, 11));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

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
                    BorderFactory.createEmptyBorder(4, 9, 4, 9)
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
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)
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
                            BorderFactory.createEmptyBorder(19, 19, 19, 19)
                    ));
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(BORDER_COLOR, 1),
                            BorderFactory.createEmptyBorder(20, 20, 20, 20)
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