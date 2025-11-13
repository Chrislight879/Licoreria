package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.VentaRapidaController;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.VentaDetalle;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class VenderForm extends JInternalFrame {
    private VentaRapidaController ventaController;
    private List<VentaDetalle> carrito;
    private DefaultTableModel modelProductos, modelCarrito;

    // Componentes UI
    private JTextField txtBuscar, txtCliente, txtCantidad;
    private JTable tableProductos, tableCarrito;
    private JLabel lblTotal, lblItems, lblStock;
    private JButton btnAgregar, btnQuitar, btnProcesar, btnLimpiar, btnBuscar;

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
    private final Color INFO_COLOR = new Color(52, 152, 219); // Azul info

    public VenderForm() {
        initComponents();
        ventaController = new VentaRapidaController();
        carrito = new ArrayList<>();
        cargarProductos();
        actualizarResumen();
    }

    private void initComponents() {
        setTitle("🏪 Punto de Venta Rápido");
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

        // Centro - Productos y Carrito
        mainPanel.add(createCenterPanel(), BorderLayout.CENTER);

        // Footer - Resumen y acciones
        mainPanel.add(createFooterPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Título
        JLabel titleLabel = new JLabel("PUNTO DE VENTA RÁPIDO");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_WHITE);

        // Panel de búsqueda
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(0, 0, 0, 0));

        txtBuscar = new ModernTextField("Buscar producto...", 250, 35);
        btnBuscar = new ModernButton("🔍 Buscar", ACCENT_COLOR);
        btnBuscar.addActionListener(e -> buscarProductos());

        // Cliente
        JPanel clientPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        clientPanel.setBackground(new Color(0, 0, 0, 0));

        JLabel lblCliente = new JLabel("Cliente:");
        lblCliente.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCliente.setForeground(TEXT_WHITE);
        txtCliente = new ModernTextField("Cliente General", 150, 30);

        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);
        clientPanel.add(lblCliente);
        clientPanel.add(txtCliente);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.CENTER);
        headerPanel.add(clientPanel, BorderLayout.EAST);

        // Evento Enter en búsqueda
        txtBuscar.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    buscarProductos();
                }
            }
        });

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(new Color(0, 0, 0, 0));

        // Panel de Productos
        centerPanel.add(createProductosPanel());

        // Panel del Carrito
        centerPanel.add(createCarritoPanel());

        return centerPanel;
    }

    private JPanel createProductosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "📦 Productos Disponibles",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Modelo de tabla de productos
        modelProductos = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Precio", "Stock", "Unidad"}, 0
        ) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableProductos = new JTable(modelProductos);
        tableProductos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tableProductos.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableProductos.setRowHeight(30);
        tableProductos.setBackground(new Color(50, 65, 95));
        tableProductos.setForeground(TEXT_WHITE);
        tableProductos.setGridColor(BORDER_COLOR);
        tableProductos.setSelectionBackground(ACCENT_COLOR);
        tableProductos.setSelectionForeground(TEXT_WHITE);

        // Personalizar header de la tabla
        tableProductos.getTableHeader().setBackground(PRIMARY_COLOR);
        tableProductos.getTableHeader().setForeground(TEXT_WHITE);
        tableProductos.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Renderer para stock bajo
        tableProductos.setDefaultRenderer(Object.class, (TableCellRenderer) new StockCellRenderer());

        JScrollPane scrollPane = new JScrollPane(tableProductos);
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        // Panel de controles para agregar
        JPanel controlsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        controlsPanel.setBackground(CARD_BACKGROUND);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        JLabel lblCant = new JLabel("Cantidad:");
        lblCant.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCant.setForeground(TEXT_WHITE);
        txtCantidad = new ModernTextField("1", 80, 35);
        btnAgregar = new ModernButton("➕ Agregar", SUCCESS_COLOR);
        btnAgregar.addActionListener(e -> agregarAlCarrito());

        controlsPanel.add(lblCant);
        controlsPanel.add(txtCantidad);
        controlsPanel.add(btnAgregar);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);

        // Doble click para agregar
        tableProductos.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    agregarAlCarrito();
                }
            }
        });

        return panel;
    }

    private JPanel createCarritoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "🛒 Carrito de Venta",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Modelo de tabla del carrito
        modelCarrito = new DefaultTableModel(
                new Object[]{"Producto", "Cantidad", "P. Unitario", "Subtotal"}, 0
        ) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tableCarrito = new JTable(modelCarrito);
        tableCarrito.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tableCarrito.setRowHeight(30);
        tableCarrito.setBackground(new Color(50, 65, 95));
        tableCarrito.setForeground(TEXT_WHITE);
        tableCarrito.setGridColor(BORDER_COLOR);
        tableCarrito.setSelectionBackground(ACCENT_COLOR);
        tableCarrito.setSelectionForeground(TEXT_WHITE);

        // Personalizar header de la tabla
        tableCarrito.getTableHeader().setBackground(PRIMARY_COLOR);
        tableCarrito.getTableHeader().setForeground(TEXT_WHITE);
        tableCarrito.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        JScrollPane scrollPane = new JScrollPane(tableCarrito);
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        // Panel de controles del carrito
        JPanel controlsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        controlsPanel.setBackground(CARD_BACKGROUND);
        controlsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        btnQuitar = new ModernButton("🗑️ Quitar", DANGER_COLOR);
        btnQuitar.addActionListener(e -> quitarDelCarrito());

        btnLimpiar = new ModernButton("🧹 Limpiar", WARNING_COLOR);
        btnLimpiar.addActionListener(e -> limpiarCarrito());

        controlsPanel.add(btnQuitar);
        controlsPanel.add(btnLimpiar);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(controlsPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBackground(CARD_BACKGROUND);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Panel de resumen
        JPanel summaryPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        summaryPanel.setBackground(CARD_BACKGROUND);

        lblItems = createSummaryLabel("Items: 0", "📦", TEXT_WHITE);
        lblTotal = createSummaryLabel("Total: $0.00", "💰", SUCCESS_COLOR);
        lblStock = createSummaryLabel("Stock: OK", "✅", TEXT_WHITE);

        summaryPanel.add(lblItems);
        summaryPanel.add(lblTotal);
        summaryPanel.add(lblStock);

        // Panel de acciones
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setBackground(CARD_BACKGROUND);

        btnProcesar = new ModernButton("💳 Procesar Venta", SUCCESS_COLOR);
        btnProcesar.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btnProcesar.setPreferredSize(new Dimension(180, 45));
        btnProcesar.addActionListener(e -> procesarVenta());
        btnProcesar.setEnabled(false);

        actionPanel.add(btnProcesar);

        footerPanel.add(summaryPanel, BorderLayout.WEST);
        footerPanel.add(actionPanel, BorderLayout.EAST);

        return footerPanel;
    }

    private JLabel createSummaryLabel(String text, String icon, Color textColor) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" +
                "<div style='font-size: 20px; margin-bottom: 5px;'>" + icon + "</div>" +
                "<div style='font-size: 14px; font-weight: bold; color: " +
                String.format("#%02x%02x%02x", textColor.getRed(), textColor.getGreen(), textColor.getBlue()) +
                ";'>" + text + "</div>" +
                "</div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        return label;
    }

    private void cargarProductos() {
        modelProductos.setRowCount(0);
        try {
            List<Producto> productos = ventaController.getProductosParaVenta();
            for (Producto p : productos) {
                modelProductos.addRow(new Object[]{
                        p.getProductoId(),
                        p.getNombre(),
                        p.getPrecio(),
                        p.getStockVendedor(),
                        p.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            showError("Error al cargar productos: " + e.getMessage());
        }
    }

    private void buscarProductos() {
        String searchTerm = txtBuscar.getText().trim();
        if (searchTerm.isEmpty()) {
            cargarProductos();
            return;
        }

        modelProductos.setRowCount(0);
        try {
            List<Producto> productos = ventaController.buscarProductos(searchTerm);
            for (Producto p : productos) {
                modelProductos.addRow(new Object[]{
                        p.getProductoId(),
                        p.getNombre(),
                        p.getPrecio(),
                        p.getStockVendedor(),
                        p.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            showError("Error al buscar productos: " + e.getMessage());
        }
    }

    private void agregarAlCarrito() {
        int selectedRow = tableProductos.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Seleccione un producto primero");
            return;
        }

        try {
            int productoId = (int) modelProductos.getValueAt(selectedRow, 0);
            String productoNombre = (String) modelProductos.getValueAt(selectedRow, 1);
            BigDecimal precio = (BigDecimal) modelProductos.getValueAt(selectedRow, 2);
            double stock = (double) modelProductos.getValueAt(selectedRow, 3);
            String unidad = (String) modelProductos.getValueAt(selectedRow, 4);

            double cantidad;
            try {
                cantidad = Double.parseDouble(txtCantidad.getText().trim());
            } catch (NumberFormatException e) {
                showError("Cantidad inválida");
                return;
            }

            if (cantidad <= 0) {
                showError("La cantidad debe ser mayor a 0");
                return;
            }

            if (cantidad > stock) {
                showError("Stock insuficiente. Disponible: " + stock);
                return;
            }

            // Crear detalle de venta
            VentaDetalle detalle = new VentaDetalle();
            detalle.setProductoId(productoId);
            detalle.setProductoNombre(productoNombre);
            detalle.setCantidad(cantidad);
            detalle.setPrecioUnitario(precio);
            detalle.setSubTotal(precio.multiply(BigDecimal.valueOf(cantidad)));
            detalle.setUnidadMedida(unidad);
            detalle.setStockDisponible(stock);

            // Verificar si ya existe en el carrito
            boolean existe = false;
            for (VentaDetalle item : carrito) {
                if (item.getProductoId() == productoId) {
                    item.setCantidad(item.getCantidad() + cantidad);
                    item.setSubTotal(item.getPrecioUnitario().multiply(BigDecimal.valueOf(item.getCantidad())));
                    existe = true;
                    break;
                }
            }

            if (!existe) {
                carrito.add(detalle);
            }

            actualizarCarrito();
            actualizarResumen();
            txtCantidad.setText("1");

        } catch (Exception e) {
            showError("Error al agregar producto: " + e.getMessage());
        }
    }

    private void quitarDelCarrito() {
        int selectedRow = tableCarrito.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Seleccione un item del carrito para quitar");
            return;
        }

        carrito.remove(selectedRow);
        actualizarCarrito();
        actualizarResumen();
    }

    private void limpiarCarrito() {
        if (carrito.isEmpty()) {
            showInfo("El carrito ya está vacío");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #F39C12;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>🧹 Confirmar Limpieza</div>" +
                        "<div style='color: #ECF0F1;'>¿Está seguro de limpiar todo el carrito?</div>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar limpieza",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            carrito.clear();
            actualizarCarrito();
            actualizarResumen();
        }
    }

    private void actualizarCarrito() {
        modelCarrito.setRowCount(0);
        for (VentaDetalle item : carrito) {
            modelCarrito.addRow(new Object[]{
                    item.getProductoNombre(),
                    String.format("%.2f %s", item.getCantidad(), item.getUnidadMedida()),
                    "$" + item.getPrecioUnitario(),
                    "$" + item.getSubTotal()
            });
        }
    }

    private void actualizarResumen() {
        int items = carrito.size();
        BigDecimal total = carrito.stream()
                .map(VentaDetalle::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblItems.setText(createSummaryLabel("Items: " + items, "📦", TEXT_WHITE).getText());
        lblTotal.setText(createSummaryLabel("Total: $" + total, "💰", SUCCESS_COLOR).getText());

        // Actualizar estado del botón de procesar
        btnProcesar.setEnabled(!carrito.isEmpty());

        // Actualizar estado de stock
        boolean stockBajo = carrito.stream()
                .anyMatch(item -> item.getCantidad() > item.getStockDisponible());

        if (stockBajo) {
            lblStock.setText(createSummaryLabel("Stock: BAJO", "⚠️", WARNING_COLOR).getText());
        } else {
            lblStock.setText(createSummaryLabel("Stock: OK", "✅", TEXT_WHITE).getText());
        }
    }

    private void procesarVenta() {
        if (carrito.isEmpty()) {
            showWarning("El carrito está vacío");
            return;
        }

        String cliente = txtCliente.getText().trim();
        if (cliente.isEmpty()) {
            cliente = "Cliente General";
        }

        try {
            // Mostrar confirmación
            BigDecimal total = carrito.stream()
                    .map(VentaDetalle::getSubTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                            "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                            "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>💳 Confirmar Venta</div>" +
                            "<div style='color: #ECF0F1; text-align: left;'>" +
                            "<p><b>Cliente:</b> " + cliente + "</p>" +
                            "<p><b>Total:</b> $" + total + "</p>" +
                            "<p><b>Items:</b> " + carrito.size() + "</p>" +
                            "</div>" +
                            "</div>" +
                            "</div></html>",
                    "Confirmar Venta",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Procesar venta
            boolean success = ventaController.procesarVentaRapida(
                    cliente,
                    SessionManager.getCurrentUser().getUsuarioId(),
                    carrito
            );

            if (success) {
                showSuccess("¡Venta procesada exitosamente!");
                limpiarCarrito();
                cargarProductos(); // Actualizar stocks
                txtCliente.setText("Cliente General");
            } else {
                showError("Error al procesar la venta");
            }

        } catch (Exception e) {
            showError("Error al procesar venta: " + e.getMessage());
        }
    }

    // Clases auxiliares para componentes modernos con tema azul
    class ModernTextField extends JTextField {
        public ModernTextField(String placeholder, int width, int height) {
            super(placeholder);
            setFont(new Font("Segoe UI", Font.PLAIN, 12));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            setPreferredSize(new Dimension(width, height));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setCaretColor(TEXT_WHITE);
            setOpaque(true);

            // Placeholder mejorado
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

            if (getText().equals(placeholder)) {
                setForeground(new Color(200, 220, 255));
            }
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
            setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

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

    class StockCellRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(TEXT_WHITE);

            if (column == 3) { // Columna de stock
                try {
                    double stock = (double) table.getModel().getValueAt(row, 3);
                    if (stock < 5) {
                        c.setBackground(new Color(120, 60, 60)); // Rojo oscuro
                    } else if (stock < 10) {
                        c.setBackground(new Color(120, 100, 60)); // Amarillo oscuro
                    } else {
                        c.setBackground(new Color(50, 65, 95)); // Azul oscuro normal
                    }
                } catch (Exception e) {
                    c.setBackground(new Color(50, 65, 95));
                }
            } else {
                c.setBackground(new Color(50, 65, 95));
            }

            return c;
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

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #F39C12;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>⚠️ Advertencia</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
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
}