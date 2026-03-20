package com.roomrent.controller;

import com.roomrent.app.MainApp;
import com.roomrent.dao.UserDAO;
import com.roomrent.dao.UserDAOImpl;
import com.roomrent.model.Owner;
import com.roomrent.model.Tenant;
import com.roomrent.model.User;
import com.roomrent.util.PasswordUtil;
import com.roomrent.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * ProfileController — allows any logged-in user to view and update their profile details.
 * Demonstrates: Polymorphism (Owner vs Tenant fields shown differently),
 *               Encapsulation (password only updated if new one is supplied).
 */
public class ProfileController {

    @FXML private Label pageTitleLabel;
    @FXML private Label roleLabel;
    @FXML private TextField nameField;
    @FXML private TextField emailField;       // read-only
    @FXML private TextField phoneField;
    @FXML private TextField extraField;       // address (Owner) or occupation (Tenant)
    @FXML private Label extraLabel;
    @FXML private Spinner<Integer> peopleSpinner;
    @FXML private Label peopleLabel;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private Label successLabel;

    private final UserDAO userDAO = new UserDAOImpl();

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);
        SpinnerValueFactory<Integer> factory =
            new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1);
        peopleSpinner.setValueFactory(factory);

        User user = SessionManager.getInstance().getCurrentUser();
        pageTitleLabel.setText("My Profile");
        roleLabel.setText("Role: " + user.getRole());

        // Populate common fields
        nameField.setText(user.getFullName());
        emailField.setText(user.getEmail());
        emailField.setEditable(false);
        emailField.setStyle("-fx-background-color: #f3f4f6; -fx-text-fill: #6b7280;");
        phoneField.setText(user.getPhone());

        // Polymorphic field population
        if (user instanceof Owner owner) {
            extraLabel.setText("Address:");
            extraField.setText(owner.getAddress());
            extraField.setPromptText("Your address");
            peopleLabel.setVisible(false);
            peopleSpinner.setVisible(false);
        } else if (user instanceof Tenant tenant) {
            extraLabel.setText("Occupation:");
            extraField.setText(tenant.getOccupation());
            extraField.setPromptText("Your occupation");
            peopleLabel.setVisible(true);
            peopleSpinner.setVisible(true);
            peopleSpinner.getValueFactory().setValue(tenant.getNumberOfPeople());
        }
    }

    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        successLabel.setVisible(false);

        User user = SessionManager.getInstance().getCurrentUser();
        String name  = nameField.getText().trim();
        String phone = phoneField.getText().trim();
        String extra = extraField.getText().trim();
        String newPw = newPasswordField.getText();
        String confPw = confirmPasswordField.getText();

        // Validate
        if (name.isBlank()) { showError("Full name is required."); return; }
        if (!PasswordUtil.isValidPhone(phone)) { showError("Phone must be 7-15 digits."); return; }
        if (!newPw.isEmpty()) {
            if (!PasswordUtil.isValidPassword(newPw)) {
                showError("New password must be at least 6 characters."); return;
            }
            if (!newPw.equals(confPw)) {
                showError("Passwords do not match."); return;
            }
            user.setPassword(PasswordUtil.hashPassword(newPw));
        }

        // Update common fields
        user.setFullName(name);
        user.setPhone(phone);

        // Polymorphic field update
        if (user instanceof Owner owner) {
            owner.setAddress(extra);
        } else if (user instanceof Tenant tenant) {
            tenant.setOccupation(extra);
            tenant.setNumberOfPeople(peopleSpinner.getValue());
        }

        boolean success = userDAO.update(user);
        if (success) {
            SessionManager.getInstance().setCurrentUser(user);
            successLabel.setText("Profile updated successfully!");
            successLabel.setVisible(true);
            newPasswordField.clear();
            confirmPasswordField.clear();
        } else {
            showError("Update failed. Please try again.");
        }
    }

    @FXML
    private void handleBack() {
        String role = SessionManager.getInstance().getCurrentUser().getRole();
        if ("OWNER".equals(role)) {
            MainApp.navigateTo("owner-dashboard");
        } else {
            MainApp.navigateTo("tenant-dashboard");
        }
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
