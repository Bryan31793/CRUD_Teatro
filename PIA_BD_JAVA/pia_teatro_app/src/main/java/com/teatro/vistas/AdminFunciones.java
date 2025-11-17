package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminFunciones extends JFrame {

    // --- Componentes ---
    private JComboBox<ComboBoxItem> cmbFuncionesExistentes;
    private JComboBox<ComboBoxItem> cmbZonas;
    private JTextField txtPrecio;
    private JButton btnAsignarPrecio;

    // (Aquí irían los campos para crear una NUEVA función, 
    // pero por simplicidad, nos enfocaremos en asignar precios)

    public AdminFunciones() {
        setTitle("Administrar Funciones y Precios");
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- Asignar Precios ---
        add(new JLabel("Asignar Precios a Funciones Existentes")).setBounds(120, 20, 300, 25);

        add(new JLabel("Función:")).setBounds(30, 60, 100, 25);
        cmbFuncionesExistentes = new JComboBox<>();
        cmbFuncionesExistentes.setBounds(140, 60, 320, 25);
        add(cmbFuncionesExistentes);

        add(new JLabel("Zona:")).setBounds(30, 100, 100, 25);
        cmbZonas = new JComboBox<>();
        cmbZonas.setBounds(140, 100, 320, 25);
        add(cmbZonas);

        add(new JLabel("Precio ($):")).setBounds(30, 140, 100, 25);
        txtPrecio = new JTextField();
        txtPrecio.setBounds(140, 140, 100, 25);
        add(txtPrecio);

        btnAsignarPrecio = new JButton("Asignar/Actualizar Precio");
        btnAsignarPrecio.setBounds(140, 180, 200, 30);
        add(btnAsignarPrecio);

        // --- Cargar Datos ---
        cargarDatosComboBox();

        // --- Acción del Botón ---
        btnAsignarPrecio.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                asignarPrecio();
            }
        });
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

    private void asignarPrecio() {
        // 1. Obtener datos del formulario
        ComboBoxItem funcionSel = (ComboBoxItem) cmbFuncionesExistentes.getSelectedItem();
        ComboBoxItem zonaSel = (ComboBoxItem) cmbZonas.getSelectedItem();
        double precio;

        try {
            precio = Double.parseDouble(txtPrecio.getText());
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un precio válido.");
            return;
        }

        if (funcionSel == null || zonaSel == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione una función y una zona.");
            return;
        }

        // 2. Llamar al Stored Procedure
        Connection con = Conexion.getConnection();
        if (con == null) return;

        // {CALL sp_AsignarPrecio(IN p_funcionID, IN p_zonaID, IN p_precio)}
        String sql = "{CALL sp_AsignarPrecio(?, ?, ?)}";

        try (java.sql.CallableStatement cst = con.prepareCall(sql)) {
            cst.setInt(1, funcionSel.getId());
            cst.setInt(2, zonaSel.getId());
            cst.setDouble(3, precio);
            
            // Ejecutar el SP (es un UPDATE/INSERT, así que usamos executeUpdate)
            cst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "¡Precio asignado/actualizado con éxito!");
            txtPrecio.setText(""); // Limpiar campo

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }
}