package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.dao.TaskDao;
import com.trainer.model.Task;
import com.trainer.util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetTasksHandler implements HttpHandler {
    private final TaskDao taskDao = new TaskDao();

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            return;
        }

        try {
            List<Task> tasks = taskDao.getAllTasks();
            
            List<Map<String, Object>> simplifiedTasks = tasks.stream()
                .map(task -> {
                    Map<String, Object> t = new HashMap<>();
                    t.put("id", task.getId());
                    t.put("title", task.getTitle());
                    t.put("description", task.getDescription());
                    t.put("type", task.getType());
                    t.put("maxPoints", task.getMaxPoints());
                    return t;
                })
                .collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tasks", simplifiedTasks);
            
            String jsonResponse = JsonUtil.toJson(response);
            byte[] bytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
            
            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Database error: " + e.getMessage());
            String jsonError = JsonUtil.toJson(error);
            byte[] bytes = jsonError.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(500, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}