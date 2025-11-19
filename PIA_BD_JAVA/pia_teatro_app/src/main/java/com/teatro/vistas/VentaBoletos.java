package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class VentaBoletos extends JFrame {

    // --- Colores del tema ---
    private static final Color COLOR_FONDO = new Color(30, 41, 82);
    private static final Color COLOR_DORADO = new Color(255, 193, 7);
    private static final Color COLOR_PANEL = new Color(45, 55, 100);
    private static final Color COLOR_CARD = new Color(55, 65, 110);
    private static final Color COLOR_TEXTO = Color.WHITE;

    // --- Componentes ---
    private JComboBox<ComboBoxItem> cmbFuncion;
    private JComboBox<ComboBoxItem> cmbCliente;
    private JComboBox<ComboBoxItem> cmbMetodoPago;
    private JList<ComboBoxItem> listAsientos;
    private JButton btnConfirmarVenta;
    private JButton btnCancelar;
    private DefaultListModel<ComboBoxItem> listModelAsientos;
    private JLabel lblTotal;
    private JTextArea txtResumen;

    // --- Datos de Sesión ---
    private int idEmpleadoLogueado;

    public VentaBoletos(int idEmpleadoLogueado) {
        this.idEmpleadoLogueado = idEmpleadoLogueado;

        // --- Configuración de la Ventana ---
        setTitle("🎫 Venta de Boletos - Teatro FCFM");
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(null);

        // --- Encabezado ---
        JLabel lblTitulo = new JLabel("NUEVA VENTA DE BOLETOS");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setBounds(280, 20, 400, 35);
        add(lblTitulo);

        JLabel lblSubtitulo = new JLabel("Complete los datos para registrar la venta");
        lblSubtitulo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtitulo.setForeground(new Color(200, 200, 200));
        lblSubtitulo.setBounds(280, 55, 400, 25);
        add(lblSubtitulo);

        // --- Panel Izquierdo: Selección de Función y Cliente ---
        JPanel panelSeleccion = crearPanelConBorde("Datos de la Venta", 30, 100, 420, 350);
        add(panelSeleccion);

        // Función
        JLabel lblFuncion = crearLabel("Seleccione la Función:", 20, 30);
        panelSeleccion.add(lblFuncion);
        
        cmbFuncion = crearComboBox(20, 60, 380);
        panelSeleccion.add(cmbFuncion);

        // Cliente
        JLabel lblCliente = crearLabel("Seleccione el Cliente:", 20, 110);
        panelSeleccion.add(lblCliente);
        
        cmbCliente = crearComboBox(20, 140, 380);
        panelSeleccion.add(cmbCliente);

        // Método de Pago
        JLabel lblMetodo = crearLabel("Método de Pago:", 20, 190);
        panelSeleccion.add(lblMetodo);
        
        cmbMetodoPago = crearComboBox(20, 220, 380);
        panelSeleccion.add(cmbMetodoPago);

        // --- Panel Derecho: Asientos Disponibles ---
        JPanel panelAsientos = crearPanelConBorde("Asientos Disponibles", 470, 100, 400, 350);
        add(panelAsientos);

        JLabel lblInstruccion = new JLabel("Seleccione uno o varios asientos (Ctrl + Click):");
        lblInstruccion.setFont(new Font("Arial", Font.ITALIC, 12));
        lblInstruccion.setForeground(new Color(200, 200, 200));
        lblInstruccion.setBounds(20, 30, 360, 25);
        panelAsientos.add(lblInstruccion);

        listModelAsientos = new DefaultListModel<>();
        listAsientos = new JList<>(listModelAsientos);
        listAsientos.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listAsientos.setFont(new Font("Arial", Font.PLAIN, 13));
        listAsientos.setBackground(COLOR_CARD);
        listAsientos.setForeground(COLOR_TEXTO);
        listAsientos.setSelectionBackground(COLOR_DORADO);
        listAsientos.setSelectionForeground(COLOR_FONDO);
        
        JScrollPane scrollAsientos = new JScrollPane(listAsientos);
        scrollAsientos.setBounds(20, 60, 360, 260);
        scrollAsientos.setBorder(BorderFactory.createLineBorder(COLOR_DORADO, 1));
        panelAsientos.add(scrollAsientos);

        // --- Panel Inferior: Resumen y Botones ---
        JPanel panelInferior = crearPanelConBorde("Resumen de la Venta", 30, 470, 840, 160);
        add(panelInferior);

        txtResumen = new JTextArea();
        txtResumen.setEditable(false);
        txtResumen.setFont(new Font("Courier New", Font.PLAIN, 13));
        txtResumen.setBackground(COLOR_CARD);
        txtResumen.setForeground(COLOR_TEXTO);
        txtResumen.setText("Seleccione los asientos para ver el resumen...");
        
        JScrollPane scrollResumen = new JScrollPane(txtResumen);
        scrollResumen.setBounds(20, 30, 500, 100);
        scrollResumen.setBorder(BorderFactory.createLineBorder(COLOR_DORADO, 1));
        panelInferior.add(scrollResumen);

        // Total
        lblTotal = new JLabel("TOTAL: $0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotal.setForeground(COLOR_DORADO);
        lblTotal.setBounds(540, 40, 280, 30);
        panelInferior.add(lblTotal);

        // Botones
        btnConfirmarVenta = crearBotonPrincipal("CONFIRMAR VENTA", 540, 80);
        btnConfirmarVenta.addActionListener(e -> registrarVenta());
        panelInferior.add(btnConfirmarVenta);

        btnCancelar = crearBotonSecundario("CANCELAR", 690, 80);
        btnCancelar.addActionListener(e -> dispose());
        panelInferior.add(btnCancelar);

        // --- Eventos ---
        cmbFuncion.addActionListener(e -> cargarAsientosDisponibles());
        
        listAsientos.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarResumen();
            }
        });

        // --- Cargar datos iniciales ---
        cargarDatosIniciales();
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
        btn.setBounds(x, y, 140, 40);
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

    private JButton crearBotonSecundario(String texto, int x, int y) {
        JButton btn = new JButton(texto);
        btn.setBounds(x, y, 140, 40);
        btn.setFont(new Font("Arial", Font.BOLD, 12));
        btn.setBackground(new Color(108, 117, 125));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(new Color(90, 98, 104));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(new Color(108, 117, 125));
            }
        });
        
        return btn;
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
        listModelAsientos.clear();
        ComboBoxItem funcionSeleccionada = (ComboBoxItem) cmbFuncion.getSelectedItem();
        if (funcionSeleccionada == null) return;
        
        int funcionID = funcionSeleccionada.getId();
        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Error de conexión a la base de datos");
            return;
        }

        // Primero obtenemos la sala de la función
        String sql = "SELECT a.id_asiento, z.nombre_zona, a.fila, a.numero_asiento, " +
                     "COALESCE(pz.precio, 0) as precio " +
                     "FROM Asiento a " +
                     "JOIN Zona z ON a.Zona_id_zona = z.id_zona " +
                     "JOIN Funcion f ON z.Sala_id_sala = f.Sala_id_sala " +
                     "LEFT JOIN PrecioZona pz ON pz.Zona_id_zona = z.id_zona AND pz.Funcion_id_funcion = ? " +
                     "WHERE f.id_funcion = ? " +
                     "AND a.id_asiento NOT IN (" +
                     "  SELECT Asiento_id_asiento FROM Boleto WHERE Funcion_id_funcion = ?" +
                     ") " +
                     "ORDER BY z.nombre_zona, a.fila, a.numero_asiento";
        
        try (PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, funcionID);
            pst.setInt(2, funcionID);
            pst.setInt(3, funcionID);
            
            try (ResultSet rs = pst.executeQuery()) {
                int contador = 0;
                while (rs.next()) {
                    double precio = rs.getDouble("precio");
                    String desc = String.format("%s - Fila %s, Asiento %d ($%.2f)",
                        rs.getString("nombre_zona"),
                        rs.getString("fila"),
                        rs.getInt("numero_asiento"),
                        precio
                    );
                    listModelAsientos.addElement(new ComboBoxItem(rs.getInt("id_asiento"), desc));
                    contador++;
                }
                
                // Debug: mostrar cuántos asientos se encontraron
                if (contador == 0) {
                    txtResumen.setText("No hay asientos disponibles para esta función.\n" +
                                      "Puede que la función esté agotada o no tenga precios asignados.");
                    lblTotal.setText("TOTAL: $0.00");
                } else {
                    txtResumen.setText(contador + " asientos disponibles.\nSeleccione uno o varios para continuar.");
                    lblTotal.setText("TOTAL: $0.00");
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error al cargar asientos: " + e.getMessage(),
                "Error SQL",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }

    private void actualizarResumen() {
        List<ComboBoxItem> asientosSel = listAsientos.getSelectedValuesList();
        
        if (asientosSel.isEmpty()) {
            txtResumen.setText("Seleccione los asientos para ver el resumen...");
            lblTotal.setText("TOTAL: $0.00");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ASIENTOS SELECCIONADOS:\n");
        sb.append("─────────────────────────────────────────\n");
        
        double total = 0.0;
        int contador = 1;
        
        for (ComboBoxItem item : asientosSel) {
            String desc = item.getTexto();
            // Extraer el precio del texto (último valor entre paréntesis)
            int inicioPrec = desc.lastIndexOf("$") + 1;
            int finPrec = desc.lastIndexOf(")");
            double precio = Double.parseDouble(desc.substring(inicioPrec, finPrec));
            
            sb.append(String.format("%d. %s\n", contador++, desc));
            total += precio;
        }
        
        sb.append("─────────────────────────────────────────\n");
        sb.append(String.format("Cantidad de boletos: %d\n", asientosSel.size()));
        
        txtResumen.setText(sb.toString());
        lblTotal.setText(String.format("TOTAL: $%.2f", total));
    }

    private void registrarVenta() {
        ComboBoxItem funcionSel = (ComboBoxItem) cmbFuncion.getSelectedItem();
        ComboBoxItem clienteSel = (ComboBoxItem) cmbCliente.getSelectedItem();
        ComboBoxItem metodoSel = (ComboBoxItem) cmbMetodoPago.getSelectedItem();
        List<ComboBoxItem> asientosSel = listAsientos.getSelectedValuesList();

        if (funcionSel == null || clienteSel == null || metodoSel == null || asientosSel.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, complete todos los campos y seleccione al menos un asiento.",
                "Datos incompletos",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        StringBuilder asientosCSV = new StringBuilder();
        for (ComboBoxItem item : asientosSel) {
            if (asientosCSV.length() > 0) {
                asientosCSV.append(",");
            }
            asientosCSV.append(item.getId());
        }

        Connection con = Conexion.getConnection();
        if (con == null) return;
        
        String sql = "{CALL sp_RegistrarVenta(?, ?, ?, ?, ?)}";
        
        try (java.sql.CallableStatement cst = con.prepareCall(sql)) {
            cst.setInt(1, clienteSel.getId());
            cst.setInt(2, this.idEmpleadoLogueado);
            cst.setInt(3, metodoSel.getId());
            cst.setInt(4, funcionSel.getId());
            cst.setString(5, asientosCSV.toString());
            
            ResultSet rs = cst.executeQuery();
            
            if (rs.next()) {
                String mensaje = rs.getString("Mensaje");
                if (mensaje.contains("éxito")) {
                    JOptionPane.showMessageDialog(this,
                        "¡Venta registrada exitosamente!\n\n" +
                        "ID de Venta: " + rs.getInt("ID de Venta") + "\n" +
                        "Total: $" + String.format("%.2f", rs.getDouble("Monto Total")),
                        "Venta Exitosa",
                        JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Error: " + mensaje, 
                        "Error en Venta", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error de SQL: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }
}