package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class AdminEmpleados extends JFrame {

    // --- Componentes ---
    private JTable tablaEmpleados;
    private JTextField txtNombre, txtApellido, txtUsuario;
    private JPasswordField txtPassword;
    private JComboBox<ComboBoxItem> cmbRol;
    private JButton btnAgregar, btnEditar, btnEliminar, btnLimpiar;
    
    // Variable para guardar el ID del empleado seleccionado (para editar/borrar)
    private int idEmpleadoSeleccionado = -1;

    public AdminEmpleados() {
        setTitle("Gestión de Empleados (Director)");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- 1. Panel de Formulario (Izquierda) ---
        JLabel lblTitulo = new JLabel("Datos del Empleado");
        lblTitulo.setBounds(20, 20, 200, 20);
        add(lblTitulo);

        add(new JLabel("Nombre:")).setBounds(20, 50, 80, 25);
        txtNombre = new JTextField();
        txtNombre.setBounds(100, 50, 180, 25);
        add(txtNombre);

        add(new JLabel("Apellido:")).setBounds(20, 90, 80, 25);
        txtApellido = new JTextField();
        txtApellido.setBounds(100, 90, 180, 25);
        add(txtApellido);

        add(new JLabel("Usuario:")).setBounds(20, 130, 80, 25);
        txtUsuario = new JTextField();
        txtUsuario.setBounds(100, 130, 180, 25);
        add(txtUsuario);

        add(new JLabel("Password:")).setBounds(20, 170, 80, 25);
        txtPassword = new JPasswordField();
        txtPassword.setBounds(100, 170, 180, 25);
        add(txtPassword);

        add(new JLabel("Rol:")).setBounds(20, 210, 80, 25);
        cmbRol = new JComboBox<>();
        cmbRol.setBounds(100, 210, 180, 25);
        add(cmbRol);

        // Botones
        btnAgregar = new JButton("Contratar");
        btnAgregar.setBounds(20, 260, 120, 30);
        add(btnAgregar);

        btnEditar = new JButton("Guardar Cambios");
        btnEditar.setBounds(150, 260, 130, 30);
        btnEditar.setEnabled(false); // Desactivado al inicio
        add(btnEditar);

        btnEliminar = new JButton("Despedir");
        btnEliminar.setBounds(20, 300, 120, 30);
        btnEliminar.setEnabled(false);
        add(btnEliminar);

        btnLimpiar = new JButton("Limpiar");
        btnLimpiar.setBounds(150, 300, 130, 30);
        add(btnLimpiar);

        // --- 2. Panel de Tabla (Derecha) ---
        JScrollPane scrollTabla = new JScrollPane();
        scrollTabla.setBounds(300, 50, 560, 380);
        tablaEmpleados = new JTable();
        scrollTabla.setViewportView(tablaEmpleados);
        add(scrollTabla);

        // --- Cargar Datos Iniciales ---
        cargarRoles();
        cargarTablaEmpleados();

        // --- Eventos ---
        btnAgregar.addActionListener(e -> registrarEmpleado());
        btnEditar.addActionListener(e -> editarEmpleado());
        btnEliminar.addActionListener(e -> eliminarEmpleado());
        btnLimpiar.addActionListener(e -> limpiarFormulario());

        // Evento al hacer clic en la tabla (para llenar el formulario)
        tablaEmpleados.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarEmpleado();
            }
        });
    }

    // ---------------------------------------------------
    // MÉTODOS DE CONEXIÓN Y LÓGICA
    // ---------------------------------------------------

    private void cargarRoles() {
        Connection con = Conexion.getConnection();
        if (con == null) return;
        try (PreparedStatement pst = con.prepareStatement("SELECT id_rol, nombre_rol FROM Rol");
             ResultSet rs = pst.executeQuery()) {
            cmbRol.removeAllItems();
            while (rs.next()) {
                cmbRol.addItem(new ComboBoxItem(rs.getInt("id_rol"), rs.getString("nombre_rol")));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        finally { Conexion.close(con); }
    }

    private void cargarTablaEmpleados() {
        Connection con = Conexion.getConnection();
        if (con == null) return;
        // Llamamos al SP sp_VerEmpleados
        try (java.sql.CallableStatement cst = con.prepareCall("{CALL sp_VerEmpleados()}");
             ResultSet rs = cst.executeQuery()) {
             
            // Usamos el método útil de VerReportes para llenar la tabla
            tablaEmpleados.setModel(VerReportes.buildTableModel(rs));
            
        } catch (SQLException e) { e.printStackTrace(); }
        finally { Conexion.close(con); }
    }

    private void registrarEmpleado() {
        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        String usuario = txtUsuario.getText();
        String pass = new String(txtPassword.getPassword());
        ComboBoxItem rolSel = (ComboBoxItem) cmbRol.getSelectedItem();

        if (nombre.isEmpty() || usuario.isEmpty() || pass.isEmpty() || rolSel == null) {
            JOptionPane.showMessageDialog(this, "Complete todos los campos.");
            return;
        }

        Connection con = Conexion.getConnection();
        if (con == null) return;

        try (java.sql.CallableStatement cst = con.prepareCall("{CALL sp_RegistrarEmpleado(?, ?, ?, ?, ?)}")) {
            cst.setString(1, nombre);
            cst.setString(2, apellido);
            cst.setString(3, usuario);
            cst.setString(4, pass);
            cst.setInt(5, rolSel.getId());
            
            cst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Empleado contratado con éxito.");
            cargarTablaEmpleados(); // Refrescar tabla
            limpiarFormulario();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } finally { Conexion.close(con); }
    }

    private void editarEmpleado() {
        if (idEmpleadoSeleccionado == -1) return;

        String nombre = txtNombre.getText();
        String apellido = txtApellido.getText();
        ComboBoxItem rolSel = (ComboBoxItem) cmbRol.getSelectedItem();

        Connection con = Conexion.getConnection();
        if (con == null) return;

        try (java.sql.CallableStatement cst = con.prepareCall("{CALL sp_EditarEmpleado(?, ?, ?, ?)}")) {
            cst.setInt(1, idEmpleadoSeleccionado);
            cst.setString(2, nombre);
            cst.setString(3, apellido);
            cst.setInt(4, rolSel.getId());
            
            cst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Empleado actualizado (Nota: El password no se cambia aquí).");
            cargarTablaEmpleados();
            limpiarFormulario();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        } finally { Conexion.close(con); }
    }

    private void eliminarEmpleado() {
        if (idEmpleadoSeleccionado == -1) return;
        
        int confirm = JOptionPane.showConfirmDialog(this, "¿Seguro que desea despedir a este empleado?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        Connection con = Conexion.getConnection();
        if (con == null) return;

        try (java.sql.CallableStatement cst = con.prepareCall("{CALL sp_EliminarEmpleado(?)}")) {
            cst.setInt(1, idEmpleadoSeleccionado);
            cst.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Empleado eliminado.");
            cargarTablaEmpleados();
            limpiarFormulario();

        } catch (SQLException e) {
            // Aquí saltará TRIGGER 'trg_ProtegerRolUsado' si intentas borrar un Rol, 
            // o una restricción de llave foránea si el empleado ya hizo ventas.
            JOptionPane.showMessageDialog(this, "No se puede eliminar: " + e.getMessage());
        } finally { Conexion.close(con); }
    }

    private void seleccionarEmpleado() {
        int fila = tablaEmpleados.getSelectedRow();
        if (fila >= 0) {
            // Obtenemos datos de la tabla visual
            idEmpleadoSeleccionado = (int) tablaEmpleados.getValueAt(fila, 0); // Suponiendo que ID es col 0
            txtNombre.setText((String) tablaEmpleados.getValueAt(fila, 1));
            txtApellido.setText((String) tablaEmpleados.getValueAt(fila, 2));
            txtUsuario.setText((String) tablaEmpleados.getValueAt(fila, 3));
            
            // Bloqueamos usuario y pass (no se editan tan fácil)
            txtUsuario.setEditable(false);
            txtPassword.setEnabled(false);
            
            // Activar botones de edición
            btnAgregar.setEnabled(false);
            btnEditar.setEnabled(true);
            btnEliminar.setEnabled(true);
        }
    }

    private void limpiarFormulario() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtUsuario.setText("");
        txtPassword.setText("");
        txtUsuario.setEditable(true);
        txtPassword.setEnabled(true);
        idEmpleadoSeleccionado = -1;
        tablaEmpleados.clearSelection();
        
        btnAgregar.setEnabled(true);
        btnEditar.setEnabled(false);
        btnEliminar.setEnabled(false);
    }
}
