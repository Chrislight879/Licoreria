package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.BodegaController;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.utils.StockUtils;
import com.mycompany.licoreria.utils.DateUtils;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class BodegaMainForm extends javax.swing.JInternalFrame {
    private BodegaController bodegaController;
    private DefaultTableModel tableModelProductos;
    private DefaultTableModel tableModelPeticiones;
    private DefaultTableModel tableModelStockBajo;

    public BodegaMainForm() {
        initComponents();
        bodegaController = new BodegaController();
        initializeTables();
        loadAllData();
        setTitle("Módulo de Bodega - Gestión de Inventario");
    }

    private void initializeTables() {
        // Tabla de productos
        tableModelProductos = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Proveedor", "Stock Bodega", "Mínimo", "Unidad", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblProductos.setModel(tableModelProductos);

        // Tabla de peticiones de vendedores
        tableModelPeticiones = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Solicitante", "Cantidad", "Fecha", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPeticiones.setModel(tableModelPeticiones);

        // Tabla de stock bajo
        tableModelStockBajo = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Stock Actual", "Mínimo", "Diferencia", "Prioridad"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblStockBajo.setModel(tableModelStockBajo);
    }

    private void loadAllData() {
        loadProductos();
        loadPeticionesPendientes();
        loadStockBajo();
        updateEstadisticas();
    }

    private void loadProductos() {
        try {
            tableModelProductos.setRowCount(0);
            List<Producto> productos = bodegaController.getAllProductos();

            for (Producto producto : productos) {
                String estadoStock = StockUtils.getNivelStock(
                        producto.getStockBodega(), producto.getCantidadMinimaBodega());

                tableModelProductos.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        producto.getProveedorNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        producto.getUnidadMedida(),
                        estadoStock
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPeticionesPendientes() {
        try {
            tableModelPeticiones.setRowCount(0);
            List<PeticionStock> peticiones = bodegaController.getPeticionesPendientes();

            for (PeticionStock peticion : peticiones) {
                tableModelPeticiones.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        peticion.getUsuarioSolicitanteNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()),
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaSolicitud().getTime())),
                        peticion.getEstado().toUpperCase()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar peticiones: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadStockBajo() {
        try {
            tableModelStockBajo.setRowCount(0);
            List<Producto> productosStockBajo = bodegaController.getProductosStockBajo();

            for (Producto producto : productosStockBajo) {
                double diferencia = producto.getStockBodega() - producto.getCantidadMinimaBodega();
                String prioridad = producto.getStockBodega() <= producto.getCantidadMinimaBodega() * 0.3 ?
                        "ALTA" : "MEDIA";

                tableModelStockBajo.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        StockUtils.formatCantidad(diferencia),
                        prioridad
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
            String estadisticas = bodegaController.getEstadisticasBodega();
            lblEstadisticas.setText(estadisticas);
        } catch (Exception e) {
            lblEstadisticas.setText("Error al cargar estadísticas");
        }
    }

    private void buscarProductos() {
        try {
            String searchTerm = txtBuscarProductos.getText().trim();
            tableModelProductos.setRowCount(0);

            List<Producto> productos = bodegaController.searchProductos(searchTerm);

            for (Producto producto : productos) {
                String estadoStock = StockUtils.getNivelStock(
                        producto.getStockBodega(), producto.getCantidadMinimaBodega());

                tableModelProductos.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        producto.getProveedorNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        producto.getUnidadMedida(),
                        estadoStock
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar productos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void solicitarProductoProveedor() {
        int selectedRow = tblStockBajo.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto con stock bajo para solicitar",
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
            double cantidadRecomendada = stockMinimo * 3; // 3 veces el mínimo

            String cantidadStr = JOptionPane.showInputDialog(this,
                    "Solicitar producto a proveedor:\n\n" +
                            "Producto: " + productoNombre + "\n" +
                            "Stock actual: " + stockActual + "\n" +
                            "Stock mínimo: " + stockMinimo + "\n\n" +
                            "Cantidad a solicitar (recomendado: " + cantidadRecomendada + "):",
                    "Solicitar a Proveedor",
                    JOptionPane.QUESTION_MESSAGE);

            if (cantidadStr != null && !cantidadStr.trim().isEmpty()) {
                double cantidad = Double.parseDouble(cantidadStr);

                String observaciones = JOptionPane.showInputDialog(this,
                        "Observaciones para la solicitud:",
                        "Observaciones",
                        JOptionPane.QUESTION_MESSAGE);

                if (observaciones != null) {
                    boolean success = bodegaController.crearSolicitudCompra(
                            productoId, cantidad, observaciones);

                    if (success) {
                        JOptionPane.showMessageDialog(this,
                                "Solicitud de compra creada exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        loadAllData();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Error al crear la solicitud de compra",
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
                    "Error al solicitar producto: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void despacharPeticion() {
        int selectedRow = tblPeticiones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una petición para despachar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int peticionId = (int) tableModelPeticiones.getValueAt(selectedRow, 0);
            String producto = (String) tableModelPeticiones.getValueAt(selectedRow, 1);
            String solicitante = (String) tableModelPeticiones.getValueAt(selectedRow, 2);
            double cantidad = Double.parseDouble(
                    ((String) tableModelPeticiones.getValueAt(selectedRow, 3)).replace(",", "")
            );

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar despacho de petición?\n\n" +
                            "Producto: " + producto + "\n" +
                            "Solicitante: " + solicitante + "\n" +
                            "Cantidad: " + cantidad,
                    "Confirmar Despacho",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = bodegaController.despacharPeticion(peticionId);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Petición despachada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadAllData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al despachar la petición",
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
                    "Error al despachar petición: " + e.getMessage(),
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
        txtBuscarProductos = new javax.swing.JTextField();
        btnBuscarProductos = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPeticiones = new javax.swing.JTable();
        btnDespacharPeticion = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblStockBajo = new javax.swing.JTable();
        btnSolicitarProveedor = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        lblEstadisticas = new javax.swing.JLabel();
        btnRefrescar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Módulo de Bodega");

        jTabbedPane1.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N

        tblProductos.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        jScrollPane1.setViewportView(tblProductos);

        txtBuscarProductos.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarProductos.setText("Buscar productos...");

        btnBuscarProductos.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnBuscarProductos.setText("BUSCAR");
        btnBuscarProductos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarProductosActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel1.setText("INVENTARIO COMPLETO DE BODEGA");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE)
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                                .addComponent(txtBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel1)))
                                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
                jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscarProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Inventario Bodega", jPanel1);

        tblPeticiones.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        jScrollPane2.setViewportView(tblPeticiones);

        btnDespacharPeticion.setBackground(new java.awt.Color(51, 153, 255));
        btnDespacharPeticion.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnDespacharPeticion.setText("DESPACHAR PETICIÓN");
        btnDespacharPeticion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDespacharPeticionActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel2.setText("PETICIONES DE VENDEDORES - PENDIENTES");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE)
                                        .addGroup(jPanel2Layout.createSequentialGroup()
                                                .addComponent(btnDespacharPeticion, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel2)))
                                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel2Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnDespacharPeticion, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Peticiones de Vendedores", jPanel2);

        tblStockBajo.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null},
                        {null, null, null, null}
                },
                new String [] {
                        "Title 1", "Title 2", "Title 3", "Title 4"
                }
        ));
        jScrollPane3.setViewportView(tblStockBajo);

        btnSolicitarProveedor.setBackground(new java.awt.Color(255, 153, 51));
        btnSolicitarProveedor.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnSolicitarProveedor.setText("SOLICITAR A PROVEEDOR");
        btnSolicitarProveedor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSolicitarProveedorActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel3.setText("PRODUCTOS CON STOCK BAJO - REABASTECER");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1015, Short.MAX_VALUE)
                                        .addGroup(jPanel3Layout.createSequentialGroup()
                                                .addComponent(btnSolicitarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel3)))
                                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(btnSolicitarProveedor, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel3))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 534, Short.MAX_VALUE)
                                .addContainerGap())
        );

        jTabbedPane1.addTab("Stock Bajo - Reabastecer", jPanel3);

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        jLabel4.setText("Estadísticas:");

        lblEstadisticas.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
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
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                                        .addComponent(jLabel4)
                                        .addComponent(lblEstadisticas)
                                        .addComponent(btnRefrescar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTabbedPane1)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarProductosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarProductosActionPerformed
        buscarProductos();
    }//GEN-LAST:event_btnBuscarProductosActionPerformed

    private void btnDespacharPeticionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDespacharPeticionActionPerformed
        despacharPeticion();
    }//GEN-LAST:event_btnDespacharPeticionActionPerformed

    private void btnSolicitarProveedorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSolicitarProveedorActionPerformed
        solicitarProductoProveedor();
    }//GEN-LAST:event_btnSolicitarProveedorActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        loadAllData();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscarProductos;
    private javax.swing.JButton btnDespacharPeticion;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JButton btnSolicitarProveedor;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JLabel lblEstadisticas;
    private javax.swing.JTable tblPeticiones;
    private javax.swing.JTable tblProductos;
    private javax.swing.JTable tblStockBajo;
    private javax.swing.JTextField txtBuscarProductos;
    // End of variables declaration//GEN-END:variables
}