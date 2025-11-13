package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.PeticionVendedorController;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.PeticionVendedor;
import com.mycompany.licoreria.utils.SessionManager;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class VendedorPedirForm extends JInternalFrame {
    private PeticionVendedorController peticionController;
    private DefaultTableModel modelProductos, modelPeticiones;

    // Componentes UI
    private JTextField txtBuscar, txtCantidad, txtObservaciones;
    private JTable tableProductos, tablePeticiones;
    private JLabel lblStockBodega, lblStockVendedor, lblEstado;
    private JButton btnSolicitar, btnCancelar, btnBuscar, btnRefrescar;
    private JComboBox<String> cmbFiltroEstado;

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

    public VendedorPedirForm() {
        initComponents();
        peticionController = new PeticionVendedorController();
        cargarProductosBodega();
        cargarMisPeticiones();
    }

    private void initComponents() {
        setTitle("📦 Solicitar Stock a Bodega");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1000, 700);
        setLayout(new BorderLayout(10, 10));

        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

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
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("SOLICITUD DE STOCK A BODEGA");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_WHITE);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        searchPanel.setBackground(new Color(0, 0, 0, 0));

        txtBuscar = new ModernTextField("Buscar producto en bodega...", 200, 35);
        btnBuscar = new ModernButton("🔍 Buscar", ACCENT_COLOR);
        btnBuscar.addActionListener(e -> buscarProductos());

        btnRefrescar = new ModernButton("🔄 Actualizar", INFO_COLOR);
        btnRefrescar.addActionListener(e -> refrescarDatos());

        searchPanel.add(txtBuscar);
        searchPanel.add(btnBuscar);
        searchPanel.add(btnRefrescar);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        centerPanel.setBackground(new Color(0, 0, 0, 0));

        centerPanel.add(createProductosPanel());
        centerPanel.add(createPeticionesPanel());

        return centerPanel;
    }

    private JPanel createProductosPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "🏪 Productos en Bodega",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        modelProductos = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Stock Bodega", "Unidad"}, 0
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

        // Renderer para stock
        tableProductos.setDefaultRenderer(Object.class, new StockBodegaRenderer());

        JScrollPane scrollPane = new JScrollPane(tableProductos);
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        // Panel de información y controles
        JPanel infoPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        JLabel lblCantidad = new JLabel("Cantidad a solicitar:");
        lblCantidad.setForeground(TEXT_WHITE);
        lblCantidad.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtCantidad = new ModernTextField("1", 100, 35);

        JLabel lblObservaciones = new JLabel("Observaciones:");
        lblObservaciones.setForeground(TEXT_WHITE);
        lblObservaciones.setFont(new Font("Segoe UI", Font.BOLD, 12));

        txtObservaciones = new ModernTextField("Solicitud de stock...", 100, 35);

        infoPanel.add(lblCantidad);
        infoPanel.add(txtCantidad);
        infoPanel.add(lblObservaciones);
        infoPanel.add(txtObservaciones);

        btnSolicitar = new ModernButton("📨 Enviar Solicitud", SUCCESS_COLOR);
        btnSolicitar.addActionListener(e -> enviarSolicitud());

        panel.add(infoPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(btnSolicitar, BorderLayout.SOUTH);

        // Listener para selección de producto
        tableProductos.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarInfoProducto();
            }
        });

        return panel;
    }

    private JPanel createPeticionesPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(CARD_BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "📋 Mis Solicitudes",
                        javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Filtro de estado
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        filterPanel.setBackground(CARD_BACKGROUND);

        JLabel lblFiltro = new JLabel("Filtrar por estado:");
        lblFiltro.setForeground(TEXT_WHITE);
        lblFiltro.setFont(new Font("Segoe UI", Font.BOLD, 12));

        cmbFiltroEstado = new JComboBox<>(new String[]{"Todos", "Pendiente", "Aprobada", "Rechazada", "Despachada"});
        cmbFiltroEstado.setBackground(new Color(50, 65, 95));
        cmbFiltroEstado.setForeground(TEXT_WHITE);
        cmbFiltroEstado.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        cmbFiltroEstado.addActionListener(e -> filtrarPeticiones());

        btnCancelar = new ModernButton("❌ Cancelar", DANGER_COLOR);
        btnCancelar.addActionListener(e -> cancelarPeticion());

        filterPanel.add(lblFiltro);
        filterPanel.add(cmbFiltroEstado);
        filterPanel.add(btnCancelar);

        modelPeticiones = new DefaultTableModel(
                new Object[]{"ID", "Producto", "Cantidad", "Fecha", "Estado"}, 0
        ) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        tablePeticiones = new JTable(modelPeticiones);
        tablePeticiones.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        tablePeticiones.setRowHeight(30);
        tablePeticiones.setBackground(new Color(50, 65, 95));
        tablePeticiones.setForeground(TEXT_WHITE);
        tablePeticiones.setGridColor(BORDER_COLOR);
        tablePeticiones.setSelectionBackground(ACCENT_COLOR);
        tablePeticiones.setSelectionForeground(TEXT_WHITE);

        // Personalizar header de la tabla
        tablePeticiones.getTableHeader().setBackground(PRIMARY_COLOR);
        tablePeticiones.getTableHeader().setForeground(TEXT_WHITE);
        tablePeticiones.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Renderer para estados
        tablePeticiones.setDefaultRenderer(Object.class, new EstadoPeticionRenderer());

        JScrollPane scrollPane = new JScrollPane(tablePeticiones);
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        footerPanel.setBackground(CARD_BACKGROUND);
        footerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        lblStockBodega = createInfoLabel("Stock Bodega: --", "🏪", TEXT_WHITE);
        lblStockVendedor = createInfoLabel("Stock Vendedor: --", "🛒", TEXT_WHITE);
        lblEstado = createInfoLabel("Seleccione producto", "ℹ️", TEXT_WHITE);

        footerPanel.add(lblStockBodega);
        footerPanel.add(lblStockVendedor);
        footerPanel.add(lblEstado);

        return footerPanel;
    }

    private JLabel createInfoLabel(String text, String icon, Color textColor) {
        JLabel label = new JLabel("<html><div style='text-align: center;'>" +
                "<div style='font-size: 18px; margin-bottom: 5px;'>" + icon + "</div>" +
                "<div style='font-size: 12px; color: " +
                String.format("#%02x%02x%02x", textColor.getRed(), textColor.getGreen(), textColor.getBlue()) +
                ";'>" + text + "</div>" +
                "</div></html>");
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    private void cargarProductosBodega() {
        modelProductos.setRowCount(0);
        try {
            List<Producto> productos = peticionController.getInventarioBodega();
            for (Producto p : productos) {
                modelProductos.addRow(new Object[]{
                        p.getProductoId(),
                        p.getNombre(),
                        p.getStockBodega(),
                        p.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            showError("Error al cargar productos de bodega: " + e.getMessage());
        }
    }

    private void buscarProductos() {
        String searchTerm = txtBuscar.getText().trim();
        if (searchTerm.isEmpty()) {
            cargarProductosBodega();
            return;
        }

        modelProductos.setRowCount(0);
        try {
            List<Producto> productos = peticionController.buscarProductosBodega(searchTerm);
            for (Producto p : productos) {
                modelProductos.addRow(new Object[]{
                        p.getProductoId(),
                        p.getNombre(),
                        p.getStockBodega(),
                        p.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            showError("Error al buscar productos: " + e.getMessage());
        }
    }

    private void cargarMisPeticiones() {
        modelPeticiones.setRowCount(0);
        try {
            List<PeticionVendedor> peticiones = peticionController.getPeticionesPorVendedor(
                    SessionManager.getCurrentUser().getUsuarioId()
            );

            for (PeticionVendedor p : peticiones) {
                modelPeticiones.addRow(new Object[]{
                        p.getPeticionId(),
                        p.getProductoNombre(),
                        String.format("%.2f %s", p.getCantidadSolicitada(), p.getUnidadMedida()),
                        p.getFechaSolicitud(),
                        p.getEstado()
                });
            }
        } catch (Exception e) {
            showError("Error al cargar peticiones: " + e.getMessage());
        }
    }

    private void filtrarPeticiones() {
        String filtro = (String) cmbFiltroEstado.getSelectedItem();
        if ("Todos".equals(filtro)) {
            cargarMisPeticiones();
            return;
        }

        modelPeticiones.setRowCount(0);
        try {
            List<PeticionVendedor> peticiones = peticionController.getPeticionesPorVendedor(
                    SessionManager.getCurrentUser().getUsuarioId()
            );

            for (PeticionVendedor p : peticiones) {
                if (filtro.equalsIgnoreCase(p.getEstado())) {
                    modelPeticiones.addRow(new Object[]{
                            p.getPeticionId(),
                            p.getProductoNombre(),
                            String.format("%.2f %s", p.getCantidadSolicitada(), p.getUnidadMedida()),
                            p.getFechaSolicitud(),
                            p.getEstado()
                    });
                }
            }
        } catch (Exception e) {
            showError("Error al filtrar peticiones: " + e.getMessage());
        }
    }

    private void actualizarInfoProducto() {
        int selectedRow = tableProductos.getSelectedRow();
        if (selectedRow == -1) {
            lblStockBodega.setText(createInfoLabel("Stock Bodega: --", "🏪", TEXT_WHITE).getText());
            lblStockVendedor.setText(createInfoLabel("Stock Vendedor: --", "🛒", TEXT_WHITE).getText());
            lblEstado.setText(createInfoLabel("Seleccione producto", "ℹ️", TEXT_WHITE).getText());
            return;
        }

        try {
            int productoId = (int) modelProductos.getValueAt(selectedRow, 0);
            double stockBodega = (double) modelProductos.getValueAt(selectedRow, 2);

            // Obtener stock del vendedor
            Producto producto = peticionController.getProductoPorId(productoId);
            double stockVendedor = producto != null ? producto.getStockVendedor() : 0;

            lblStockBodega.setText(createInfoLabel("Stock Bodega: " + stockBodega, "🏪", TEXT_WHITE).getText());
            lblStockVendedor.setText(createInfoLabel("Stock Vendedor: " + stockVendedor, "🛒", TEXT_WHITE).getText());

            // Validar si se puede solicitar
            boolean puedeSolicitar = stockBodega > 0;
            if (puedeSolicitar) {
                lblEstado.setText(createInfoLabel("Puede solicitar", "✅", SUCCESS_COLOR).getText());
            } else {
                lblEstado.setText(createInfoLabel("Sin stock en bodega", "❌", DANGER_COLOR).getText());
            }

        } catch (Exception e) {
            showError("Error al obtener información del producto: " + e.getMessage());
        }
    }

    private void enviarSolicitud() {
        int selectedRow = tableProductos.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Seleccione un producto de la bodega");
            return;
        }

        try {
            int productoId = (int) modelProductos.getValueAt(selectedRow, 0);
            String productoNombre = (String) modelProductos.getValueAt(selectedRow, 1);
            double stockBodega = (double) modelProductos.getValueAt(selectedRow, 2);

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

            if (cantidad > stockBodega) {
                showError("Stock insuficiente en bodega. Disponible: " + stockBodega);
                return;
            }

            String observaciones = txtObservaciones.getText().trim();
            if (observaciones.isEmpty()) {
                observaciones = "Solicitud de stock regular";
            }

            // Confirmar solicitud
            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                            "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                            "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>📨 Confirmar Solicitud</div>" +
                            "<div style='color: #ECF0F1; text-align: left;'>" +
                            "<p><b>Producto:</b> " + productoNombre + "</p>" +
                            "<p><b>Cantidad:</b> " + cantidad + "</p>" +
                            "<p><b>Observaciones:</b> " + observaciones + "</p>" +
                            "</div>" +
                            "</div>" +
                            "</div></html>",
                    "Confirmar Solicitud",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm != JOptionPane.YES_OPTION) {
                return;
            }

            // Enviar solicitud
            boolean success = peticionController.crearPeticion(
                    productoId,
                    SessionManager.getCurrentUser().getUsuarioId(),
                    cantidad,
                    observaciones
            );

            if (success) {
                showSuccess("¡Solicitud enviada exitosamente!");
                limpiarFormulario();
                cargarMisPeticiones();
            } else {
                showError("Error al enviar la solicitud");
            }

        } catch (Exception e) {
            showError("Error al enviar solicitud: " + e.getMessage());
        }
    }

    private void cancelarPeticion() {
        int selectedRow = tablePeticiones.getSelectedRow();
        if (selectedRow == -1) {
            showWarning("Seleccione una solicitud para cancelar");
            return;
        }

        try {
            int peticionId = (int) modelPeticiones.getValueAt(selectedRow, 0);
            String estado = (String) modelPeticiones.getValueAt(selectedRow, 4);

            if (!"pendiente".equals(estado)) {
                showWarning("Solo se pueden cancelar solicitudes pendientes");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><div style='text-align: center; padding: 10px;'>" +
                            "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                            "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>❌ Confirmar Cancelación</div>" +
                            "<div style='color: #ECF0F1;'>¿Está seguro de cancelar esta solicitud?</div>" +
                            "</div>" +
                            "</div></html>",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = peticionController.eliminarPeticion(peticionId);
                if (success) {
                    showSuccess("Solicitud cancelada exitosamente");
                    cargarMisPeticiones();
                } else {
                    showError("Error al cancelar la solicitud");
                }
            }

        } catch (Exception e) {
            showError("Error al cancelar solicitud: " + e.getMessage());
        }
    }

    private void refrescarDatos() {
        cargarProductosBodega();
        cargarMisPeticiones();
        showInfo("Datos actualizados correctamente");
    }

    private void limpiarFormulario() {
        txtCantidad.setText("1");
        txtObservaciones.setText("Solicitud de stock...");
        tableProductos.clearSelection();
        actualizarInfoProducto();
    }

    // Clases de renderizado actualizadas para tema oscuro
    class StockBodegaRenderer extends DefaultTableCellRenderer implements TableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(TEXT_WHITE);

            if (column == 2) { // Columna de stock bodega
                try {
                    double stock = (double) value;
                    if (stock == 0) {
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

    class EstadoPeticionRenderer extends DefaultTableCellRenderer {
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            c.setForeground(TEXT_WHITE);

            if (column == 4) { // Columna de estado
                String estado = (String) value;
                switch (estado.toLowerCase()) {
                    case "pendiente":
                        c.setBackground(new Color(120, 100, 60)); // Amarillo oscuro
                        break;
                    case "aprobada":
                        c.setBackground(new Color(60, 120, 80)); // Verde oscuro
                        break;
                    case "rechazada":
                        c.setBackground(new Color(120, 60, 60)); // Rojo oscuro
                        break;
                    case "despachada":
                        c.setBackground(new Color(60, 90, 120)); // Azul oscuro
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

    // Clases para componentes modernos con tema azul
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