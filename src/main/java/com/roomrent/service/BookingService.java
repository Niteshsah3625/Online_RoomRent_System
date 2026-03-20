package com.roomrent.service;

import com.roomrent.dao.BookingDAO;
import com.roomrent.dao.RoomDAO;
import com.roomrent.dao.RoomDAOImpl;
import com.roomrent.model.Booking;
import com.roomrent.model.Room;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * BookingService — business logic for creating and managing bookings.
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final RoomDAO    roomDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.roomDAO    = new RoomDAOImpl();
    }

    /**
     * Creates a new booking request for a tenant.
     * Validates dates and room availability.
     */
    public boolean createBooking(int roomId, int tenantId, LocalDate startDate,
                                  LocalDate endDate, String message) {
        // Validate dates
        if (startDate == null || endDate == null)
            throw new IllegalArgumentException("Start and end dates are required.");
        if (!endDate.isAfter(startDate))
            throw new IllegalArgumentException("End date must be after start date.");
        if (startDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException("Start date cannot be in the past.");

        // Check room exists and is available
        Optional<Room> roomOpt = roomDAO.findById(roomId);
        if (roomOpt.isEmpty()) throw new IllegalArgumentException("Room not found.");
        Room room = roomOpt.get();
        if (room.getStatus() != Room.RoomStatus.AVAILABLE)
            throw new IllegalStateException("This room is no longer available.");

        // Create booking
        Booking booking = new Booking();
        booking.setRoomId(roomId);
        booking.setTenantId(tenantId);
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setMessage(message);
        booking.setTotalAmount(booking.calculateTotal(room.getPricePerMonth()));
        booking.setStatus(Booking.BookingStatus.PENDING);

        return bookingDAO.createBooking(booking);
    }

    /**
     * Owner confirms a pending booking and marks room as Booked.
     */
    public boolean confirmBooking(int bookingId, int ownerId) {
        Optional<Booking> opt = bookingDAO.findById(bookingId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Booking not found.");
        Booking booking = opt.get();
        boolean updated = bookingDAO.updateStatus(bookingId, Booking.BookingStatus.CONFIRMED);
        if (updated) roomDAO.updateStatus(booking.getRoomId(), Room.RoomStatus.BOOKED);
        return updated;
    }

    /**
     * Cancels a booking and returns room to available.
     */
    public boolean cancelBooking(int bookingId) {
        Optional<Booking> opt = bookingDAO.findById(bookingId);
        if (opt.isEmpty()) throw new IllegalArgumentException("Booking not found.");
        Booking booking = opt.get();
        boolean updated = bookingDAO.updateStatus(bookingId, Booking.BookingStatus.CANCELLED);
        if (updated) roomDAO.updateStatus(booking.getRoomId(), Room.RoomStatus.AVAILABLE);
        return updated;
    }

    public List<Booking> getTenantBookings(int tenantId) {
        return bookingDAO.findByTenant(tenantId);
    }

    public List<Booking> getOwnerBookings(int ownerId) {
        return bookingDAO.findByOwner(ownerId);
    }

    public Optional<Booking> findById(int bookingId) {
        return bookingDAO.findById(bookingId);
    }
}
