package com.teatro.vistas;

/**
 * Un objeto simple para guardar un ID (valor) y un Texto (etiqueta)
 * en los JComboBox.
 */
public class ComboBoxItem {
    private int id;
    private String texto;

    public ComboBoxItem(int id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public int getId() {
        return id;
    }

    public String getTexto() {
        return texto;
    }

    // Esto es lo que el JComboBox mostrará en la lista
    @Override
    public String toString() {
        return texto;
    }
}