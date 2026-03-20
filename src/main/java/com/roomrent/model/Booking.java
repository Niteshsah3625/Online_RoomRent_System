package com.roomrent.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Booking entity — represents a room booking by a tenant.
 * Demonstrates Composition (references Room and Tenant).
 */
public class Booking {

    public enum BookingStatus { PENDING, CONFIRMED, CANCELLED, COMPLETED }

    private int bookingId;
    private int roomId;
    private int tenantId;
    private String roomTitle;       // Denormalized for display
    private String tenantName;      // Denormalized for display
    private LocalDate startDate;
    private LocalDate endDate;
    private double totalAmount;
    private BookingStatus status;
    private String message;         // Message from tenant to owner
    private LocalDate bookingDate;

    public Booking() {
        this.status = BookingStatus.PENDING;
        this.bookingDate = LocalDate.now();
    }

    public Booking(int bookingId, int roomId, int tenantId, LocalDate startDate,
                   LocalDate endDate, double pricePerMonth, String message) {
        this.bookingId = bookingId;
        this.roomId = roomId;
        this.tenantId = tenantId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.message = message;
        this.status = BookingStatus.PENDING;
        this.bookingDate = LocalDate.now();
        this.totalAmount = calculateTotal(pricePerMonth);
    }

    /**
     * Business logic method — calculates total rent amount
     */
    public double calculateTotal(double pricePerMonth) {
        if (startDate == null || endDate == null) return 0;
        long months = ChronoUnit.MONTHS.between(startDate, endDate);
        if (months < 1) months = 1;
        return months * pricePerMonth;
    }

    /**
     * Returns duration in months as string
     */
    public String getDurationString() {
        if (startDate == null || endDate == null) return "N/A";
        long months = ChronoUnit.MONTHS.between(startDate, endDate);
        if (months < 1) return "Less than 1 month";
        return months + " month(s)";
    }

    // Getters and Setters
    public int getBookingId()                        { return bookingId; }
    public void setBookingId(int bookingId)          { this.bookingId = bookingId; }

    public int getRoomId()                           { return roomId; }
    public void setRoomId(int roomId)                { this.roomId = roomId; }

    public int getTenantId()                         { return tenantId; }
    public void setTenantId(int tenantId)            { this.tenantId = tenantId; }

    public String getRoomTitle()                     { return roomTitle; }
    public void setRoomTitle(String roomTitle)       { this.roomTitle = roomTitle; }

    public String getTenantName()                    { return tenantName; }
    public void setTenantName(String tenantName)     { this.tenantName = tenantName; }

    public LocalDate getStartDate()                  { return startDate; }
    public void setStartDate(LocalDate startDate)    { this.startDate = startDate; }

    public LocalDate getEndDate()                    { return endDate; }
    public void setEndDate(LocalDate endDate)        { this.endDate = endDate; }

    public double getTotalAmount()                   { return totalAmount; }
    public void setTotalAmount(double totalAmount)   { this.totalAmount = totalAmount; }

    public BookingStatus getStatus()                 { return status; }
    public void setStatus(BookingStatus status)      { this.status = status; }

    public String getMessage()                       { return message; }
    public void setMessage(String message)           { this.message = message; }

    public LocalDate getBookingDate()                { return bookingDate; }
    public void setBookingDate(LocalDate d)          { this.bookingDate = d; }

    public String getFormattedAmount() {
        return String.format("Rs. %.0f", totalAmount);
    }

    @Override
    public String toString() {
        return "Booking{id=" + bookingId + ", roomId=" + roomId + ", tenantId=" + tenantId
                + ", status=" + status + "}";
    }
}
