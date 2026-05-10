package com.trainer;

import com.sun.net.httpserver.HttpServer;
import com.trainer.server.handlers.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

public class Launcher {
    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(8888), 0);

        server.createContext("/api/register", new RegisterHandler());
        server.createContext("/api/login", new LoginHandler());
        server.createContext("/api/tasks", new GetTasksHandler());
        server.createContext("/api/tasks/", new SubmitTaskHandler());
        server.createContext("/api/profile", new ProfileHandler());

        // Тестовый эндпоинт
        server.createContext("/ping", exchange -> {
            String response = "{\"pong\":true}";
            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, bytes.length);
            exchange.getResponseBody().write(bytes);
            exchange.getResponseBody().close();
        });

        server.setExecutor(null);

server.createContext("/test", exchange -> {
    String response = "{\"status\":\"ok\",\"message\":\"Server works!\"}";
    byte[] bytes = response.getBytes(java.nio.charset.StandardCharsets.UTF_8);
    exchange.getResponseHeaders().set("Content-Type", "application/json");
    exchange.sendResponseHeaders(200, bytes.length);
    exchange.getResponseBody().write(bytes);
    exchange.getResponseBody().close();
});

        server.start();

        System.out.println("==========================================");
        System.out.println("🚀 Сервер тренажёра аналитика запущен!");
        System.out.println("==========================================");
        System.out.println("📍 Адрес: http://localhost:8888");
        System.out.println();
        System.out.println("📋 Доступные эндпоинты:");
        System.out.println("  GET    /ping                      - тест");
        System.out.println("  POST   /api/register              - регистрация");
        System.out.println("  POST   /api/login                 - вход");
        System.out.println("  GET    /api/tasks                 - список заданий");
        System.out.println("  POST   /api/tasks/{id}/submit     - отправить ответ");
        System.out.println("  GET    /api/profile               - мой прогресс");
        System.out.println("==========================================");
    }
}