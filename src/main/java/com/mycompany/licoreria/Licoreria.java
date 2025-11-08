package com.mycompany.licoreria;

import com.mycompany.licoreria.formularios.*;
import javax.swing.JFrame;
import javax.swing.JDesktopPane;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.Color;
import java.awt.Font;

public class Licoreria {
    private static JFrame frame;
    private static JDesktopPane desktopPane;

    public static void main(String[] args) {
        // Establecer el look and feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getLookAndFeel());
        } catch (Exception e) {
            System.err.println("Error al establecer el look and feel: " + e.getMessage());
        }

        SwingUtilities.invokeLater(() -> {
            createAndShowGUI();
        });
    }

    private static void createAndShowGUI() {
        // Crear ventana principal
        frame = new JFrame("Sistema de Gestión - Licorería");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });

        // Obtener dimensiones de la pantalla
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setSize((int)(screenSize.width * 0.9), (int)(screenSize.height * 0.9));
        frame.setLocationRelativeTo(null);

        // Configurar icono de la aplicación
        try {
            // Puedes agregar un icono aquí si tienes uno
            // frame.setIconImage(new ImageIcon("ruta/al/icono.png").getImage());
        } catch (Exception e) {
            System.err.println("Error al cargar el icono: " + e.getMessage());
        }

        // Crear desktop pane con fondo
        desktopPane = new JDesktopPane();
        setupDesktopPaneBackground();
        frame.setContentPane(desktopPane);

        // Crear menú
        createMenu();

        // Mostrar ventana
        frame.setVisible(true);

        // Mostrar mensaje de bienvenida
        mostrarMensajeBienvenida();
    }

    private static void setupDesktopPaneBackground() {
        // Crear un label con mensaje de bienvenida centrado
        JLabel lblBienvenida = new JLabel(
                "<html><div style='text-align: center;'>"
                        + "<h1 style='color: #2E4053; font-size: 36px;'>Sistema de Gestión - Licorería</h1>"
                        + "<br>"
                        + "<p style='color: #566573; font-size: 18px;'>Bienvenido al sistema integral de gestión</p>"
                        + "<br>"
                        + "<div style='background: #F8F9F9; padding: 20px; border-radius: 10px; margin: 20px;'>"
                        + "<h3 style='color: #2E4053;'>Módulos Disponibles:</h3>"
                        + "<ul style='text-align: left; color: #566573; font-size: 14px;'>"
                        + "<li><b>🔐 Sistema de Login</b> - Autenticación segura</li>"
                        + "<li><b>👥 Gestión de Usuarios</b> - Administración completa</li>"
                        + "<li><b>📊 Panel de Administración</b> - Control total del sistema</li>"
                        + "<li><b>📋 Historial del Sistema</b> - Registro de actividades</li>"
                        + "<li><b>📦 Módulo de Bodega</b> - Gestión de inventario</li>"
                        + "<li><b>🛒 Punto de Venta</b> - Sistema de ventas completo</li>"
                        + "<li><b>📮 Solicitar Stock</b> - Peticiones a bodega</li>"
                        + "<li><b>📈 Peticiones de Stock</b> - Gestión de solicitudes</li>"
                        + "</ul>"
                        + "</div>"
                        + "<p style='color: #85929E; font-size: 12px;'>Seleccione una opción del menú para comenzar</p>"
                        + "</div></html>",
                JLabel.CENTER
        );
        lblBienvenida.setHorizontalAlignment(JLabel.CENTER);
        lblBienvenida.setVerticalAlignment(JLabel.CENTER);
        lblBienvenida.setBounds(0, 0, desktopPane.getWidth(), desktopPane.getHeight());
        desktopPane.add(lblBienvenida, Integer.valueOf(0)); // Capa de fondo
    }

    private static void createMenu() {
        JMenuBar menuBar = new JMenuBar();

        // Menú Sistema
        JMenu menuSistema = new JMenu("Sistema");
        menuSistema.setMnemonic('S');

        JMenuItem menuItemLogin = new JMenuItem("Iniciar Sesión");
        menuItemLogin.setMnemonic('I');
        menuItemLogin.addActionListener(e -> abrirFormularioLogin());

        JMenuItem menuItemDashboard = new JMenuItem("Dashboard Principal");
        menuItemDashboard.setMnemonic('D');
        menuItemDashboard.addActionListener(e -> mostrarDashboard());

        JMenuItem menuItemSalir = new JMenuItem("Salir");
        menuItemSalir.setMnemonic('S');
        menuItemSalir.addActionListener(e -> confirmarSalida());

        menuSistema.add(menuItemLogin);
        menuSistema.add(menuItemDashboard);
        menuSistema.addSeparator();
        menuSistema.add(menuItemSalir);

        // Menú Administración
        JMenu menuAdministracion = new JMenu("Administración");
        menuAdministracion.setMnemonic('A');

        JMenuItem menuItemPanelAdmin = new JMenuItem("Panel de Administración");
        menuItemPanelAdmin.setMnemonic('P');
        menuItemPanelAdmin.addActionListener(e -> abrirPanelAdministracion());

        JMenuItem menuItemUsuarios = new JMenuItem("Gestión de Usuarios");
        menuItemUsuarios.setMnemonic('U');
        menuItemUsuarios.addActionListener(e -> abrirFormularioUsuarios());

        JMenuItem menuItemHistorial = new JMenuItem("Historial del Sistema");
        menuItemHistorial.setMnemonic('H');
        menuItemHistorial.addActionListener(e -> abrirFormularioHistorial());

        JMenuItem menuItemPeticiones = new JMenuItem("Peticiones de Stock");
        menuItemPeticiones.setMnemonic('S');
        menuItemPeticiones.addActionListener(e -> abrirFormularioPeticiones());

        menuAdministracion.add(menuItemPanelAdmin);
        menuAdministracion.addSeparator();
        menuAdministracion.add(menuItemUsuarios);
        menuAdministracion.add(menuItemHistorial);
        menuAdministracion.add(menuItemPeticiones);

        // Menú Bodega
        JMenu menuBodega = new JMenu("Bodega");
        menuBodega.setMnemonic('B');

        JMenuItem menuItemModuloBodega = new JMenuItem("Módulo de Bodega");
        menuItemModuloBodega.setMnemonic('M');
        menuItemModuloBodega.addActionListener(e -> abrirModuloBodega());

        JMenuItem menuItemPedirProductos = new JMenuItem("Solicitar a Proveedores");
        menuItemPedirProductos.setMnemonic('P');
        menuItemPedirProductos.addActionListener(e -> abrirBodegaPedirProductos());

        JMenuItem menuItemVerPeticionesBodega = new JMenuItem("Ver Peticiones de Vendedores");
        menuItemVerPeticionesBodega.setMnemonic('V');
        menuItemVerPeticionesBodega.addActionListener(e -> abrirBodegaVerPeticiones());

        menuBodega.add(menuItemModuloBodega);
        menuBodega.addSeparator();
        menuBodega.add(menuItemPedirProductos);
        menuBodega.add(menuItemVerPeticionesBodega);

        // Menú Vendedor
        JMenu menuVendedor = new JMenu("Vendedor");
        menuVendedor.setMnemonic('V');

        JMenuItem menuItemPuntoVenta = new JMenuItem("Punto de Venta");
        menuItemPuntoVenta.setMnemonic('P');
        menuItemPuntoVenta.addActionListener(e -> abrirVenderForm());

        JMenuItem menuItemPedirStock = new JMenuItem("Solicitar Stock a Bodega");
        menuItemPedirStock.setMnemonic('S');
        menuItemPedirStock.addActionListener(e -> abrirVendedorPedirForm());

        JMenuItem menuItemModuloVendedor = new JMenuItem("Módulo Completo de Vendedor");
        menuItemModuloVendedor.setMnemonic('M');
        menuItemModuloVendedor.addActionListener(e -> abrirVendedorMainForm());

        menuVendedor.add(menuItemPuntoVenta);
        menuVendedor.add(menuItemPedirStock);
        menuVendedor.addSeparator();
        menuVendedor.add(menuItemModuloVendedor);

        // Menú Inventario
        JMenu menuInventario = new JMenu("Inventario");
        menuInventario.setMnemonic('I');

        JMenuItem menuItemProductos = new JMenuItem("Gestión de Productos");
        menuItemProductos.setMnemonic('P');
        menuItemProductos.addActionListener(e -> mostrarMensajeEnDesarrollo("Gestión de Productos"));

        JMenuItem menuItemStock = new JMenuItem("Control de Stock");
        menuItemStock.setMnemonic('S');
        menuItemStock.addActionListener(e -> mostrarMensajeEnDesarrollo("Control de Stock"));

        menuInventario.add(menuItemProductos);
        menuInventario.add(menuItemStock);

        // Menú Reportes
        JMenu menuReportes = new JMenu("Reportes");
        menuReportes.setMnemonic('R');

        JMenuItem menuItemReporteVentas = new JMenuItem("Reporte de Ventas");
        menuItemReporteVentas.setMnemonic('V');
        menuItemReporteVentas.addActionListener(e -> mostrarMensajeEnDesarrollo("Reporte de Ventas"));

        JMenuItem menuItemReporteStock = new JMenuItem("Reporte de Stock");
        menuItemReporteStock.setMnemonic('S');
        menuItemReporteStock.addActionListener(e -> mostrarMensajeEnDesarrollo("Reporte de Stock"));

        menuReportes.add(menuItemReporteVentas);
        menuReportes.add(menuItemReporteStock);

        // Menú Ayuda
        JMenu menuAyuda = new JMenu("Ayuda");
        menuAyuda.setMnemonic('y');

        JMenuItem menuItemAcercaDe = new JMenuItem("Acerca de...");
        menuItemAcercaDe.setMnemonic('A');
        menuItemAcercaDe.addActionListener(e -> mostrarAcercaDe());

        JMenuItem menuItemManual = new JMenuItem("Manual de Usuario");
        menuItemManual.setMnemonic('M');
        menuItemManual.addActionListener(e -> mostrarMensajeEnDesarrollo("Manual de Usuario"));

        menuAyuda.add(menuItemAcercaDe);
        menuAyuda.add(menuItemManual);

        // Agregar menús a la barra
        menuBar.add(menuSistema);
        menuBar.add(menuAdministracion);
        menuBar.add(menuBodega);
        menuBar.add(menuVendedor);
        menuBar.add(menuInventario);
        menuBar.add(menuReportes);
        menuBar.add(menuAyuda);

        frame.setJMenuBar(menuBar);
    }

    private static void abrirFormularioLogin() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof login) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        login loginForm = new login();
        loginForm.setVisible(true);
        desktopPane.add(loginForm);

        // Centrar el formulario
        centrarFormulario(loginForm);

        try {
            loginForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirPanelAdministracion() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof AdminMainForm) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    window.setMaximum(true);
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        AdminMainForm adminForm = new AdminMainForm();
        adminForm.setVisible(true);
        desktopPane.add(adminForm);

        // Centrar y maximizar el formulario
        centrarFormulario(adminForm);
        try {
            adminForm.setMaximum(true);
            adminForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al maximizar formulario: " + e.getMessage());
        }
    }

    private static void abrirFormularioUsuarios() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof AdminCrearUsuarios) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        AdminCrearUsuarios usuariosForm = new AdminCrearUsuarios();
        usuariosForm.setVisible(true);
        desktopPane.add(usuariosForm);

        // Centrar el formulario
        centrarFormulario(usuariosForm);

        try {
            usuariosForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirFormularioHistorial() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof AdminHistoriall) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        AdminHistoriall historialForm = new AdminHistoriall();
        historialForm.setVisible(true);
        desktopPane.add(historialForm);

        // Centrar el formulario
        centrarFormulario(historialForm);

        try {
            historialForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirFormularioPeticiones() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof AdminVerPeticiones) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        AdminVerPeticiones peticionesForm = new AdminVerPeticiones();
        peticionesForm.setVisible(true);
        desktopPane.add(peticionesForm);

        // Centrar el formulario
        centrarFormulario(peticionesForm);

        try {
            peticionesForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirModuloBodega() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof BodegaMainForm) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        BodegaMainForm bodegaForm = new BodegaMainForm();
        bodegaForm.setVisible(true);
        desktopPane.add(bodegaForm);

        // Centrar el formulario
        centrarFormulario(bodegaForm);

        try {
            bodegaForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirBodegaPedirProductos() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof BodegaPedirProductos) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        BodegaPedirProductos pedirProductosForm = new BodegaPedirProductos();
        pedirProductosForm.setVisible(true);
        desktopPane.add(pedirProductosForm);

        // Centrar el formulario
        centrarFormulario(pedirProductosForm);

        try {
            pedirProductosForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirBodegaVerPeticiones() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof BodegaVerPeticiones) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        BodegaVerPeticiones verPeticionesForm = new BodegaVerPeticiones();
        verPeticionesForm.setVisible(true);
        desktopPane.add(verPeticionesForm);

        // Centrar el formulario
        centrarFormulario(verPeticionesForm);

        try {
            verPeticionesForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    // NUEVOS MÉTODOS PARA VENDEDOR
    private static void abrirVenderForm() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof VenderForm) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        VenderForm venderForm = new VenderForm();
        venderForm.setVisible(true);
        desktopPane.add(venderForm);

        // Centrar el formulario
        centrarFormulario(venderForm);

        try {
            venderForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirVendedorPedirForm() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof VendedorPedirForm) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        VendedorPedirForm pedirForm = new VendedorPedirForm();
        pedirForm.setVisible(true);
        desktopPane.add(pedirForm);

        // Centrar el formulario
        centrarFormulario(pedirForm);

        try {
            pedirForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    private static void abrirVendedorMainForm() {
        // Verificar si ya está abierto el formulario
        for (var window : desktopPane.getAllFrames()) {
            if (window instanceof VendedorMainForm) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        VendedorMainForm vendedorForm = new VendedorMainForm();
        vendedorForm.setVisible(true);
        desktopPane.add(vendedorForm);

        // Centrar el formulario
        centrarFormulario(vendedorForm);

        try {
            vendedorForm.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }

    public static void centrarFormulario(javax.swing.JInternalFrame formulario) {
        Dimension desktopSize = desktopPane.getSize();
        Dimension formSize = formulario.getSize();

        // Si el formulario es más grande que el desktop, ajustar tamaño
        if (formSize.width > desktopSize.width * 0.9 || formSize.height > desktopSize.height * 0.9) {
            formulario.setSize(
                    (int)(desktopSize.width * 0.9),
                    (int)(desktopSize.height * 0.9)
            );
            formSize = formulario.getSize();
        }

        formulario.setLocation(
                (desktopSize.width - formSize.width) / 2,
                (desktopSize.height - formSize.height) / 2
        );
    }

    private static void mostrarDashboard() {
        // Limpiar el desktop pane
        desktopPane.removeAll();
        setupDesktopPaneBackground();
        desktopPane.revalidate();
        desktopPane.repaint();
    }

    private static void confirmarSalida() {
        int confirmacion = javax.swing.JOptionPane.showConfirmDialog(
                frame,
                "<html><div style='text-align: center;'>"
                        + "<h3>¿Está seguro que desea salir del sistema?</h3>"
                        + "<p>Todos los formularios abiertos se cerrarán.</p>"
                        + "</div></html>",
                "Confirmar salida",
                javax.swing.JOptionPane.YES_NO_OPTION,
                javax.swing.JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == javax.swing.JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }

    private static void mostrarMensajeEnDesarrollo(String modulo) {
        javax.swing.JOptionPane.showMessageDialog(
                frame,
                "<html><div style='text-align: center;'>"
                        + "<h3>Módulo en Desarrollo</h3>"
                        + "<p>El módulo <b>'" + modulo + "'</b> está actualmente en desarrollo.</p>"
                        + "<p>Estará disponible en próximas actualizaciones del sistema.</p>"
                        + "</div></html>",
                "Módulo en desarrollo",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void mostrarAcercaDe() {
        javax.swing.JOptionPane.showMessageDialog(
                frame,
                "<html><div style='text-align: center;'>"
                        + "<h2 style='color: #2E4053;'>Sistema de Gestión - Licorería</h2>"
                        + "<div style='background: #F8F9F9; padding: 15px; border-radius: 8px; margin: 10px;'>"
                        + "<p><b>Versión:</b> 3.0</p>"
                        + "<p><b>Desarrollado por:</b> Emerson</p>"
                        + "<p><b>Fecha:</b> Noviembre 2024</p>"
                        + "</div>"
                        + "<h3 style='color: #2E4053;'>Módulos Implementados:</h3>"
                        + "<ul style='text-align: left;'>"
                        + "<li><b>🔐 Sistema de Login</b> - Autenticación segura</li>"
                        + "<li><b>👥 Gestión de Usuarios</b> - CRUD completo con roles</li>"
                        + "<li><b>📊 Panel de Administración</b> - Interfaz MDI completa</li>"
                        + "<li><b>📋 Historial del Sistema</b> - Registro de actividades</li>"
                        + "<li><b>📦 Módulo de Bodega</b> - Gestión completa de inventario</li>"
                        + "<li><b>🛒 Punto de Venta</b> - Sistema de ventas completo</li>"
                        + "<li><b>📮 Solicitar Stock</b> - Peticiones a bodega</li>"
                        + "<li><b>📈 Peticiones de Stock</b> - Gestión de solicitudes</li>"
                        + "<li><b>🔄 Módulo de Vendedor</b> - Interfaz completa para vendedores</li>"
                        + "</ul>"
                        + "<p style='color: #85929E; font-size: 12px;'>Sistema desarrollado con Java Swing y MySQL</p>"
                        + "</div></html>",
                "Acerca del Sistema",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    private static void mostrarMensajeBienvenida() {
        javax.swing.JOptionPane.showMessageDialog(
                frame,
                "<html><div style='text-align: center;'>"
                        + "<h2 style='color: #2E4053;'>¡Bienvenido al Sistema de Licorería!</h2>"
                        + "<div style='background: #E8F6F3; padding: 15px; border-radius: 8px; margin: 10px;'>"
                        + "<p>El sistema se ha iniciado correctamente.</p>"
                        + "<p><b>Módulos disponibles:</b></p>"
                        + "<ul style='text-align: left;'>"
                        + "<li>🔐 Sistema de Login</li>"
                        + "<li>👥 Gestión de Usuarios</li>"
                        + "<li>📊 Panel de Administración (MDI)</li>"
                        + "<li>📋 Historial del Sistema</li>"
                        + "<li>📦 Módulo de Bodega</li>"
                        + "<li>🛒 Punto de Venta</li>"
                        + "<li>📮 Solicitar Stock a Bodega</li>"
                        + "<li>📈 Peticiones de Stock</li>"
                        + "<li>🔄 Módulo Completo de Vendedor</li>"
                        + "</ul>"
                        + "</div>"
                        + "<p>Utilice los menús para acceder a las diferentes funcionalidades.</p>"
                        + "</div></html>",
                "Inicio del Sistema",
                javax.swing.JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Método público para acceder al frame principal desde otros formularios si es necesario
    public static JFrame getMainFrame() {
        return frame;
    }

    // Método público para acceder al desktop pane desde otros formularios
    public static JDesktopPane getDesktopPane() {
        return desktopPane;
    }

    // Método para mostrar un formulario genérico (puede ser útil para otros formularios)
    public static void mostrarFormulario(javax.swing.JInternalFrame formulario) {
        // Verificar si ya está abierto
        for (var window : desktopPane.getAllFrames()) {
            if (window.getClass().equals(formulario.getClass())) {
                try {
                    window.setSelected(true);
                    window.moveToFront();
                    return;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        // Si no está abierto, crear nuevo
        formulario.setVisible(true);
        desktopPane.add(formulario);
        centrarFormulario(formulario);

        try {
            formulario.setSelected(true);
        } catch (java.beans.PropertyVetoException e) {
            System.err.println("Error al seleccionar formulario: " + e.getMessage());
        }
    }
}