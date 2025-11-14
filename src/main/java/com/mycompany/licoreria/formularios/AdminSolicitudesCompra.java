package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.SolicitudCompraController;
import com.mycompany.licoreria.models.SolicitudCompra;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AdminSolicitudesCompra extends JInternalFrame {
    private SolicitudCompraController solicitudController;
    private DefaultTableModel tableModel;
    private JTable solicitudesTable;
    private JComboBox<String> cmbEstado;
    private JLabel lblEstadisticas;

    // Colores
    private final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private final Color SUCCESS_COLOR = new Color(86, 202, 133);
    private final Color WARNING_COLOR = new Color(255, 193, 87);
    private final Color DANGER_COLOR = new Color(255, 118, 117);
    private final Color TEXT_WHITE = Color.WHITE;
    private final Color BACKGROUND_COLOR = new Color(30, 40, 60);
    private final Color HEADER_COLOR = new Color(40, 55, 80);

    public AdminSolicitudesCompra() {
        initComponents();
        solicitudController = new SolicitudCompraController();
        cargarSolicitudes();
        actualizarEstadisticas();
    }

    private void initComponents() {
        setTitle("📦 Solicitudes de Compra a Proveedores");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setSize(1200, 700);

        // Panel principal
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mainPanel.setBackground(BACKGROUND_COLOR);

        // Header
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Tabla
        mainPanel.add(createTablePanel(), BorderLayout.CENTER);

        // Botones de acción
        mainPanel.add(createActionPanel(), BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(HEADER_COLOR);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        // Título
        JLabel titleLabel = new JLabel("Solicitudes de Compra a Proveedores");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);

        // Filtros
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.setBackground(new Color(0, 0, 0, 0));

        JLabel lblFiltro = new JLabel("Filtrar por estado:");
        lblFiltro.setForeground(TEXT_WHITE);

        cmbEstado = new JComboBox<>(new String[]{"Todas", "pendiente", "aprobada", "rechazada", "completada"});
        cmbEstado.setBackground(Color.WHITE);
        cmbEstado.setForeground(Color.BLACK);
        cmbEstado.addActionListener(e -> filtrarPorEstado());

        JButton btnActualizar = new JButton("🔄 Actualizar");
        btnActualizar.setForeground(TEXT_WHITE);
        btnActualizar.setBackground(PRIMARY_COLOR);
        btnActualizar.addActionListener(e -> cargarSolicitudes());

        filterPanel.add(lblFiltro);
        filterPanel.add(cmbEstado);
        filterPanel.add(btnActualizar);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(filterPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(HEADER_COLOR);

        // Modelo de tabla
        String[] columnNames = {
                "ID", "Producto", "Cantidad", "Proveedor", "Solicitante",
                "Fecha", "Estado", "Observaciones"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        solicitudesTable = new JTable(tableModel);
        solicitudesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        solicitudesTable.setRowHeight(30);
        solicitudesTable.setBackground(BACKGROUND_COLOR);
        solicitudesTable.setForeground(TEXT_WHITE);
        solicitudesTable.setGridColor(Color.GRAY);
        solicitudesTable.getTableHeader().setBackground(PRIMARY_COLOR);
        solicitudesTable.getTableHeader().setForeground(TEXT_WHITE);
        solicitudesTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(solicitudesTable);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Estadísticas
        lblEstadisticas = new JLabel();
        lblEstadisticas.setForeground(TEXT_WHITE);
        lblEstadisticas.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        tablePanel.add(lblEstadisticas, BorderLayout.SOUTH);

        return tablePanel;
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        actionPanel.setBackground(HEADER_COLOR);

        JButton btnAprobar = createStyledButton("✅ Aprobar", SUCCESS_COLOR);
        btnAprobar.addActionListener(e -> aprobarSolicitud());

        JButton btnRechazar = createStyledButton("❌ Rechazar", DANGER_COLOR);
        btnRechazar.addActionListener(e -> rechazarSolicitud());

        JButton btnCompletar = createStyledButton("📦 Despachar", WARNING_COLOR);
        btnCompletar.addActionListener(e -> completarSolicitud());

        JButton btnDetalles = createStyledButton("👁️ Ver Detalles", SECONDARY_COLOR);
        btnDetalles.addActionListener(e -> verDetalles());

        actionPanel.add(btnAprobar);
        actionPanel.add(btnRechazar);
        actionPanel.add(btnCompletar);
        actionPanel.add(btnDetalles);

        return actionPanel;
    }

    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(TEXT_WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 12));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void cargarSolicitudes() {
        tableModel.setRowCount(0);
        try {
            List<SolicitudCompra> solicitudes = solicitudController.getAllSolicitudes();
            for (SolicitudCompra s : solicitudes) {
                tableModel.addRow(new Object[]{
                        s.getSolicitudId(),
                        s.getProductoNombre(),
                        s.getCantidadSolicitada() + " " + s.getUnidadMedida(),
                        s.getProveedorNombre(),
                        s.getUsuarioSolicitanteNombre(),
                        s.getFechaSolicitud(),
                        s.getEstado(),
                        s.getObservaciones()
                });
            }
            actualizarEstadisticas();
        } catch (Exception e) {
            showErrorMessage("Error: " + e.getMessage());
        }
    }

    private void filtrarPorEstado() {
        String estado = (String) cmbEstado.getSelectedItem();
        if ("Todas".equals(estado)) {
            cargarSolicitudes();
            return;
        }

        tableModel.setRowCount(0);
        try {
            List<SolicitudCompra> solicitudes = solicitudController.getSolicitudesByEstado(estado);
            for (SolicitudCompra s : solicitudes) {
                tableModel.addRow(new Object[]{
                        s.getSolicitudId(),
                        s.getProductoNombre(),
                        s.getCantidadSolicitada() + " " + s.getUnidadMedida(),
                        s.getProveedorNombre(),
                        s.getUsuarioSolicitanteNombre(),
                        s.getFechaSolicitud(),
                        s.getEstado(),
                        s.getObservaciones()
                });
            }
        } catch (Exception e) {
            showErrorMessage("Error: " + e.getMessage());
        }
    }

    private void aprobarSolicitud() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Seleccione una solicitud");
            return;
        }

        int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            if (solicitudController.aprobarSolicitud(solicitudId)) {
                showSuccessMessage("Solicitud aprobada exitosamente");
                cargarSolicitudes();
            }
        } catch (Exception e) {
            showErrorMessage("Error: " + e.getMessage());
        }
    }

    private void rechazarSolicitud() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Seleccione una solicitud");
            return;
        }

        int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            if (solicitudController.rechazarSolicitud(solicitudId)) {
                showSuccessMessage("Solicitud rechazada");
                cargarSolicitudes();
            }
        } catch (Exception e) {
            showErrorMessage("Error: " + e.getMessage());
        }
    }

    private void completarSolicitud() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Seleccione una solicitud");
            return;
        }

        int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
        String producto = (String) tableModel.getValueAt(selectedRow, 1);
        String cantidad = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = showConfirmDialog(
                "¿Está seguro de despachar esta solicitud?\n\n" +
                        "Producto: " + producto + "\n" +
                        "Cantidad: " + cantidad + "\n\n" +
                        "⚠️ Esta acción SUMARÁ el stock a bodega automáticamente.",
                "Confirmar Despacho"
        );

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (solicitudController.completarSolicitud(solicitudId)) {
                    showSuccessMessage(
                            "✅ Solicitud despachada exitosamente\n" +
                                    "Stock actualizado en bodega"
                    );
                    cargarSolicitudes();
                }
            } catch (Exception e) {
                showErrorMessage("Error: " + e.getMessage());
            }
        }
    }

    private void verDetalles() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            showWarningMessage("Seleccione una solicitud");
            return;
        }

        int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
        String producto = (String) tableModel.getValueAt(selectedRow, 1);
        String cantidad = (String) tableModel.getValueAt(selectedRow, 2);
        String proveedor = (String) tableModel.getValueAt(selectedRow, 3);
        String solicitante = (String) tableModel.getValueAt(selectedRow, 4);
        String fecha = tableModel.getValueAt(selectedRow, 5).toString();
        String estado = (String) tableModel.getValueAt(selectedRow, 6);
        String observaciones = (String) tableModel.getValueAt(selectedRow, 7);

        String mensaje = String.format(
                "<html><div style='width: 300px; color: white; background-color: #283750; padding: 15px; border-radius: 5px;'>" +
                        "<h3 style='color: white; margin-top: 0;'>Detalles de Solicitud #%d</h3>" +
                        "<b>Producto:</b> %s<br>" +
                        "<b>Cantidad:</b> %s<br>" +
                        "<b>Proveedor:</b> %s<br>" +
                        "<b>Solicitante:</b> %s<br>" +
                        "<b>Fecha:</b> %s<br>" +
                        "<b>Estado:</b> %s<br>" +
                        "<b>Observaciones:</b><br>%s" +
                        "</div></html>",
                solicitudId, producto, cantidad, proveedor, solicitante, fecha, estado,
                observaciones != null ? observaciones : "Ninguna"
        );

        JOptionPane.showMessageDialog(this, mensaje, "Detalles de Solicitud", JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarEstadisticas() {
        try {
            String stats = solicitudController.getEstadisticasSolicitudes();
            lblEstadisticas.setText("📊 " + stats);
        } catch (Exception e) {
            lblEstadisticas.setText("Error al cargar estadísticas");
        }
    }

    // Métodos personalizados para mostrar mensajes con colores
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='color: white;'>" + message + "</div></html>",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='color: white;'>" + message + "</div></html>",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarningMessage(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='color: white;'>" + message + "</div></html>",
                "Advertencia",
                JOptionPane.WARNING_MESSAGE);
    }

    private int showConfirmDialog(String message, String title) {
        Object[] options = {"Sí", "No"};
        return JOptionPane.showOptionDialog(this,
                "<html><div style='color: white; width: 300px;'>" + message + "</div></html>",
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);
    }
}