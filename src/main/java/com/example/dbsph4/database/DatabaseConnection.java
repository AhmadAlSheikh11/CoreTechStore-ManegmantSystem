package com.example.dbsph4.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String URL      = "jdbc:mysql://localhost:3306/CoreTechStore";
    private static final String USER     = "root";
    private static final String PASSWORD = "1230853";

    /**
     * Establishes a connection to the CoreTechStore MySQL database.
     * Always sets autoCommit = true so that every single-statement write
     * (INSERT / UPDATE / DELETE) is committed immediately on execution.
     * Multi-statement transactions (e.g. checkout) must call
     * conn.setAutoCommit(false) themselves and commit/rollback manually.
     *
     * @return an open Connection, or null if the connection fails.
     */
    public static Connection getConnection() {
        try {
            System.out.println("[DB-TRACE] Connecting to: " + URL);
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);

            // ── CRITICAL: guarantee auto-commit for single-statement writes ──
            conn.setAutoCommit(true);

            System.out.println("[DB-SUCCESS] Connected to CoreTechStore database.");
            return conn;
        } catch (SQLException e) {
            System.err.println("[DB-ERROR] Connection failed — check MySQL status, URL, and credentials.");
            System.err.println("[DB-ERROR] " + e.getMessage() +
                    " | SQLState: " + e.getSQLState() +
                    " | Code: "    + e.getErrorCode());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Safely closes an open database connection.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB-TRACE] Connection closed.");
            } catch (SQLException e) {
                System.err.println("[DB-ERROR] Failed to close connection.");
                e.printStackTrace();
            }
        }
    }
}