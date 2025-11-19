package com.teatro.vistas;

import com.teatro.database.Conexion;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    // --- Colores del tema (basados en tu logo) ---
    private static final Color COLOR_FONDO = new Color(30, 41, 82); // Azul oscuro
    private static final Color COLOR_DORADO = new Color(255, 193, 7); // Dorado
    private static final Color COLOR_PANEL = new Color(45, 55, 100); // Azul medio
    private static final Color COLOR_TEXTO = Color.WHITE;
    private static final Color COLOR_HOVER = new Color(255, 215, 0); // Dorado claro

    // --- Componentes ---
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JLabel lblLogo;
    private JPanel panelPrincipal;
    private JPanel panelFormulario;

    public Login() {
        // --- Configuración de la ventana ---
        setTitle("Sistema de Teatro FCFM");
        setSize(500, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Panel principal con fondo azul oscuro
        panelPrincipal = new JPanel();
        panelPrincipal.setLayout(null);
        panelPrincipal.setBackground(COLOR_FONDO);
        setContentPane(panelPrincipal);

        // --- Logo (puedes reemplazar con tu imagen real) ---
        lblLogo = new JLabel();
        lblLogo.setBounds(150, 40, 200, 200);
        lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Aquí puedes cargar tu logo real:
        // ImageIcon logo = new ImageIcon(getClass().getResource("/logo_teatro.png"));
        // lblLogo.setIcon(new ImageIcon(logo.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH)));
        
        // Por ahora usamos texto estilizado
        lblLogo.setText("<html><div style='text-align: center;'>"
                + "<span style='font-size: 48px; color: #FFC107;'>🎭</span><br>"
                + "<span style='font-size: 24px; color: white; font-weight: bold;'>TEATRO</span><br>"
                + "<span style='font-size: 14px; color: #FFC107;'>FÍSICO-MATEMÁTICO</span>"
                + "</div></html>");
        panelPrincipal.add(lblLogo);

        // --- Panel del formulario (tarjeta) ---
        panelFormulario = new JPanel();
        panelFormulario.setLayout(null);
        panelFormulario.setBounds(50, 260, 400, 300);
        panelFormulario.setBackground(COLOR_PANEL);
        panelFormulario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 2),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panelPrincipal.add(panelFormulario);

        // --- Título del formulario ---
        JLabel lblTitulo = new JLabel("INICIAR SESIÓN");
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 20));
        lblTitulo.setForeground(COLOR_DORADO);
        lblTitulo.setBounds(120, 20, 200, 30);
        panelFormulario.add(lblTitulo);

        // --- Campo Usuario ---
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        lblUsuario.setForeground(COLOR_TEXTO);
        lblUsuario.setBounds(30, 70, 80, 25);
        panelFormulario.add(lblUsuario);

        txtUsuario = new JTextField();
        txtUsuario.setBounds(30, 100, 340, 40);
        txtUsuario.setFont(new Font("Arial", Font.PLAIN, 14));
        txtUsuario.setBackground(new Color(60, 70, 120));
        txtUsuario.setForeground(COLOR_TEXTO);
        txtUsuario.setCaretColor(COLOR_TEXTO);
        txtUsuario.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFormulario.add(txtUsuario);

        // --- Campo Contraseña ---
        JLabel lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
        lblContrasena.setForeground(COLOR_TEXTO);
        lblContrasena.setBounds(30, 150, 100, 25);
        panelFormulario.add(lblContrasena);

        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(30, 180, 340, 40);
        txtContrasena.setFont(new Font("Arial", Font.PLAIN, 14));
        txtContrasena.setBackground(new Color(60, 70, 120));
        txtContrasena.setForeground(COLOR_TEXTO);
        txtContrasena.setCaretColor(COLOR_TEXTO);
        txtContrasena.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_DORADO, 1),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        panelFormulario.add(txtContrasena);

        // --- Botón Ingresar con efecto hover ---
        btnLogin = new JButton("INGRESAR");
        btnLogin.setBounds(100, 240, 200, 45);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnLogin.setBackground(COLOR_DORADO);
        btnLogin.setForeground(COLOR_FONDO);
        btnLogin.setFocusPainted(false);
        btnLogin.setBorderPainted(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Efecto hover
        btnLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnLogin.setBackground(COLOR_HOVER);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnLogin.setBackground(COLOR_DORADO);
            }
        });
        
        panelFormulario.add(btnLogin);

        // --- Pie de página ---
        JLabel lblFooter = new JLabel("© 2025 Teatro FCFM - Sistema de Gestión");
        lblFooter.setFont(new Font("Arial", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(150, 150, 150));
        lblFooter.setBounds(120, 580, 300, 20);
        panelPrincipal.add(lblFooter);

        // --- Eventos ---
        
        // Enter en el campo de contraseña también hace login
        txtContrasena.addActionListener(e -> validarLogin());
        
        // Botón de login
        btnLogin.addActionListener(e -> validarLogin());
    }

    /**
     * Valida el usuario y la contraseña contra la base de datos.
     */
    private void validarLogin() {
        String usuario = txtUsuario.getText().trim();
        String contrasena = new String(txtContrasena.getPassword());

        // Validación básica
        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarError("Por favor complete todos los campos");
            return;
        }

        Connection con = Conexion.getConnection();
        if (con == null) {
            mostrarError("Error: No se pudo conectar a la base de datos.");
            return;
        }

        String sql = "SELECT id_empleado, Rol_id_rol, nombre_e FROM Empleado WHERE nombre_usuario = ? AND contrasenia = SHA2(?, 256)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, usuario);
            pst.setString(2, contrasena);

            try (ResultSet rs = pst.executeQuery()) {
                
                if (rs.next()) {
                    // ¡Login exitoso!
                    int idEmpleado = rs.getInt("id_empleado");
                    int idRol = rs.getInt("Rol_id_rol");
                    String nombreEmpleado = rs.getString("nombre_e");
                    
                    // Cerrar login
                    this.dispose();
                    
                    // Abrir menú principal
                    MenuPrincipal menu = new MenuPrincipal(idEmpleado, idRol, nombreEmpleado);
                    menu.setVisible(true);

                } else {
                    // Login fallido
                    mostrarError("Usuario o contraseña incorrectos");
                    txtContrasena.setText("");
                    txtUsuario.requestFocus();
                }
            }
        } catch (SQLException e) {
            mostrarError("Error al ejecutar la consulta: " + e.getMessage());
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }

    /**
     * Muestra un mensaje de error con estilo personalizado
     */
    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
            this,
            mensaje,
            "Error de Autenticación",
            JOptionPane.ERROR_MESSAGE
        );
    }

    // Main para probar el Login independientemente
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}
