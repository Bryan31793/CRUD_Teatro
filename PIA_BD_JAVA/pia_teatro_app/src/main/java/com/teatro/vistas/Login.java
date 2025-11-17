package com.teatro.vistas;

import com.teatro.database.Conexion; // Importa tu clase de conexión
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login extends JFrame {

    // --- Componentes de la interfaz ---
    private JTextField txtUsuario;
    private JPasswordField txtContrasena;
    private JButton btnLogin;
    private JLabel lblUsuario;
    private JLabel lblContrasena;

    public Login() {
        // --- Configuración básica de la ventana ---
        setTitle("Login - Sistema de Teatro");
        setSize(400, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Centrar en pantalla
        setLayout(null); // Usaremos layout absoluto simple

        // 1. Etiqueta Usuario
        lblUsuario = new JLabel("Usuario:");
        lblUsuario.setBounds(50, 30, 80, 25);
        add(lblUsuario);

        // 2. Campo de texto Usuario
        txtUsuario = new JTextField();
        txtUsuario.setBounds(150, 30, 180, 25);
        add(txtUsuario);

        // 3. Etiqueta Contraseña
        lblContrasena = new JLabel("Contraseña:");
        lblContrasena.setBounds(50, 70, 80, 25);
        add(lblContrasena);

        // 4. Campo de texto Contraseña
        txtContrasena = new JPasswordField();
        txtContrasena.setBounds(150, 70, 180, 25);
        add(txtContrasena);

        // 5. Botón de Login
        btnLogin = new JButton("Ingresar");
        btnLogin.setBounds(150, 110, 100, 30);
        add(btnLogin);

        // --- Esta es la Lógica Clave ---
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                validarLogin();
            }
        });
    }

    /**
     * Valida el usuario y la contraseña contra la base de datos.
     */
    private void validarLogin() {
        String usuario = txtUsuario.getText();
        String contrasena = new String(txtContrasena.getPassword());

        Connection con = Conexion.getConnection();
        if (con == null) {
            JOptionPane.showMessageDialog(this, "Error: No se pudo conectar a la base de datos.", "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Esta es la consulta MÁS IMPORTANTE de tu login.
        // 1. Compara el usuario.
        // 2. Compara la contraseña usando SHA2() (Tu Requisito de Encriptación)
        // 3. Retorna el ID del empleado y su ROL.
        String sql = "SELECT id_empleado, Rol_id_rol FROM Empleado WHERE nombre_usuario = ? AND contrasenia = SHA2(?, 256)";

        try (PreparedStatement pst = con.prepareStatement(sql)) {
            
            pst.setString(1, usuario);
            pst.setString(2, contrasena);

            try (ResultSet rs = pst.executeQuery()) {
                
                if (rs.next()) {
                    // ¡Login exitoso!
                    int idEmpleado = rs.getInt("id_empleado");
                    int idRol = rs.getInt("Rol_id_rol");
                    
                    JOptionPane.showMessageDialog(this, "¡Bienvenido! Rol ID: " + idRol);
                    
                    // Aquí es donde "modelas" la aplicación:
                    // 1. Cierras la ventana de Login
                    this.dispose(); 
                    
                    // 2. Abres el Menú Principal, pasándole el ROL
                    // (Esta clase 'MenuPrincipal' es la que crearemos a continuación)
                    MenuPrincipal menu = new MenuPrincipal(idEmpleado, idRol);
                    menu.setVisible(true);

                } else {
                    // Login fallido
                    JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.", "Error de Login", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al ejecutar la consulta: " + e.getMessage(), "Error de SQL", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            Conexion.close(con);
        }
    }
}