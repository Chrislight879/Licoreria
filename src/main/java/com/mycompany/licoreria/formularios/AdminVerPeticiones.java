package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.PeticionStockController;
import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.utils.DateUtils;
import com.mycompany.licoreria.utils.StockUtils;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AdminVerPeticiones extends javax.swing.JInternalFrame {
    private PeticionStockController peticionController;
    private DefaultTableModel tableModelPeticiones;
    private DefaultTableModel tableModelPeticionesEspera;
    private DefaultTableModel tableModelBodega;

    // ID del usuario administrador actual (debería venir del login)
    private int usuarioAdminId = 1; // Por defecto, cambiar según tu sistema de autenticación

    public AdminVerPeticiones() {
        initComponents();
        peticionController = new PeticionStockController();
        initializeTables();
        loadAllData();
        setTitle("Gestión de Peticiones de Stock");
    }

    private void initializeTables() {
        // Tabla de peticiones general
        tableModelPeticiones = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Solicitante", "Cantidad", "Fecha", "Estado", "Observaciones"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPeticiones.setModel(tableModelPeticiones);

        // Tabla de peticiones en espera (pendientes)
        tableModelPeticionesEspera = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Solicitante", "Cantidad", "Fecha Solicitud"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPeticionesEsperando.setModel(tableModelPeticionesEspera);

        // Tabla de inventario bodega
        tableModelBodega = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Stock", "Mínimo", "Unidad", "Nivel"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblBodega.setModel(tableModelBodega);
    }

    private void loadAllData() {
        loadPeticiones();
        loadPeticionesPendientes();
        loadInventarioBodega();
        updateEstadisticas();
    }

    private void loadPeticiones() {
        try {
            tableModelPeticiones.setRowCount(0);
            List<PeticionStock> peticiones = peticionController.getAllPeticiones();

            for (PeticionStock peticion : peticiones) {
                tableModelPeticiones.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        peticion.getUsuarioSolicitanteNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()),
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaSolicitud().getTime())),
                        peticion.getEstado().toUpperCase(),
                        peticion.getObservaciones()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar peticiones: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPeticionesPendientes() {
        try {
            tableModelPeticionesEspera.setRowCount(0);
            List<PeticionStock> peticionesPendientes = peticionController.getPeticionesPendientes();

            for (PeticionStock peticion : peticionesPendientes) {
                tableModelPeticionesEspera.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        peticion.getUsuarioSolicitanteNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()),
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaSolicitud().getTime()))
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar peticiones pendientes: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInventarioBodega() {
        try {
            tableModelBodega.setRowCount(0);
            List<Object[]> inventario = peticionController.getInventarioBodega();

            for (Object[] item : inventario) {
                int productoId = (int) item[0];
                String productoNombre = (String) item[1];
                double stockDisponible = (double) item[2];
                double stockMinimo = (double) item[3];
                String unidadMedida = (String) item[4];

                String nivelStock = StockUtils.getNivelStock(stockDisponible, stockMinimo);

                tableModelBodega.addRow(new Object[]{
                        productoId,
                        productoNombre,
                        StockUtils.formatCantidad(stockDisponible) + " " + unidadMedida,
                        StockUtils.formatCantidad(stockMinimo) + " " + unidadMedida,
                        unidadMedida,
                        nivelStock
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEstadisticas() {
        try {
            String estadisticas = peticionController.getEstadisticasPeticiones();
            // Puedes mostrar esto en una etiqueta si agregas una al formulario
            System.out.println("Estadísticas: " + estadisticas);
        } catch (Exception e) {
            // No mostrar error para estadísticas
        }
    }

    private void searchPeticiones() {
        try {
            String searchTerm = txtBuscarPeticiones.getText().trim();
            tableModelPeticiones.setRowCount(0);

            List<PeticionStock> peticiones = peticionController.searchPeticiones(searchTerm);

            for (PeticionStock peticion : peticiones) {
                tableModelPeticiones.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        peticion.getUsuarioSolicitanteNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()),
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaSolicitud().getTime())),
                        peticion.getEstado().toUpperCase(),
                        peticion.getObservaciones()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar peticiones: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void aceptarPeticion() {
        int selectedRow = tblPeticionesEsperando.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una petición pendiente para aceptar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int peticionId = (int) tableModelPeticionesEspera.getValueAt(selectedRow, 0);
            String producto = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 1);
            double cantidad = Double.parseDouble(
                    ((String) tableModelPeticionesEspera.getValueAt(selectedRow, 3)).replace(",", "")
            );

            String observaciones = JOptionPane.showInputDialog(this,
                    "Ingrese observaciones para la petición:\nProducto: " + producto + "\nCantidad: " + cantidad,
                    "Aceptar Petición",
                    JOptionPane.QUESTION_MESSAGE);

            if (observaciones != null && !observaciones.trim().isEmpty()) {
                boolean success = peticionController.aprobarPeticion(peticionId, usuarioAdminId, observaciones);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Petición aceptada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadAllData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al aceptar la petición",
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
                    "Error al aceptar petición: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void rechazarPeticion() {
        int selectedRow = tblPeticionesEsperando.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una petición pendiente para rechazar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int peticionId = (int) tableModelPeticionesEspera.getValueAt(selectedRow, 0);
            String producto = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 1);

            String observaciones = JOptionPane.showInputDialog(this,
                    "Ingrese el motivo del rechazo para:\nProducto: " + producto,
                    "Rechazar Petición",
                    JOptionPane.QUESTION_MESSAGE);

            if (observaciones != null && !observaciones.trim().isEmpty()) {
                boolean success = peticionController.rechazarPeticion(peticionId, usuarioAdminId, observaciones);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Petición rechazada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadAllData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al rechazar la petición",
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
                    "Error al rechazar petición: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblBodega = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPeticiones = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        btnAceptar = new javax.swing.JButton();
        btnRechazar = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPeticionesEsperando = new javax.swing.JTable();
        txtBuscarPeticiones = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        btnRefrescar = new javax.swing.JButton();
        btnDespachar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestión de Peticiones de Stock");

        tblBodega.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblBodega);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel1.setText("PETICIONES EN ESPERA");

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

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel2.setText("INVENTARIO BODEGA");

        btnAceptar.setBackground(new java.awt.Color(102, 255, 102));
        btnAceptar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnAceptar.setText("ACEPTAR");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        btnRechazar.setBackground(new java.awt.Color(255, 0, 51));
        btnRechazar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnRechazar.setText("RECHAZAR");
        btnRechazar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRechazarActionPerformed(evt);
            }
        });

        tblPeticionesEsperando.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblPeticionesEsperando);

        txtBuscarPeticiones.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarPeticiones.setText("Buscar en Peticiones...");
        txtBuscarPeticiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarPeticionesActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel3.setText("TODAS LAS PETICIONES");

        btnRefrescar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnRefrescar.setText("REFRESCAR");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        btnDespachar.setBackground(new java.awt.Color(51, 153, 255));
        btnDespachar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnDespachar.setText("DESPACHAR");
        btnDespachar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDespacharActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel2)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel1)
                                                .addGap(152, 152, 152))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 385, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(33, 33, 33)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(btnAceptar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnRechazar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnDespachar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 429, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 179, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel3)
                                                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 776, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                                .addContainerGap(18, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(39, 39, 39)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnRechazar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnDespachar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(jLabel2)
                                                        .addComponent(jLabel1))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 352, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 347, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel3)
                                        .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(20, 20, 20))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        aceptarPeticion();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnRechazarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRechazarActionPerformed
        rechazarPeticion();
    }//GEN-LAST:event_btnRechazarActionPerformed

    private void txtBuscarPeticionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarPeticionesActionPerformed
        searchPeticiones();
    }//GEN-LAST:event_txtBuscarPeticionesActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        loadAllData();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    private void btnDespacharActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDespacharActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnDespacharActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnDespachar;
    private javax.swing.JButton btnRechazar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTable tblBodega;
    private javax.swing.JTable tblPeticiones;
    private javax.swing.JTable tblPeticionesEsperando;
    private javax.swing.JTextField txtBuscarPeticiones;
    // End of variables declaration//GEN-END:variables
}