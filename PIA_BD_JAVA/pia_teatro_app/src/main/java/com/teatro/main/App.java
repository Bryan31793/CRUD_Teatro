package com.teatro.main;

import com.teatro.vistas.Login; // Importa tu futura clase Login
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class App {
    public static void main(String[] args) {
        
        // Esto hace que la interfaz Swing se vea moderna (opcional pero recomendado)
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Llama a la ventana de Login y la hace visible
        // SwingUtilities.invokeLater asegura que la GUI se cree en el hilo correcto
        SwingUtilities.invokeLater(() -> {
            new Login().setVisible(true);
        });
    }
}