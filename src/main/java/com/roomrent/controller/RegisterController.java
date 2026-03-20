package com.roomrent.controller;

import com.roomrent.app.MainApp;
import com.roomrent.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * RegisterController — handles new user registration for both Owner and Tenant roles.
 */
public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField extraField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private ComboBox<String> roleCombo;
    @FXML private Label errorLabel;
    @FXML private Label extraLabel;
    @FXML private Spinner<Integer> peopleSpinner;
    @FXML private Label peopleLabel;

    private final AuthService authService = new AuthService();

    @FXML
    public void initialize() {
        roleCombo.getItems().addAll("TENANT", "OWNER");
        roleCombo.setValue("TENANT");
        errorLabel.setVisible(false);
        SpinnerValueFactory<Integer> factory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        peopleSpinner.setValueFactory(factory);
        toggleExtraField();
        roleCombo.setOnAction(e -> toggleExtraField());
    }

    private void toggleExtraField() {
        boolean isTenant = "TENANT".equals(roleCombo.getValue());
        extraLabel.setText(isTenant ? "Occupation:" : "Address:");
        extraField.setPromptText(isTenant ? "e.g. Student / Engineer" : "e.g. Kathmandu, Nepal");
        peopleLabel.setVisible(isTenant);
        peopleSpinner.setVisible(isTenant);
    }

    @FXML
    private void handleRegister() {
        errorLabel.setVisible(false);
        String name     = nameField.getText().trim();
        String email    = emailField.getText().trim();
        String phone    = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirm  = confirmPasswordField.getText();
        String extra    = extraField.getText().trim();

        if (!password.equals(confirm)) {
            showError("Passwords do not match."); return;
        }

        try {
            boolean success;
            if ("OWNER".equals(roleCombo.getValue())) {
                success = authService.registerOwner(name, email, phone, password, extra);
            } else {
                success = authService.registerTenant(name, email, phone, password, extra,
                                                     peopleSpinner.getValue());
            }
            if (success) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION,
                    "Registration successful! Please log in.", ButtonType.OK);
                alert.setHeaderText("Account Created");
                alert.showAndWait();
                MainApp.navigateTo("login");
            } else {
                showError("Registration failed. Please try again.");
            }
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void goToLogin() {
        MainApp.navigateTo("login");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
