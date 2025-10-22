package com.dongfang.dongfang.service;

import com.dongfang.dongfang.model.Mensaje;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;

@Service
public class ChatService {

    private final Map<String, List<Mensaje>> mensajesPorUsuario = new ConcurrentHashMap<>();
    private static final int MAX_MENSAJES = 50; // límite de mensajes por usuario

    OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    public List<Mensaje> getMensajes(String idUsuario){
        return mensajesPorUsuario.getOrDefault(idUsuario, new ArrayList<>());
    }

    public Mensaje enviarMensaje(Mensaje mensajeUsuario){
        mensajesPorUsuario.putIfAbsent(mensajeUsuario.getIdUser(), new ArrayList<>());
        List<Mensaje> historial = mensajesPorUsuario.get(mensajeUsuario.getIdUser());

        historial.add(mensajeUsuario);

        // Limitar la lista a los últimos MAX_MENSAJES
        if (historial.size() > MAX_MENSAJES) {
            historial.subList(0, historial.size() - MAX_MENSAJES).clear();
        }

        List<Mensaje> ultimosMensajes = historial.stream()
                .skip(Math.max(0, historial.size() - 20))
                .toList();

        String prompt = "Eres un asistente de impresión amable y cercano. Debes responder siempre en el mismo idioma que el usuario. \n" +
                "Tipos de impresión posibles: Volante (中文: 传单), Tarjeta (中文: 名片), Etiqueta (中文: 贴纸)\n" +
                "\n" +
                "Instrucciones:\n" +
                "1. Si el usuario menciona explícitamente un producto, devuelve solo el nombre del producto en español.\n" +
                "2. Si el usuario da indicios contextuales sobre para qué necesita la impresión, sugiere el producto más probable y pide confirmación de forma amigable en el mismo idioma del usuario (por ejemplo: 'Parece que quieres un volante para tus clientes, ¿es correcto?').\n" +
                "3. Si el usuario responde de cualquier forma que indique confirmación, aunque no sea literalmente 'sí', 'ok' o equivalente, devuelve solo el nombre del producto en español.\n" +
                "4. Si no se detecta ningún producto ni contexto, haz una pregunta breve, amigable y entusiasta en el idioma del usuario para guiarlo, mostrando interés por sus necesidades.\n" +
                "5. No agregues explicaciones ni saludos innecesarios.\n" +
                "6. Asegúrate de que la conversación se sienta cercana, comprensiva y con un toque emocional ligero, para que el usuario perciba que le estás ayudando.\n" +
                "\n" +
                "Historial reciente:" +
                ultimosMensajes.stream()
                        .map(m -> m.getRole() + ": " + m.getMensaje())
                        .reduce("", (a,b) -> a + "\n" + b);

        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(prompt)
                .model("gpt-5-nano")
                .build();

        Response response = client.responses().create(params);

        StringBuilder sb = new StringBuilder();
        response.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .forEach(texto -> sb.append(texto.text()));

        Mensaje mensajeRobot = new Mensaje("assistant", sb.toString());
        historial.add(mensajeRobot);

        // Limitar de nuevo después de agregar el mensaje del asistente
        if (historial.size() > MAX_MENSAJES) {
            historial.subList(0, historial.size() - MAX_MENSAJES).clear();
        }

        System.out.println("ID Usuario: " + mensajeUsuario.getIdUser());
        System.out.println("Historial actual: " + historial.size() + " mensajes");

        return mensajeRobot;
    }
}
