package com.trainer.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Task {
    private UUID id;
    private String title;
    private String description;
    private String type;  // TEST, ERROR_SPOTTING, OPEN
    private int maxPoints;
    private String config;
    private LocalDateTime createdAt;

    // Конструкторы
    public Task() {}

    public Task(UUID id, String title, String description, String type,
                int maxPoints, String config, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.type = type;
        this.maxPoints = maxPoints;
        this.config = config;
        this.createdAt = createdAt;
    }

    // Геттеры и сеттеры
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public int getMaxPoints() { return maxPoints; }
    public void setMaxPoints(int maxPoints) { this.maxPoints = maxPoints; }

    public String getConfig() { return config; }
    public void setConfig(String config) { this.config = config; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}