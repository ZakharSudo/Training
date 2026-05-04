package com.trainer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = System.getenv().getOrDefault("DB_URL", "jdbc:postgresql://localhost:5432/train_db");
    private static final String USER = System.getenv().getOrDefault("DB_USER", "ilya");
    private static final String PASSWORD = System.getenv().getOrDefault("DB_PASSWORD", "ilya123");

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to: " + URL + " as user: " + USER);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}