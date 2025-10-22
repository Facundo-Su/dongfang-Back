package com.dongfang.dongfang.service;

import com.dongfang.dongfang.model.Etiqueta;
import com.dongfang.dongfang.model.Volante;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Service
public class ConsultaPrecioEtiqueta {

    private Sheets service;
    private String spreadsheetId;


    public ConsultaPrecioEtiqueta(@Value("${google.spreadsheet.id}") String spreadsheetId) throws Exception {
        this.spreadsheetId = spreadsheetId;

        JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

        // Leer el JSON completo desde Environment Variable
        String jsonContent = System.getenv("GOOGLE_SHEETS_CREDENTIALS");
        if (jsonContent == null) {
            throw new IllegalStateException("La variable GOOGLE_SHEETS_CREDENTIALS no está definida");
        }

        InputStream credentialsStream = new ByteArrayInputStream(jsonContent.getBytes());

        GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                .createScoped(Collections.singleton("https://www.googleapis.com/auth/spreadsheets"));

        service = new Sheets.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                jsonFactory,
                new HttpCredentialsAdapter(credentials))
                .setApplicationName("Consulta Google Sheets")
                .build();
    }

    public String obtenerPrecio(Etiqueta etiqueta) throws Exception {
        int cantidad = etiqueta.getCantidad();

        // Determinar el paso según el rango de cantidad
        int paso;
        if (cantidad <= 300) {
            paso = 50;
        } else if (cantidad <= 1000) {
            paso = 100;
        } else if (cantidad <= 10000) {
            paso = 1000;
        } else if (cantidad <= 50000) {
            paso = 5000;
        } else {
            // Si es mayor a 50.000, calculamos proporcionalmente
            String precio50kStr = buscarPrecioExacto(50000, etiqueta);
            if (precio50kStr == null) {
                return null;
            }

            try {
                double precio50k = Double.parseDouble(precio50kStr.replaceAll("[^0-9.]", ""));
                double precioProporcional = precio50k / 50000.0 * cantidad;
                return String.valueOf(precioProporcional);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        if (cantidad % paso == 0) {
            return buscarPrecioExacto(cantidad, etiqueta);
        }

        int cantidadInferior = (cantidad / paso) * paso;
        int cantidadSuperior = cantidadInferior + paso;

        String precioInferiorStr = buscarPrecioExacto(cantidadInferior, etiqueta);
        String precioSuperiorStr = buscarPrecioExacto(cantidadSuperior, etiqueta);


        if (precioInferiorStr == null || precioSuperiorStr == null) {
            return null;
        }

        try {
            double precioInferior = Double.parseDouble(precioInferiorStr.replaceAll("[^0-9.]", ""));
            double precioSuperior = Double.parseDouble(precioSuperiorStr.replaceAll("[^0-9.]", ""));
            double precioInterpolado = precioInferior +
                    (precioSuperior - precioInferior) * (cantidad - cantidadInferior) / (cantidadSuperior - cantidadInferior);

            return String.valueOf((int) Math.round(precioInterpolado));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String buscarPrecioExacto(int cantidad, Etiqueta etiqueta) throws Exception {
        String rango = "Etiquetas!A10:L40";

        ValueRange response = service.spreadsheets().values().get(spreadsheetId, rango).execute();
        List<List<Object>> valores = response.getValues();

        if (valores == null || valores.isEmpty()) {
            return null;
        }

        List<Object> filaTamano = valores.size() > 1 ? valores.get(0) : null;

        for (List<Object> fila : valores) {
            if (fila.isEmpty()) continue;

            int valorFila;
            try {
                valorFila = Integer.parseInt(fila.get(0).toString().replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                continue;
            }

            if (valorFila != cantidad) continue;

            String precio = buscarPrecioEnFila(fila, filaTamano, etiqueta);
            if (precio != null) {
                return precio;
            }
        }

        return null;
    }

    private String buscarPrecioEnFila(List<Object> fila, List<Object> filaTamano, Etiqueta etiqueta) throws Exception {
        if (filaTamano == null) {
            return null;
        }

        double area = etiqueta.getAncho() * etiqueta.getLargo();

        int lastColumn = filaTamano.size();

        for (int col = 1; col < lastColumn; col++) {  // Revisar si conviene empezar en 0
            Object celdaObj = filaTamano.get(col);
            String celdaStr = (celdaObj != null) ? celdaObj.toString().trim() : "";

            if (celdaStr.isEmpty()) {
                continue;
            }

            double limite;
                // Extrae solo los números y posibles decimales
            String soloNumero = celdaStr.replaceAll("[^0-9.]", "");
            limite = Double.parseDouble(soloNumero);



            if (area <= limite) {
                double A1 =this.obtenerValorDDD_A1();
                double precioBase = (fila.get(col) != null) ?
                        Double.parseDouble(fila.get(col).toString().trim().replaceAll("[^0-9.]", "")) : 0;

                double precioFinal;

                switch (etiqueta.getTipo()) {
                    case "Tamano Especial":
                        precioFinal = Math.floor((precioBase * 1.5 + A1 * 2) / 10) * 10;
                        break;
                    case "Transparente Vinilo":
                    case "Vinilo":
                    case "Dorado":
                        precioFinal = Math.floor((precioBase * 1.9 + A1 * 2) / 10) * 10;
                        break;
                    case "Normal":
                    default:
                        precioFinal = precioBase;
                        break;
                }

                // Devolver como String
                return String.valueOf((int) precioFinal);
            }

        }

        return null;
    }

    private double obtenerValorDDD_A1() throws Exception {
        String rango = "DDD!A1";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, rango).execute();
        List<List<Object>> valores = response.getValues();

        if (valores == null || valores.isEmpty()) {
            throw new IllegalStateException("No se encontró valor en DDD!A1");
        }

        Object a1Obj = valores.get(0).get(0);
        String a1Str = (a1Obj != null) ? a1Obj.toString().trim() : "0";
        return Double.parseDouble(a1Str.replaceAll("[^0-9.]", ""));
    }





}
