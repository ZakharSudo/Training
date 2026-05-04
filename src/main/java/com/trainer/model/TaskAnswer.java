package com.trainer.model;

import java.util.UUID;

public class TaskAnswer {
    private UUID id;
    private String answerText;
    private boolean correct;
    private int sortOrder;

    // Конструкторы, геттеры, сеттеры
    public TaskAnswer() {}

    public TaskAnswer(UUID id, String answerText, boolean correct, int sortOrder) {
        this.id = id;
        this.answerText = answerText;
        this.correct = correct;
        this.sortOrder = sortOrder;
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getAnswerText() { return answerText; }
    public void setAnswerText(String answerText) { this.answerText = answerText; }

    public boolean isCorrect() { return correct; }
    public void setCorrect(boolean correct) { this.correct = correct; }

    public int getSortOrder() { return sortOrder; }
    public void setSortOrder(int sortOrder) { this.sortOrder = sortOrder; }
}