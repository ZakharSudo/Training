package com.trainer.server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.trainer.dao.TaskDao;
import com.trainer.model.Task;
import com.trainer.util.JsonUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("tasks", tasks);

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
            exchange.sendResponseHeaders(500, errorBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(errorBytes);
            os.close();
        }
    }
}