package com.trainer.service;

import java.util.HashSet;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.trainer.dao.TaskDao;
import com.trainer.dao.UserResultDao;
import com.trainer.model.Task;
import com.trainer.model.TaskAnswer;
import com.trainer.model.UserResult;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

public class TaskSubmissionService {
    private final TaskDao taskDao = new TaskDao();
    private final UserResultDao userResultDao = new UserResultDao();
    private final Gson gson = new Gson();

    public UserResult submitAnswer(UUID userId, UUID taskId, String answer) throws Exception {
        if (userResultDao.hasUserCompletedTask(userId, taskId)) {
            throw new Exception("Вы уже проходили это задание");
        }

        Task task = taskDao.getTaskById(taskId);
        if (task == null) {
            throw new Exception("Задание не найдено");
        }

        int points = 0;
        String feedback = "";

        switch (task.getType()) {
            case "TEST":
                points = checkTestAnswer(taskId, answer, task.getMaxPoints());
                feedback = "Вы получили " + points + " из " + task.getMaxPoints() + " баллов";
                break;
            case "ERROR_SPOTTING":
                points = checkErrorSpotting(taskId, answer, task.getMaxPoints());
                feedback = "Вы нашли ошибок на " + points + " из " + task.getMaxPoints() + " баллов";
                break;
            case "OPEN":
                points = checkOpenTask(taskId, answer, task.getMaxPoints());
                feedback = "Автоматическая проверка: " + points + " из " + task.getMaxPoints() + " баллов";
                break;
            default:
                throw new Exception("Неизвестный тип задания");
        }

        UserResult result = new UserResult();
        result.setUserId(userId);
        result.setTaskId(taskId);
        result.setStatus("COMPLETED");
        result.setPointsAwarded(points);
        result.setUserAnswer(answer);
        result.setFeedback(feedback);

        userResultDao.saveResult(result);
        userResultDao.updateProgress(userId, points, task.getType());

        return result;
    }

    private int checkTestAnswer(UUID taskId, String answer, int maxPoints) throws Exception {
    List<TaskAnswer> allAnswers = taskDao.getTaskAnswers(taskId);
    List<TaskAnswer> correctAnswers = allAnswers.stream()
            .filter(TaskAnswer::isCorrect)
            .collect(Collectors.toList());
    
    // Парсим ответ пользователя (формат: "id1,id2" или просто "id1")
    Set<UUID> userAnswerIds = new HashSet<>();
    for (String part : answer.split(",")) {
        try {
            userAnswerIds.add(UUID.fromString(part.trim()));
        } catch (IllegalArgumentException e) {
            // Если пришёл текст, а не UUID — ищем совпадение по тексту
            String trimmed = part.trim().toLowerCase();
            allAnswers.stream()
                .filter(a -> a.getAnswerText().toLowerCase().contains(trimmed))
                .findFirst()
                .ifPresent(a -> userAnswerIds.add(a.getId()));
        }
    }
    
    Set<UUID> correctIds = correctAnswers.stream()
            .map(TaskAnswer::getId)
            .collect(Collectors.toSet());
    
    // Подсчёт правильных ответов
    long userCorrect = userAnswerIds.stream().filter(correctIds::contains).count();
    
    // Процент правильных ответов
    if (correctIds.isEmpty()) return 0;
    
    double percentage = (double) userCorrect / correctIds.size();
    int points = (int) (percentage * maxPoints);
    
    // Если все ответы правильные — даём полный балл
    if (userCorrect == correctIds.size() && userCorrect == userAnswerIds.size()) {
        points = maxPoints;
    }
    
    return Math.min(points, maxPoints);
}

    private int checkErrorSpotting(UUID taskId, String answer, int maxPoints) throws Exception {
        String expectedErrorsJson = taskDao.getExpectedErrors(taskId);
        Type listType = new TypeToken<List<String>>(){}.getType();
        List<String> expectedErrors = gson.fromJson(expectedErrorsJson, listType);

        List<String> userErrors = Arrays.asList(answer.toLowerCase().split("[,\\n]"));
        userErrors = userErrors.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        List<String> normalizedExpected = expectedErrors.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());

        long foundCount = 0;
        for (String userError : userErrors) {
            for (String expected : normalizedExpected) {
                if (expected.contains(userError) || userError.contains(expected)) {
                    foundCount++;
                    break;
                }
            }
        }

        if (foundCount == 0) return 0;
        return (int) ((double) foundCount / normalizedExpected.size() * maxPoints);
    }

    private int checkOpenTask(UUID taskId, String answer, int maxPoints) throws Exception {
        Task task = taskDao.getTaskById(taskId);
        String config = task.getConfig();

        @SuppressWarnings("unchecked")
        Map<String, Object> configMap = gson.fromJson(config, Map.class);
        List<String> keywords = (List<String>) configMap.get("keywords");

        if (keywords == null || keywords.isEmpty()) {
            return maxPoints;
        }

        String lowerAnswer = answer.toLowerCase();
        int foundKeywords = 0;
        for (String keyword : keywords) {
            if (lowerAnswer.contains(keyword.toLowerCase())) {
                foundKeywords++;
            }
        }

        if (foundKeywords == 0) return 0;
        return (int) ((double) foundKeywords / keywords.size() * maxPoints);
    }
}