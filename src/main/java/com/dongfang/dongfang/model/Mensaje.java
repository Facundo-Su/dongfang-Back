package com.dongfang.dongfang.model;

import lombok.*;


public class Mensaje {

    // Getters y Setters
    private String role;
    private String mensaje;

    private String idUser;

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }



    public String getRole() {
        return role;
    }

    public String getMensaje() {
        return mensaje;
    }

    // Constructor vacío
    public Mensaje() {}

    // Constructor con parámetros
    public Mensaje(String role, String mensaje) {
        this.role = role;
        this.mensaje = mensaje;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    // ToString (opcional, útil para debug)
    @Override
    public String toString() {
        return "Mensaje{" +
                "role='" + role + '\'' +
                ", mensaje='" + mensaje + '\'' +
                '}';
    }
}
