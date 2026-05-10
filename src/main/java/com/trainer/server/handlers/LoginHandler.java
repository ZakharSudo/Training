package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }
        
        try {
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> request = JsonUtil.fromJson(body, Map.class);
            
            String email = request.get("email");
            String password = request.get("password");
            
            // Временное решение: любой логин успешен
            // В реальном проекте нужно проверять пароль
            UUID userId = UUID.randomUUID();
            String token = UUID.randomUUID().toString();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId.toString());
            response.put("email", email);
            response.put("role", "STUDENT");
            response.put("token", token);
            
            String jsonResponse = JsonUtil.toJson(response);
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
            
        } catch (Exception e) {
            String error = JsonUtil.toJson(Map.of("success", false, "error", e.getMessage()));
            byte[] errorBytes = error.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(401, errorBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorBytes);
            os.close();
        }
    }
}