package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.PeticionVendedorController;
import com.mycompany.licoreria.models.Producto;
import com.mycompany.licoreria.models.PeticionVendedor;
import com.mycompany.licoreria.utils.SessionManager;
import com.mycompany.licoreria.utils.StockUtils;
import com.mycompany.licoreria.utils.DateUtils;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class VendedorPedirForm extends javax.swing.JInternalFrame {
    private PeticionVendedorController peticionController;
    private DefaultTableModel tableModelInventarioBodega;
    private DefaultTableModel tableModelInventarioVenta;
    private DefaultTableModel tableModelPeticionesEnviadas;
    private DefaultTableModel tableModelPeticiones;
    private List<PeticionVendedor> peticionesTemporales;
    private int usuarioId;

    public VendedorPedirForm() {
        initComponents();
        peticionController = new PeticionVendedorController();
        peticionesTemporales = new ArrayList<>();
        usuarioId = SessionManager.getCurrentUser() != null ?
                SessionManager.getCurrentUser().getUsuarioId() : 1;
        initializeTables();
        loadAllData();
        setTitle("Solicitudes de Stock - Vendedor");
    }

    private void initializeTables() {
        // Tabla de inventario en bodega
        tableModelInventarioBodega = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Stock Bodega", "Mínimo", "Unidad"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInventarioBodega.setModel(tableModelInventarioBodega);

        // Tabla de inventario del vendedor
        tableModelInventarioVenta = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Stock Vendedor", "Mínimo", "Unidad", "Estado"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblInventarioVenta.setModel(tableModelInventarioVenta);

        // Tabla de peticiones temporales (para enviar)
        tableModelPeticionesEnviadas = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Cantidad", "Unidad", "Observaciones", "Eliminar"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Solo la columna de eliminar es editable
            }
        };
        tblPeticionesEnviadas.setModel(tableModelPeticionesEnviadas);

        // Tabla de peticiones enviadas (historial)
        tableModelPeticiones = new DefaultTableModel(
                new Object[][]{},
                new String[]{"ID", "Producto", "Cantidad", "Fecha", "Estado", "Observaciones"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblPeticiones.setModel(tableModelPeticiones);
    }

    private void loadAllData() {
        loadInventarioBodega();
        loadInventarioVendedor();
        loadPeticionesEnviadas();
        loadPeticionesHistorial();
        updateEstadisticas();
    }

    private void loadInventarioBodega() {
        try {
            tableModelInventarioBodega.setRowCount(0);
            List<Producto> productos = peticionController.getInventarioBodega();

            for (Producto producto : productos) {
                String estadoStock = StockUtils.getNivelStock(
                        producto.getStockBodega(), producto.getCantidadMinimaBodega());

                tableModelInventarioBodega.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
                        producto.getUnidadMedida()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario de bodega: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadInventarioVendedor() {
        try {
            tableModelInventarioVenta.setRowCount(0);
            List<Producto> productos = peticionController.getInventarioVendedor();

            for (Producto producto : productos) {
                String estadoStock = StockUtils.getNivelStock(
                        producto.getStockVendedor(), producto.getCantidadMinimaVendedor());

                tableModelInventarioVenta.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockVendedor()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaVendedor()),
                        producto.getUnidadMedida(),
                        estadoStock
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar inventario del vendedor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void loadPeticionesEnviadas() {
        tableModelPeticionesEnviadas.setRowCount(0);
        for (PeticionVendedor peticion : peticionesTemporales) {
            tableModelPeticionesEnviadas.addRow(new Object[]{
                    peticion.getProductoId(),
                    peticion.getProductoNombre(),
                    StockUtils.formatCantidad(peticion.getCantidadSolicitada()),
                    peticion.getUnidadMedida(),
                    peticion.getObservaciones(),
                    "ELIMINAR"
            });
        }
    }

    private void loadPeticionesHistorial() {
        try {
            tableModelPeticiones.setRowCount(0);
            List<PeticionVendedor> peticiones = peticionController.getPeticionesPorVendedor(usuarioId);

            for (PeticionVendedor peticion : peticiones) {
                tableModelPeticiones.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
                        StockUtils.formatCantidad(peticion.getCantidadSolicitada()),
                        DateUtils.formatDateForDisplay(new java.sql.Date(peticion.getFechaSolicitud().getTime())),
                        peticion.getEstado().toUpperCase(),
                        peticion.getObservaciones()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al cargar historial de peticiones: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateEstadisticas() {
        try {
            String estadisticas = peticionController.getEstadisticasPeticiones(usuarioId);
            // Puedes mostrar esto en una etiqueta si agregas una
            System.out.println("Estadísticas: " + estadisticas);
        } catch (Exception e) {
            // No mostrar error para estadísticas
        }
    }

    private void buscarProductosBodega() {
        try {
            String searchTerm = txtBuscarBodega.getText().trim();
            tableModelInventarioBodega.setRowCount(0);

            List<Producto> productos = peticionController.buscarProductosBodega(searchTerm);

            for (Producto producto : productos) {
                String estadoStock = StockUtils.getNivelStock(
                        producto.getStockBodega(), producto.getCantidadMinimaBodega());

                tableModelInventarioBodega.addRow(new Object[]{
                        producto.getProductoId(),
                        producto.getNombre(),
                        StockUtils.formatCantidad(producto.getStockBodega()),
                        StockUtils.formatCantidad(producto.getCantidadMinimaBodega()),
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

    private void buscarPeticiones() {
        try {
            String searchTerm = txtBuscarPeticiones.getText().trim();
            tableModelPeticiones.setRowCount(0);

            List<PeticionVendedor> peticiones = peticionController.buscarPeticiones(usuarioId, searchTerm);

            for (PeticionVendedor peticion : peticiones) {
                tableModelPeticiones.addRow(new Object[]{
                        peticion.getPeticionId(),
                        peticion.getProductoNombre(),
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

    private void agregarPeticion() {
        int selectedRow = tblInventarioBodega.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione un producto de la bodega para solicitar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productoId = (int) tableModelInventarioBodega.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelInventarioBodega.getValueAt(selectedRow, 1);
            double stockBodega = Double.parseDouble(
                    ((String) tableModelInventarioBodega.getValueAt(selectedRow, 2)).replace(",", "")
            );
            String unidadMedida = (String) tableModelInventarioBodega.getValueAt(selectedRow, 4);

            // Verificar si ya existe una petición temporal para este producto
            boolean yaExiste = peticionesTemporales.stream()
                    .anyMatch(p -> p.getProductoId() == productoId);

            if (yaExiste) {
                JOptionPane.showMessageDialog(this,
                        "Ya existe una petición para este producto en la lista temporal",
                        "Advertencia",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Pedir cantidad
            String cantidadStr = JOptionPane.showInputDialog(this,
                    "Solicitar producto:\n\n" +
                            "Producto: " + productoNombre + "\n" +
                            "Stock en bodega: " + stockBodega + " " + unidadMedida + "\n\n" +
                            "Ingrese la cantidad a solicitar:",
                    "Solicitar Producto",
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

                if (cantidad > stockBodega) {
                    JOptionPane.showMessageDialog(this,
                            "Stock insuficiente en bodega. Disponible: " + stockBodega,
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Pedir observaciones
                String observaciones = JOptionPane.showInputDialog(this,
                        "Observaciones para la solicitud:",
                        "Observaciones",
                        JOptionPane.QUESTION_MESSAGE);

                if (observaciones != null) {
                    // Crear petición temporal
                    PeticionVendedor peticion = new PeticionVendedor();
                    peticion.setProductoId(productoId);
                    peticion.setProductoNombre(productoNombre);
                    peticion.setCantidadSolicitada(cantidad);
                    peticion.setUnidadMedida(unidadMedida);
                    peticion.setObservaciones(observaciones.trim());

                    peticionesTemporales.add(peticion);
                    loadPeticionesEnviadas();

                    JOptionPane.showMessageDialog(this,
                            "Producto agregado a la lista de peticiones",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "La cantidad debe ser un número válido",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al agregar petición: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPeticionTemporal() {
        int selectedRow = tblPeticionesEnviadas.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una petición temporal para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int productoId = (int) tableModelPeticionesEnviadas.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelPeticionesEnviadas.getValueAt(selectedRow, 1);

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar petición para: " + productoNombre + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                peticionesTemporales.removeIf(p -> p.getProductoId() == productoId);
                loadPeticionesEnviadas();

                JOptionPane.showMessageDialog(this,
                        "Petición eliminada de la lista temporal",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al eliminar petición: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarPeticiones() {
        if (peticionesTemporales.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No hay peticiones para enviar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Enviar " + peticionesTemporales.size() + " peticiones a bodega?",
                    "Confirmar envío",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                int exitosas = 0;
                int fallidas = 0;

                for (PeticionVendedor peticion : peticionesTemporales) {
                    try {
                        boolean success = peticionController.crearPeticion(
                                peticion.getProductoId(),
                                usuarioId,
                                peticion.getCantidadSolicitada(),
                                peticion.getObservaciones()
                        );

                        if (success) {
                            exitosas++;
                        } else {
                            fallidas++;
                        }
                    } catch (Exception e) {
                        fallidas++;
                        System.err.println("Error enviando petición: " + e.getMessage());
                    }
                }

                // Limpiar lista temporal
                peticionesTemporales.clear();
                loadPeticionesEnviadas();
                loadPeticionesHistorial();

                JOptionPane.showMessageDialog(this,
                        "Peticiones enviadas:\n" +
                                "Exitosas: " + exitosas + "\n" +
                                "Fallidas: " + fallidas,
                        "Resultado del envío",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error al enviar peticiones: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void eliminarPeticionHistorial() {
        int selectedRow = tblPeticiones.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Seleccione una petición del historial para eliminar",
                    "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int peticionId = (int) tableModelPeticiones.getValueAt(selectedRow, 0);
            String productoNombre = (String) tableModelPeticiones.getValueAt(selectedRow, 1);
            String estado = (String) tableModelPeticiones.getValueAt(selectedRow, 4);

            // Solo permitir eliminar peticiones pendientes
            if (!"PENDIENTE".equals(estado)) {
                JOptionPane.showMessageDialog(this,
                        "Solo se pueden eliminar peticiones con estado PENDIENTE",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar petición #" + peticionId + " para: " + productoNombre + "?",
                    "Confirmar eliminación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = peticionController.eliminarPeticion(peticionId);

                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Petición eliminada exitosamente",
                            "Éxito",
                            JOptionPane.INFORMATION_MESSAGE);
                    loadPeticionesHistorial();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Error al eliminar la petición",
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
                    "Error al eliminar petición: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        tblInventarioBodega = new javax.swing.JTable();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblInventarioVenta = new javax.swing.JTable();
        txtBuscarBodega = new javax.swing.JTextField();
        txtBuscarInventario = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        tblPeticionesEnviadas = new javax.swing.JTable();
        jLabel9 = new javax.swing.JLabel();
        btnEliminarProductoPeticion = new javax.swing.JButton();
        btnEnviarPeticion = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblPeticiones = new javax.swing.JTable();
        txtBuscarPeticiones = new javax.swing.JTextField();
        btnEliminarPeticion = new javax.swing.JButton();
        btnPedir = new javax.swing.JButton();
        btnRefrescar = new javax.swing.JButton();

        setClosable(true);
        setIconifiable(true);
        setMaximizable(true);
        setResizable(true);
        setTitle("Solicitudes de Stock - Vendedor");

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
        jScrollPane1.setViewportView(tblInventarioBodega);

        jLabel7.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel7.setText("INVENTARIO DEL VENDEDOR");

        jLabel8.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel8.setText("PETICIONES TEMPORALES");

        tblInventarioVenta.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(tblInventarioVenta);

        txtBuscarBodega.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarBodega.setText("Buscar en Bodega...");
        txtBuscarBodega.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarBodegaActionPerformed(evt);
            }
        });

        txtBuscarInventario.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarInventario.setText("Buscar en Inventario...");
        txtBuscarInventario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarInventarioActionPerformed(evt);
            }
        });

        tblPeticionesEnviadas.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane3.setViewportView(tblPeticionesEnviadas);

        jLabel9.setFont(new java.awt.Font("Liberation Sans", 1, 16)); // NOI18N
        jLabel9.setText("INVENTARIO EN BODEGA");

        btnEliminarProductoPeticion.setBackground(new java.awt.Color(255, 51, 102));
        btnEliminarProductoPeticion.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEliminarProductoPeticion.setText("ELIMINAR");
        btnEliminarProductoPeticion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarProductoPeticionActionPerformed(evt);
            }
        });

        btnEnviarPeticion.setBackground(new java.awt.Color(102, 255, 102));
        btnEnviarPeticion.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEnviarPeticion.setText("ENVIAR TODAS");
        btnEnviarPeticion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEnviarPeticionActionPerformed(evt);
            }
        });

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
        jScrollPane4.setViewportView(tblPeticiones);

        txtBuscarPeticiones.setFont(new java.awt.Font("Liberation Sans", 0, 14)); // NOI18N
        txtBuscarPeticiones.setText("Buscar en Peticiones...");
        txtBuscarPeticiones.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtBuscarPeticionesActionPerformed(evt);
            }
        });

        btnEliminarPeticion.setBackground(new java.awt.Color(255, 51, 102));
        btnEliminarPeticion.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnEliminarPeticion.setText("ELIMINAR");
        btnEliminarPeticion.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarPeticionActionPerformed(evt);
            }
        });

        btnPedir.setBackground(new java.awt.Color(51, 153, 255));
        btnPedir.setFont(new java.awt.Font("Liberation Sans", 1, 14)); // NOI18N
        btnPedir.setText("SOLICITAR");
        btnPedir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPedirActionPerformed(evt);
            }
        });

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
                                .addGap(17, 17, 17)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 345, Short.MAX_VALUE)
                                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtBuscarBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnPedir, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnEliminarPeticion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addGap(0, 0, Short.MAX_VALUE)
                                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addComponent(btnEnviarPeticion, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(380, 380, 380))
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addComponent(btnEliminarProductoPeticion, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                                .addGap(380, 380, 380))
                                                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                                                .addComponent(jLabel8)
                                                                                .addGap(282, 282, 282))))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 592, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(0, 0, Short.MAX_VALUE))))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(txtBuscarInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(jLabel7))
                                                .addGap(0, 0, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabel9)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 343, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(21, 21, 21))))
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(24, 24, 24)
                                                .addComponent(jLabel8)
                                                .addGap(206, 206, 206)
                                                .addComponent(btnEliminarProductoPeticion, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18)
                                                .addComponent(btnEnviarPeticion, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(18, 18, 18))
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addContainerGap()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(txtBuscarBodega, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                                .addComponent(btnPedir, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnRefrescar, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(6, 6, 6)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(15, 15, 15))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(jLabel7)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(txtBuscarInventario, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                        .addGroup(layout.createSequentialGroup()
                                                                .addComponent(txtBuscarPeticiones, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                                .addGap(18, 18, 18)
                                                                .addComponent(btnEliminarPeticion, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                                .addContainerGap(12, Short.MAX_VALUE))))
                        .addGroup(layout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel9)
                                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 306, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addContainerGap(378, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void txtBuscarBodegaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarBodegaActionPerformed
        buscarProductosBodega();
    }//GEN-LAST:event_txtBuscarBodegaActionPerformed

    private void txtBuscarInventarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarInventarioActionPerformed
        // Buscar en inventario del vendedor (puedes implementar esto si lo necesitas)
    }//GEN-LAST:event_txtBuscarInventarioActionPerformed

    private void txtBuscarPeticionesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtBuscarPeticionesActionPerformed
        buscarPeticiones();
    }//GEN-LAST:event_txtBuscarPeticionesActionPerformed

    private void btnPedirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPedirActionPerformed
        agregarPeticion();
    }//GEN-LAST:event_btnPedirActionPerformed

    private void btnEliminarProductoPeticionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarProductoPeticionActionPerformed
        eliminarPeticionTemporal();
    }//GEN-LAST:event_btnEliminarProductoPeticionActionPerformed

    private void btnEnviarPeticionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEnviarPeticionActionPerformed
        enviarPeticiones();
    }//GEN-LAST:event_btnEnviarPeticionActionPerformed

    private void btnEliminarPeticionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarPeticionActionPerformed
        eliminarPeticionHistorial();
    }//GEN-LAST:event_btnEliminarPeticionActionPerformed

    private void btnRefrescarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefrescarActionPerformed
        loadAllData();
    }//GEN-LAST:event_btnRefrescarActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnEliminarPeticion;
    private javax.swing.JButton btnEliminarProductoPeticion;
    private javax.swing.JButton btnEnviarPeticion;
    private javax.swing.JButton btnPedir;
    private javax.swing.JButton btnRefrescar;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTable tblInventarioBodega;
    private javax.swing.JTable tblInventarioVenta;
    private javax.swing.JTable tblPeticiones;
    private javax.swing.JTable tblPeticionesEnviadas;
    private javax.swing.JTextField txtBuscarBodega;
    private javax.swing.JTextField txtBuscarInventario;
    private javax.swing.JTextField txtBuscarPeticiones;
    // End of variables declaration//GEN-END:variables
}