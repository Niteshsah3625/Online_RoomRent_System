package com.roomrent.controller;

import com.roomrent.app.MainApp;
import com.roomrent.model.User;
import com.roomrent.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;

/**
 * LoginController — handles user login UI logic.
 * Demonstrates MVC: Controller coordinates between View (FXML) and Service.
 */
public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;
    @FXML private Button loginButton;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        // Allow pressing Enter in password field to trigger login
        passwordField.setOnAction(e -> handleLogin());
    }

    @FXML
    private void handleLogin() {
        String email    = emailField.getText().trim();
        String password = passwordField.getText();

        errorLabel.setVisible(false);

        try {
            User user = authService.login(email, password);
            if (user == null) {
                showError("Invalid email or password. Please try again.");
                return;
            }
            // Navigate based on role
            if ("OWNER".equals(user.getRole())) {
                MainApp.navigateTo("owner-dashboard");
            } else {
                MainApp.navigateTo("tenant-dashboard");
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void goToRegister() {
        MainApp.navigateTo("register");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
