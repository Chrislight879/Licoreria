package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.SolicitudCompraController;
import com.mycompany.licoreria.models.SolicitudCompra;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
        mainPanel.setBackground(new Color(30, 40, 60));

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
        headerPanel.setBackground(new Color(40, 55, 80));
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
        cmbEstado.addActionListener(e -> filtrarPorEstado());

        JButton btnActualizar = new JButton("🔄 Actualizar");
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
        tablePanel.setBackground(new Color(40, 55, 80));

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
        solicitudesTable.getTableHeader().setBackground(PRIMARY_COLOR);
        solicitudesTable.getTableHeader().setForeground(TEXT_WHITE);

        JScrollPane scrollPane = new JScrollPane(solicitudesTable);
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
        actionPanel.setBackground(new Color(40, 55, 80));

        JButton btnAprobar = new JButton("✅ Aprobar");
        btnAprobar.addActionListener(e -> aprobarSolicitud());

        JButton btnRechazar = new JButton("❌ Rechazar");
        btnRechazar.addActionListener(e -> rechazarSolicitud());

        JButton btnCompletar = new JButton("📦 Despachar");
        btnCompletar.addActionListener(e -> completarSolicitud());

        JButton btnDetalles = new JButton("👁️ Ver Detalles");
        btnDetalles.addActionListener(e -> verDetalles());

        actionPanel.add(btnAprobar);
        actionPanel.add(btnRechazar);
        actionPanel.add(btnCompletar);
        actionPanel.add(btnDetalles);

        return actionPanel;
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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
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
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aprobarSolicitud() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            if (solicitudController.aprobarSolicitud(solicitudId)) {
                JOptionPane.showMessageDialog(this, "Solicitud aprobada exitosamente");
                cargarSolicitudes();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechazarSolicitud() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
        try {
            if (solicitudController.rechazarSolicitud(solicitudId)) {
                JOptionPane.showMessageDialog(this, "Solicitud rechazada");
                cargarSolicitudes();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void completarSolicitud() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int solicitudId = (int) tableModel.getValueAt(selectedRow, 0);
        String producto = (String) tableModel.getValueAt(selectedRow, 1);
        String cantidad = (String) tableModel.getValueAt(selectedRow, 2);

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Está seguro de despachar esta solicitud?\n\n" +
                        "Producto: " + producto + "\n" +
                        "Cantidad: " + cantidad + "\n\n" +
                        "⚠️ Esta acción SUMARÁ el stock a bodega automáticamente.",
                "Confirmar Despacho",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                if (solicitudController.completarSolicitud(solicitudId)) {
                    JOptionPane.showMessageDialog(this,
                            "✅ Solicitud despachada exitosamente\n" +
                                    "Stock actualizado en bodega",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    cargarSolicitudes();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void verDetalles() {
        int selectedRow = solicitudesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione una solicitud", "Advertencia", JOptionPane.WARNING_MESSAGE);
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
                "<html><div style='width: 300px;'>" +
                        "<h3>Detalles de Solicitud #%d</h3>" +
                        "<b>Producto:</b> %s<br>" +
                        "<b>Cantidad:</b> %s<br>" +
                        "<b>Proveedor:</b> %s<br>" +
                        "<b>Solicitante:</b> %s<br>" +
                        "<b>Fecha:</b> %s<br>" +
                        "<b>Estado:</b> %s<br>" +
                        "<b>Observaciones:</b><br>%s" +
                        "</div></html>",
                solicitudId, producto, cantidad, proveedor, solicitante, fecha, estado, observaciones
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
}