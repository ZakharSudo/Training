package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.dao.DatabaseConnection;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.trainer.util.JsonUtil;

public class ProfileHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            // Получаем userId из заголовка
            String userIdHeader = exchange.getRequestHeaders().getFirst("X-User-Id");
            if (userIdHeader == null) {
                throw new Exception("Требуется аутентификация");
            }
            UUID userId = UUID.fromString(userIdHeader);

            // Получаем прогресс из БД
            String sql = "SELECT * FROM user_progress WHERE user_id = ?";
            Map<String, Object> progress = new HashMap<>();

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setObject(1, userId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    progress.put("totalPoints", rs.getInt("total_points"));
                    progress.put("tasksCompleted", rs.getInt("tasks_completed"));
                    progress.put("testPoints", rs.getInt("test_points"));
                    progress.put("errorSpottingPoints", rs.getInt("error_spotting_points"));
                    progress.put("openTasksPoints", rs.getInt("open_tasks_points"));
                    progress.put("lastActivity", rs.getTimestamp("last_activity").toString());
                } else {
                    progress.put("totalPoints", 0);
                    progress.put("tasksCompleted", 0);
                    progress.put("testPoints", 0);
                    progress.put("errorSpottingPoints", 0);
                    progress.put("openTasksPoints", 0);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("progress", progress);

            String jsonResponse = JsonUtil.toJson(response);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, jsonResponse.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(jsonResponse.getBytes());
            os.close();

        } catch (Exception e) {
            String error = JsonUtil.toJson(Map.of("success", false, "error", e.getMessage()));
            exchange.sendResponseHeaders(500, error.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(error.getBytes());
            os.close();
        }
    }
}