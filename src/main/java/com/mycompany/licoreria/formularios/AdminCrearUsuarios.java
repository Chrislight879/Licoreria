package com.mycompany.licoreria.formularios;

import com.mycompany.licoreria.controllers.UserController;
import com.mycompany.licoreria.models.User;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class AdminCrearUsuarios extends JInternalFrame {
    private UserController userController;

    // Componentes de la UI
    private JTextField txtUsername, txtSearch;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JComboBox<String> cmbRol;
    private JButton btnCreate, btnUpdate, btnDelete, btnClear, btnSearch;
    private JTable usersTable;
    private DefaultTableModel tableModel;

    // Variables para el usuario seleccionado
    private User selectedUser;

    // Paleta de colores azules mejorada
    private final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private final Color SECONDARY_COLOR = new Color(100, 149, 237);
    private final Color ACCENT_COLOR = new Color(30, 144, 255);
    private final Color BACKGROUND_COLOR = new Color(30, 40, 60);
    private final Color CARD_BACKGROUND = new Color(40, 55, 80);
    private final Color BORDER_COLOR = new Color(100, 130, 180);
    private final Color TEXT_WHITE = Color.WHITE;
    private final Color SUCCESS_COLOR = new Color(86, 202, 133);
    private final Color WARNING_COLOR = new Color(255, 193, 87);
    private final Color DANGER_COLOR = new Color(255, 118, 117);

    public AdminCrearUsuarios() {
        initComponents();
        setupModernDesign();
        userController = new UserController();
        loadUsersData();
        loadRoles();
    }

    private void initComponents() {
        setTitle("Gestión de Usuarios - Sistema Licorería");
        setClosable(true);
        setResizable(true);
        setMaximizable(true);
        setIconifiable(true);
        setDefaultCloseOperation(JInternalFrame.DISPOSE_ON_CLOSE);

        setSize(1200, 700);
        setLayout(new BorderLayout(10, 10));

        // Panel principal con gradiente
        JPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Header más compacto
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Content area dividida horizontalmente
        JSplitPane horizontalSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        horizontalSplitPane.setDividerLocation(500);
        horizontalSplitPane.setDividerSize(3);
        horizontalSplitPane.setBorder(BorderFactory.createEmptyBorder());
        horizontalSplitPane.setBackground(BACKGROUND_COLOR);

        // Form Panel (izquierda) con scroll
        horizontalSplitPane.setLeftComponent(createFormPanelWithScroll());

        // Table Panel (derecha)
        horizontalSplitPane.setRightComponent(createTablePanel());

        mainPanel.add(horizontalSplitPane, BorderLayout.CENTER);

        add(mainPanel);

        // Centrar en el desktop
        centrarEnDesktop();
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(0, 0, 0, 0));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));

        // Título más compacto
        JLabel titleLabel = new JLabel("👥 Gestión de Usuarios");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_WHITE);

        // Barra de búsqueda más compacta
        JPanel searchPanel = new JPanel(new BorderLayout(8, 0));
        searchPanel.setBackground(new Color(0, 0, 0, 0));

        txtSearch = new ModernTextField("Buscar usuarios...");
        txtSearch.setPreferredSize(new Dimension(200, 32));

        btnSearch = new ModernButton("🔍 Buscar", ACCENT_COLOR);
        btnSearch.setPreferredSize(new Dimension(90, 32));
        btnSearch.addActionListener(e -> searchUsers());

        // Evento de búsqueda en tiempo real
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { searchUsers(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { searchUsers(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { searchUsers(); }
        });

        searchPanel.add(txtSearch, BorderLayout.CENTER);
        searchPanel.add(btnSearch, BorderLayout.EAST);

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JScrollPane createFormPanelWithScroll() {
        JPanel formPanel = createFormPanel();

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        // Personalizar la barra de scroll
        JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
        verticalScrollBar.setPreferredSize(new Dimension(10, Integer.MAX_VALUE));
        verticalScrollBar.setBackground(CARD_BACKGROUND);
        verticalScrollBar.setForeground(ACCENT_COLOR);

        return scrollPane;
    }

    private JPanel createFormPanel() {
        JPanel formPanel = new JPanel(new BorderLayout());
        formPanel.setBackground(CARD_BACKGROUND);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Formulario de Usuario",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Panel de campos del formulario
        JPanel fieldsPanel = new JPanel(new GridBagLayout());
        fieldsPanel.setBackground(CARD_BACKGROUND);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 8, 6, 8);
        gbc.weightx = 1.0;

        // Username
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        JLabel lblUsername = new JLabel("Nombre de Usuario:");
        lblUsername.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblUsername.setForeground(TEXT_WHITE);
        fieldsPanel.add(lblUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        txtUsername = new ModernTextField("Ingrese el nombre de usuario");
        txtUsername.setPreferredSize(new Dimension(300, 36));
        fieldsPanel.add(txtUsername, gbc);

        // Contraseña
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblPassword.setForeground(TEXT_WHITE);
        fieldsPanel.add(lblPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        txtPassword = new ModernPasswordField("Ingrese la contraseña");
        txtPassword.setPreferredSize(new Dimension(300, 36));
        fieldsPanel.add(txtPassword, gbc);

        // Confirmar Contraseña
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        JLabel lblConfirmPassword = new JLabel("Confirmar Contraseña:");
        lblConfirmPassword.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblConfirmPassword.setForeground(TEXT_WHITE);
        fieldsPanel.add(lblConfirmPassword, gbc);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        txtConfirmPassword = new ModernPasswordField("Confirme la contraseña");
        txtConfirmPassword.setPreferredSize(new Dimension(300, 36));
        fieldsPanel.add(txtConfirmPassword, gbc);

        // Rol
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JLabel lblRol = new JLabel("Rol:");
        lblRol.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblRol.setForeground(TEXT_WHITE);
        fieldsPanel.add(lblRol, gbc);

        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        cmbRol = new ModernComboBox();
        cmbRol.setPreferredSize(new Dimension(300, 36));
        fieldsPanel.add(cmbRol, gbc);

        // Espacio entre campos y botones
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 8, 8, 8);
        fieldsPanel.add(Box.createVerticalStrut(10), gbc);

        // Panel de botones
        gbc.gridx = 0; gbc.gridy = 9; gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 8, 8, 8);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 8, 8));
        buttonPanel.setBackground(CARD_BACKGROUND);

        btnCreate = new ModernButton("➕ Crear Usuario", SUCCESS_COLOR);
        btnCreate.setPreferredSize(new Dimension(120, 35));
        btnCreate.addActionListener(e -> createUser());

        btnUpdate = new ModernButton("✏️ Actualizar", WARNING_COLOR);
        btnUpdate.setPreferredSize(new Dimension(120, 35));
        btnUpdate.setEnabled(false);
        btnUpdate.addActionListener(e -> updateUser());

        btnDelete = new ModernButton("🗑️ Eliminar", DANGER_COLOR);
        btnDelete.setPreferredSize(new Dimension(120, 35));
        btnDelete.setEnabled(false);
        btnDelete.addActionListener(e -> deleteUser());

        btnClear = new ModernButton("🧹 Limpiar", new Color(149, 165, 166));
        btnClear.setPreferredSize(new Dimension(120, 35));
        btnClear.addActionListener(e -> clearForm());

        buttonPanel.add(btnCreate);
        buttonPanel.add(btnUpdate);
        buttonPanel.add(btnDelete);
        buttonPanel.add(btnClear);

        fieldsPanel.add(buttonPanel, gbc);

        // Agregar espacio flexible para centrar verticalmente
        gbc.gridx = 0; gbc.gridy = 10; gbc.gridwidth = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        fieldsPanel.add(Box.createVerticalGlue(), gbc);

        formPanel.add(fieldsPanel, BorderLayout.CENTER);

        return formPanel;
    }

    private JPanel createTablePanel() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(CARD_BACKGROUND);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(SECONDARY_COLOR, 2),
                        "Lista de Usuarios",
                        0, 0,
                        new Font("Segoe UI", Font.BOLD, 14),
                        TEXT_WHITE
                ),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Modelo de tabla
        String[] columnNames = {"ID", "Usuario", "Rol", "Estado", "Fecha Creación"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        usersTable = new JTable(tableModel);
        usersTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usersTable.setRowHeight(32);
        usersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersTable.setIntercellSpacing(new Dimension(0, 0));
        usersTable.setShowGrid(false);
        usersTable.setBackground(new Color(50, 65, 95));
        usersTable.setForeground(TEXT_WHITE);
        usersTable.setGridColor(BORDER_COLOR);
        usersTable.setSelectionBackground(ACCENT_COLOR);
        usersTable.setSelectionForeground(TEXT_WHITE);

        // Header personalizado
        JTableHeader header = usersTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 12));
        header.setBackground(PRIMARY_COLOR);
        header.setForeground(TEXT_WHITE);
        header.setPreferredSize(new Dimension(header.getWidth(), 35));

        // Renderer para estado
        usersTable.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        // Ajustar anchos de columnas
        usersTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        usersTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        usersTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        usersTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        usersTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        // Scroll pane
        JScrollPane scrollPane = new JScrollPane(usersTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(new Color(50, 65, 95));

        // Listener para selección de fila
        usersTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && usersTable.getSelectedRow() != -1) {
                loadSelectedUser();
            }
        });

        // Panel de información más compacto
        JPanel infoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(CARD_BACKGROUND);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));

        JLabel infoLabel = new JLabel("💡 Seleccione un usuario para editarlo o eliminarlo");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(180, 200, 255));

        infoPanel.add(infoLabel);

        tablePanel.add(scrollPane, BorderLayout.CENTER);
        tablePanel.add(infoPanel, BorderLayout.SOUTH);

        return tablePanel;
    }

    private void loadUsersData() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return userController.getAllUsers();
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    tableModel.setRowCount(0);

                    for (User user : users) {
                        Object[] row = {
                                user.getUsuarioId(),
                                user.getUsername(),
                                user.getRolTitulo(),
                                user.isActivo() ? "Activo" : "Inactivo",
                                "2024-01-15"
                        };
                        tableModel.addRow(row);
                    }

                    updateStats(users.size());

                } catch (Exception e) {
                    showError("Error al cargar usuarios: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void loadRoles() {
        SwingWorker<List<String>, Void> worker = new SwingWorker<List<String>, Void>() {
            @Override
            protected List<String> doInBackground() throws Exception {
                return userController.getAvailableRoles();
            }

            @Override
            protected void done() {
                try {
                    List<String> roles = get();
                    cmbRol.removeAllItems();
                    for (String role : roles) {
                        cmbRol.addItem(role);
                    }
                } catch (Exception e) {
                    showError("Error al cargar roles: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void createUser() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String selectedRole = (String) cmbRol.getSelectedItem();

        // Validaciones
        if (username.isEmpty() || username.equals("Ingrese el nombre de usuario")) {
            showError("Debe ingresar un nombre de usuario válido");
            txtUsername.requestFocus();
            return;
        }

        if (password.isEmpty() || password.equals("Ingrese la contraseña")) {
            showError("Debe ingresar una contraseña");
            txtPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden");
            txtConfirmPassword.requestFocus();
            return;
        }

        if (selectedRole == null) {
            showError("Debe seleccionar un rol");
            cmbRol.requestFocus();
            return;
        }

        // Obtener ID del rol desde el string seleccionado
        int rolId = extractRoleId(selectedRole);

        // Mostrar confirmación
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>➕ Crear Usuario</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p><b>Usuario:</b> " + username + "</p>" +
                        "<p><b>Rol:</b> " + selectedRole + "</p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar Creación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Ejecutar creación
        btnCreate.setText("Creando...");
        btnCreate.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    return userController.createUser(username, password, password, rolId);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showSuccess("Usuario creado exitosamente");
                        clearForm();
                        loadUsersData();
                    }
                } catch (Exception e) {
                    showError("Error al crear usuario: " + e.getMessage());
                } finally {
                    btnCreate.setText("➕ Crear Usuario");
                    btnCreate.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void updateUser() {
        if (selectedUser == null) {
            showError("No hay usuario seleccionado para actualizar");
            return;
        }

        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPassword = new String(txtConfirmPassword.getPassword());
        String selectedRole = (String) cmbRol.getSelectedItem();

        // Validaciones básicas
        if (username.isEmpty() || username.equals("Ingrese el nombre de usuario")) {
            showError("Debe ingresar un nombre de usuario válido");
            txtUsername.requestFocus();
            return;
        }

        if (selectedRole == null) {
            showError("Debe seleccionar un rol");
            cmbRol.requestFocus();
            return;
        }

        // Validar contraseña solo si se está cambiando
        boolean cambiarPassword = !password.isEmpty() && !password.equals("Ingrese la contraseña");
        if (cambiarPassword) {
            if (!password.equals(confirmPassword)) {
                showError("Las contraseñas no coinciden");
                txtConfirmPassword.requestFocus();
                return;
            }
        }

        int rolId = extractRoleId(selectedRole);

        // Mostrar confirmación
        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #F39C12;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>✏️ Actualizar Usuario</div>" +
                        "<div style='color: #ECF0F1; text-align: left;'>" +
                        "<p><b>Usuario ID:</b> " + selectedUser.getUsuarioId() + "</p>" +
                        "<p><b>Nuevo Usuario:</b> " + username + "</p>" +
                        "<p><b>Nuevo Rol:</b> " + selectedRole + "</p>" +
                        "<p><b>Cambiar Contraseña:</b> " + (cambiarPassword ? "Sí" : "No") + "</p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar Actualización",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Ejecutar actualización
        btnUpdate.setText("Actualizando...");
        btnUpdate.setEnabled(false);

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                try {
                    // En una implementación real, aquí llamarías a un método updateUser en el controller
                    // Por ahora, simulamos la actualización eliminando y creando uno nuevo
                    userController.deleteUser(selectedUser.getUsuarioId());
                    return userController.createUser(username,
                            cambiarPassword ? password : "tempPassword123",
                            cambiarPassword ? password : "tempPassword123",
                            rolId);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage());
                }
            }

            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        showSuccess("Usuario actualizado exitosamente");
                        clearForm();
                        loadUsersData();
                    }
                } catch (Exception e) {
                    showError("Error al actualizar usuario: " + e.getMessage());
                } finally {
                    btnUpdate.setText("✏️ Actualizar");
                    btnUpdate.setEnabled(true);
                }
            }
        };

        worker.execute();
    }

    private void deleteUser() {
        if (selectedUser == null) {
            showError("Debe seleccionar un usuario para eliminar");
            return;
        }

        int userId = selectedUser.getUsuarioId();
        String username = selectedUser.getUsername();

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 10px;'>🗑️ Eliminar Usuario</div>" +
                        "<div style='color: #ECF0F1;'>" +
                        "<p>Esta acción desactivará al usuario: <b>" + username + "</b></p>" +
                        "<p><small>El usuario ya no podrá acceder al sistema.</small></p>" +
                        "</div>" +
                        "</div>" +
                        "</div></html>",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    return userController.deleteUser(userId);
                }

                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            showSuccess("Usuario eliminado exitosamente");
                            clearForm();
                            loadUsersData();
                        }
                    } catch (Exception e) {
                        showError("Error al eliminar usuario: " + e.getMessage());
                    }
                }
            };

            worker.execute();
        }
    }

    private void searchUsers() {
        String searchTerm = txtSearch.getText().trim();

        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                if (searchTerm.isEmpty()) {
                    return userController.getAllUsers();
                } else {
                    return userController.searchUsers(searchTerm);
                }
            }

            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    tableModel.setRowCount(0);

                    for (User user : users) {
                        Object[] row = {
                                user.getUsuarioId(),
                                user.getUsername(),
                                user.getRolTitulo(),
                                user.isActivo() ? "Activo" : "Inactivo",
                                "2024-01-15"
                        };
                        tableModel.addRow(row);
                    }

                    updateStats(users.size());

                } catch (Exception e) {
                    showError("Error al buscar usuarios: " + e.getMessage());
                }
            }
        };

        worker.execute();
    }

    private void loadSelectedUser() {
        int selectedRow = usersTable.getSelectedRow();
        if (selectedRow != -1) {
            int userId = (int) tableModel.getValueAt(selectedRow, 0);
            String username = (String) tableModel.getValueAt(selectedRow, 1);
            String rol = (String) tableModel.getValueAt(selectedRow, 2);
            String estado = (String) tableModel.getValueAt(selectedRow, 3);

            // Buscar el usuario completo en la lista
            List<User> allUsers = userController.getAllUsers();
            selectedUser = allUsers.stream()
                    .filter(user -> user.getUsuarioId() == userId)
                    .findFirst()
                    .orElse(null);

            if (selectedUser != null) {
                txtUsername.setText(username);
                cmbRol.setSelectedItem(rol);

                // Limpiar campos de contraseña
                txtPassword.setText("");
                txtConfirmPassword.setText("");

                // Habilitar botones de edición
                btnUpdate.setEnabled(true);
                btnDelete.setEnabled(true);
                btnCreate.setEnabled(false);
            }
        }
    }

    private void clearForm() {
        txtUsername.setText("Ingrese el nombre de usuario");
        txtPassword.setText("Ingrese la contraseña");
        txtConfirmPassword.setText("Confirme la contraseña");
        if (cmbRol.getItemCount() > 0) {
            cmbRol.setSelectedIndex(0);
        }

        usersTable.clearSelection();
        btnUpdate.setEnabled(false);
        btnDelete.setEnabled(false);
        btnCreate.setEnabled(true);
        selectedUser = null;

        txtSearch.setText("Buscar usuarios...");
        loadUsersData();
    }

    private int extractRoleId(String roleString) {
        if (roleString.contains("-")) {
            try {
                return Integer.parseInt(roleString.split("-")[0].trim());
            } catch (NumberFormatException e) {
                return 2;
            }
        }
        return 2;
    }

    private void updateStats(int userCount) {
        // Puedes implementar aquí la actualización de estadísticas si las muestras
    }

    private void setupModernDesign() {
        getRootPane().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER_COLOR, 1),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)
        ));
    }

    private void centrarEnDesktop() {
        try {
            com.mycompany.licoreria.Licoreria.centrarFormulario(this);
        } catch (Exception e) {
            // Si falla el centrado, continuar sin él
        }
    }

    // Métodos de utilidad para mensajes
    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #E74C3C;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>❌ Error</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #27AE60;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>✅ Éxito</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this,
                "<html><div style='text-align: center; padding: 10px;'>" +
                        "<div style='background: #2C3E50; padding: 15px; border-radius: 8px; border-left: 4px solid #3498DB;'>" +
                        "<div style='color: #FFFFFF; font-weight: bold; margin-bottom: 5px;'>ℹ️ Información</div>" +
                        "<div style='color: #ECF0F1;'>" + message + "</div>" +
                        "</div>" +
                        "</div></html>",
                "Información",
                JOptionPane.INFORMATION_MESSAGE);
    }

    // Clases internas para componentes modernos con tema azul
    class ModernTextField extends JTextField {
        private String placeholder;

        public ModernTextField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setCaretColor(TEXT_WHITE);
            setOpaque(true);

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (getText().equals(placeholder)) {
                        setText("");
                        setForeground(TEXT_WHITE);
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getText().isEmpty()) {
                        setText(placeholder);
                        setForeground(new Color(200, 220, 255));
                    }
                }
            });

            setText(placeholder);
            setForeground(new Color(200, 220, 255));
        }
    }

    class ModernPasswordField extends JPasswordField {
        private String placeholder;

        public ModernPasswordField(String placeholder) {
            this.placeholder = placeholder;
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setCaretColor(TEXT_WHITE);
            setOpaque(true);
            setEchoChar('•');

            addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (new String(getPassword()).equals(placeholder)) {
                        setText("");
                        setForeground(TEXT_WHITE);
                        setEchoChar('•');
                    }
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (getPassword().length == 0) {
                        setText(placeholder);
                        setForeground(new Color(200, 220, 255));
                        setEchoChar((char) 0);
                    }
                }
            });

            setText(placeholder);
            setForeground(new Color(200, 220, 255));
            setEchoChar((char) 0);
        }
    }

    class ModernComboBox extends JComboBox<String> {
        public ModernComboBox() {
            setFont(new Font("Segoe UI", Font.PLAIN, 13));
            setBackground(new Color(50, 65, 95));
            setForeground(TEXT_WHITE);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(BORDER_COLOR, 1),
                    BorderFactory.createEmptyBorder(8, 12, 8, 12)
            ));
            setRenderer(new ModernComboBoxRenderer());
        }
    }

    class ModernComboBoxRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            label.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            if (isSelected) {
                label.setBackground(ACCENT_COLOR);
                label.setForeground(TEXT_WHITE);
            } else {
                label.setBackground(new Color(50, 65, 95));
                label.setForeground(TEXT_WHITE);
            }

            return label;
        }
    }

    class ModernButton extends JButton {
        private Color originalColor;

        public ModernButton(String text, Color color) {
            super(text);
            this.originalColor = color;

            setFont(new Font("Segoe UI", Font.BOLD, 11));
            setBackground(color);
            setForeground(TEXT_WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor.darker());
                    }
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (isEnabled()) {
                        setBackground(originalColor);
                    }
                }
            });
        }
    }

    class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Segoe UI", Font.BOLD, 10));
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));

            if ("Activo".equals(value)) {
                label.setBackground(new Color(86, 202, 133, 50));
                label.setForeground(SUCCESS_COLOR);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(SUCCESS_COLOR, 1),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7)
                ));
            } else {
                label.setBackground(new Color(255, 118, 117, 50));
                label.setForeground(DANGER_COLOR);
                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DANGER_COLOR, 1),
                        BorderFactory.createEmptyBorder(3, 7, 3, 7)
                ));
            }

            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }

            return label;
        }
    }

    // Clase para el fondo con gradiente
    class GradientPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Gradiente azul oscuro moderno
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(30, 40, 60),
                    getWidth(), getHeight(), new Color(50, 70, 100)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Elementos decorativos sutiles
            g2d.setColor(new Color(255, 255, 255, 10));
            g2d.fillOval(-50, -50, 150, 150);
            g2d.fillOval(getWidth() - 100, getHeight() - 100, 200, 200);
        }
    }
}