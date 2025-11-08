package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.VentaController;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.DetalleVenta;
import com.mycompany.licoreria.models.Venta;
import com.mycompany.licoreria.utils.SessionManager;
import com.mycompany.licoreria.utils.StockUtils;
import com.mycompany.licoreria.utils.DateUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class VendedorMainForm extends javax.swing.JInternalFrame {
    private VentaController ventaController;
    private DefaultTableModel tableModelProductos;
    private DefaultTableModel tableModelCarrito;
    private DefaultTableModel tableModelVentas;
    private DefaultTableModel tableModelStockBajo;
    private List<DetalleVenta> carrito;
    private int usuarioId;

    public VendedorMainForm() {
        initComponents();
        ventaController = new VentaController();
        carrito = new ArrayList<>();
        usuarioId = SessionManager.getCurrentUser() != null ?
                SessionManager.getCurrentUser().getUsuarioId() : 1; // Default si no hay sesión
        initializeTables();
        loadAllData();
        setTitle("Módulo de Vendedor - Punto de Venta");
    }

    private void initializeTables() {
        // Tabla de productos disponibles
        tableModelProductos = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Precio", "Stock", "Unidad"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProductos.setModel(tableModelProductos);

        // Tabla del carrito de compras
        tableModelCarrito = new DefaultTableModel(
                new Object[][]{},
                new String[]{"Producto", "Cantidad", "Precio Unit.", "Subtotal", "Quitar"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 1; // Solo la cantidad es editable
            }
        };
        tblCarrito.setModel(tableModelCarrito);

        // Tabla de historial de ventas
        tableModelVentas = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID Venta", "Fecha", "Cliente", "Total", "Detalles"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblVentas.setModel(tableModelVentas);

        // Tabla de productos con stock bajo
        tableModelStockBajo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Stock Actual", "Mínimo", "Solicitar"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Solo la columna de solicitar es editable
            }
        };
        tblStockBajo.setModel(tableModelStockBajo);
    }

    private void loadAllData() {
        loadProductos();
        loadHistorialVentas();
        loadProductosStockBajo();
        updateEstadisticas();
        updateTotalCarrito();
    }

    private void loadProductos() {
        try {
            tableModelProductos.setRowCount(0);
            List<Producto> productos = ventaController.getProductosParaVenta();

            for (Producto producto : productos) {
                tableModelProductos.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        "$" + producto.getPrecio(),
                        StockUtils.formatCantidad(producto.getStockVendedor()),
                        producto.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadHistorialVentas() {
        try {
            tableModelVentas.setRowCount(0);
            List<Venta> ventas = ventaController.getHistorialVentas(usuarioId);

            for (Venta venta : ventas) {
                tableModelVentas.addRow(new Object[]{
                        venta.getVentaId(),
                        DateUtils.formatDateForDisplay(new java.sql.Date(venta.getFechaVenta().getTime())),
                        venta.getCliente(),
                        "$" + venta.getTotal(),
                        "Ver Detalles"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar historial: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadProductosStockBajo() {
        try {
            tableModelStockBajo.setRowCount(0);
            List<Producto> productos = ventaController.getProductosStockBajo();

            for (Producto producto : productos) {
                tableModelStockBajo.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockVendedor()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaVendedor()),
                        "SOLICITAR"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar stock bajo: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEstadisticas() {
        try {
            String estadisticas = ventaController.getEstadisticas(usuarioId);
            lblEstadisticas.setText(estadisticas);
        } catch (Exception e) {
            lblEstadisticas.setText("Error al cargar estadísticas");
        }
    }

    private void buscarProductos() {
        try {
            String searchTerm = txtBuscarProducto.getText().trim();
            tableModelProductos.setRowCount(0);

            List<Producto> productos = ventaController.buscarProductos(searchTerm);

            for (Producto producto : productos) {
                tableModelProductos.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        "$" + producto.getPrecio(),
                        StockUtils.formatCantidad(producto.getStockVendedor()),
                        producto.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarAlCarrito() {
        int selectedRow = tblProductos.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para agregar al carrito",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productoId = (int) tableModelProductos.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelProductos.getValueAt(selectedRow, 1);
            BigDecimal precio = new BigDecimal(
                    ((String) tableModelProductos.getValueAt(selectedRow, 2)).replace("$", "")
            );
            double stockDisponible = Double.parseDouble(
                    ((String) tableModelProductos.getValueAt(selectedRow, 3)).replace(",", "")
            );

            // Pedir cantidad
            String cantidadStr = JOptionPane.showInputDialog(this,
                    "Agregar al carrito:\n\n" +
                            "Producto: " + productoNombre + "\n" +
                            "Precio: " + precio + "\n" +
                            "Stock disponible: " + stockDisponible + "\n\n" +
                            "Ingrese la cantidad:",
                    "Agregar al Carrito",
                    JOptionPane.QUESTION_MESSAGE);

            if (cantidadStr != null && !cantidadStr.trim().isEmpty()) {
                double cantidad = Double.parseDouble(cantidadStr);

                // Validar stock
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad debe ser mayor a 0",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cantidad > stockDisponible) {
                    JOptionPane.showMessageDialog(this,
                            "Stock insuficiente. Disponible: " + stockDisponible,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Crear detalle y agregar al carrito
                DetalleVenta detalle = new DetalleVenta(productoId, cantidad, precio);
                detalle.setProductoNombre(productoNombre);
                carrito.add(detalle);

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
                    "Error al agregar al carrito: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarCarrito() {
        tableModelCarrito.setRowCount(0);

        for (int i = 0; i < carrito.size(); i++) {
            DetalleVenta detalle = carrito.get(i);
            tableModelCarrito.addRow(new Object[]{
                    detalle.getProductoNombre(),
                    detalle.getCantidad(),
                    "$" + detalle.getPrecioUnitario(),
                    "$" + detalle.getSubTotal(),
                    "QUITAR"
            });
        }

        updateTotalCarrito();
    }

    private void updateTotalCarrito() {
        BigDecimal total = carrito.stream()
                .map(DetalleVenta::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        lblTotalVenta.setText("Total: $" + total);
    }

    private void procesarVenta() {
        if (carrito.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "El carrito está vacío",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            String cliente = txtCliente.getText().trim();
            if (cliente.isEmpty()) {
                cliente = "CLIENTE GENERAL";
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar venta?\n\n" +
                            "Cliente: " + cliente + "\n" +
                            "Total: " + lblTotalVenta.getText() + "\n" +
                            "Items: " + carrito.size(),
                    "Confirmar Venta",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = ventaController.procesarVenta(cliente, usuarioId, carrito);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Venta procesada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);

                    // Limpiar carrito y formulario
                    carrito.clear();
                    actualizarCarrito();
                    txtCliente.setText("");
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

    private void solicitarStock() {
        int selectedRow = tblStockBajo.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto para solicitar stock",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productoId = (int) tableModelStockBajo.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelStockBajo.getValueAt(selectedRow, 1);
            double stockActual = Double.parseDouble(
                    ((String) tableModelStockBajo.getValueAt(selectedRow, 2)).replace(",", "")
            );
            double stockMinimo = Double.parseDouble(
                    ((String) tableModelStockBajo.getValueAt(selectedRow, 3)).replace(",", "")
            );

            // Calcular cantidad recomendada
            double cantidadRecomendada = stockMinimo * 3;

            String cantidadStr = JOptionPane.showInputDialog(this,
                    "Solicitar stock a bodega:\n\n" +
                            "Producto: " + productoNombre + "\n" +
                            "Stock actual: " + stockActual + "\n" +
                            "Stock mínimo: " + stockMinimo + "\n\n" +
                            "Cantidad a solicitar (recomendado: " + cantidadRecomendada + "):",
                    "Solicitar Stock",
                    JOptionPane.QUESTION_MESSAGE);

            if (cantidadStr != null && !cantidadStr.trim().isEmpty()) {
                double cantidad = Double.parseDouble(cantidadStr);

                String observaciones = JOptionPane.showInputDialog(this,
                        "Observaciones para la solicitud:",
                        "Observaciones",
                        JOptionPane.QUESTION_MESSAGE);

                if (observaciones != null) {
                    boolean success = ventaController.crearPeticionStock(
                            productoId, usuarioId, cantidad, observaciones);

                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Solicitud de stock enviada exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadProductosStockBajo();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Error al enviar la solicitud",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un número válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al solicitar stock: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
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
            int ventaId = (int) tableModelVentas.getValueAt(selectedRow, 0);
            List<DetalleVenta> detalles = ventaController.getDetallesVenta(ventaId);

            StringBuilder detallesStr = new StringBuilder();
            detallesStr.append("<html><div style='text-align: center;'>");
            detallesStr.append("<h3>Detalles de Venta #").append(ventaId).append("</h3>");
            detallesStr.append("<table border='1' style='margin: 0 auto; width: 80%;'>");
            detallesStr.append("<tr><th>Producto</th><th>Cantidad</th><th>Precio</th><th>Subtotal</th></tr>");

            for (DetalleVenta detalle : detalles) {
                detallesStr.append("<tr>")
                        .append("<td>").append(detalle.getProductoNombre()).append("</td>")
                        .append("<td>").append(detalle.getCantidad()).append(" ").append(detalle.getUnidadMedida()).append("</td>")
                        .append("<td>$").append(detalle.getPrecioUnitario()).append("</td>")
                        .append("<td>$").append(detalle.getSubTotal()).append("</td>")
                        .append("</tr>");
            }

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

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblProductos = new javax.swing.JTable();
        txtBuscarProducto = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        btnAgregarCarrito = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblCarrito = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        txtCliente = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnProcesarVenta = new javax.swing.JButton();
        lblTotalVenta = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblVentas = new javax.swing.JTable();
        jLabel4 = new javax.swing.JLabel();
        btnVerDetalles = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblStockBajo = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        btnSolicitarStock = new javax.swing.JButton();
        lblEstadisticas = new javax.swing.JLabel();
        btnRefrescar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Módulo de Vendedor");

        jTabbedPane1.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblProductos);

        txtBuscarProducto.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarProducto.setText("Buscar producto...");

        btnBuscar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        btnAgregarCarrito.setBackground(new java.awt.Color(51, 153, 255));
        btnAgregarCarrito.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnAgregarCarrito.setText("AGREGAR AL CARRITO");
        btnAgregarCarrito.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarCarritoActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel1.setText("PRODUCTOS DISPONIBLES PARA VENTA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1169, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnAgregarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel1)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtBuscarProducto, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnAgregarCarrito, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Productos", jPanel1);

        tblCarrito.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblCarrito);

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel2.setText("CARRITO DE COMPRAS");

        txtCliente.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtCliente.setText("CLIENTE GENERAL");

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jLabel3.setText("Cliente:");

        btnProcesarVenta.setBackground(new java.awt.Color(0, 153, 51));
        btnProcesarVenta.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        btnProcesarVenta.setForeground(new java.awt.Color(255, 255, 255));
        btnProcesarVenta.setText("PROCESAR VENTA");
        btnProcesarVenta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnProcesarVentaActionPerformed(evt);
            }
        });

        lblTotalVenta.setFont(new java.awt.Font("Liberation Sans", 1, 18)); // NOI18N
        lblTotalVenta.setForeground(new java.awt.Color(0, 102, 0));
        lblTotalVenta.setText("Total: $0.00");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1169, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(lblTotalVenta)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnProcesarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addGap(0, 0, Short.MAX_VALUE)))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtCliente, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3)
                                        .addComponent(btnProcesarVenta, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblTotalVenta))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Punto de Venta", jPanel2);

        tblVentas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblVentas);

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel4.setText("HISTORIAL DE VENTAS");

        btnVerDetalles.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnVerDetalles.setText("VER DETALLES");
        btnVerDetalles.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerDetallesActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1169, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(btnVerDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel4)))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel4)
                                        .addComponent(btnVerDetalles, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Historial de Ventas", jPanel3);

        tblStockBajo.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane4.setViewportView(tblStockBajo);

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel5.setText("PRODUCTOS CON STOCK BAJO - SOLICITAR A BODEGA");

        btnSolicitarStock.setBackground(new java.awt.Color(255, 153, 51));
        btnSolicitarStock.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnSolicitarStock.setText("SOLICITAR STOCK");
        btnSolicitarStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolicitarStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 1169, Short.MAX_VALUE)
                                        .addGroup(jPanel4Layout.createSequentialGroup()
                                                .addComponent(btnSolicitarStock, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel5)))
                                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
                jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel4Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel5)
                                        .addComponent(btnSolicitarStock, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 587, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Solicitar Stock", jPanel4);

        lblEstadisticas.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        lblEstadisticas.setText("Cargando estadísticas...");

        btnRefrescar.setFont(new java.awt.Font("Liberation Sans", 1, 12)); // NOI18N
        btnRefrescar.setText("Refrescar Datos");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jTabbedPane1)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(lblEstadisticas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnRefrescar)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(lblEstadisticas)
                                        .addComponent(btnRefrescar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTabbedPane1)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        buscarProductos();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void btnAgregarCarritoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarCarritoActionPerformed
        agregarAlCarrito();
    }//GEN-LAST:event_btnAgregarCarritoActionPerformed

    private void btnProcesarVentaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnProcesarVentaActionPerformed
        procesarVenta();
    }//GEN-LAST:event_btnProcesarVentaActionPerformed

    private void btnVerDetallesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerDetallesActionPerformed
        verDetallesVenta();
    }//GEN-LAST:event_btnVerDetallesActionPerformed

    private void btnSolicitarStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolicitarStockActionPerformed
        solicitarStock();
    }//GEN-LAST:event_btnSolicitarStockActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        loadAllData();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAgregarCarrito;
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnProcesarVenta;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JButton btnSolicitarStock;
    private javax.swing.JButton btnVerDetalles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblEstadisticas;
    private javax.swing.JLabel lblTotalVenta;
    private javax.swing.JTable tblCarrito;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTable tblStockBajo;
    private javax.swing.JTable tblVentas;
    private javax.swing.JTextField txtBuscarProducto;
    private javax.swing.JTextField txtCliente;
    // End of variables declaration//GEN-END:variables
}