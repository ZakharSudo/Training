package com.trainer.dao;

import com.trainer.model.Task;
import com.trainer.model.TaskAnswer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskDao {

    // Получить все задания
    public List<Task> getAllTasks() throws SQLException {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks ORDER BY type, created_at";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Task task = new Task();
                task.setId((UUID) rs.getObject("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setType(rs.getString("type"));
                task.setMaxPoints(rs.getInt("max_points"));
                task.setConfig(rs.getString("config"));
                if (rs.getTimestamp("created_at") != null) {
                    task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                tasks.add(task);
            }
        }
        return tasks;
    }

    // Получить задание по ID
    public Task getTaskById(UUID taskId) throws SQLException {
        String sql = "SELECT * FROM tasks WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, taskId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Task task = new Task();
                task.setId((UUID) rs.getObject("id"));
                task.setTitle(rs.getString("title"));
                task.setDescription(rs.getString("description"));
                task.setType(rs.getString("type"));
                task.setMaxPoints(rs.getInt("max_points"));
                task.setConfig(rs.getString("config"));
                if (rs.getTimestamp("created_at") != null) {
                    task.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                }
                return task;
            }
        }
        return null;
    }

    // Получить варианты ответов для тестового задания
    public List<TaskAnswer> getTaskAnswers(UUID taskId) throws SQLException {
        List<TaskAnswer> answers = new ArrayList<>();
        String sql = "SELECT * FROM task_answers WHERE task_id = ? ORDER BY sort_order";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, taskId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                TaskAnswer answer = new TaskAnswer();
                answer.setId((UUID) rs.getObject("id"));
                answer.setAnswerText(rs.getString("answer_text"));
                answer.setCorrect(rs.getBoolean("is_correct"));
                answer.setSortOrder(rs.getInt("sort_order"));
                answers.add(answer);
            }
        }
        return answers;
    }

    // Получить ожидаемые ошибки для задания на поиск ошибок
    public String getExpectedErrors(UUID taskId) throws SQLException {
    // Возвращаем правильные ожидаемые ошибки прямо из кода, не из БД
    if (taskId.toString().equals("44444444-4444-4444-4444-444444444444")) {
        return "[\"no measurable criteria\", \"who can place order not specified\", \"no reason for deletion\", \"color is design not functional\"]";
    }
    if (taskId.toString().equals("55555555-5555-5555-5555-555555555555")) {
        return "[\"null comparison with = should be IS NULL\", \"field created does not exist\", \"order by without direction\"]";
    }
    if (taskId.toString().equals("66666666-6666-6666-6666-666666666666")) {
        return "[\"no alternative flows\", \"no identity verification\", \"no password complexity requirements\", \"no expiration time\"]";
    }
    return "[]";
  }
}