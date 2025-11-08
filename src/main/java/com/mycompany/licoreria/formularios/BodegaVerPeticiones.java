package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.PeticionBodegaController;
import com.mycompany.licoreria.models.PeticionStock;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.utils.StockUtils;
import com.mycompany.licoreria.utils.DateUtils;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class BodegaVerPeticiones extends javax.swing.JInternalFrame {
    private PeticionBodegaController peticionController;
    private DefaultTableModel tableModelPeticionesEspera;
    private DefaultTableModel tableModelInventarioBodega;
    private DefaultTableModel tableModelPeticionesAceptadas;

    // ID del usuario bodeguero actual (debería venir del login)
    private int usuarioBodegueroId = 3; // Por defecto usuario bodeguero

    public BodegaVerPeticiones() {
        initComponents();
        peticionController = new PeticionBodegaController();
        initializeTables();
        loadAllData();
        setTitle("Gestión de Peticiones - Módulo Bodega");
    }

    private void initializeTables() {
        // Tabla de peticiones en espera
        tableModelPeticionesEspera = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Solicitante", "Cantidad", "Stock Bodega", "Fecha", "Prioridad"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPeticionesEspera.setModel(tableModelPeticionesEspera);

        // Tabla de inventario bodega
        tableModelInventarioBodega = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Stock", "Mínimo", "Unidad", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInventarioBodega.setModel(tableModelInventarioBodega);

        // Tabla de peticiones aceptadas
        tableModelPeticionesAceptadas = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Solicitante", "Cantidad", "Fecha Aprobación", "Estado", "Aprobador"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPeticionesAceptadas.setModel(tableModelPeticionesAceptadas);
    }

    private void loadAllData() {
        loadPeticionesEspera();
        loadInventarioBodega();
        loadPeticionesAceptadas();
        checkPeticionesUrgentes();
    }

    private void loadPeticionesEspera() {
        try {
            tableModelPeticionesEspera.setRowCount(0);
            List<PeticionStock> peticiones = peticionController.getPeticionesPendientes();

            for (PeticionStock peticion : peticiones) {
                String prioridad = peticion.getStockBodega() < peticion.getCantidadSolicitada() ?
                        "CRÍTICA" : "NORMAL";

                tableModelPeticionesEspera.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        peticion.getUsuarioSolicitanteNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()) + " " + peticion.getUnidadMedida(),
                        StockUtils.formatCantidad(peticion.getStockBodega()) + " " + peticion.getUnidadMedida(),
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaSolicitud().getTime())),
                        prioridad
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar peticiones en espera: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInventarioBodega() {
        try {
            tableModelInventarioBodega.setRowCount(0);
            List<Producto> inventario = peticionController.getInventarioBodegaCompleto();

            for (Producto producto : inventario) {
                String estadoStock = StockUtils.getNivelStock(
                        producto.getStockBodega(), producto.getCantidadMinimaBodega());

                tableModelInventarioBodega.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        producto.getUnidadMedida(),
                        estadoStock
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPeticionesAceptadas() {
        try {
            tableModelPeticionesAceptadas.setRowCount(0);
            List<PeticionStock> peticiones = peticionController.getPeticionesAceptadas();

            for (PeticionStock peticion : peticiones) {
                String fechaAprobacion = peticion.getFechaAprobacion() != null ?
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaAprobacion().getTime())) :
                        "N/A";

                tableModelPeticionesAceptadas.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        peticion.getUsuarioSolicitanteNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()) + " " + peticion.getUnidadMedida(),
                        fechaAprobacion,
                        peticion.getEstado().toUpperCase(),
                        peticion.getUsuarioAprobadorNombre()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar peticiones aceptadas: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void checkPeticionesUrgentes() {
        try {
            if (peticionController.hayPeticionesUrgentes()) {
                List<PeticionStock> peticionesCriticas = peticionController.getPeticionesCriticas();
                JOptionPane.showMessageDialog(this,
                        "¡ALERTA! Hay " + peticionesCriticas.size() + " peticiones críticas\n" +
                                "con stock insuficiente en bodega.",
                        "Peticiones Críticas",
                        JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            // No mostrar error para esta verificación
        }
    }

    private void buscarInventarioBodega() {
        try {
            String searchTerm = txtBuscarEnBodega.getText().trim();
            tableModelInventarioBodega.setRowCount(0);

            List<Producto> inventario = peticionController.searchInventarioBodega(searchTerm);

            for (Producto producto : inventario) {
                String estadoStock = StockUtils.getNivelStock(
                        producto.getStockBodega(), producto.getCantidadMinimaBodega());

                tableModelInventarioBodega.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        producto.getUnidadMedida(),
                        estadoStock
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar en inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPeticionesAceptadas() {
        try {
            String searchTerm = txtBuscarPeticiones.getText().trim();
            tableModelPeticionesAceptadas.setRowCount(0);

            List<PeticionStock> peticiones = peticionController.searchPeticionesAceptadas(searchTerm);

            for (PeticionStock peticion : peticiones) {
                String fechaAprobacion = peticion.getFechaAprobacion() != null ?
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaAprobacion().getTime())) :
                        "N/A";

                tableModelPeticionesAceptadas.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        peticion.getUsuarioSolicitanteNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()) + " " + peticion.getUnidadMedida(),
                        fechaAprobacion,
                        peticion.getEstado().toUpperCase(),
                        peticion.getUsuarioAprobadorNombre()
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
        int selectedRow = tblPeticionesEspera.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una petición en espera para aceptar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int peticionId = (int) tableModelPeticionesEspera.getValueAt(selectedRow, 0);
            String producto = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 1);
            String solicitante = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 2);
            String cantidad = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 3);
            String stockBodega = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 4);
            String prioridad = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 6);

            // Mostrar advertencia si es crítica
            if ("CRÍTICA".equals(prioridad)) {
                int confirm = JOptionPane.showConfirmDialog(this,
                        "¡ATENCIÓN! Esta petición es CRÍTICA\n\n" +
                                "Producto: " + producto + "\n" +
                                "Cantidad solicitada: " + cantidad + "\n" +
                                "Stock en bodega: " + stockBodega + "\n\n" +
                                "¿Desea continuar con la aprobación?",
                        "Petición Crítica",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm != JOptionPane.YES_OPTION) {
                    return;
                }
            }

            String observaciones = JOptionPane.showInputDialog(this,
                    "Aprobar petición:\n\n" +
                            "Producto: " + producto + "\n" +
                            "Solicitante: " + solicitante + "\n" +
                            "Cantidad: " + cantidad + "\n\n" +
                            "Observaciones:",
                    "Aprobar Petición",
                    JOptionPane.QUESTION_MESSAGE);

            if (observaciones != null && !observaciones.trim().isEmpty()) {
                boolean success = peticionController.aprobarPeticion(
                        peticionId, usuarioBodegueroId, observaciones);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Petición aprobada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadAllData();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al aprobar la petición",
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
        int selectedRow = tblPeticionesEspera.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una petición en espera para rechazar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int peticionId = (int) tableModelPeticionesEspera.getValueAt(selectedRow, 0);
            String producto = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 1);
            String solicitante = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 2);
            String cantidad = (String) tableModelPeticionesEspera.getValueAt(selectedRow, 3);

            String motivo = JOptionPane.showInputDialog(this,
                    "Rechazar petición:\n\n" +
                            "Producto: " + producto + "\n" +
                            "Solicitante: " + solicitante + "\n" +
                            "Cantidad: " + cantidad + "\n\n" +
                            "Motivo del rechazo:",
                    "Rechazar Petición",
                    JOptionPane.QUESTION_MESSAGE);

            if (motivo != null && !motivo.trim().isEmpty()) {
                boolean success = peticionController.rechazarPeticion(
                        peticionId, usuarioBodegueroId, motivo);

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
        tblPeticionesEspera = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblInventarioBodega = new javax.swing.JTable();
        jLabel10 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPeticionesAceptadas = new javax.swing.JTable();
        jLabel11 = new javax.swing.JLabel();
        txtBuscarEnBodega = new javax.swing.JTextField();
        txtBuscarPeticiones = new javax.swing.JTextField();
        btnRechazar = new javax.swing.JButton();
        btnAceptar = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();
        lblEstadisticas = new javax.swing.JLabel();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Gestión de Peticiones - Bodega");

        tblPeticionesEspera.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblPeticionesEspera);

        jLabel9.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel9.setText("INVENTARIO EN BODEGA");

        tblInventarioBodega.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblInventarioBodega);

        jLabel10.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel10.setText("PETICIONES EN ESPERA");

        tblPeticionesAceptadas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblPeticionesAceptadas);

        jLabel11.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel11.setText("PETICIONES PROCESADAS");

        txtBuscarEnBodega.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarEnBodega.setText("Buscar en Bodega...");
        txtBuscarEnBodega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarEnBodegaActionPerformed(evt);
            }
        });

        txtBuscarPeticiones.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarPeticiones.setText("Buscar en Peticiones...");
        txtBuscarPeticiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarPeticionesActionPerformed(evt);
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

        btnAceptar.setBackground(new java.awt.Color(102, 255, 102));
        btnAceptar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnAceptar.setText("ACEPTAR");
        btnAceptar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAceptarActionPerformed(evt);
            }
        });

        btnRefrescar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnRefrescar.setText("REFRESCAR");
        btnRefrescar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefrescarActionPerformed(evt);
            }
        });

        lblEstadisticas.setFont(new java.awt.Font("Liberation Sans", 0, 12)); // NOI18N
        lblEstadisticas.setText("Cargando estadísticas...");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(12, 12, 12)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel9)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(6, 6, 6)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 332, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(124, 124, 124)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                                        .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnRechazar, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                        .addComponent(txtBuscarEnBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel10)
                                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 349, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(0, 0, Short.MAX_VALUE))))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel11)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 796, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(lblEstadisticas, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(22, 22, 22)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel10)
                                        .addComponent(jLabel9))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(txtBuscarEnBodega, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 309, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(btnRechazar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnAceptar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel11)
                                        .addComponent(lblEstadisticas)
                                        .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(8, 8, 8)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 283, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(22, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarEnBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarEnBodegaActionPerformed
        buscarInventarioBodega();
    }//GEN-LAST:event_txtBuscarEnBodegaActionPerformed

    private void txtBuscarPeticionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarPeticionesActionPerformed
        buscarPeticionesAceptadas();
    }//GEN-LAST:event_txtBuscarPeticionesActionPerformed

    private void btnRechazarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRechazarActionPerformed
        rechazarPeticion();
    }//GEN-LAST:event_btnRechazarActionPerformed

    private void btnAceptarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAceptarActionPerformed
        aceptarPeticion();
    }//GEN-LAST:event_btnAceptarActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        loadAllData();
        try {
            String estadisticas = peticionController.getEstadisticasPeticiones();
            lblEstadisticas.setText(estadisticas);
        } catch (Exception e) {
            lblEstadisticas.setText("Error al cargar estadísticas");
        }
    }//GEN-LAST:event_btnRefrescarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAceptar;
    private javax.swing.JButton btnRechazar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblEstadisticas;
    private javax.swing.JTable tblInventarioBodega;
    private javax.swing.JTable tblPeticionesAceptadas;
    private javax.swing.JTable tblPeticionesEspera;
    private javax.swing.JTextField txtBuscarEnBodega;
    private javax.swing.JTextField txtBuscarPeticiones;
    // End of variables declaration//GEN-END:variables
}