package com.teatro.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    // Tus credenciales de la base de datos
    // Usamos el usuario LIMITADO que creamos por seguridad
    private static final String URL = "jdbc:mysql://localhost:3306/pia_teatro";
    private static final String USUARIO = "app_taquilla";
    private static final String CONTRASENA = "JavaPass123"; // (O la que pusiste en el script 04)

    /**
     * Obtiene una conexión a la base de datos.
     * @return un objeto Connection o null si falla.
     */
    public static Connection getConnection() {
        Connection conexion = null;
        
        try {
            // 1. Registrar el driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // 2. Abrir la conexión
            conexion = DriverManager.getConnection(URL, USUARIO, CONTRASENA);
            
        } catch (ClassNotFoundException e) {
            System.out.println("Error: MySQL JDBC Driver no encontrado (Revisa tu pom.xml).");
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos (Revisa URL, Usuario, Contraseña).");
            e.printStackTrace();
        }
        
        return conexion;
    }

    /**
     * Cierra una conexión de forma segura.
     */
    public static void close(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error al cerrar la conexión.");
                e.printStackTrace();
            }
        }
    }
}