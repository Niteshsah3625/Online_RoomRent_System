package com.roomrent.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Main JavaFX Application class — entry point of the Room Rent System.
 */
public class MainApp extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage;
        primaryStage.setTitle("Online Room Rent System");
        primaryStage.setResizable(true);
        navigateTo("login");
        primaryStage.show();
    }

    /**
     * Navigates to a named FXML view.
     * @param viewName the FXML filename without extension (e.g. "login", "dashboard")
     */
    public static void navigateTo(String viewName) {
        try {
            // FXML files are in project root in this repo, so load from root path.
            String path = "/" + viewName + ".fxml";
            URL resource = MainApp.class.getResource(path);
            if (resource == null) throw new IOException("FXML not found: " + path);
            Parent root = FXMLLoader.load(resource);
            Scene scene = new Scene(root);
            // Apply global stylesheet
            URL css = MainApp.class.getResource("/style.css");
            if (css != null) scene.getStylesheets().add(css.toExternalForm());
            primaryStage.setScene(scene);
            primaryStage.centerOnScreen();
        } catch (IOException e) {
            System.err.println("[MainApp] Navigation error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
