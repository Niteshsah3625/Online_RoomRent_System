package com.roomrent.controller;

import com.roomrent.app.MainApp;
import com.roomrent.model.Booking;
import com.roomrent.model.Room;
import com.roomrent.service.BookingService;
import com.roomrent.service.RoomService;
import com.roomrent.util.SessionManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * OwnerDashboardController — Owner sees their listings and incoming booking requests.
 * Demonstrates MVC: controller coordinates service calls and table display.
 */
public class OwnerDashboardController {

    // ─── Welcome ─────────────────────────────────────────────────────────────
    @FXML private Label welcomeLabel;

    // ─── Rooms Table ─────────────────────────────────────────────────────────
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, Integer> colRoomId;
    @FXML private TableColumn<Room, String>  colTitle;
    @FXML private TableColumn<Room, String>  colLocation;
    @FXML private TableColumn<Room, Double>  colPrice;
    @FXML private TableColumn<Room, String>  colRoomType;
    @FXML private TableColumn<Room, String>  colStatus;

    // ─── Bookings Table ──────────────────────────────────────────────────────
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, Integer> colBookId;
    @FXML private TableColumn<Booking, String>  colTenant;
    @FXML private TableColumn<Booking, String>  colBookRoom;
    @FXML private TableColumn<Booking, String>  colBookStatus;
    @FXML private TableColumn<Booking, String>  colAmount;
    @FXML private TableColumn<Booking, String>  colDuration;

    // ─── Stats Labels ─────────────────────────────────────────────────────────
    @FXML private Label totalRoomsLabel;
    @FXML private Label availableRoomsLabel;
    @FXML private Label pendingBookingsLabel;

    private final RoomService    roomService    = new RoomService();
    private final BookingService bookingService = new BookingService();

    @FXML
    public void initialize() {
        String name = SessionManager.getInstance().getCurrentUser().getFullName();
        welcomeLabel.setText("Welcome back, " + name + "!");
        setupRoomTable();
        setupBookingTable();
        loadData();
    }

    private void setupRoomTable() {
        colRoomId.setCellValueFactory(new PropertyValueFactory<>("roomId"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerMonth"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        // Colour-code status column
        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "AVAILABLE"   -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "BOOKED"      -> setStyle("-fx-text-fill: #e67e22; -fx-font-weight: bold;");
                    case "UNAVAILABLE" -> setStyle("-fx-text-fill: #c0392b; -fx-font-weight: bold;");
                    default            -> setStyle("");
                }
            }
        });
    }

    private void setupBookingTable() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("bookingId"));
        colTenant.setCellValueFactory(new PropertyValueFactory<>("tenantName"));
        colBookRoom.setCellValueFactory(new PropertyValueFactory<>("roomTitle"));
        colBookStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationString"));
        // Colour-code booking status
        colBookStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                switch (item) {
                    case "PENDING"   -> setStyle("-fx-text-fill: #f39c12; -fx-font-weight: bold;");
                    case "CONFIRMED" -> setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");
                    case "CANCELLED" -> setStyle("-fx-text-fill: #c0392b;");
                    default          -> setStyle("");
                }
            }
        });
    }

    private void loadData() {
        int ownerId = SessionManager.getInstance().getCurrentUser().getUserId();
        var rooms    = roomService.getOwnerRooms(ownerId);
        var bookings = bookingService.getOwnerBookings(ownerId);
        roomTable.setItems(FXCollections.observableArrayList(rooms));
        bookingTable.setItems(FXCollections.observableArrayList(bookings));
        // Stats
        long available = rooms.stream().filter(r -> r.getStatus() == Room.RoomStatus.AVAILABLE).count();
        long pending   = bookings.stream().filter(b -> b.getStatus() == Booking.BookingStatus.PENDING).count();
        totalRoomsLabel.setText(String.valueOf(rooms.size()));
        availableRoomsLabel.setText(String.valueOf(available));
        pendingBookingsLabel.setText(String.valueOf(pending));
    }

    @FXML
    private void handleAddRoom() {
        MainApp.navigateTo("add-room");
    }

    @FXML
    private void handleProfile() {
        MainApp.navigateTo("profile");
    }

    @FXML
    private void handleEditRoom() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Please select a room to edit."); return; }
        RoomEditContext.setRoom(selected);
        MainApp.navigateTo("add-room");
    }

    @FXML
    private void handleDeleteRoom() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Please select a room to delete."); return; }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "Delete room \"" + selected.getTitle() + "\"?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) {
                try {
                    roomService.deleteRoom(selected.getRoomId(),
                        SessionManager.getInstance().getCurrentUser().getUserId());
                    loadData();
                    showAlert(Alert.AlertType.INFORMATION, "Room deleted.");
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleConfirmBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Select a booking first."); return; }
        if (selected.getStatus() != Booking.BookingStatus.PENDING) {
            showAlert(Alert.AlertType.WARNING, "Only PENDING bookings can be confirmed."); return;
        }
        try {
            bookingService.confirmBooking(selected.getBookingId(),
                SessionManager.getInstance().getCurrentUser().getUserId());
            loadData();
            showAlert(Alert.AlertType.INFORMATION, "Booking confirmed successfully!");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, e.getMessage());
        }
    }

    @FXML
    private void handleCancelBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Select a booking first."); return; }
        bookingService.cancelBooking(selected.getBookingId());
        loadData();
        showAlert(Alert.AlertType.INFORMATION, "Booking cancelled.");
    }

    @FXML
    private void handleRefresh() { loadData(); }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();
        MainApp.navigateTo("login");
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }
}
