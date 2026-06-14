package com.example.dbsph4.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Database credentials constants
    private static final String URL = "jdbc:mysql://localhost:3306/CoreTechStore";
    private static final String USER = "root";
    private static final String PASSWORD = "1230853";

    /**
     * Establishes a connection to the MySQL database.
     * Includes detailed console tracking for step-by-step error detection.
     * @return Connection object if successful, null otherwise.
     */
    public static Connection getConnection() {
        Connection connection = null;
        try {
            // Step 1: Log the connection attempt
            System.out.println("[DB-TRACE] Attempting to connect to database: " + URL);

            // Step 2: Establish connection
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Step 3: Log successful connection
            System.out.println("[DB-SUCCESS] Successfully connected to CoreTechStore database!");

        } catch (SQLException e) {
            // Comprehensive error handling based on course slides
            System.err.println("[DB-ERROR] Connection failed! Check database status, URL, or credentials.");
            System.err.println("[DB-ERROR-MESSAGE] " + e.getMessage());
            System.err.println("[DB-ERROR-CODE] SQL State: " + e.getSQLState() + " | Error Code: " + e.getErrorCode());
            e.printStackTrace(); // Prints the exact line number of the failure
        }
        return connection;
    }

    /**
     * Safely closes an open database connection.
     * @param connection The connection object to close.
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("[DB-TRACE] Database connection closed safely.");
            } catch (SQLException e) {
                System.err.println("[DB-ERROR] Failed to close database connection.");
                e.printStackTrace();
            }
        }
    }
}