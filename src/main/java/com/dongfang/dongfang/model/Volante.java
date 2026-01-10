package com.dongfang.dongfang.model;

public class Volante {
    private int cantidad;
    private String tamanio;
    private String color;
    private String tipo;
    private String ancho;
    private String largo;

    // Getters y setters
    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }

    public String getAncho() {
        return ancho;
    }

    public void setAncho(String ancho) {
        this.ancho = ancho;
    }

    public String getLargo() {
        return largo;
    }

    public void setLargo(String largo) {
        this.largo = largo;
    }

    public String getTamanio() { return tamanio; }
    public void setTamanio(String tamanio) { this.tamanio = tamanio; }
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    @Override
    public String toString() {
        return "Volante{" +
                "cantidad=" + cantidad +
                ", tamanio='" + tamanio + '\'' +
                ", color='" + color + '\'' +
                ", tipo='" + tipo + '\'' +
                ", ancho='" + ancho + '\'' +
                ", largo='" + largo + '\'' +
                '}';
    }

}

