package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.model.UserResult;
import com.trainer.service.TaskSubmissionService;
import com.trainer.util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SubmitTaskHandler implements HttpHandler {
    private final TaskSubmissionService submissionService = new TaskSubmissionService();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length < 4) {
                throw new Exception("Неверный формат URL");
            }
            UUID taskId = UUID.fromString(parts[3]);

            String userIdHeader = exchange.getRequestHeaders().getFirst("X-User-Id");
            if (userIdHeader == null) {
                throw new Exception("Требуется аутентификация");
            }
            UUID userId = UUID.fromString(userIdHeader);

            // ВАЖНО: читаем тело в UTF-8!
            String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
            System.out.println("SubmitTaskHandler received body: " + body);

            Map<String, String> request = JsonUtil.fromJson(body, Map.class);
            String answer = request.get("answer");

            UserResult result = submissionService.submitAnswer(userId, taskId, answer);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("pointsAwarded", result.getPointsAwarded());
            response.put("feedback", result.getFeedback());

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
            exchange.sendResponseHeaders(400, errorBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorBytes);
            os.close();
        }
    }
}