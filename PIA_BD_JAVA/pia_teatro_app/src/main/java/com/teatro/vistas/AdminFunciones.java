package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AdminFunciones extends JFrame {

    // --- Colores del tema ---
    private static final Color COLOR_FONDO = new Color(30, 41, 82);
    private static final Color COLOR_DORADO = new Color(255, 193, 7);
    private static final Color COLOR_PANEL = new Color(45, 55, 100);
    private static final Color COLOR_CARD = new Color(55, 65, 110);
    private static final Color COLOR_TEXTO = Color.WHITE;

    // --- Componentes para Asignar Precios ---
    private JComboBox<ComboBoxItem> cmbFuncionesExistentes;
    private JComboBox<ComboBoxItem> cmbZonas;
    private JTextField txtPrecio;
    private JButton btnAsignarPrecio;

    // --- Componentes para Crear Función ---
    private JTextField txtTitulo;
    private JTextField txtDirector;
    private JTextField txtGenero;
    private JTextField txtDuracion;
    private JTextField txtFecha;
    private JTextField txtHora;
    private JTextField txtSala;
    private JComboBox<ComboBoxItem> cmbSala;
    private JButton btnCrearFuncion;

    public AdminFunciones() {
        // --- Configuración de la ventana ---
        setTitle("🎬 Administrar Funciones - Teatro FCFM");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(null);

        // --- Encabezado ---
        JLabel lblTitulo = new JLabel("ADMINISTRACIÓN DE FUNCIONES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setBounds(280, 20, 500, 35);
        add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Gestione las funciones y configure los precios por zona");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setBounds(280, 55, 500, 25);
        add(lblSubtitulo);

        /* 
        // --- Panel 1: Crear Nueva Función ---
        JPanel panelCrear = crearPanelConBorde("🎭 Crear Nueva Función", 30, 100, 460, 580);
        add(panelCrear);

        // Título de la función
        JLabel lblTituloFun = crearLabel("Título de la Función:", 20, 40);
        panelCrear.add(lblTituloFun);
        
        txtTitulo = crearTextField(20, 70, 420);
        panelCrear.add(txtTitulo);

        // Director
        JLabel lblDirector = crearLabel("Director:", 20, 120);
        panelCrear.add(lblDirector);
        
        txtDirector = crearTextField(20, 150, 420);
        panelCrear.add(txtDirector);

        // Género
        JLabel lblGenero = crearLabel("Género:", 20, 200);
        panelCrear.add(lblGenero);
        
        txtGenero = crearTextField(20, 230, 420);
        panelCrear.add(txtGenero);

        // Duración
        JLabel lblDuracion = crearLabel("Duración (minutos):", 20, 280);
        panelCrear.add(lblDuracion);
        
        txtDuracion = crearTextField(20, 310, 200);
        panelCrear.add(txtDuracion);

        // Fecha
        JLabel lblFecha = crearLabel("Fecha (YYYY-MM-DD):", 20, 360);
        panelCrear.add(lblFecha);
        
        txtFecha = crearTextField(20, 390, 200);
        txtFecha.setToolTipText("Ejemplo: 2025-12-25");
        panelCrear.add(txtFecha);

        // Hora
        JLabel lblHora = crearLabel("Hora (HH:MM:SS):", 240, 360);
        panelCrear.add(lblHora);
        
        txtHora = crearTextField(240, 390, 200);
        txtHora.setToolTipText("Ejemplo: 20:00:00");
        panelCrear.add(txtHora);

        // Sala
        JLabel lblSala = crearLabel("Sala:", 20, 440);
        panelCrear.add(lblSala);

        txtSala = crearTextField(20, 470, 420);
        txtSala.setToolTipText("Ejemplo: 20:00:00");
        panelCrear.add(txtSala);
        
        //cmbSala = crearComboBox(20, 470, 420);
        //panelCrear.add(cmbSala);

        // Botón Crear
        btnCrearFuncion = crearBotonPrincipal("CREAR FUNCIÓN", 150, 520);
        btnCrearFuncion.addActionListener(e -> crearFuncion());
        panelCrear.add(btnCrearFuncion); */

        // --- Panel 2: Asignar Precios ---
        JPanel panelPrecios = crearPanelConBorde("💰 Asignar Precios por Zona", 510, 100, 460, 400);
        add(panelPrecios);

        // Instrucciones
        JLabel lblInstruccion = new JLabel("<html><i>Seleccione una función y zona para asignar o actualizar el precio</i></html>");
        lblInstruccion.setFont(new Font("Arial", Font.PLAIN, 12));
        lblInstruccion.setForeground(new Color(200, 200, 200));
        lblInstruccion.setBounds(20, 40, 420, 30);
        panelPrecios.add(lblInstruccion);

        // Función
        JLabel lblFuncion = crearLabel("Seleccione la Función:", 20, 90);
        panelPrecios.add(lblFuncion);
        
        cmbFuncionesExistentes = crearComboBox(20, 120, 420);
        panelPrecios.add(cmbFuncionesExistentes);

        // Zona
        JLabel lblZona = crearLabel("Seleccione la Zona:", 20, 170);
        panelPrecios.add(lblZona);
        
        cmbZonas = crearComboBox(20, 200, 420);
        panelPrecios.add(cmbZonas);

        // Precio
        JLabel lblPrecio = crearLabel("Precio ($):", 20, 250);
        panelPrecios.add(lblPrecio);
        
        txtPrecio = crearTextField(20, 280, 200);
        txtPrecio.setToolTipText("Ingrese el precio sin símbolos, ej: 850.00");
        panelPrecios.add(txtPrecio);

        // Botón Asignar
        btnAsignarPrecio = crearBotonPrincipal("ASIGNAR PRECIO", 150, 330);
        btnAsignarPrecio.addActionListener(e -> asignarPrecio());
        panelPrecios.add(btnAsignarPrecio);

        // --- Panel 3: Información Rápida ---
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(null);
        panelInfo.setBounds(510, 520, 460, 160);
        panelInfo.setBackground(COLOR_PANEL);
        panelInfo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        add(panelInfo);

        JLabel lblInfoTitulo = new JLabel("💡 Información Importante");
        lblInfoTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblInfoTitulo.setForeground(COLOR_DORADO);
        lblInfoTitulo.setBounds(20, 10, 300, 25);
        panelInfo.add(lblInfoTitulo);

        JTextArea txtInfo = new JTextArea();
        txtInfo.setText(
            "• Las funciones deben tener fecha futura\n" +
            "• Cada zona puede tener un precio diferente\n" +
            "• Los precios pueden actualizarse en cualquier momento\n" +
            "• Recuerde asignar precios a todas las zonas de la sala"
        );
        txtInfo.setFont(new Font("Arial", Font.PLAIN, 12));
        txtInfo.setForeground(COLOR_TEXTO);
        txtInfo.setBackground(COLOR_PANEL);
        txtInfo.setEditable(false);
        txtInfo.setBounds(20, 40, 420, 100);
        panelInfo.add(txtInfo);

        // --- Cargar datos ---
        cargarDatosComboBox();
    }

    private JPanel crearPanelConBorde(String titulo, int x, int y, int width, int height) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(x, y, width, height);
        panel.setBackground(COLOR_PANEL);
        
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            titulo,
            TitledBorder.LEFT,
            TitledBorder.TOP,
            new Font("Arial", Font.BOLD, 14),
            COLOR_DORADO
        );
        panel.setBorder(border);
        
        return panel;
    }

    private JLabel crearLabel(String texto, int x, int y) {
        JLabel lbl = new JLabel(texto);
        lbl.setFont(new Font("Arial", Font.PLAIN, 14));
        lbl.setForeground(COLOR_TEXTO);
        lbl.setBounds(x, y, 300, 25);
        return lbl;
    }

    private JTextField crearTextField(int x, int y, int width) {
        JTextField txt = new JTextField();
        txt.setBounds(x, y, width, 35);
        txt.setFont(new Font("Arial", Font.PLAIN, 13));
        txt.setBackground(COLOR_CARD);
        txt.setForeground(COLOR_TEXTO);
        txt.setCaretColor(COLOR_TEXTO);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return txt;
    }

    private JComboBox<ComboBoxItem> crearComboBox(int x, int y, int width) {
        JComboBox<ComboBoxItem> cmb = new JComboBox<>();
        cmb.setBounds(x, y, width, 35);
        cmb.setFont(new Font("Arial", Font.PLAIN, 13));
        cmb.setBackground(COLOR_CARD);
        cmb.setForeground(COLOR_TEXTO);
        return cmb;
    }

    private JButton crearBotonPrincipal(String texto, int x, int y) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 160, 40);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(COLOR_DORADO);
        btn.setForeground(COLOR_FONDO);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(255, 215, 0));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(COLOR_DORADO);
            }
        });
        
        return btn;
    }

    private void cargarDatosComboBox() {
        Connection con = Conexion.getConnection();
        if (con == null) return;

        // Cargar Funciones
        try (PreparedStatement pst = con.prepareStatement("SELECT id_funcion, titulo_funcion FROM Funcion");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                cmbFuncionesExistentes.addItem(new ComboBoxItem(rs.getInt("id_funcion"), rs.getString("titulo_funcion")));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Cargar Zonas
        try (PreparedStatement pst = con.prepareStatement("SELECT id_zona, nombre_zona FROM Zona");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                cmbZonas.addItem(new ComboBoxItem(rs.getInt("id_zona"), rs.getString("nombre_zona")));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        Conexion.close(con);
    }

    private void crearFuncion() {
        // Validar campos
        String titulo = txtTitulo.getText().trim();
        String director = txtDirector.getText().trim();
        String genero = txtGenero.getText().trim();
        String duracionStr = txtDuracion.getText().trim();
        String fecha = txtFecha.getText().trim();
        String hora = txtHora.getText().trim();
        String Sala = txtSala.getText().trim();
        ComboBoxItem salaSel = (ComboBoxItem) cmbSala.getSelectedItem();

        if (titulo.isEmpty() || fecha.isEmpty() || hora.isEmpty() || salaSel == null) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete los campos obligatorios:\n• Título\n• Fecha\n• Hora\n• Sala",
                "Datos Incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int duracion = 0;
        try {
            if (!duracionStr.isEmpty()) {
                duracion = Integer.parseInt(duracionStr);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "La duración debe ser un número entero",
                "Error de Formato",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = Conexion.getConnection();
        if (con == null) return;

        String sql = "{CALL sp_CrearFuncion(?, ?, ?, ?, ?, ?, ?)}";

        try (CallableStatement cst = con.prepareCall(sql)) {
            cst.setString(1, titulo);
            cst.setString(2, director.isEmpty() ? null : director);
            cst.setString(3, genero.isEmpty() ? null : genero);
            cst.setInt(4, duracion);
            cst.setString(5, fecha);
            cst.setString(6, hora);
            cst.setInt(7, salaSel.getId());

            cst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "¡Función creada exitosamente!\n\nNo olvide asignar los precios por zona.",
                "Función Creada",
                JOptionPane.INFORMATION_MESSAGE);

            // Limpiar campos
            txtTitulo.setText("");
            txtDirector.setText("");
            txtGenero.setText("");
            txtDuracion.setText("");
            txtFecha.setText("");
            txtHora.setText("");

            // Recargar combo de funciones
            cmbFuncionesExistentes.removeAllItems();
            cargarDatosComboBox();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al crear la función:\n" + e.getMessage(),
                "Error SQL",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }

    private void asignarPrecio() {
        ComboBoxItem funcionSel = (ComboBoxItem) cmbFuncionesExistentes.getSelectedItem();
        ComboBoxItem zonaSel = (ComboBoxItem) cmbZonas.getSelectedItem();
        String precioStr = txtPrecio.getText().trim();

        if (funcionSel == null || zonaSel == null || precioStr.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Por favor complete todos los campos para asignar el precio",
                "Datos Incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        double precio;
        try {
            precio = Double.parseDouble(precioStr);
            if (precio <= 0) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                "Por favor ingrese un precio válido mayor a 0",
                "Precio Inválido",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        Connection con = Conexion.getConnection();
        if (con == null) return;

        String sql = "{CALL sp_AsignarPrecio(?, ?, ?)}";

        try (CallableStatement cst = con.prepareCall(sql)) {
            cst.setInt(1, funcionSel.getId());
            cst.setInt(2, zonaSel.getId());
            cst.setDouble(3, precio);

            cst.executeUpdate();

            JOptionPane.showMessageDialog(this,
                "¡Precio asignado/actualizado con éxito!\n\n" +
                "Función: " + funcionSel.getTexto() + "\n" +
                "Zona: " + zonaSel.getTexto() + "\n" +
                "Precio: $" + String.format("%.2f", precio),
                "Precio Configurado",
                JOptionPane.INFORMATION_MESSAGE);

            txtPrecio.setText("");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                "Error al asignar precio:\n" + e.getMessage(),
                "Error SQL",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }
}