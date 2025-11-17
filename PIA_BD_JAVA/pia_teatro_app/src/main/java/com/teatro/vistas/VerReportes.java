package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Vector;

public class VerReportes extends JFrame {

    private JTabbedPane tabbedPane;
    private JTable tablaReporteVentas;
    private JTable tablaReporteDisponibilidad;

    public VerReportes() {
        setTitle("Módulo de Reportes");
        setSize(900, 600); // Ventana grande para los reportes
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        // --- Pestaña 1: Reporte de Ventas (para el Director) ---
        JPanel panelVentas = new JPanel(new java.awt.BorderLayout());
        tablaReporteVentas = new JTable();
        panelVentas.add(new JScrollPane(tablaReporteVentas), java.awt.BorderLayout.CENTER);
        tabbedPane.addTab("Reporte de Ventas", panelVentas);

        // --- Pestaña 2: Reporte de Disponibilidad (para el Admin) ---
        JPanel panelDisponibilidad = new JPanel(new java.awt.BorderLayout());
        tablaReporteDisponibilidad = new JTable();
        panelDisponibilidad.add(new JScrollPane(tablaReporteDisponibilidad), java.awt.BorderLayout.CENTER);
        tabbedPane.addTab("Disponibilidad de Funciones", panelDisponibilidad);

        // Añadir el panel de pestañas a la ventana
        add(tabbedPane);

        // Cargar los datos
        cargarReporteVentas();
        cargarReporteDisponibilidad();
    }

    /**
     * Carga los datos de la vista v_ReporteVentas en la JTable.
     */
    private void cargarReporteVentas() {
        Connection con = Conexion.getConnection();
        if (con == null) return;

        String sql = "SELECT * FROM v_ReporteVentas";
        
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            // Usamos un DefaultTableModel para construir la tabla
            tablaReporteVentas.setModel(buildTableModel(rs));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar el reporte de ventas.");
        } finally {
            Conexion.close(con);
        }
    }

    /**
     * Carga los datos de la vista v_DisponibilidadFunciones en la JTable.
     */
    private void cargarReporteDisponibilidad() {
        Connection con = Conexion.getConnection();
        if (con == null) return;

        String sql = "SELECT * FROM v_DisponibilidadFunciones";
        
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            tablaReporteDisponibilidad.setModel(buildTableModel(rs));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar el reporte de disponibilidad.");
        } finally {
            Conexion.close(con);
        }
    }

    /**
     * Método genérico para convertir un ResultSet en un TableModel.
     * Es una utilidad muy poderosa.
     * @param rs El ResultSet de la consulta.
     * @return Un DefaultTableModel con los datos.
     * @throws SQLException
     */
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();

        // Nombres de las columnas
        Vector<String> columnNames = new Vector<>();
        int columnCount = metaData.getColumnCount();
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnLabel(column));
        }

        // Datos de las filas
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>();
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        return new DefaultTableModel(data, columnNames);
    }
}