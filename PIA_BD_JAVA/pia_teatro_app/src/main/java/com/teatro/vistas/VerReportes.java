package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.util.Vector;

public class VerReportes extends JFrame {

    // --- Colores del tema ---
    private static final Color COLOR_FONDO = new Color(30, 41, 82);
    private static final Color COLOR_DORADO = new Color(255, 193, 7);
    private static final Color COLOR_PANEL = new Color(45, 55, 100);
    private static final Color COLOR_CARD = new Color(55, 65, 110);
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_FILA_PAR = new Color(60, 70, 115);
    private static final Color COLOR_FILA_IMPAR = new Color(50, 60, 105);

    private JTabbedPane tabbedPane;
    private JTable tablaReporteVentas;
    private JTable tablaReporteDisponibilidad;
    private JButton btnActualizar;
    private JLabel lblTotalVentas;
    private JLabel lblCantidadVentas;

    public VerReportes() {
        // --- Configuración de la ventana ---
        setTitle("📊 Reportes - Teatro FCFM");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        // --- Panel Superior (Header) ---
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(null);
        panelSuperior.setPreferredSize(new Dimension(1100, 100));
        panelSuperior.setBackground(COLOR_PANEL);
        panelSuperior.setBorder(BorderFactory.createMatteBorder(0, 0, 3, 0, COLOR_DORADO));

        JLabel lblTitulo = new JLabel("MÓDULO DE REPORTES");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setBounds(30, 20, 400, 30);
        panelSuperior.add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Consulte las estadísticas y disponibilidad del sistema");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setBounds(30, 55, 500, 25);
        panelSuperior.add(lblSubtitulo);

        // Botón actualizar
        btnActualizar = new JButton("🔄 ACTUALIZAR");
        btnActualizar.setBounds(900, 30, 170, 40);
        btnActualizar.setFont(new Font("Arial", Font.BOLD, 13));
        btnActualizar.setBackground(COLOR_DORADO);
        btnActualizar.setForeground(COLOR_FONDO);
        btnActualizar.setFocusPainted(false);
        btnActualizar.setBorderPainted(false);
        btnActualizar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnActualizar.addActionListener(e -> actualizarReportes());
        panelSuperior.add(btnActualizar);

        add(panelSuperior, BorderLayout.NORTH);

        // --- Panel de Pestañas ---
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 14));
        tabbedPane.setBackground(COLOR_FONDO);
        tabbedPane.setForeground(COLOR_TEXTO);

        // Pestaña 1: Reporte de Ventas
        JPanel panelVentas = crearPanelReporteVentas();
        tabbedPane.addTab("  💰 Reporte de Ventas  ", panelVentas);

        // Pestaña 2: Disponibilidad de Funciones
        JPanel panelDisponibilidad = crearPanelDisponibilidad();
        tabbedPane.addTab("  🎭 Disponibilidad de Funciones  ", panelDisponibilidad);

        add(tabbedPane, BorderLayout.CENTER);

        // Cargar datos iniciales
        cargarReporteVentas();
        cargarReporteDisponibilidad();
    }

    private JPanel crearPanelReporteVentas() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panel de estadísticas superiores
        JPanel panelEstadisticas = new JPanel();
        panelEstadisticas.setLayout(new GridLayout(1, 2, 20, 0));
        panelEstadisticas.setPreferredSize(new Dimension(1000, 100));
        panelEstadisticas.setBackground(COLOR_FONDO);

        // Tarjeta 1: Total de ventas
        JPanel cardTotal = crearTarjetaEstadistica("💵", "Total en Ventas", "$0.00");
        lblTotalVentas = (JLabel) cardTotal.getComponent(2); // Guardamos referencia
        panelEstadisticas.add(cardTotal);

        // Tarjeta 2: Cantidad de ventas
        JPanel cardCantidad = crearTarjetaEstadistica("📝", "Número de Ventas", "0");
        lblCantidadVentas = (JLabel) cardCantidad.getComponent(2);
        panelEstadisticas.add(cardCantidad);

        panel.add(panelEstadisticas, BorderLayout.NORTH);

        // Tabla de ventas
        tablaReporteVentas = crearTablaEstilizada();
        JScrollPane scrollPane = new JScrollPane(tablaReporteVentas);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_DORADO, 2));
        scrollPane.getViewport().setBackground(COLOR_CARD);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearPanelDisponibilidad() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBackground(COLOR_FONDO);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Encabezado informativo
        JPanel panelInfo = new JPanel();
        panelInfo.setLayout(new FlowLayout(FlowLayout.LEFT));
        panelInfo.setBackground(COLOR_PANEL);
        panelInfo.setPreferredSize(new Dimension(1000, 60));
        panelInfo.setBorder(BorderFactory.createLineBorder(COLOR_DORADO, 2));

        JLabel lblInfo = new JLabel("📌 Consulte la disponibilidad de asientos para todas las funciones programadas");
        lblInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblInfo.setForeground(COLOR_TEXTO);
        panelInfo.add(lblInfo);

        panel.add(panelInfo, BorderLayout.NORTH);

        // Tabla de disponibilidad
        tablaReporteDisponibilidad = crearTablaEstilizada();
        JScrollPane scrollPane = new JScrollPane(tablaReporteDisponibilidad);
        scrollPane.setBorder(BorderFactory.createLineBorder(COLOR_DORADO, 2));
        scrollPane.getViewport().setBackground(COLOR_CARD);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel crearTarjetaEstadistica(String icono, String titulo, String valor) {
        JPanel tarjeta = new JPanel();
        tarjeta.setLayout(null);
        tarjeta.setBackground(COLOR_PANEL);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Ícono
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Arial", Font.PLAIN, 40));
        lblIcono.setBounds(20, 15, 60, 60);
        tarjeta.add(lblIcono);

        // Título
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblTitulo.setForeground(new Color(200, 200, 200));
        lblTitulo.setBounds(90, 20, 300, 20);
        tarjeta.add(lblTitulo);

        // Valor
        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("Arial", Font.BOLD, 26));
        lblValor.setForeground(COLOR_DORADO);
        lblValor.setBounds(90, 45, 300, 35);
        tarjeta.add(lblValor);

        return tarjeta;
    }

    private JTable crearTablaEstilizada() {
        JTable tabla = new JTable();
        tabla.setFont(new Font("Arial", Font.PLAIN, 13));
        tabla.setRowHeight(30);
        tabla.setBackground(COLOR_CARD);
        tabla.setForeground(COLOR_TEXTO);
        tabla.setGridColor(new Color(70, 80, 120));
        tabla.setSelectionBackground(COLOR_DORADO);
        tabla.setSelectionForeground(COLOR_FONDO);
        tabla.setShowGrid(true);

        // Estilo del encabezado
        JTableHeader header = tabla.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBackground(COLOR_PANEL);
        header.setForeground(COLOR_DORADO);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Renderer para filas alternadas
        tabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(COLOR_FILA_PAR);
                    } else {
                        c.setBackground(COLOR_FILA_IMPAR);
                    }
                    c.setForeground(COLOR_TEXTO);
                } else {
                    c.setBackground(COLOR_DORADO);
                    c.setForeground(COLOR_FONDO);
                }
                
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        return tabla;
    }

    private void cargarReporteVentas() {
        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos");
            return;
        }

        String sql = "SELECT * FROM v_ReporteVentas";
        
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            
            tablaReporteVentas.setModel(buildTableModel(rs));
            calcularEstadisticasVentas();

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar el reporte de ventas: " + e.getMessage());
        } finally {
            Conexion.close(con);
        }
    }

    private void cargarReporteDisponibilidad() {
        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos");
            return;
        }

        String sql = "SELECT * FROM v_DisponibilidadFunciones";
        
        try (PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            tablaReporteDisponibilidad.setModel(buildTableModel(rs));

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar el reporte de disponibilidad: " + e.getMessage());
        } finally {
            Conexion.close(con);
        }
    }

    private void calcularEstadisticasVentas() {
        Connection con = Conexion.getConnection();
        if (con == null) {
            // No mostramos error de conexión aquí para no ser redundantes
            return; 
        }

        try {
            // --- 1. Calcular el Total en Ventas (Aislado) ---
            String sqlTotal = "SELECT SUM(total_venta) as total FROM Venta";
            
            try (PreparedStatement pstTotal = con.prepareStatement(sqlTotal);
                 ResultSet rsTotal = pstTotal.executeQuery()) {
                
                if (rsTotal.next()) {
                    double total = rsTotal.getDouble("total");
                    // Formato con signo de dólar y dos decimales
                    lblTotalVentas.setText(String.format("$%.2f", total)); 
                } else {
                     lblTotalVentas.setText("$0.00"); // En caso de que no haya ventas
                }
            }

            // --- 2. Calcular la Cantidad de Ventas (Aislado) ---
            String sqlCantidad = "SELECT COUNT(*) as cantidad FROM Venta";
            
            try (PreparedStatement pstCantidad = con.prepareStatement(sqlCantidad);
                 ResultSet rsCantidad = pstCantidad.executeQuery()) {
                 
                if (rsCantidad.next()) {
                    int cantidad = rsCantidad.getInt("cantidad");
                    lblCantidadVentas.setText(String.valueOf(cantidad));
                } else {
                    lblCantidadVentas.setText("0"); // En caso de que no haya ventas
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error de SQL al calcular estadísticas: " + e.getMessage());
        } finally {
            // Cerramos la conexión una sola vez
            Conexion.close(con);
        }
    }

    private void actualizarReportes() {
        cargarReporteVentas();
        cargarReporteDisponibilidad();
        JOptionPane.showMessageDialog(this, "Reportes actualizados correctamente", "Actualizado", JOptionPane.INFORMATION_MESSAGE);
    }

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

        return new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hacer la tabla no editable
            }
        };
    }
}
