package com.trainer.dao;

import com.trainer.model.User;

import java.sql.*;
import java.util.UUID;

public class UserDao {

    // Регистрация нового пользователя
    public User createUser(String email, String passwordHash, String fullName) throws SQLException {
        String sql = "INSERT INTO users (id, email, password_hash, full_name, role) VALUES (?, ?, ?, ?, 'STUDENT') RETURNING id, created_at";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            UUID id = UUID.randomUUID();
            stmt.setObject(1, id);
            stmt.setString(2, email);
            stmt.setString(3, passwordHash);
            stmt.setString(4, fullName);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setId(id);
                user.setEmail(email);
                user.setPasswordHash(passwordHash);
                user.setFullName(fullName);
                user.setRole("STUDENT");
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                return user;
            }
        }
        return null;
    }

    // Поиск пользователя по email
    public User findByEmail(String email) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId((UUID) rs.getObject("id"));
                user.setEmail(rs.getString("email"));
                user.setPasswordHash(rs.getString("password_hash"));
                user.setFullName(rs.getString("full_name"));
                user.setRole(rs.getString("role"));
                user.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                if (rs.getTimestamp("last_login") != null) {
                    user.setLastLogin(rs.getTimestamp("last_login").toLocalDateTime());
                }
                return user;
            }
        }
        return null;
    }

    // Обновление времени последнего входа
    public void updateLastLogin(UUID userId) throws SQLException {
        String sql = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setObject(1, userId);
            stmt.executeUpdate();
        }
    }
}