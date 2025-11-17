package com.teatro.vistas;

import javax.swing.*;
import java.awt.Font;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuPrincipal extends JFrame {

    // --- Variables de Sesión del Usuario ---
    private int idEmpleadoLogueado;
    private int idRolLogueado;

    // --- Componentes del Menú ---
    private JButton btnVenderBoletos;
    private JButton btnReportes;
    private JButton btnAdminFunciones;
    private JButton btnGestionarEmpleados; // <-- NUEVO BOTÓN
    private JLabel lblBienvenido;

    /**
     * Constructor que recibe los datos del usuario desde el Login.
     * @param idEmpleado El ID del empleado que inició sesión.
     * @param idRol El ID del rol (1=Vendedor, 2=Admin, 3=Director).
     */
    public MenuPrincipal(int idEmpleado, int idRol) {
        this.idEmpleadoLogueado = idEmpleado;
        this.idRolLogueado = idRol;

        // --- Configuración de la Ventana Principal ---
        setTitle("Menú Principal - Sistema de Teatro PIA");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(45, 45, 45)); // Fondo oscuro

        // --- Componentes ---
        lblBienvenido = new JLabel("Bienvenido al Sistema de Teatro");
        lblBienvenido.setFont(new Font("Arial", Font.BOLD, 24));
        lblBienvenido.setForeground(Color.WHITE);
        lblBienvenido.setBounds(200, 30, 400, 30);
        add(lblBienvenido);

        // Botón 1: Vender Boletos
        btnVenderBoletos = new JButton("Vender Boletos");
        btnVenderBoletos.setBounds(300, 100, 200, 50);
        btnVenderBoletos.setFont(new Font("Arial", Font.PLAIN, 16));
        add(btnVenderBoletos);

        // Botón 2: Ver Reportes
        btnReportes = new JButton("Ver Reportes");
        btnReportes.setBounds(300, 170, 200, 50); // Ajuste de posición
        btnReportes.setFont(new Font("Arial", Font.PLAIN, 16));
        add(btnReportes);
        
        // Botón 3: Administrar Funciones (Crear/Editar)
        btnAdminFunciones = new JButton("Administrar Funciones");
        btnAdminFunciones.setBounds(300, 240, 200, 50); // Ajuste de posición
        btnAdminFunciones.setFont(new Font("Arial", Font.PLAIN, 16));
        add(btnAdminFunciones);

        // Botón 4: Gestionar Empleados (NUEVO)
        btnGestionarEmpleados = new JButton("Gestionar Empleados");
        btnGestionarEmpleados.setBounds(300, 310, 200, 50); // Ajuste de posición
        btnGestionarEmpleados.setFont(new Font("Arial", Font.PLAIN, 16));
        add(btnGestionarEmpleados);

        // --- LÓGICA DE ROLES (El requisito clave) ---
        aplicarPermisos(idRol);

        // --- Acción para el botón Vender Boletos ---
        btnVenderBoletos.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirModuloVenta();
            }
        });
        
        // --- Acción para el botón Ver Reportes ---
        btnReportes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirModuloReportes();
            }
        });
        
        // --- Acción para el botón Admin Funciones ---
        btnAdminFunciones.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirModuloAdminFunciones();
            }
        });

        btnGestionarEmpleados.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                abrirModuloEmpleados();
            }
        });


    }

    /**
     * Muestra u oculta botones basado en el ID del Rol.
     * @param idRol El rol del usuario.
     */
    private void aplicarPermisos(int idRol) {
        // En tu base de datos:
        // 1 = Vendedor
        // 2 = Administrador
        // 3 = Director

        if (idRol == 1) { // Vendedor
            btnVenderBoletos.setVisible(true);
            btnReportes.setVisible(false);
            btnAdminFunciones.setVisible(false);
            btnGestionarEmpleados.setVisible(false); // No puede gestionar
            
        } else if (idRol == 2) { // Administrador
            btnVenderBoletos.setVisible(true);
            btnReportes.setVisible(true);
            btnAdminFunciones.setVisible(true);
            btnGestionarEmpleados.setVisible(false); // No puede gestionar
            
        } else if (idRol == 3) { // Director
            
            // El Director puede hacerlo TODO
            btnVenderBoletos.setVisible(true);
            btnReportes.setVisible(true);
            btnAdminFunciones.setVisible(true);
            btnGestionarEmpleados.setVisible(true);
            
        } else {
            // Seguridad por si acaso
            JOptionPane.showMessageDialog(this, "Rol desconocido. Saliendo.");
            System.exit(0);
        }
    }
    
    /**
     * Abre la ventana de Venta de Boletos
     */
    private void abrirModuloVenta() {
        VentaBoletos ventanaVenta = new VentaBoletos(this.idEmpleadoLogueado);
        ventanaVenta.setVisible(true);
    }
    
    /**
     * Abre la ventana de Reportes
     */
    private void abrirModuloReportes() {
        VerReportes ventanaReportes = new VerReportes();
        ventanaReportes.setVisible(true);
    }
    
    /**
     * Abre la ventana de Administración de Funciones
     */
    private void abrirModuloAdminFunciones() {
        AdminFunciones ventanaAdmin = new AdminFunciones();
        ventanaAdmin.setVisible(true);
    }

    private void abrirModuloEmpleados() {
        AdminEmpleados ventanaEmp = new AdminEmpleados();
        ventanaEmp.setVisible(true);
    }

}