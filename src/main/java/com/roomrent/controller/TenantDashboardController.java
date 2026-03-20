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
 * TenantDashboardController — Tenant searches available rooms and manages their bookings.
 */
public class TenantDashboardController {

    @FXML private Label welcomeLabel;

    // ─── Search Fields ────────────────────────────────────────────────────────
    @FXML private TextField searchKeyword;
    @FXML private TextField searchLocation;
    @FXML private TextField minPriceField;
    @FXML private TextField maxPriceField;
    @FXML private ComboBox<String> roomTypeCombo;

    // ─── Rooms Table ─────────────────────────────────────────────────────────
    @FXML private TableView<Room> roomTable;
    @FXML private TableColumn<Room, String>  colTitle;
    @FXML private TableColumn<Room, String>  colLocation;
    @FXML private TableColumn<Room, Double>  colPrice;
    @FXML private TableColumn<Room, String>  colRoomType;
    @FXML private TableColumn<Room, String>  colAmenities;
    @FXML private TableColumn<Room, String>  colOwner;

    // ─── My Bookings Table ───────────────────────────────────────────────────
    @FXML private TableView<Booking> bookingTable;
    @FXML private TableColumn<Booking, String>  colRoomTitle;
    @FXML private TableColumn<Booking, String>  colBookStatus;
    @FXML private TableColumn<Booking, String>  colAmount;
    @FXML private TableColumn<Booking, String>  colStartDate;
    @FXML private TableColumn<Booking, String>  colEndDate;

    // ─── Detail Panel ────────────────────────────────────────────────────────
    @FXML private Label detailTitle;
    @FXML private Label detailLocation;
    @FXML private Label detailPrice;
    @FXML private Label detailAmenities;
    @FXML private Label detailOwner;
    @FXML private Label detailOwnerPhone;

    private final RoomService    roomService    = new RoomService();
    private final BookingService bookingService = new BookingService();

    @FXML
    public void initialize() {
        String name = SessionManager.getInstance().getCurrentUser().getFullName();
        welcomeLabel.setText("Welcome, " + name + "!");
        roomTypeCombo.getItems().addAll("ALL", "SINGLE", "DOUBLE", "STUDIO", "APARTMENT");
        roomTypeCombo.setValue("ALL");
        setupRoomTable();
        setupBookingTable();
        loadAllRooms();
        loadMyBookings();
        // Show details when a room is selected
        roomTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, old, selected) -> showRoomDetails(selected));
    }

    private void setupRoomTable() {
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("pricePerMonth"));
        colRoomType.setCellValueFactory(new PropertyValueFactory<>("roomType"));
        colAmenities.setCellValueFactory(new PropertyValueFactory<>("amenitiesString"));
        colOwner.setCellValueFactory(new PropertyValueFactory<>("ownerName"));
    }

    private void setupBookingTable() {
        colRoomTitle.setCellValueFactory(new PropertyValueFactory<>("roomTitle"));
        colBookStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        colEndDate.setCellValueFactory(new PropertyValueFactory<>("endDate"));
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

    private void showRoomDetails(Room room) {
        if (room == null) return;
        detailTitle.setText(room.getTitle());
        detailLocation.setText("📍 " + room.getLocation());
        detailPrice.setText(room.getFormattedPrice());
        detailAmenities.setText("✔ " + room.getAmenitiesString());
        detailOwner.setText("Owner: " + (room.getOwnerName() != null ? room.getOwnerName() : "N/A"));
        detailOwnerPhone.setText("Phone: " + (room.getOwnerPhone() != null ? room.getOwnerPhone() : "N/A"));
    }

    @FXML
    private void handleSearch() {
        String loc  = searchLocation.getText().trim();
        String kw   = searchKeyword.getText().trim();
        String type = "ALL".equals(roomTypeCombo.getValue()) ? null : roomTypeCombo.getValue();
        double min  = parseDouble(minPriceField.getText());
        double max  = parseDouble(maxPriceField.getText());
        var results = roomService.searchRooms(loc, min, max, type, kw);
        roomTable.setItems(FXCollections.observableArrayList(results));
    }

    @FXML
    private void handleClearSearch() {
        searchKeyword.clear(); searchLocation.clear();
        minPriceField.clear(); maxPriceField.clear();
        roomTypeCombo.setValue("ALL");
        loadAllRooms();
    }

    @FXML
    private void handleBookRoom() {
        Room selected = roomTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Please select a room to book.");
            return;
        }
        RoomEditContext.setRoom(selected);
        MainApp.navigateTo("book-room");
    }

    @FXML
    private void handleCancelBooking() {
        Booking selected = bookingTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showAlert(Alert.AlertType.WARNING, "Select a booking to cancel."); return; }
        if (selected.getStatus() == Booking.BookingStatus.CONFIRMED) {
            showAlert(Alert.AlertType.WARNING, "Cannot cancel a confirmed booking. Contact the owner."); return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Cancel this booking?", ButtonType.YES, ButtonType.NO);
        confirm.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.YES) { bookingService.cancelBooking(selected.getBookingId()); loadMyBookings(); }
        });
    }

    @FXML private void handleRefresh() { loadAllRooms(); loadMyBookings(); }

    @FXML private void handleProfile() { MainApp.navigateTo("profile"); }

    @FXML private void handleLogout() {
        SessionManager.getInstance().logout();
        MainApp.navigateTo("login");
    }

    private void loadAllRooms() {
        roomTable.setItems(FXCollections.observableArrayList(roomService.getAllAvailableRooms()));
    }

    private void loadMyBookings() {
        int id = SessionManager.getInstance().getCurrentUser().getUserId();
        bookingTable.setItems(FXCollections.observableArrayList(bookingService.getTenantBookings(id)));
    }

    private void showAlert(Alert.AlertType type, String msg) {
        new Alert(type, msg, ButtonType.OK).showAndWait();
    }

    private double parseDouble(String s) {
        try { return Double.parseDouble(s); } catch (Exception e) { return 0; }
    }
}
