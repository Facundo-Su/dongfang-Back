package com.dongfang.dongfang.model;

import lombok.Getter;

@Getter
public class DatosCliente {
    // Getters y Setters
    private String nombreLocal;
    private String direccion;
    private String localidad;
    private String contacto; // puede ser teléfono o WeChat

    // Constructor vacío
    public DatosCliente() {
    }

    // Constructor completo
    public DatosCliente(String nombreLocal, String direccion, String localidad, String contacto) {
        this.nombreLocal = nombreLocal;
        this.direccion = direccion;
        this.localidad = localidad;
        this.contacto = contacto;
    }

    public void setNombreLocal(String nombreLocal) {
        this.nombreLocal = nombreLocal;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setLocalidad(String localidad) {
        this.localidad = localidad;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getNombreLocal() {
        return nombreLocal;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getLocalidad() {
        return localidad;
    }

    public String getContacto() {
        return contacto;
    }

    // Para depuración o logs
    @Override
    public String toString() {
        return "DatosCliente{" +
                "nombreLocal='" + nombreLocal + '\'' +
                ", direccion='" + direccion + '\'' +
                ", localidad='" + localidad + '\'' +
                ", contacto='" + contacto + '\'' +
                '}';
    }
}
