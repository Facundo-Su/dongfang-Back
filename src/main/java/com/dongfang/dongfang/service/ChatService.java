package com.dongfang.dongfang.service;

import com.dongfang.dongfang.model.Mensaje;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;


@Service
public class ChatService {

    private final Map<String, List<Mensaje>> mensajesPorUsuario = new ConcurrentHashMap<>();

    OpenAIClient client = OpenAIOkHttpClient.fromEnv();

    public List<Mensaje> getMensajes(String idUsuario){
        return mensajesPorUsuario.getOrDefault(idUsuario, new ArrayList<>());
    }

    public Mensaje enviarMensaje(Mensaje mensajeUsuario){
        mensajesPorUsuario.putIfAbsent(mensajeUsuario.getIdUser(), new ArrayList<>());
        List<Mensaje> historial = mensajesPorUsuario.get(mensajeUsuario.getIdUser());

        historial.add(mensajeUsuario);

        List<Mensaje> ultimosMensajes = historial.stream()
                .skip(Math.max(0, historial.size() - 20))
                .toList();

        String prompt = "Eres un asistente de impresión. \n" +
                "Tipos de impresión posibles: Volante (中文: 传单), Tarjeta (中文: 名片), Folleto (中文: 宣传册).\n" +
                "\n" +
                "Instrucciones:\n" +
                "1. Si el usuario menciona un producto, devuelve solo el nombre del producto en el idioma español.\n" +
                "2. Si no se detecta ningún producto, haz una pregunta breve en el idioma del usuario para guiarlo.\n" +
                "3. No agregues explicaciones ni saludos, pero permite que la pregunta cambie según lo que el usuario ya dijo.\n" +
                "4. Si no se identifica un producto, formula una **pregunta breve diferente** que guíe al usuario.\n"+
                "\n" +
                "Historial reciente:"
                + ultimosMensajes.stream()
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

        System.out.println("ID Usuario: " + mensajeUsuario.getIdUser());
        System.out.println("Historial actual: " + historial.size() + " mensajes");

        return mensajeRobot;
    }
}
