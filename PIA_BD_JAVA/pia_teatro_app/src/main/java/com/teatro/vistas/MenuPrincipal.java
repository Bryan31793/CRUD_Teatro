package com.teatro.vistas;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MenuPrincipal extends JFrame {

    // --- Colores del tema ---
    private static final Color COLOR_FONDO = new Color(30, 41, 82);
    private static final Color COLOR_DORADO = new Color(255, 193, 7);
    private static final Color COLOR_PANEL = new Color(45, 55, 100);
    private static final Color COLOR_CARD = new Color(55, 65, 110);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_HOVER = new Color(255, 215, 0);

    // --- Variables de Sesión ---
    private int idEmpleadoLogueado;
    private int idRolLogueado;
    private String nombreEmpleado;

    // --- Componentes ---
    private JPanel panelSuperior;
    private JPanel panelContenido;
    private JLabel lblBienvenida;
    private JLabel lblHora;
    private JButton btnCerrarSesion;
    private Timer reloj;

    public MenuPrincipal(int idEmpleado, int idRol, String nombreEmpleado) {
        this.idEmpleadoLogueado = idEmpleado;
        this.idRolLogueado = idRol;
        this.nombreEmpleado = nombreEmpleado;

        // --- Configuración de la ventana ---
        setTitle("Sistema de Teatro FCFM - Menú Principal");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(COLOR_FONDO);

        // --- Panel Superior (Header) ---
        crearPanelSuperior();

        // --- Panel de Contenido (Menú de opciones) ---
        crearPanelContenido();

        // --- Iniciar reloj ---
        iniciarReloj();
    }

    private void crearPanelSuperior() {
        panelSuperior = new JPanel();
        panelSuperior.setLayout(null);
        panelSuperior.setPreferredSize(new Dimension(1000, 100));
        panelSuperior.setBackground(COLOR_PANEL);
        panelSuperior.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_DORADO));

        // Logo/Ícono
        JLabel lblLogo = new JLabel("🎭");
        lblLogo.setFont(new Font("Arial", Font.PLAIN, 48));
        lblLogo.setBounds(30, 25, 60, 60);
        panelSuperior.add(lblLogo);

        // Título del sistema
        JLabel lblTitulo = new JLabel("SISTEMA DE TEATRO FCFM");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setBounds(100, 20, 400, 30);
        panelSuperior.add(lblTitulo);

        // Bienvenida con nombre del usuario
        lblBienvenida = new JLabel("Bienvenido(a), " + nombreEmpleado);
        lblBienvenida.setFont(new Font("Arial", Font.PLAIN, 14));
        lblBienvenida.setForeground(COLOR_TEXTO);
        lblBienvenida.setBounds(100, 55, 300, 25);
        panelSuperior.add(lblBienvenida);

        // Rol del usuario
        String nombreRol = obtenerNombreRol(idRolLogueado);
        JLabel lblRol = new JLabel("Rol: " + nombreRol);
        lblRol.setFont(new Font("Arial", Font.ITALIC, 12));
        lblRol.setForeground(new Color(200, 200, 200));
        lblRol.setBounds(100, 75, 200, 20);
        panelSuperior.add(lblRol);

        // Reloj
        lblHora = new JLabel();
        lblHora.setFont(new Font("Arial", Font.BOLD, 16));
        lblHora.setForeground(COLOR_TEXTO);
        lblHora.setBounds(780, 30, 150, 25);
        panelSuperior.add(lblHora);

        // Botón Cerrar Sesión
        btnCerrarSesion = new JButton("Cerrar Sesión");
        btnCerrarSesion.setBounds(780, 60, 180, 35);
        btnCerrarSesion.setFont(new Font("Arial", Font.BOLD, 12));
        btnCerrarSesion.setBackground(new Color(220, 53, 69));
        btnCerrarSesion.setForeground(Color.WHITE);
        btnCerrarSesion.setFocusPainted(false);
        btnCerrarSesion.setBorderPainted(false);
        btnCerrarSesion.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnCerrarSesion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnCerrarSesion.setBackground(new Color(200, 35, 51));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnCerrarSesion.setBackground(new Color(220, 53, 69));
            }
        });
        
        btnCerrarSesion.addActionListener(e -> cerrarSesion());
        panelSuperior.add(btnCerrarSesion);

        add(panelSuperior, BorderLayout.NORTH);
    }

    private void crearPanelContenido() {
        panelContenido = new JPanel();
        panelContenido.setLayout(new GridLayout(2, 2, 30, 30));
        panelContenido.setBackground(COLOR_FONDO);
        panelContenido.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Crear tarjetas según el rol
        if (idRolLogueado >= 1) { // Todos pueden vender
            panelContenido.add(crearTarjeta("💳", "Vender Boletos", "Registrar nuevas ventas", e -> abrirModuloVenta()));
        }

        if (idRolLogueado >= 2) { // Admin y Director
            panelContenido.add(crearTarjeta("📊", "Ver Reportes", "Consultar estadísticas", e -> abrirModuloReportes()));
            panelContenido.add(crearTarjeta("🎬", "Administrar Funciones", "Gestionar funciones y precios", e -> abrirModuloAdminFunciones()));
        }

        if (idRolLogueado == 3) { // Solo Director
            panelContenido.add(crearTarjeta("👥", "Gestionar Empleados", "Contratar y administrar personal", e -> abrirModuloEmpleados()));
        }

        add(panelContenido, BorderLayout.CENTER);
    }

    private JPanel crearTarjeta(String icono, String titulo, String descripcion, ActionListener action) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(null);
        tarjeta.setBackground(COLOR_CARD);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        tarjeta.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Ícono
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Arial", Font.PLAIN, 80));
        lblIcono.setBounds(150, 30, 100, 100);
        lblIcono.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(lblIcono);

        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setBounds(20, 140, 360, 30);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(lblTitulo);

        // Descripción
        JLabel lblDesc = new JLabel(descripcion);
        lblDesc.setFont(new Font("Arial", Font.PLAIN, 14));
        lblDesc.setForeground(new Color(200, 200, 200));
        lblDesc.setBounds(20, 175, 360, 30);
        lblDesc.setHorizontalAlignment(SwingConstants.CENTER);
        tarjeta.add(lblDesc);

        // Efecto hover
        tarjeta.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                tarjeta.setBackground(new Color(65, 75, 120));
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_HOVER, 3),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                tarjeta.setBackground(COLOR_CARD);
                tarjeta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COLOR_DORADO, 2),
                    new EmptyBorder(20, 20, 20, 20)
                ));
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                action.actionPerformed(null);
            }
        });

        return tarjeta;
    }

    private void iniciarReloj() {
        reloj = new Timer(1000, e -> {
            LocalDateTime ahora = LocalDateTime.now();
            DateTimeFormatter formato = DateTimeFormatter.ofPattern("HH:mm:ss");
            lblHora.setText(ahora.format(formato));
        });
        reloj.start();
    }

    private String obtenerNombreRol(int idRol) {
        switch (idRol) {
            case 1: return "Vendedor";
            case 2: return "Administrador";
            case 3: return "Director";
            default: return "Desconocido";
        }
    }

    private void abrirModuloVenta() {
        VentaBoletos ventana = new VentaBoletos(this.idEmpleadoLogueado);
        ventana.setVisible(true);
    }

    private void abrirModuloReportes() {
        VerReportes ventana = new VerReportes();
        ventana.setVisible(true);
    }

    private void abrirModuloAdminFunciones() {
        AdminFunciones ventana = new AdminFunciones();
        ventana.setVisible(true);
    }

    private void abrirModuloEmpleados() {
        AdminEmpleados ventana = new AdminEmpleados();
        ventana.setVisible(true);
    }

    private void cerrarSesion() {
        int confirmacion = JOptionPane.showConfirmDialog(
            this,
            "¿Está seguro que desea cerrar sesión?",
            "Confirmar Cierre de Sesión",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );

        if (confirmacion == JOptionPane.YES_OPTION) {
            if (reloj != null) {
                reloj.stop();
            }
            this.dispose();
            new Login().setVisible(true);
        }
    }
}



