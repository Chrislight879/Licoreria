package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.PedidoBodegaController;
import com.mycompany.licoreria.models.PedidoBodega;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.utils.StockUtils;
import com.mycompany.licoreria.utils.DateUtils;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class BodegaPedirProductos extends javax.swing.JInternalFrame {
    private PedidoBodegaController pedidoController;
    private DefaultTableModel tableModelInventario;
    private DefaultTableModel tableModelPedidosPendientes;
    private DefaultTableModel tableModelPedidosEnviados;

    // ID del usuario actual (debería venir del login)
    private int usuarioActualId = 2; // Por defecto usuario vendedor

    public BodegaPedirProductos() {
        initComponents();
        pedidoController = new PedidoBodegaController();
        initializeTables();
        loadAllData();
        setTitle("Solicitar Productos a Bodega");
    }

    private void initializeTables() {
        // Tabla de inventario bodega
        tableModelInventario = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Stock Bodega", "Mínimo", "Unidad", "Disponible"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInventario.setModel(tableModelInventario);

        // Tabla de pedidos pendientes (lista temporal)
        tableModelPedidosPendientes = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Cantidad", "Unidad", "Stock Disponible"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPedidosPendientes.setModel(tableModelPedidosPendientes);

        // Tabla de pedidos enviados
        tableModelPedidosEnviados = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Cantidad", "Fecha", "Estado", "Observaciones"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPedidosEnviados.setModel(tableModelPedidosEnviados);
    }

    private void loadAllData() {
        loadInventarioBodega();
        loadPedidosEnviados();
        updateEstadisticas();
    }

    private void loadInventarioBodega() {
        try {
            tableModelInventario.setRowCount(0);
            List<Producto> inventario = pedidoController.getInventarioBodega();

            for (Producto producto : inventario) {
                boolean disponible = producto.getStockBodega() > 0;

                tableModelInventario.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        producto.getUnidadMedida(),
                        disponible ? "SÍ" : "NO"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPedidosEnviados() {
        try {
            tableModelPedidosEnviados.setRowCount(0);
            List<PedidoBodega> pedidos = pedidoController.getPedidosEnviados(usuarioActualId);

            for (PedidoBodega pedido : pedidos) {
                tableModelPedidosEnviados.addRow(new Object[]{
                        pedido.getPedidoId(),
                        pedido.getProductoNombre(),
                        StockUtils.formatCantidad(pedido.getCantidadSolicitada()) + " " + pedido.getUnidadMedida(),
                        DateUtils.formatDateForDisplay(new java.sql.Date(pedido.getFechaSolicitud().getTime())),
                        pedido.getEstado().toUpperCase(),
                        pedido.getObservaciones()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar pedidos enviados: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEstadisticas() {
        try {
            String estadisticas = pedidoController.getEstadisticasPedidos(usuarioActualId);
            lblTotal.setText(estadisticas);
        } catch (Exception e) {
            lblTotal.setText("Error al cargar estadísticas");
        }
    }

    private void buscarInventario() {
        try {
            String searchTerm = txtBuscarBodega.getText().trim();
            tableModelInventario.setRowCount(0);

            List<Producto> inventario = pedidoController.searchInventarioBodega(searchTerm);

            for (Producto producto : inventario) {
                boolean disponible = producto.getStockBodega() > 0;

                tableModelInventario.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        producto.getUnidadMedida(),
                        disponible ? "SÍ" : "NO"
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar inventario: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPedidosEnviados() {
        try {
            String searchTerm = txtBuscarPeticiones.getText().trim();
            tableModelPedidosEnviados.setRowCount(0);

            List<PedidoBodega> pedidos = pedidoController.searchPedidos(searchTerm, usuarioActualId);

            for (PedidoBodega pedido : pedidos) {
                tableModelPedidosEnviados.addRow(new Object[]{
                        pedido.getPedidoId(),
                        pedido.getProductoNombre(),
                        StockUtils.formatCantidad(pedido.getCantidadSolicitada()) + " " + pedido.getUnidadMedida(),
                        DateUtils.formatDateForDisplay(new java.sql.Date(pedido.getFechaSolicitud().getTime())),
                        pedido.getEstado().toUpperCase(),
                        pedido.getObservaciones()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al buscar pedidos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void agregarPedidoLista() {
        int selectedRow = tblInventario.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto del inventario",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productoId = (int) tableModelInventario.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelInventario.getValueAt(selectedRow, 1);
            double stockBodega = Double.parseDouble(
                    ((String) tableModelInventario.getValueAt(selectedRow, 2)).replace(",", "")
            );
            String unidad = (String) tableModelInventario.getValueAt(selectedRow, 4);

            // Verificar stock disponible
            if (stockBodega <= 0) {
                JOptionPane.showMessageDialog(this,
                        "No hay stock disponible para: " + productoNombre,
                        "Stock no disponible",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String cantidadStr = JOptionPane.showInputDialog(this,
                    "Cantidad a solicitar de: " + productoNombre + "\n" +
                            "Stock disponible: " + stockBodega + " " + unidad + "\n" +
                            "Unidad: " + unidad,
                    "Solicitar Producto",
                    JOptionPane.QUESTION_MESSAGE);

            if (cantidadStr != null && !cantidadStr.trim().isEmpty()) {
                double cantidad = Double.parseDouble(cantidadStr);

                // Validar cantidad
                if (cantidad <= 0) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad debe ser mayor a 0",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (cantidad > stockBodega) {
                    JOptionPane.showMessageDialog(this,
                            "La cantidad solicitada excede el stock disponible\n" +
                                    "Solicitado: " + cantidad + " | Disponible: " + stockBodega,
                            "Stock insuficiente",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }

                // Agregar a la lista temporal
                tableModelPedidosPendientes.addRow(new Object[]{
                        productoId,
                        productoNombre,
                        StockUtils.formatCantidad(cantidad),
                        unidad,
                        StockUtils.formatCantidad(stockBodega)
                });

                JOptionPane.showMessageDialog(this,
                        "Producto agregado a la lista de pedidos",
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

    private void enviarPedidos() {
        if (tableModelPedidosPendientes.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "No hay pedidos en la lista para enviar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Confirmar envío de " + tableModelPedidosPendientes.getRowCount() + " pedido(s)?",
                    "Confirmar Envío",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean todosEnviados = true;
                int pedidosEnviados = 0;

                for (int i = 0; i < tableModelPedidosPendientes.getRowCount(); i++) {
                    int productoId = (int) tableModelPedidosPendientes.getValueAt(i, 0);
                    String productoNombre = (String) tableModelPedidosPendientes.getValueAt(i, 1);
                    double cantidad = Double.parseDouble(
                            ((String) tableModelPedidosPendientes.getValueAt(i, 2)).replace(",", "")
                    );

                    String observaciones = JOptionPane.showInputDialog(this,
                            "Observaciones para: " + productoNombre,
                            "Observaciones del Pedido",
                            JOptionPane.QUESTION_MESSAGE);

                    if (observaciones != null) {
                        boolean success = pedidoController.crearPedidoBodega(
                                productoId, usuarioActualId, cantidad, observaciones);

                        if (success) {
                            pedidosEnviados++;
                        } else {
                            todosEnviados = false;
                        }
                    } else {
                        todosEnviados = false;
                        break;
                    }
                }

                if (todosEnviados) {
                    JOptionPane.showMessageDialog(this,
                            pedidosEnviados + " pedido(s) enviados exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    // Limpiar lista temporal
                    tableModelPedidosPendientes.setRowCount(0);
                    loadPedidosEnviados();
                    updateEstadisticas();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Algunos pedidos no pudieron ser enviados",
                            "Error parcial",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this,
                    e.getMessage(),
                    "Error de validación",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al enviar pedidos: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPedidoLista() {
        int selectedRow = tblPedidosPendientes.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un pedido de la lista para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "¿Eliminar este pedido de la lista?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            tableModelPedidosPendientes.removeRow(selectedRow);
        }
    }

    private void eliminarPedidoEnviado() {
        int selectedRow = tblPedidosEnviados.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un pedido enviado para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int pedidoId = (int) tableModelPedidosEnviados.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelPedidosEnviados.getValueAt(selectedRow, 1);
            String estado = (String) tableModelPedidosEnviados.getValueAt(selectedRow, 4);

            // Solo se pueden cancelar pedidos pendientes
            if (!"PENDIENTE".equals(estado)) {
                JOptionPane.showMessageDialog(this,
                        "Solo se pueden cancelar pedidos en estado PENDIENTE",
                        "No se puede cancelar",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Cancelar pedido de: " + productoNombre + "?",
                    "Confirmar Cancelación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = pedidoController.eliminarPedido(pedidoId, usuarioActualId);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Pedido cancelado exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadPedidosEnviados();
                    updateEstadisticas();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al cancelar el pedido",
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
                    "Error al eliminar pedido: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblInventario = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        txtBuscarBodega = new javax.swing.JTextField();
        btnPedir = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblPedidosPendientes = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        btnEnviar = new javax.swing.JButton();
        btnEliminarLista = new javax.swing.JButton();
        btnEliminarInventario = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPedidosEnviados = new javax.swing.JTable();
        btnEliminarEnviado = new javax.swing.JButton();
        txtBuscarPeticiones = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        btnRefrescar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Solicitar Productos a Bodega");

        tblInventario.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane1.setViewportView(tblInventario);

        jLabel1.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel1.setText("LISTA DE PEDIDOS");

        txtBuscarBodega.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarBodega.setText("Buscar en Bodega...");
        txtBuscarBodega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarBodegaActionPerformed(evt);
            }
        });

        btnPedir.setBackground(new java.awt.Color(102, 255, 102));
        btnPedir.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnPedir.setText("AGREGAR A LISTA");
        btnPedir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedirActionPerformed(evt);
            }
        });

        tblPedidosPendientes.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblPedidosPendientes);

        jLabel2.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel2.setText("INVENTARIO BODEGA");

        btnEnviar.setBackground(new java.awt.Color(102, 255, 102));
        btnEnviar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEnviar.setText("ENVIAR PEDIDOS");
        btnEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarActionPerformed(evt);
            }
        });

        btnEliminarLista.setBackground(new java.awt.Color(255, 0, 51));
        btnEliminarLista.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEliminarLista.setText("ELIMINAR DE LISTA");
        btnEliminarLista.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarListaActionPerformed(evt);
            }
        });

        btnEliminarInventario.setBackground(new java.awt.Color(255, 0, 51));
        btnEliminarInventario.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEliminarInventario.setText("LIMPIAR BÚSQUEDA");
        btnEliminarInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarInventarioActionPerformed(evt);
            }
        });

        tblPedidosEnviados.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblPedidosEnviados);

        btnEliminarEnviado.setBackground(new java.awt.Color(255, 0, 51));
        btnEliminarEnviado.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEliminarEnviado.setText("CANCELAR PEDIDO");
        btnEliminarEnviado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarEnviadoActionPerformed(evt);
            }
        });

        txtBuscarPeticiones.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarPeticiones.setText("Buscar en Peticiones...");
        txtBuscarPeticiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarPeticionesActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel3.setText("PEDIDOS ENVIADOS");

        jLabel4.setFont(new java.awt.Font("Liberation Sans", 0, 24)); // NOI18N
        jLabel4.setText("TOTAL:");

        jLabel5.setFont(new java.awt.Font("Liberation Sans", 0, 18)); // NOI18N
        jLabel5.setText("Estadísticas:");

        lblTotal.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        lblTotal.setText("Cargando estadísticas...");

        btnRefrescar.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnRefrescar.setText("REFRESCAR");
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
                                .addGap(23, 23, 23)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel3)
                                                        .addComponent(btnEliminarEnviado, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addComponent(jLabel5)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 600, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                                        .addComponent(jLabel2)
                                                                        .addGroup(layout.createSequentialGroup()
                                                                                .addComponent(txtBuscarBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 188, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                                .addComponent(btnPedir, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(18, 18, 18)
                                                                                .addComponent(btnEliminarInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                                                .addGap(81, 81, 81)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(btnEliminarLista, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                        .addComponent(jLabel4))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addComponent(jLabel1)
                                                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 439, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                                .addGap(29, 29, 29))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addGap(26, 26, 26)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                        .addComponent(jLabel2)
                                        .addComponent(jLabel1)
                                        .addComponent(jLabel4))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(btnEliminarLista, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btnEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                                        .addComponent(jLabel5)
                                                                        .addComponent(lblTotal)
                                                                        .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addGap(20, 20, 20))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(18, 18, 18)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(btnPedir, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEliminarInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtBuscarBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 313, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, Short.MAX_VALUE)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel3)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(btnEliminarEnviado, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarBodegaActionPerformed
        buscarInventario();
    }//GEN-LAST:event_txtBuscarBodegaActionPerformed

    private void txtBuscarPeticionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarPeticionesActionPerformed
        buscarPedidosEnviados();
    }//GEN-LAST:event_txtBuscarPeticionesActionPerformed

    private void btnEliminarInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarInventarioActionPerformed
        // Limpiar búsqueda
        txtBuscarBodega.setText("");
        loadInventarioBodega();
    }//GEN-LAST:event_btnEliminarInventarioActionPerformed

    private void btnPedirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedirActionPerformed
        agregarPedidoLista();
    }//GEN-LAST:event_btnPedirActionPerformed

    private void btnEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarActionPerformed
        enviarPedidos();
    }//GEN-LAST:event_btnEnviarActionPerformed

    private void btnEliminarListaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarListaActionPerformed
        eliminarPedidoLista();
    }//GEN-LAST:event_btnEliminarListaActionPerformed

    private void btnEliminarEnviadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarEnviadoActionPerformed
        eliminarPedidoEnviado();
    }//GEN-LAST:event_btnEliminarEnviadoActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        loadAllData();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminarEnviado;
    private javax.swing.JButton btnEliminarInventario;
    private javax.swing.JButton btnEliminarLista;
    private javax.swing.JButton btnEnviar;
    private javax.swing.JButton btnPedir;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JTable tblInventario;
    private javax.swing.JTable tblPedidosEnviados;
    private javax.swing.JTable tblPedidosPendientes;
    private javax.swing.JTextField txtBuscarBodega;
    private javax.swing.JTextField txtBuscarPeticiones;
    // End of variables declaration//GEN-END:variables
}