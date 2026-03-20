package com.roomrent.controller;

import com.roomrent.app.MainApp;
import com.roomrent.model.Room;
import com.roomrent.service.RoomService;
import com.roomrent.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * AddRoomController — handles both adding a new room and editing an existing one.
 * Uses RoomEditContext to determine if we're in "edit" mode.
 */
public class AddRoomController {

    @FXML private Label formTitleLabel;
    @FXML private TextField titleField;
    @FXML private TextField locationField;
    @FXML private TextField priceField;
    @FXML private TextField maxOccField;
    @FXML private TextArea  descArea;
    @FXML private ComboBox<String> typeCombo;
    @FXML private CheckBox wifiCheck;
    @FXML private CheckBox parkingCheck;
    @FXML private CheckBox furnitureCheck;
    @FXML private Label errorLabel;
    @FXML private Button saveButton;

    private final RoomService roomService = new RoomService();
    private Room editingRoom = null;

    @FXML
    public void initialize() {
        typeCombo.getItems().addAll("SINGLE", "DOUBLE", "STUDIO", "APARTMENT");
        typeCombo.setValue("SINGLE");
        errorLabel.setVisible(false);

        // Check if we are editing an existing room
        editingRoom = RoomEditContext.getRoom();
        RoomEditContext.setRoom(null); // clear after reading

        if (editingRoom != null) {
            formTitleLabel.setText("Edit Room");
            saveButton.setText("Update Room");
            populateFields(editingRoom);
        } else {
            formTitleLabel.setText("Add New Room");
            saveButton.setText("Save Room");
        }
    }

    private void populateFields(Room room) {
        titleField.setText(room.getTitle());
        locationField.setText(room.getLocation());
        priceField.setText(String.valueOf((int) room.getPricePerMonth()));
        maxOccField.setText(String.valueOf(room.getMaxOccupants()));
        descArea.setText(room.getDescription());
        typeCombo.setValue(room.getRoomType().name());
        wifiCheck.setSelected(room.isHasWifi());
        parkingCheck.setSelected(room.isHasParking());
        furnitureCheck.setSelected(room.isHasFurniture());
    }

    @FXML
    private void handleSave() {
        errorLabel.setVisible(false);
        try {
            double price = Double.parseDouble(priceField.getText().trim());
            int    occ   = Integer.parseInt(maxOccField.getText().trim());

            Room room = (editingRoom != null) ? editingRoom : new Room();
            room.setTitle(titleField.getText().trim());
            room.setLocation(locationField.getText().trim());
            room.setDescription(descArea.getText().trim());
            room.setPricePerMonth(price);
            room.setMaxOccupants(occ);
            room.setRoomType(Room.RoomType.valueOf(typeCombo.getValue()));
            room.setHasWifi(wifiCheck.isSelected());
            room.setHasParking(parkingCheck.isSelected());
            room.setHasFurniture(furnitureCheck.isSelected());
            room.setOwnerId(SessionManager.getInstance().getCurrentUser().getUserId());

            boolean success;
            if (editingRoom != null) {
                success = roomService.updateRoom(room);
                if (success) showInfoAndGoBack("Room updated successfully!");
            } else {
                success = roomService.addRoom(room);
                if (success) showInfoAndGoBack("Room added successfully!");
            }
            if (!success) showError("Operation failed. Please try again.");

        } catch (NumberFormatException e) {
            showError("Price and Max Occupants must be valid numbers.");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.navigateTo("owner-dashboard");
    }

    private void showInfoAndGoBack(String message) {
        new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK).showAndWait();
        MainApp.navigateTo("owner-dashboard");
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}
