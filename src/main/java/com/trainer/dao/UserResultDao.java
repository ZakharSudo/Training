package com.trainer.dao;


import com.trainer.model.UserResult;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserResultDao {

    public Map<String, Object> getUserProgress(UUID userId) throws SQLException {
    Map<String, Object> progress = new HashMap<>();
    String sql = "SELECT total_points, tasks_completed, test_points, error_spotting_points, open_tasks_points, last_activity FROM user_progress WHERE user_id = ?";
    
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
            progress.put("lastActivity", null);
        }
    }
    return progress;
}

    // Проверить, проходил ли пользователь задание
    public boolean hasUserCompletedTask(UUID userId, UUID taskId) throws SQLException {
        String sql = "SELECT 1 FROM user_results WHERE user_id = ? AND task_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setObject(2, taskId);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        }
    }

    // Сохранить результат выполнения
    public void saveResult(UserResult result) throws SQLException {
        String sql = "INSERT INTO user_results (id, user_id, task_id, status, points_awarded, user_answer, feedback) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, UUID.randomUUID());
            stmt.setObject(2, result.getUserId());
            stmt.setObject(3, result.getTaskId());
            stmt.setString(4, result.getStatus());
            stmt.setInt(5, result.getPointsAwarded());
            stmt.setString(6, result.getUserAnswer());
            stmt.setString(7, result.getFeedback());
            stmt.executeUpdate();
        }
    }

    // Обновить прогресс пользователя
    public void updateProgress(UUID userId, int pointsAwarded, String taskType) throws SQLException {
        String sql = "INSERT INTO user_progress (user_id, total_points, tasks_completed) " +
                "VALUES (?, ?, 1) " +
                "ON CONFLICT (user_id) DO UPDATE SET " +
                "total_points = user_progress.total_points + ?," +
                "tasks_completed = user_progress.tasks_completed + 1," +
                "last_activity = CURRENT_TIMESTAMP";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.setInt(2, pointsAwarded);
            stmt.setInt(3, pointsAwarded);
            stmt.executeUpdate();
        }

        // Обновляем специфичные баллы по типу задания
        String typeColumn = "";
        switch (taskType) {
            case "TEST":
                typeColumn = "test_points";
                break;
            case "ERROR_SPOTTING":
                typeColumn = "error_spotting_points";
                break;
            case "OPEN":
                typeColumn = "open_tasks_points";
                break;
        }

        if (!typeColumn.isEmpty()) {
            String updateTypeSql = "UPDATE user_progress SET " + typeColumn + " = " + typeColumn + " + ? WHERE user_id = ?";
            // Нужно открыть НОВОЕ соединение для этого запроса
            try (Connection conn2 = DatabaseConnection.getConnection();
                 PreparedStatement stmt2 = conn2.prepareStatement(updateTypeSql)) {
                stmt2.setInt(1, pointsAwarded);
                stmt2.setObject(2, userId);
                stmt2.executeUpdate();
            }
        }
    }
}