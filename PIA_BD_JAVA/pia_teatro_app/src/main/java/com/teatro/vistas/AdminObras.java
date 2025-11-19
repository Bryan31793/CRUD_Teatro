package com.teatro.vistas;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import com.teatro.database.Conexion;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;

public class AdminObras extends JFrame {
    private JTable tablaFuncion;
    private JTextField txtSala, txtFuncion, txtGenero, txtDirector,
            txtDuracion, txtFecha, txtHora;
    private JButton btnAgregar;

    public AdminObras() {
        setTitle("Agregar funcion");
        setSize(900, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        // --- 1. Panel de Formulario (Izquierda) ---
        JLabel lblTitulo = new JLabel("Datos de la Funcion");
        lblTitulo.setBounds(20, 20, 200, 20);
        add(lblTitulo);

        add(new JLabel("Sala ID:")).setBounds(20, 50, 80, 25);
        txtSala = new JTextField();
        txtSala.setBounds(100, 50, 180, 25);
        add(txtSala);

        add(new JLabel("Funcion:")).setBounds(20, 90, 80, 25);
        txtFuncion = new JTextField();
        txtFuncion.setBounds(100, 90, 180, 25);
        add(txtFuncion);

        add(new JLabel("Genero:")).setBounds(20, 130, 80, 25);
        txtGenero = new JTextField();
        txtGenero.setBounds(100, 130, 180, 25);
        add(txtGenero);

        add(new JLabel("Director:")).setBounds(20, 170, 80, 25);
        txtDirector = new JTextField();
        txtDirector.setBounds(100, 170, 180, 25);
        add(txtDirector);

        add(new JLabel("Duracion (min):")).setBounds(20, 210, 80, 25);
        txtDuracion = new JTextField();
        txtDuracion.setBounds(100, 210, 180, 25);
        add(txtDuracion);

        add(new JLabel("Fecha (YYYY-MM-DD):")).setBounds(20, 250, 150, 25);
        txtFecha = new JTextField();
        txtFecha.setBounds(160, 250, 180, 25);
        add(txtFecha);

        add(new JLabel("Hora (HH:MM:SS):")).setBounds(20, 290, 150, 25);
        txtHora = new JTextField();
        txtHora.setBounds(140, 290, 180, 25);
        add(txtHora);

        // Botones
        btnAgregar = new JButton("Agregar");
        btnAgregar.setBounds(20, 330, 120, 30);
        add(btnAgregar);

        // --- 2. Panel de Tabla (Derecha) ---
        JScrollPane scrollTabla = new JScrollPane();
        scrollTabla.setBounds(300, 50, 560, 380);
        tablaFuncion = new JTable();
        scrollTabla.setViewportView(tablaFuncion);
        add(scrollTabla);

        // --- Cargar Datos Iniciales ---
        cargarTablaFunciones();

        // --- Eventos ---
        btnAgregar.addActionListener(e -> registrarFuncion());

        // Evento al hacer clic en la tabla
        tablaFuncion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                seleccionarFuncion();
            }
        });
    }

    // Método para registrar función usando el stored procedure
    // Método para registrar función usando el stored procedure
private void registrarFuncion() {
    // Validar que los campos no estén vacíos
    if (txtSala.getText().trim().isEmpty() || 
        txtFuncion.getText().trim().isEmpty() ||
        txtGenero.getText().trim().isEmpty() || 
        txtDirector.getText().trim().isEmpty() ||
        txtDuracion.getText().trim().isEmpty() || 
        txtFecha.getText().trim().isEmpty() ||
        txtHora.getText().trim().isEmpty()) {
        
        JOptionPane.showMessageDialog(this, 
            "Por favor, complete todos los campos", 
            "Campos vacíos", 
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    Connection conn = null;
    CallableStatement cst = null;

    try {
        conn = Conexion.getConnection();
        
        // Llamar al stored procedure sp_CrearFuncion
        String sql = "{CALL sp_CrearFuncion(?, ?, ?, ?, ?, ?, ?)}";
        
        cst = conn.prepareCall(sql);
        
        // Establecer los parámetros IN
        cst.setString(1, txtFuncion.getText().trim());          // p_titulo_funcion
        cst.setString(2, txtDirector.getText().trim());         // p_director
        cst.setString(3, txtGenero.getText().trim());           // p_genero
        cst.setInt(4, Integer.parseInt(txtDuracion.getText().trim())); // p_duracion_minutos
        cst.setDate(5, java.sql.Date.valueOf(txtFecha.getText().trim())); // p_fecha_fun
        cst.setTime(6, java.sql.Time.valueOf(txtHora.getText().trim())); // p_hora_fun
        cst.setInt(7, Integer.parseInt(txtSala.getText().trim())); // p_id_sala

        // Ejecutar el procedimiento
        cst.execute();
        
        // Si llegamos aquí sin excepción, fue exitoso
        JOptionPane.showMessageDialog(this, 
            "Función registrada correctamente", 
            "Éxito", 
            JOptionPane.INFORMATION_MESSAGE);
        
        limpiarCampos();
        cargarTablaFunciones();

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error: La Sala ID y Duración deben ser números válidos", 
            "Error de formato", 
            JOptionPane.ERROR_MESSAGE);
    } catch (IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(this, 
            "Error: Formato de fecha u hora incorrecto.\n" +
            "Fecha: YYYY-MM-DD (ej: 2025-11-18)\n" +
            "Hora: HH:MM:SS (ej: 19:30:00)", 
            "Error de formato", 
            JOptionPane.ERROR_MESSAGE);
    } catch (SQLException ex) {
        // Capturar mensajes personalizados del stored procedure
        String mensajeError = ex.getMessage();
        
        if (mensajeError.contains("título de la función no puede estar vacío")) {
            JOptionPane.showMessageDialog(this, 
                "El título de la función es obligatorio", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
        } else if (mensajeError.contains("sala indicada no existe")) {
            JOptionPane.showMessageDialog(this, 
                "La sala con el ID proporcionado no existe", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
        } else if (mensajeError.contains("Debe seleccionar una sala existente")) {
            JOptionPane.showMessageDialog(this, 
                "Debe ingresar un ID de sala válido", 
                "Error de validación", 
                JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error al registrar función: " + mensajeError, 
                "Error de BD", 
                JOptionPane.ERROR_MESSAGE);
        }
        ex.printStackTrace();
    } finally {
        try {
            if (cst != null) cst.close();
            if (conn != null) conn.close();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}

    // Método para cargar datos en la tabla
    private void cargarTablaFunciones() {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            conn = Conexion.getConnection();
            String sql = "SELECT id_funcion, titulo_funcion, director, genero, " +
                         "duracion_minutos, fecha_fun, hora_fun, Sala_id_sala " +
                         "FROM Funcion ORDER BY id_funcion DESC";
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();

            // Crear modelo de tabla
            DefaultTableModel modelo = new DefaultTableModel();
            modelo.addColumn("ID");
            modelo.addColumn("Título");
            modelo.addColumn("Director");
            modelo.addColumn("Género");
            modelo.addColumn("Duración (min)");
            modelo.addColumn("Fecha");
            modelo.addColumn("Hora");
            modelo.addColumn("ID Sala");

            // Llenar el modelo con datos
            while (rs.next()) {
                Object[] fila = new Object[8];
                fila[0] = rs.getInt("id_funcion");
                fila[1] = rs.getString("titulo_funcion");
                fila[2] = rs.getString("director");
                fila[3] = rs.getString("genero");
                fila[4] = rs.getInt("duracion_minutos");
                fila[5] = rs.getDate("fecha_fun");
                fila[6] = rs.getTime("hora_fun");
                fila[7] = rs.getInt("Sala_id_sala");
                modelo.addRow(fila);
            }

            tablaFuncion.setModel(modelo);

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar funciones: " + ex.getMessage(), 
                "Error de BD", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pst != null) pst.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Método para seleccionar una función de la tabla
    private void seleccionarFuncion() {
        int filaSeleccionada = tablaFuncion.getSelectedRow();
        
        if (filaSeleccionada >= 0) {
            txtSala.setText(tablaFuncion.getValueAt(filaSeleccionada, 7).toString());
            txtFuncion.setText(tablaFuncion.getValueAt(filaSeleccionada, 1).toString());
            txtDirector.setText(tablaFuncion.getValueAt(filaSeleccionada, 2).toString());
            txtGenero.setText(tablaFuncion.getValueAt(filaSeleccionada, 3).toString());
            txtDuracion.setText(tablaFuncion.getValueAt(filaSeleccionada, 4).toString());
            txtFecha.setText(tablaFuncion.getValueAt(filaSeleccionada, 5).toString());
            txtHora.setText(tablaFuncion.getValueAt(filaSeleccionada, 6).toString());
        }
    }

    // Método para limpiar los campos
    private void limpiarCampos() {
        txtSala.setText("");
        txtFuncion.setText("");
        txtGenero.setText("");
        txtDirector.setText("");
        txtDuracion.setText("");
        txtFecha.setText("");
        txtHora.setText("");
    }

    // Main para probar
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AdminObras().setVisible(true);
        });
    }
}
