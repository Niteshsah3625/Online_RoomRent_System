package com.roomrent.controller;

import com.roomrent.app.MainApp;
import com.roomrent.model.Room;
import com.roomrent.service.BookingService;
import com.roomrent.util.SessionManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

/**
 * BookRoomController — lets a tenant fill in booking dates and send a booking request.
 */
public class BookRoomController {

    @FXML private Label roomTitleLabel;
    @FXML private Label roomLocationLabel;
    @FXML private Label roomPriceLabel;
    @FXML private Label roomAmenitiesLabel;
    @FXML private DatePicker startDatePicker;
    @FXML private DatePicker endDatePicker;
    @FXML private TextArea messageArea;
    @FXML private Label estimatedAmountLabel;
    @FXML private Label errorLabel;

    private Room selectedRoom;
    private final BookingService bookingService = new BookingService();

    @FXML
    public void initialize() {
        selectedRoom = RoomEditContext.getRoom();
        errorLabel.setVisible(false);
        estimatedAmountLabel.setText("Rs. 0");

        if (selectedRoom != null) {
            roomTitleLabel.setText(selectedRoom.getTitle());
            roomLocationLabel.setText("📍 " + selectedRoom.getLocation());
            roomPriceLabel.setText(selectedRoom.getFormattedPrice());
            roomAmenitiesLabel.setText(selectedRoom.getAmenitiesString());
        }

        // Set minimum date to today
        startDatePicker.setDayCellFactory(d -> new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty); setDisable(date.isBefore(LocalDate.now()));
            }
        });
        endDatePicker.setDayCellFactory(d -> new DateCell() {
            @Override public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty); setDisable(date.isBefore(LocalDate.now().plusDays(1)));
            }
        });

        // Update estimated amount when dates change
        startDatePicker.setOnAction(e -> updateEstimate());
        endDatePicker.setOnAction(e -> updateEstimate());
    }

    private void updateEstimate() {
        if (startDatePicker.getValue() != null && endDatePicker.getValue() != null && selectedRoom != null) {
            com.roomrent.model.Booking temp = new com.roomrent.model.Booking();
            temp.setStartDate(startDatePicker.getValue());
            temp.setEndDate(endDatePicker.getValue());
            double amount = temp.calculateTotal(selectedRoom.getPricePerMonth());
            estimatedAmountLabel.setText(String.format("Rs. %.0f", amount));
        }
    }

    @FXML
    private void handleConfirmBooking() {
        errorLabel.setVisible(false);
        if (selectedRoom == null) { showError("No room selected."); return; }

        LocalDate start = startDatePicker.getValue();
        LocalDate end   = endDatePicker.getValue();

        if (start == null || end == null) { showError("Please select both start and end dates."); return; }

        try {
            int tenantId = SessionManager.getInstance().getCurrentUser().getUserId();
            boolean success = bookingService.createBooking(
                selectedRoom.getRoomId(), tenantId, start, end, messageArea.getText().trim());
            if (success) {
                new Alert(Alert.AlertType.INFORMATION,
                    "Booking request sent! Wait for the owner to confirm.", ButtonType.OK).showAndWait();
                MainApp.navigateTo("tenant-dashboard");
            } else {
                showError("Booking failed. Please try again.");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void handleCancel() {
        MainApp.navigateTo("tenant-dashboard");
    }

    private void showError(String msg) {
        errorLabel.setText(msg);
        errorLabel.setVisible(true);
    }
}
