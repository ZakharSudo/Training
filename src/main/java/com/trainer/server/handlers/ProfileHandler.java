package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.dao.UserResultDao;
import com.trainer.util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ProfileHandler implements HttpHandler {
    private final UserResultDao userResultDao = new UserResultDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String userIdHeader = exchange.getRequestHeaders().getFirst("X-User-Id");
            if (userIdHeader == null) {
                throw new Exception("Требуется аутентификация");
            }
            UUID userId = UUID.fromString(userIdHeader);

            // Получаем прогресс из БД
            Map<String, Object> progress = userResultDao.getUserProgress(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("progress", progress);

            String jsonResponse = JsonUtil.toJson(response);
            byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            String jsonError = JsonUtil.toJson(error);
            byte[] bytes = jsonError.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}