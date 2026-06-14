package com.example.dbsph4;

import com.example.dbsph4.gui.MainController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage stage) {
        try {
            System.out.println("[FX-TRACE] Loading programmatic layout using MainController...");

            // Instantiate our central pure code controller
            MainController mainController = new MainController();

            // Set up the scene with the root pane fetched from the controller
            Scene scene = new Scene(mainController.getView(), 1000, 700);
            stage.setTitle("CoreTech Store Management System");
            stage.setScene(scene);
            stage.show();

            System.out.println("[FX-SUCCESS] Full layout application frame loaded successfully!");

        } catch (Exception e) {
            System.err.println("[FX-ERROR] Critical startup error rendering view context.");
            e.printStackTrace();
        }
    }
}