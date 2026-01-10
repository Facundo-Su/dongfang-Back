package com.dongfang.dongfang.service;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.BatchUpdateValuesRequest;
import com.google.api.services.sheets.v4.model.ValueRange;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PedidoService {

    private Sheets service;
    private String spreadsheetId;




    public PedidoService(@Value("${GOOGLE_SPREADSHEET_ID_REGISTROS}") String spreadsheetId) throws Exception {
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

    public void guardarPedido(
            String nombre,
            String direccion,
            String telefono,
            String tipoProducto,
            String largo,
            String ancho,
            String color,
            String cantidad,
            String papel,
            String precio
    ) throws Exception {

        // 1️⃣ Crear la fila desde A hasta S (puedes ajustar hasta AA)
        List<Object> filaValores = new ArrayList<>();
        filaValores.add(nombre);       // A
        filaValores.add(direccion);    // B
        filaValores.add("");           // C
        filaValores.add("");           // D
        filaValores.add(telefono);     // E
        filaValores.add("");           // F
        filaValores.add(tipoProducto); // G
        filaValores.add(largo);        // H
        filaValores.add("");           // I
        filaValores.add(ancho);        // J
        filaValores.add("");           // K
        filaValores.add(color);        // L
        filaValores.add(cantidad);     // M
        filaValores.add("");           // N
        filaValores.add(papel);        // O
        filaValores.add("");           // P
        filaValores.add("");           // Q
        filaValores.add("");           // R
        filaValores.add(precio);       // S

        ValueRange body = new ValueRange().setValues(List.of(filaValores));

        // 2️⃣ Leer la columna donde siempre habrá un dato (por ejemplo la columna I, que es donde se pone el nombre)
        ValueRange existing = service.spreadsheets().values()
                .get(spreadsheetId, "Pedidos!I:I")  // Si A está vacía, mejor usar otra columna que siempre tenga datos
                .execute();

        // 3️⃣ Calcular la primera fila vacía real
        int nextRow;
        if (existing.getValues() != null && !existing.getValues().isEmpty()) {
            nextRow = existing.getValues().size() + 1;
        } else {
            nextRow = 1;
        }

        // 4️⃣ Escribir exactamente en esa fila
        String rango = "Pedidos!I" + nextRow + ":AA" + nextRow;
        service.spreadsheets().values()
                .update(spreadsheetId, rango, body)
                .setValueInputOption("USER_ENTERED")
                .execute();
    }




}
