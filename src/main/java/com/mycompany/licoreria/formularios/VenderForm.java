package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.VentaRapidaController;
import com.mycompany.licoreria.models.VentaDetalle;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.utils.SessionManager;
import com.mycompany.licoreria.utils.StockUtils;
import com.mycompany.licoreria.utils.DateUtils;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class VenderForm extends javax.swing.JInternalFrame {
    private VentaRapidaController ventaController;
    private DefaultTableModel tableModelInventario;
    private DefaultTableModel tableModelProductosConsumidos;
    private DefaultTableModel tableModelVentas;
    private List<VentaDetalle> productosConsumidos;
    private int usuarioId;

    public VenderForm() {
        initComponents();
        ventaController = new VentaRapidaController();
        productosConsumidos = new ArrayList<>();
        usuarioId = SessionManager.getCurrentUser() != null ?
                SessionManager.getCurrentUser().getUsuarioId() : 1;
        initializeTables();
        loadAllData();
        setTitle("Venta Rápida - Punto de Venta");
        setupInitialData();
    }

    private void initializeTables() {
        // Tabla de inventario disponible
        tableModelInventario = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Precio", "Stock", "Unidad"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProductosVendedor.setModel(tableModelInventario);

        // Tabla de productos en el carrito
        tableModelProductosConsumidos = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal", "Quitar"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1 || column == 4; // Cantidad y Quitar son editables
            }
        };
        tblProductosConsumidos.setModel(tableModelProductosConsumidos);

        // Tabla de ventas del día
        tableModelVentas = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Factura", "Hora", "Cliente", "Total", "Items", "Vendedor", "Acciones"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Solo la columna de acciones es editable
            }
        };
        tblVentas.setModel(tableModelVentas);
    }

    private void setupInitialData() {
        // Configurar fecha actual
        String fechaActual = new SimpleDateFormat("dd/MM/yyyy").format(new Date());
        jLabel8.setText(fechaActual);

        // Generar número de factura sugerido
        String numeroFactura = ventaController.generarNumeroFacturaSugerido();
        txtNfactura.setText(numeroFactura);

        // Configurar cliente por defecto
        txtCliente.setText("CLIENTE GENERAL");

        // Actualizar total inicial
        actualizarTotal();
    }

    private void loadAllData() {
        loadInventario();
        loadVentasDelDia();
        actualizarEstadisticas();
    }

    private void loadInventario() {
        try {
            tableModelInventario.setRowCount(0);
            List<Producto> productos = ventaController.getProductosParaVentaRapida();

            for (Producto producto : productos) {
                tableModelInventario.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        "$" + producto.getPrecio(),
                        StockUtils.formatCantidad(producto.getStockVendedor()),
                        producto.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadVentasDelDia() {
        try {
            tableModelVentas.setRowCount(0);
            List<Object[]> ventas = ventaController.getVentasDelDia();

            for (Object[] venta : ventas) {
                int facturaId = (Integer) venta[0];
                Date fecha = (Date) venta[1];
                String cliente = (String) venta[2];
                BigDecimal total = (BigDecimal) venta[3];
                String vendedor = (String) venta[4];
                int items = (Integer) venta[5];

                String hora = new SimpleDateFormat("HH:mm:ss").format(fecha);

                tableModelVentas.addRow(new Object[]{
                        "F-" + facturaId,
                        hora,
                        cliente,
                        "$" + total,
                        items + " items",
                        vendedor,
                        "VER DETALLES"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar ventas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarEstadisticas() {
        try {
            String estadisticas = ventaController.getEstadisticasDelDia();
            // Puedes mostrar esto en una etiqueta si la agregas
            System.out.println("Estadísticas: " + estadisticas);
        } catch (Exception e) {
            // No mostrar error para estadísticas
        }
    }

    private void buscarInventario() {
        try {
            String searchTerm = txtBuscarInventario.getText().trim();
            tableModelInventario.setRowCount(0);

            List<Producto> productos = ventaController.buscarProductosVentaRapida(searchTerm);

            for (Producto producto : productos) {
                tableModelInventario.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        "$" + producto.getPrecio(),
                        StockUtils.formatCantidad(producto.getStockVendedor()),
                        producto.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarVentas() {
        try {
            String searchTerm = txtBuscarVentas.getText().trim().toLowerCase();
            tableModelVentas.setRowCount(0);

            List<Object[]> ventas = ventaController.getVentasDelDia();

            for (Object[] venta : ventas) {
                int facturaId = (Integer) venta[0];
                Date fecha = (Date) venta[1];
                String cliente = (String) venta[2];
                BigDecimal total = (BigDecimal) venta[3];
                String vendedor = (String) venta[4];
                int items = (Integer) venta[5];

                String hora = new SimpleDateFormat("HH:mm:ss").format(fecha);

                // Filtrar por término de búsqueda
                if (cliente.toLowerCase().contains(searchTerm) ||
                        String.valueOf(facturaId).contains(searchTerm) ||
                        vendedor.toLowerCase().contains(searchTerm)) {

                    tableModelVentas.addRow(new Object[]{
                            "F-" + facturaId,
                            hora,
                            cliente,
                            "$" + total,
                            items + " items",
                            vendedor,
                            "VER DETALLES"
                    });
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar ventas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarProductoAlCarrito() {
        int selectedRow = tblProductosVendedor.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto del inventario para agregar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productoId = (int) tableModelInventario.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelInventario.getValueAt(selectedRow, 1);
            BigDecimal precio = new BigDecimal(
                    ((String) tableModelInventario.getValueAt(selectedRow, 2)).replace("$", "")
            );
            double stockDisponible = Double.parseDouble(
                    ((String) tableModelInventario.getValueAt(selectedRow, 3)).replace(",", "")
            );
            String unidadMedida = (String) tableModelInventario.getValueAt(selectedRow, 4);

            // Pedir cantidad
            String cantidadStr = JOptionPane.showInputDialog(this,
                    "Agregar producto:\n\n" +
                            "Producto: " + productoNombre + "\n" +
                            "Precio: $" + precio + "\n" +
                            "Stock disponible: " + stockDisponible + " " + unidadMedida + "\n\n" +
                            "Ingrese la cantidad:",
                    "Agregar al Carrito",
                    JOptionPane.QUESTION_MESSAGE);

            if (cantidadStr != null && !cantidadStr.trim().isEmpty()) {
                double cantidad = Double.parseDouble(cantidadStr);

                // Validaciones
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad debe ser mayor a 0",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cantidad > stockDisponible) {
                    JOptionPane.showMessageDialog(this,
                            "Stock insuficiente. Disponible: " + stockDisponible + " " + unidadMedida,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear y agregar detalle
                VentaDetalle detalle = new VentaDetalle(
                        productoId, productoNombre, cantidad, precio, unidadMedida, stockDisponible
                );
                productosConsumidos.add(detalle);

                // Actualizar tabla del carrito
                actualizarCarrito();

                JOptionPane.showMessageDialog(this,
                        "Producto agregado al carrito",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un número válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCarrito() {
        tableModelProductosConsumidos.setRowCount(0);

        for (int i = 0; i < productosConsumidos.size(); i++) {
            VentaDetalle detalle = productosConsumidos.get(i);
            tableModelProductosConsumidos.addRow(new Object[]{
                    detalle.getProductoNombre(),
                    detalle.getCantidad(),
                    "$" + detalle.getPrecioUnitario(),
                    "$" + detalle.getSubTotal(),
                    "QUITAR"
            });
        }

        actualizarTotal();
    }

    private void actualizarTotal() {
        BigDecimal total = productosConsumidos.stream()
                .map(VentaDetalle::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        jLabel9.setText("$" + total);
    }

    private void eliminarProductoDelCarrito() {
        int selectedRow = tblProductosConsumidos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto del carrito para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String productoNombre = (String) tableModelProductosConsumidos.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar producto del carrito?\n\n" +
                            "Producto: " + productoNombre,
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                productosConsumidos.remove(selectedRow);
                actualizarCarrito();

                JOptionPane.showMessageDialog(this,
                        "Producto eliminado del carrito",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void procesarVenta() {
        if (productosConsumidos.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El carrito está vacío. Agregue productos antes de vender.",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String cliente = txtCliente.getText().trim();
            if (cliente.isEmpty()) {
                cliente = "CLIENTE GENERAL";
            }

            // Mostrar resumen de la venta
            StringBuilder resumen = new StringBuilder();
            resumen.append("<html><div style='text-align: center;'>");
            resumen.append("<h3>Resumen de Venta</h3>");
            resumen.append("<p><b>Cliente:</b> ").append(cliente).append("</p>");
            resumen.append("<p><b>Total:</b> ").append(jLabel9.getText()).append("</p>");
            resumen.append("<p><b>Productos:</b> ").append(productosConsumidos.size()).append("</p>");
            resumen.append("</div></html>");

            int confirm = JOptionPane.showConfirmDialog(this,
                    resumen.toString(),
                    "Confirmar Venta",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = ventaController.procesarVentaRapida(cliente, usuarioId, productosConsumidos);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "<html><div style='text-align: center;'>" +
                                    "<h3>¡Venta Exitosa!</h3>" +
                                    "<p>La venta ha sido procesada correctamente</p>" +
                                    "<p><b>Total: " + jLabel9.getText() + "</b></p>" +
                                    "</div></html>",
                            "Venta Completada",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar y resetear
                    limpiarVenta();
                    loadAllData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al procesar la venta",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al procesar venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarVenta() {
        productosConsumidos.clear();
        actualizarCarrito();
        txtCliente.setText("CLIENTE GENERAL");
        txtNfactura.setText(ventaController.generarNumeroFacturaSugerido());
    }

    private void verDetallesVenta() {
        int selectedRow = tblVentas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una venta para ver detalles",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String facturaStr = (String) tableModelVentas.getValueAt(selectedRow, 0);
            int facturaId = Integer.parseInt(facturaStr.replace("F-", ""));

            List<Object[]> detalles = ventaController.getDetallesVenta(facturaId);

            StringBuilder detallesStr = new StringBuilder();
            detallesStr.append("<html><div style='text-align: center;'>");
            detallesStr.append("<h3>Detalles de Venta ").append(facturaStr).append("</h3>");
            detallesStr.append("<table border='1' style='margin: 0 auto; width: 90%; border-collapse: collapse;'>");
            detallesStr.append("<tr style='background-color: #f0f0f0;'>")
                    .append("<th>Producto</th><th>Cantidad</th><th>Precio</th><th>Subtotal</th>")
                    .append("</tr>");

            BigDecimal total = BigDecimal.ZERO;
            for (Object[] detalle : detalles) {
                String producto = (String) detalle[0];
                double cantidad = (Double) detalle[1];
                BigDecimal precio = (BigDecimal) detalle[2];
                BigDecimal subtotal = (BigDecimal) detalle[3];
                String unidad = (String) detalle[4];

                detallesStr.append("<tr>")
                        .append("<td>").append(producto).append("</td>")
                        .append("<td>").append(cantidad).append(" ").append(unidad).append("</td>")
                        .append("<td>$").append(precio).append("</td>")
                        .append("<td>$").append(subtotal).append("</td>")
                        .append("</tr>");

                total = total.add(subtotal);
            }

            detallesStr.append("<tr style='background-color: #e8f6f3; font-weight: bold;'>")
                    .append("<td colspan='3' style='text-align: right;'>TOTAL:</td>")
                    .append("<td>$").append(total).append("</td>")
                    .append("</tr>");
            detallesStr.append("</table></div></html>");

            JOptionPane.showMessageDialog(this,
                    detallesStr.toString(),
                    "Detalles de Venta",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar detalles: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void anularVenta() {
        int selectedRow = tblVentas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una venta para anular",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String facturaStr = (String) tableModelVentas.getValueAt(selectedRow, 0);
            int facturaId = Integer.parseInt(facturaStr.replace("F-", ""));
            String cliente = (String) tableModelVentas.getValueAt(selectedRow, 2);
            String total = (String) tableModelVentas.getValueAt(selectedRow, 3);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "<html><div style='text-align: center;'>" +
                            "<h3>¿Anular Venta?</h3>" +
                            "<p><b>Factura:</b> " + facturaStr + "</p>" +
                            "<p><b>Cliente:</b> " + cliente + "</p>" +
                            "<p><b>Total:</b> " + total + "</p>" +
                            "<p style='color: red;'><b>¡Esta acción no se puede deshacer!</b></p>" +
                            "</div></html>",
                    "Confirmar Anulación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = ventaController.anularVenta(facturaId);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Venta anulada exitosamente",
                            "Anulación Exitosa",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadVentasDelDia();
                    loadInventario(); // Recargar inventario por si se restauró stock
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al anular la venta",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al anular venta: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnVender = new javax.swing.JButton();
        btnElminarProducto = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductosConsumidos = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblProductosVendedor = new javax.swing.JTable();
        btnEliminarVenta = new javax.swing.JButton();
        btnElminarProductoInventario = new javax.swing.JButton();
        txtNfactura = new javax.swing.JTextField();
        txtCliente = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        txtBuscarInventario = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        txtBuscarVentas = new javax.swing.JTextField();
        btnAgregar = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Venta Rápida");

        btnVender.setBackground(new java.awt.Color(153, 255, 51));
        btnVender.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnVender.setText("PROCESAR VENTA");
        btnVender.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVenderActionPerformed(evt);
            }
        });

        btnElminarProducto.setBackground(new java.awt.Color(255, 51, 102));
        btnElminarProducto.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnElminarProducto.setText("ELIMINAR DEL CARRITO");
        btnElminarProducto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElminarProductoActionPerformed(evt);
            }
        });

        tblVentas.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null},
                        {null, null, null, null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4", "Title 5", "Title 6", "Title 7"
                }
        ));
        tblVentas.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVentasMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVentas);

        tblProductosConsumidos.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
                }
        ));
        jScrollPane2.setViewportView(tblProductosConsumidos);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel1.setText("PRODUCTOS EN CARRITO");

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jLabel2.setText("Cliente:");

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jLabel3.setText("N Factura:");

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel4.setText("VENTAS DE HOY");

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel5.setText("Total:");

        tblProductosVendedor.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null},
                        {null, null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4", "Title 5"
                }
        ));
        jScrollPane3.setViewportView(tblProductosVendedor);

        btnEliminarVenta.setBackground(new java.awt.Color(255, 51, 102));
        btnEliminarVenta.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEliminarVenta.setText("ANULAR VENTA");
        btnEliminarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarVentaActionPerformed(evt);
            }
        });

        btnElminarProductoInventario.setBackground(new java.awt.Color(255, 51, 102));
        btnElminarProductoInventario.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnElminarProductoInventario.setText("ELIMINAR");
        btnElminarProductoInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnElminarProductoInventarioActionPerformed(evt);
            }
        });

        txtNfactura.setEditable(false);
        txtNfactura.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N

        txtCliente.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N

        jLabel6.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel6.setText("INVENTARIO DISPONIBLE");

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jLabel7.setText("Fecha:");

        txtBuscarInventario.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarInventario.setText("Buscar en Inventario...");
        txtBuscarInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarInventarioActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jLabel8.setText("dd/mm/aaaa");

        txtBuscarVentas.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarVentas.setText("Buscar en Ventas...");
        txtBuscarVentas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarVentasActionPerformed(evt);
            }
        });

        btnAgregar.setBackground(new java.awt.Color(153, 255, 51));
        btnAgregar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnAgregar.setText("AGREGAR AL CARRITO");
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        jLabel9.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 102, 0));
        jLabel9.setText("$0.00");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel4)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jLabel7)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jLabel8)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(txtBuscarVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                                .addComponent(btnEliminarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(txtBuscarInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                                .addComponent(btnElminarProductoInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(jLabel6)
                                                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 506, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(jLabel1)
                                                                                .addGap(0, 0, Short.MAX_VALUE))
                                                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 506, Short.MAX_VALUE)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(jLabel3)
                                                                                        .addComponent(jLabel2))
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addComponent(txtNfactura)
                                                                                        .addComponent(txtCliente))
                                                                                .addGap(18, 18, 18)
                                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(jLabel5)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                .addComponent(jLabel9))
                                                                                        .addGroup(layout.createSequentialGroup()
                                                                                                .addComponent(btnElminarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                                .addComponent(btnVender, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                                                .addContainerGap())))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel6)
                                                        .addComponent(jLabel1))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(txtBuscarInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnElminarProductoInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel3)
                                                                        .addComponent(txtNfactura, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel5)
                                                                        .addComponent(jLabel9))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel2)
                                                                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnElminarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnVender, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(350, 350, 350)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel4)
                                                        .addComponent(jLabel7)
                                                        .addComponent(jLabel8)
                                                        .addComponent(txtBuscarVentas, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEliminarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnVenderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVenderActionPerformed
        procesarVenta();
    }//GEN-LAST:event_btnVenderActionPerformed

    private void btnElminarProductoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElminarProductoActionPerformed
        eliminarProductoDelCarrito();
    }//GEN-LAST:event_btnElminarProductoActionPerformed

    private void btnEliminarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarVentaActionPerformed
        anularVenta();
    }//GEN-LAST:event_btnEliminarVentaActionPerformed

    private void btnElminarProductoInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnElminarProductoInventarioActionPerformed
        // Este botón parece redundante, puedes eliminarlo o asignarle otra función
        JOptionPane.showMessageDialog(this,
                "Use el botón 'ELIMINAR DEL CARRITO' para quitar productos",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnElminarProductoInventarioActionPerformed

    private void txtBuscarInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarInventarioActionPerformed
        buscarInventario();
    }//GEN-LAST:event_txtBuscarInventarioActionPerformed

    private void txtBuscarVentasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarVentasActionPerformed
        buscarVentas();
    }//GEN-LAST:event_txtBuscarVentasActionPerformed

    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        agregarProductoAlCarrito();
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void tblVentasMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVentasMouseClicked
        if (evt.getClickCount() == 2) { // Doble click
            verDetallesVenta();
        }
    }//GEN-LAST:event_tblVentasMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregar;
    private javax.swing.JButton btnEliminarVenta;
    private javax.swing.JButton btnElminarProducto;
    private javax.swing.JButton btnElminarProductoInventario;
    private javax.swing.JButton btnVender;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblProductosConsumidos;
    private javax.swing.JTable tblProductosVendedor;
    private javax.swing.JTable tblVentas;
    private javax.swing.JTextField txtBuscarInventario;
    private javax.swing.JTextField txtBuscarVentas;
    private javax.swing.JTextField txtCliente;
    private javax.swing.JTextField txtNfactura;
    // End of variables declaration//GEN-END:variables
}