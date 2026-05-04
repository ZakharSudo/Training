package com.trainer.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserResult {
    private UUID id;
    private UUID userId;
    private UUID taskId;
    private String status;
    private int pointsAwarded;
    private String userAnswer;
    private String feedback;
    private LocalDateTime completedAt;

    // Конструкторы, геттеры, сеттеры
    public UserResult() {}

    public UserResult(UUID id, UUID userId, UUID taskId, String status,
                      int pointsAwarded, String userAnswer, String feedback,
                      LocalDateTime completedAt) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.status = status;
        this.pointsAwarded = pointsAwarded;
        this.userAnswer = userAnswer;
        this.feedback = feedback;
        this.completedAt = completedAt;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getPointsAwarded() { return pointsAwarded; }
    public void setPointsAwarded(int pointsAwarded) { this.pointsAwarded = pointsAwarded; }

    public String getUserAnswer() { return userAnswer; }
    public void setUserAnswer(String userAnswer) { this.userAnswer = userAnswer; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
}