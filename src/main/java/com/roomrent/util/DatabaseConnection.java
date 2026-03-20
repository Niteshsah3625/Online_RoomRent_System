package com.roomrent.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Singleton DatabaseConnection utility.
 * Demonstrates: Singleton Pattern, Resource Management.
 */
public class DatabaseConnection {

    // ─── CONFIGURE THESE ───────────────────────────────────────────
    private static final String URL      = "jdbc:mysql://localhost:3306/room_rent_db";
    private static final String USER     = "root";
    private static final String PASSWORD = ""; // Set your MySQL password
    // ───────────────────────────────────────────────────────────────

    private static Connection connection = null;

    // Private constructor prevents external instantiation (Singleton)
    private DatabaseConnection() {}

    /**
     * Returns a shared MySQL connection instance.
     * Creates tables on first connection.
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("[DB] Connection established successfully.");
                initializeTables();
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found. Add mysql-connector-java to classpath.", e);
            }
        }
        return connection;
    }

    /**
     * Creates all required tables if they don't exist.
     */
    private static void initializeTables() {
        try (Statement stmt = connection.createStatement()) {

            // Users table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id       INT AUTO_INCREMENT PRIMARY KEY,
                    full_name     VARCHAR(100) NOT NULL,
                    email         VARCHAR(100) UNIQUE NOT NULL,
                    phone         VARCHAR(20),
                    password      VARCHAR(255) NOT NULL,
                    role          ENUM('OWNER','TENANT') NOT NULL,
                    address       VARCHAR(255),
                    occupation    VARCHAR(100),
                    num_people    INT DEFAULT 1,
                    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            // Rooms table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS rooms (
                    room_id        INT AUTO_INCREMENT PRIMARY KEY,
                    title          VARCHAR(150) NOT NULL,
                    description    TEXT,
                    location       VARCHAR(255) NOT NULL,
                    price_per_month DOUBLE NOT NULL,
                    room_type      ENUM('SINGLE','DOUBLE','STUDIO','APARTMENT') DEFAULT 'SINGLE',
                    status         ENUM('AVAILABLE','BOOKED','UNAVAILABLE') DEFAULT 'AVAILABLE',
                    owner_id       INT NOT NULL,
                    max_occupants  INT DEFAULT 1,
                    has_wifi       BOOLEAN DEFAULT FALSE,
                    has_parking    BOOLEAN DEFAULT FALSE,
                    has_furniture  BOOLEAN DEFAULT FALSE,
                    image_url      VARCHAR(500),
                    created_at     TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (owner_id) REFERENCES users(user_id) ON DELETE CASCADE
                )
            """);

            // Bookings table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS bookings (
                    booking_id    INT AUTO_INCREMENT PRIMARY KEY,
                    room_id       INT NOT NULL,
                    tenant_id     INT NOT NULL,
                    start_date    DATE NOT NULL,
                    end_date      DATE NOT NULL,
                    total_amount  DOUBLE,
                    status        ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
                    message       TEXT,
                    booking_date  DATE DEFAULT (CURRENT_DATE),
                    FOREIGN KEY (room_id) REFERENCES rooms(room_id) ON DELETE CASCADE,
                    FOREIGN KEY (tenant_id) REFERENCES users(user_id) ON DELETE CASCADE
                )
            """);

            System.out.println("[DB] Tables initialized.");

        } catch (SQLException e) {
            System.err.println("[DB] Table creation error: " + e.getMessage());
        }
    }

    /**
     * Closes the current connection.
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("[DB] Connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("[DB] Error closing connection: " + e.getMessage());
        }
    }
}
