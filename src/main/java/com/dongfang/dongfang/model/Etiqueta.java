package com.dongfang.dongfang.model;

public class Etiqueta {
    // Atributos
    private double ancho;   // en cm
    private double largo;   // en cm
    private int cantidad;
    private String tipo;    // Normal, Tamaño Especial, etc.

    // Constructor vacío
    public Etiqueta() {
    }

    // Constructor completo
    public Etiqueta(double ancho, double largo, int cantidad, String tipo) {
        this.ancho = ancho;
        this.largo = largo;
        this.cantidad = cantidad;
        this.tipo = tipo;
    }

    // Getters y Setters
    public double getAncho() {
        return ancho;
    }

    public void setAncho(double ancho) {
        this.ancho = ancho;
    }

    public double getLargo() {
        return largo;
    }

    public void setLargo(double largo) {
        this.largo = largo;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    // Método para mostrar los datos
    @Override
    public String toString() {
        return "Etiqueta {" +
                "ancho=" + ancho +
                ", largo=" + largo +
                ", cantidad=" + cantidad +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}
