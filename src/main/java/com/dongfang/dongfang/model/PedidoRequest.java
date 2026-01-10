package com.dongfang.dongfang.model;

import com.fasterxml.jackson.databind.JsonNode;

public class PedidoRequest {

    private String tipoProducto; // "Etiqueta" | "Volante"
    private JsonNode producto;   // JSON dinámico
    private DatosCliente cliente;

    public String getTipoProducto() {
        return tipoProducto;
    }

    public void setTipoProducto(String tipoProducto) {
        this.tipoProducto = tipoProducto;
    }

    public JsonNode getProducto() {
        return producto;
    }

    public void setProducto(JsonNode producto) {
        this.producto = producto;
    }

    public DatosCliente getCliente() {
        return cliente;
    }

    public void setCliente(DatosCliente cliente) {
        this.cliente = cliente;
    }
}