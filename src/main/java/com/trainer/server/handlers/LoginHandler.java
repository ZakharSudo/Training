package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.model.User;
import com.trainer.service.UserService;
import com.trainer.util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

public class LoginHandler implements HttpHandler {
    private final UserService userService = new UserService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            // Читаем тело запроса
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            Map<String, String> request = JsonUtil.fromJson(body, Map.class);

            String email = request.get("email");
            String password = request.get("password");

            // Аутентификация
            User user = userService.login(email, password);

            // В реальном проекте здесь нужно создать токен сессии
            // Для демо используем временный токен
            String token = UUID.randomUUID().toString();

            String response = JsonUtil.toJson(Map.of(
                    "success", true,
                    "userId", user.getId().toString(),
                    "email", user.getEmail(),
                    "role", user.getRole(),
                    "token", token
            ));

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, response.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();

        } catch (Exception e) {
            String error = JsonUtil.toJson(Map.of("success", false, "error", e.getMessage()));
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(401, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }
}