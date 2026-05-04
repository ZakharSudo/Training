package com.trainer.service;

import com.trainer.dao.UserDao;
import com.trainer.model.User;
import com.trainer.util.PasswordHasher;

import java.sql.SQLException;
import java.util.UUID;

public class UserService {
    private final UserDao userDao = new UserDao();

    // Регистрация
    public User register(String email, String password, String fullName) throws SQLException {
        // Проверяем, не существует ли уже пользователь
        User existing = userDao.findByEmail(email);
        if (existing != null) {
            throw new RuntimeException("Пользователь с таким email уже существует");
        }

        // Хэшируем пароль
        String passwordHash = PasswordHasher.hashPassword(password);

        // Создаём пользователя
        return userDao.createUser(email, passwordHash, fullName);
    }

    // Логин
    public User login(String email, String password) throws SQLException {
        User user = userDao.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        // Проверяем пароль
        if (!PasswordHasher.checkPassword(password, user.getPasswordHash())) {
            throw new RuntimeException("Неверный пароль");
        }

        // Обновляем время последнего входа
        userDao.updateLastLogin(user.getId());

        return user;
    }
}