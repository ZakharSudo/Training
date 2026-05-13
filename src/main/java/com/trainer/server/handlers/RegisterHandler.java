package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.dao.UserDao;
import com.trainer.model.User;
import com.trainer.util.JsonUtil;
import com.trainer.util.PasswordHasher;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RegisterHandler implements HttpHandler {
    private final UserDao userDao = new UserDao();

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
            String fullName = request.getOrDefault("fullName", "");

            // Проверяем, существует ли пользователь
            User existing = userDao.findByEmail(email);
            if (existing != null) {
                throw new RuntimeException("Пользователь с таким email уже существует");
            }

            // Хэшируем пароль
            String passwordHash = PasswordHasher.hashPassword(password);
            
            // СОХРАНЯЕМ В БАЗУ ДАННЫХ!
            User user = userDao.createUser(email, passwordHash, fullName);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", user.getId().toString());
            response.put("email", user.getEmail());
            response.put("role", user.getRole());

            String jsonResponse = JsonUtil.toJson(response);
            byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(201, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            String jsonError = JsonUtil.toJson(error);
            byte[] errorBytes = jsonError.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(400, errorBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(errorBytes);
            }
        }
    }
}