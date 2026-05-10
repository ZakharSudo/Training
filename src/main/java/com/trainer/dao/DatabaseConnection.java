package com.trainer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/trainer_db";
    private static final String USER = "trainer";
    private static final String PASSWORD = "trainer123";

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("PostgreSQL JDBC Driver not found!");
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("Connecting to: " + URL);
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}