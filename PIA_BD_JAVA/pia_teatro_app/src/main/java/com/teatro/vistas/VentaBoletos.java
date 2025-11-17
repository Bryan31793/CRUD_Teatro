package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class VentaBoletos extends JFrame {

    // --- Componentes ---
    private JComboBox<ComboBoxItem> cmbFuncion;
    private JComboBox<ComboBoxItem> cmbCliente;
    private JComboBox<ComboBoxItem> cmbMetodoPago;
    private JList<ComboBoxItem> listAsientos;
    private JButton btnConfirmarVenta;
    private DefaultListModel<ComboBoxItem> listModelAsientos;

    // --- Datos de Sesión ---
    private int idEmpleadoLogueado;

    public VentaBoletos(int idEmpleadoLogueado) {
        this.idEmpleadoLogueado = idEmpleadoLogueado;

        // --- Configuración de la Ventana ---
        setTitle("Venta de Boletos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // DISPOSE para no cerrar toda la app
        setLocationRelativeTo(null);
        setLayout(null);

        // --- Inicializar Componentes ---
        add(new JLabel("Función:")).setBounds(20, 20, 100, 25);
        cmbFuncion = new JComboBox<>();
        cmbFuncion.setBounds(130, 20, 430, 25);
        add(cmbFuncion);

        add(new JLabel("Asientos Disponibles:")).setBounds(20, 60, 150, 25);
        listModelAsientos = new DefaultListModel<>();
        listAsientos = new JList<>(listModelAsientos);
        listAsientos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION); // Permite seleccionar varios
        JScrollPane scrollAsientos = new JScrollPane(listAsientos);
        scrollAsientos.setBounds(130, 60, 430, 150);
        add(scrollAsientos);

        add(new JLabel("Cliente:")).setBounds(20, 230, 100, 25);
        cmbCliente = new JComboBox<>();
        cmbCliente.setBounds(130, 230, 430, 25);
        add(cmbCliente);

        add(new JLabel("Método de Pago:")).setBounds(20, 270, 100, 25);
        cmbMetodoPago = new JComboBox<>();
        cmbMetodoPago.setBounds(130, 270, 430, 25);
        add(cmbMetodoPago);

        btnConfirmarVenta = new JButton("Confirmar Venta");
        btnConfirmarVenta.setBounds(230, 320, 150, 30);
        add(btnConfirmarVenta);

        // --- Lógica de Eventos ---

        // 1. Cuando se selecciona una Función, cargar sus asientos
        cmbFuncion.addActionListener(e -> cargarAsientosDisponibles());

        // 2. Cuando se presiona "Confirmar Venta"
        btnConfirmarVenta.addActionListener(e -> registrarVenta());

        // --- Cargar datos iniciales ---
        cargarDatosIniciales();
    }

    private void cargarDatosIniciales() {
        Connection con = Conexion.getConnection();
        if (con == null) return;

        // Cargar Funciones (que aún no han pasado)
        try (PreparedStatement pst = con.prepareStatement("SELECT id_funcion, titulo_funcion FROM Funcion WHERE fecha_fun >= CURDATE()");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                cmbFuncion.addItem(new ComboBoxItem(rs.getInt("id_funcion"), rs.getString("titulo_funcion")));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Cargar Clientes
        try (PreparedStatement pst = con.prepareStatement("SELECT id_cliente, nombre_cliente FROM Cliente");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                cmbCliente.addItem(new ComboBoxItem(rs.getInt("id_cliente"), rs.getString("nombre_cliente")));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        // Cargar Métodos de Pago
        try (PreparedStatement pst = con.prepareStatement("SELECT id_metodoPago, tipo FROM MetodoPago");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                cmbMetodoPago.addItem(new ComboBoxItem(rs.getInt("id_metodoPago"), rs.getString("tipo")));
            }
        } catch (SQLException e) { e.printStackTrace(); }

        Conexion.close(con);
    }

    private void cargarAsientosDisponibles() {
        listModelAsientos.clear(); // Limpiar la lista
        ComboBoxItem funcionSeleccionada = (ComboBoxItem) cmbFuncion.getSelectedItem();
        if (funcionSeleccionada == null) return;
        
        int funcionID = funcionSeleccionada.getId();
        Connection con = Conexion.getConnection();
        if (con == null) return;

        // Esta consulta busca asientos que NO estén en la tabla Boleto para esta función
        String sql = "SELECT a.id_asiento, CONCAT('Fila: ', a.fila, ' - Asiento: ', a.numero_asiento) AS desc_asiento " +
                     "FROM Asiento a " +
                     "JOIN Zona z ON a.Zona_id_zona = z.id_zona " +
                     "JOIN Funcion f ON z.Sala_id_sala = f.Sala_id_sala " +
                     "WHERE f.id_funcion = ? AND a.id_asiento NOT IN (" +
                     "  SELECT Asiento_id_asiento FROM Boleto WHERE Funcion_id_funcion = ?" +
                     ")";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, funcionID);
            pst.setInt(2, funcionID);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    listModelAsientos.addElement(new ComboBoxItem(rs.getInt("id_asiento"), rs.getString("desc_asiento")));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        
        Conexion.close(con);
    }

    private void registrarVenta() {
        // 1. Recopilar todos los IDs
        ComboBoxItem funcionSel = (ComboBoxItem) cmbFuncion.getSelectedItem();
        ComboBoxItem clienteSel = (ComboBoxItem) cmbCliente.getSelectedItem();
        ComboBoxItem metodoSel = (ComboBoxItem) cmbMetodoPago.getSelectedItem();
        List<ComboBoxItem> asientosSel = listAsientos.getSelectedValuesList();

        if (funcionSel == null || clienteSel == null || metodoSel == null || asientosSel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos y seleccione al menos un asiento.");
            return;
        }

        // 2. Construir el string CSV de asientos (ej: "1,2,3")
        StringBuilder asientosCSV = new StringBuilder();
        for (ComboBoxItem item : asientosSel) {
            if (asientosCSV.length() > 0) {
                asientosCSV.append(",");
            }
            asientosCSV.append(item.getId());
        }

        // 3. Llamar al Stored Procedure
        Connection con = Conexion.getConnection();
        if (con == null) return;
        
        // El SP que cree: sp_RegistrarVenta(IN p_clienteID, IN p_empleadoID, IN p_metodoID, IN p_funcionID, IN p_asientosCSV)
        String sql = "{CALL sp_RegistrarVenta(?, ?, ?, ?, ?)}";
        
        try (java.sql.CallableStatement cst = con.prepareCall(sql)) {
            cst.setInt(1, clienteSel.getId());
            cst.setInt(2, this.idEmpleadoLogueado);
            cst.setInt(3, metodoSel.getId());
            cst.setInt(4, funcionSel.getId());
            cst.setString(5, asientosCSV.toString());
            
            // ¡Ejecutar el SP!
            ResultSet rs = cst.executeQuery();
            
            // Leer la respuesta del SP
            if (rs.next()) {
                String mensaje = rs.getString("Mensaje");
                if (mensaje.contains("éxito")) {
                    JOptionPane.showMessageDialog(this, "Venta registrada con éxito!\nID de Venta: " + rs.getInt("ID de Venta"));
                    this.dispose(); // Cerrar la ventana de venta
                } else {
                    JOptionPane.showMessageDialog(this, "Error: " + mensaje, "Error en Venta", JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error de SQL: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }
}