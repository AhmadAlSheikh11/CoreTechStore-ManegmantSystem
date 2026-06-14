package com.example.dbsph4;

import com.example.dbsph4.database.DatabaseConnection;
import javafx.application.Application;
import java.sql.Connection;

public class Launcher {
    public static void main(String[] args) {
        // Step 1: Step-by-step database connection trace before UI starts
        System.out.println("[LAUNCHER-TRACE] Starting pre-flight database connection check...");
        Connection testConn = DatabaseConnection.getConnection();

        if (testConn != null) {
            System.out.println("[LAUNCHER-SUCCESS] Database connection check passed safely.");
            DatabaseConnection.closeConnection(testConn);
        } else {
            System.err.println("[LAUNCHER-WARN] Database connection check failed! Features relying on DB may not work.");
        }

        // Step 2: Launch the actual JavaFX Application
        System.out.println("[LAUNCHER-TRACE] Launching JavaFX application lifecycle...");
        Application.launch(Main.class, args);
    }
}