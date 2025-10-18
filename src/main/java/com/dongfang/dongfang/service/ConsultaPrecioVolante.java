package com.dongfang.dongfang.service;

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
public class ConsultaPrecioVolante {

    private Sheets service;
    private String spreadsheetId;


    public ConsultaPrecioVolante(@Value("${google.spreadsheet.id}") String spreadsheetId) throws Exception {
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


    public String obtenerValorPorCantidadYColumnas(Volante volante) throws Exception {
        int cantidad = volante.getCantidad();

        if (cantidad % 1000 == 0) {
            return buscarPrecioExacto(cantidad, volante);
        } else {
            int superior = ((cantidad / 1000) + 1) * 1000;
            int inferior = (cantidad / 1000) * 1000;

            String precioSuperiorStr = buscarPrecioExacto(superior, volante);
            String precioInferiorStr = buscarPrecioExacto(inferior, volante);

            if (precioSuperiorStr == null || precioInferiorStr == null) {
                return null;
            }

            try {
                double precioSuperior = Double.parseDouble(precioSuperiorStr.replaceAll("[^0-9.]", ""));
                double precioInferior = Double.parseDouble(precioInferiorStr.replaceAll("[^0-9.]", ""));
                double precioInterpolado = precioInferior + (precioSuperior - precioInferior) * (cantidad - inferior) / (superior - inferior);
                return String.valueOf((int) Math.round(precioInterpolado));
            } catch (Exception e) {
                return null;
            }
        }
    }

    private String buscarPrecioExacto(int cantidad, Volante volante) throws Exception {
        String rango = "Volantes!A1:Z50";
        ValueRange response = service.spreadsheets().values().get(spreadsheetId, rango).execute();
        List<List<Object>> valores = response.getValues();

        if (valores == null || valores.isEmpty()) return null;

        List<Object> filaTamano = valores.size() > 1 ? valores.get(1) : null;
        List<Object> filaColor = valores.size() > 2 ? valores.get(2) : null;
        List<Object> filaTipo = valores.size() > 3 ? valores.get(3) : null;

        for (List<Object> fila : valores) {
            if (fila.isEmpty()) continue;

            int valorFila;
            try {
                valorFila = Integer.parseInt(fila.get(0).toString().replaceAll("[^0-9]", ""));
            } catch (Exception e) {
                continue;
            }

            if (valorFila != cantidad) continue;

            String precio = buscarPrecioEnFila(fila, filaTamano, filaColor, filaTipo, volante);
            if (precio != null) return precio;
        }

        return null;
    }

    private String buscarPrecioEnFila(List<Object> fila, List<Object> filaTamano, List<Object> filaColor, List<Object> filaTipo, Volante volante) {
        if (filaTamano == null || filaColor == null || filaTipo == null) return null;

        int lastColumn = Math.min(filaTamano.size(), Math.min(filaColor.size(), filaTipo.size()));
        int matchCount = 0;

        for (int col = 1; col < lastColumn; col++) {
            String s = filaTamano.get(col).toString().trim();
            String c = filaColor.get(col).toString().trim();
            String t = filaTipo.get(col).toString().trim();

            if (s.equalsIgnoreCase(volante.getTamanio()) &&
                    c.equalsIgnoreCase(volante.getColor()) &&
                    t.equalsIgnoreCase(volante.getTipo())) {

                matchCount++;
                if (matchCount == 2) {
                    return fila.get(col).toString().trim();
                }
            }
        }

        return null;
    }
}
