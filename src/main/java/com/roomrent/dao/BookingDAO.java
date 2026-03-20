package com.roomrent.dao;

import com.roomrent.model.Booking;
import com.roomrent.util.DatabaseConnection;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * BookingDAO interface + MySQL implementation combined.
 * Handles all booking persistence operations.
 */
public class BookingDAO {

    public boolean createBooking(Booking booking) {
        String sql = """
            INSERT INTO bookings (room_id, tenant_id, start_date, end_date, total_amount, status, message, booking_date)
            VALUES (?,?,?,?,?,?,?,?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, booking.getRoomId());
            ps.setInt(2, booking.getTenantId());
            ps.setDate(3, Date.valueOf(booking.getStartDate()));
            ps.setDate(4, Date.valueOf(booking.getEndDate()));
            ps.setDouble(5, booking.getTotalAmount());
            ps.setString(6, booking.getStatus().name());
            ps.setString(7, booking.getMessage());
            ps.setDate(8, Date.valueOf(LocalDate.now()));
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) booking.setBookingId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[BookingDAO] createBooking error: " + e.getMessage());
        }
        return false;
    }

    public Optional<Booking> findById(int bookingId) {
        String sql = """
            SELECT b.*, r.title AS room_title, u.full_name AS tenant_name
            FROM bookings b
            JOIN rooms r ON b.room_id = r.room_id
            JOIN users u ON b.tenant_id = u.user_id
            WHERE b.booking_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, bookingId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    public List<Booking> findByTenant(int tenantId) {
        return queryBookings("""
            SELECT b.*, r.title AS room_title, u.full_name AS tenant_name
            FROM bookings b
            JOIN rooms r ON b.room_id = r.room_id
            JOIN users u ON b.tenant_id = u.user_id
            WHERE b.tenant_id = ?
            ORDER BY b.booking_date DESC
        """, tenantId);
    }

    public List<Booking> findByOwner(int ownerId) {
        return queryBookings("""
            SELECT b.*, r.title AS room_title, u.full_name AS tenant_name
            FROM bookings b
            JOIN rooms r ON b.room_id = r.room_id
            JOIN users u ON b.tenant_id = u.user_id
            WHERE r.owner_id = ?
            ORDER BY b.booking_date DESC
        """, ownerId);
    }

    public List<Booking> findByRoom(int roomId) {
        return queryBookings("""
            SELECT b.*, r.title AS room_title, u.full_name AS tenant_name
            FROM bookings b
            JOIN rooms r ON b.room_id = r.room_id
            JOIN users u ON b.tenant_id = u.user_id
            WHERE b.room_id = ?
            ORDER BY b.booking_date DESC
        """, roomId);
    }

    public boolean updateStatus(int bookingId, Booking.BookingStatus status) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE bookings SET status=? WHERE booking_id=?")) {
            ps.setString(1, status.name());
            ps.setInt(2, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] updateStatus error: " + e.getMessage());
        }
        return false;
    }

    public boolean delete(int bookingId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM bookings WHERE booking_id=?")) {
            ps.setInt(1, bookingId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[BookingDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    private List<Booking> queryBookings(String sql, int param) {
        List<Booking> bookings = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, param);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) bookings.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[BookingDAO] query error: " + e.getMessage());
        }
        return bookings;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking b = new Booking();
        b.setBookingId(rs.getInt("booking_id"));
        b.setRoomId(rs.getInt("room_id"));
        b.setTenantId(rs.getInt("tenant_id"));
        b.setStartDate(rs.getDate("start_date").toLocalDate());
        b.setEndDate(rs.getDate("end_date").toLocalDate());
        b.setTotalAmount(rs.getDouble("total_amount"));
        b.setStatus(Booking.BookingStatus.valueOf(rs.getString("status")));
        b.setMessage(rs.getString("message"));
        Date bd = rs.getDate("booking_date");
        if (bd != null) b.setBookingDate(bd.toLocalDate());
        try { b.setRoomTitle(rs.getString("room_title")); } catch (SQLException ignored) {}
        try { b.setTenantName(rs.getString("tenant_name")); } catch (SQLException ignored) {}
        return b;
    }
}
