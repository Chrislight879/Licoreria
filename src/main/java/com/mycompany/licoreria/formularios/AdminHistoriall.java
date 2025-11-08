package com.mycompany.licoreria.formularios; // no muestra el historial

import com.mycompany.licoreria.controllers.HistoryLogController;
import com.mycompany.licoreria.models.HistoryLog;
import com.mycompany.licoreria.utils.DateUtils;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AdminHistoriall extends javax.swing.JInternalFrame {
    private HistoryLogController historyController;
    private DefaultTableModel tableModel;

    public AdminHistoriall() {
        initComponents();
        historyController = new HistoryLogController();
        initializeTable();
        loadHistoryData();
        setTitle("Historial del Sistema - Conectado a BD");
    }

    private void initializeTable() {
        tableModel = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Fecha", "Proceso", "Usuario", "Producto", "Descripción"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblHistorial.setModel(tableModel);
    }

    private void loadHistoryData() {
        try {
            tableModel.setRowCount(0);
            List<HistoryLog> historyLogs = historyController.getAllHistoryLogs();

            for (HistoryLog log : historyLogs) {
                tableModel.addRow(new Object[]{
                        log.getHistoryLogId(),
                        DateUtils.formatDateForDisplay(new java.sql.Date(log.getFecha().getTime())),
                        log.getProcesoNombre() != null ? log.getProcesoNombre() : "N/A",
                        log.getUsuarioNombre() != null ? log.getUsuarioNombre() : "Sistema",
                        log.getProductoNombre() != null ? log.getProductoNombre() : "N/A",
                        log.getDescripcion()
                });
            }

            // Mostrar estadísticas
            lblEstadisticas.setText(historyController.getEstadisticas());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar historial: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            loadSampleData(); // Cargar datos de ejemplo en caso de error
        }
    }

    private void loadSampleData() {
        // Datos de ejemplo en caso de error
        tableModel.addRow(new Object[]{1, "08/11/2024", "Login", "admin", "N/A", "Usuario admin inició sesión"});
        tableModel.addRow(new Object[]{2, "08/11/2024", "Creación", "admin", "N/A", "Usuario testuser creado"});
        tableModel.addRow(new Object[]{3, "08/11/2024", "Venta", "vendedor1", "Producto A", "Venta realizada #001"});
        tableModel.addRow(new Object[]{4, "07/11/2024", "Stock", "bodega", "Producto B", "Stock actualizado"});
        lblEstadisticas.setText("Modo demo - Datos de ejemplo");
    }

    private void searchHistory() {
        try {
            String searchTerm = txtBuscarPeticiones.getText().trim();
            tableModel.setRowCount(0);

            List<HistoryLog> historyLogs = historyController.searchHistoryLogs(searchTerm);

            for (HistoryLog log : historyLogs) {
                tableModel.addRow(new Object[]{
                        log.getHistoryLogId(),
                        DateUtils.formatDateForDisplay(new java.sql.Date(log.getFecha().getTime())),
                        log.getProcesoNombre() != null ? log.getProcesoNombre() : "N/A",
                        log.getUsuarioNombre() != null ? log.getUsuarioNombre() : "Sistema",
                        log.getProductoNombre() != null ? log.getProductoNombre() : "N/A",
                        log.getDescripcion()
                });
            }

            lblEstadisticas.setText("Resultados: " + historyLogs.size() + " registros encontrados");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblHistorial = new javax.swing.JTable();
        txtBuscarPeticiones = new javax.swing.JTextField();
        btnBuscar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        lblEstadisticas = new javax.swing.JLabel();
        btnRefrescar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Historial del Sistema");

        tblHistorial.setModel(new javax.swing.table.DefaultTableModel(
                new Object [][] {
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null},
                        {null, null, null, null, null, null}
                },
                new String [] {
                        "ID", "Fecha", "Proceso", "Usuario", "Producto", "Descripción"
                }
        ));
        jScrollPane1.setViewportView(tblHistorial);

        txtBuscarPeticiones.setFont(new java.awt.Font("Liberation Sans", 0, 14));
        txtBuscarPeticiones.setText("Buscar en Historial...");
        txtBuscarPeticiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarPeticionesActionPerformed(evt);
            }
        });

        btnBuscar.setFont(new java.awt.Font("Liberation Sans", 1, 14));
        btnBuscar.setText("BUSCAR");
        btnBuscar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 14));
        jLabel1.setText("Buscar:");

        lblEstadisticas.setFont(new java.awt.Font("Liberation Sans", 2, 12));
        lblEstadisticas.setText("Cargando estadísticas...");

        btnRefrescar.setFont(new java.awt.Font("Liberation Sans", 1, 12));
        btnRefrescar.setText("Refrescar");
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
                                .addGap(19, 19, 19)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 972, Short.MAX_VALUE)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(btnRefrescar)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(lblEstadisticas)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(btnBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(jLabel1)
                                        .addComponent(lblEstadisticas)
                                        .addComponent(btnRefrescar))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
                                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnBuscarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarActionPerformed
        searchHistory();
    }//GEN-LAST:event_btnBuscarActionPerformed

    private void txtBuscarPeticionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarPeticionesActionPerformed
        searchHistory();
    }//GEN-LAST:event_txtBuscarPeticionesActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        loadHistoryData();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBuscar;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEstadisticas;
    private javax.swing.JTable tblHistorial;
    private javax.swing.JTextField txtBuscarPeticiones;
    // End of variables declaration//GEN-END:variables
}