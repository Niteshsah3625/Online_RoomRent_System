package com.roomrent.dao;

import com.roomrent.model.Room;
import com.roomrent.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of RoomDAO.
 */
public class RoomDAOImpl implements RoomDAO {

    @Override
    public boolean addRoom(Room room) {
        String sql = """
            INSERT INTO rooms (title, description, location, price_per_month, room_type,
                               status, owner_id, max_occupants, has_wifi, has_parking, has_furniture, image_url)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, room.getTitle());
            ps.setString(2, room.getDescription());
            ps.setString(3, room.getLocation());
            ps.setDouble(4, room.getPricePerMonth());
            ps.setString(5, room.getRoomType() != null ? room.getRoomType().name() : "SINGLE");
            ps.setString(6, room.getStatus().name());
            ps.setInt(7, room.getOwnerId());
            ps.setInt(8, room.getMaxOccupants());
            ps.setBoolean(9, room.isHasWifi());
            ps.setBoolean(10, room.isHasParking());
            ps.setBoolean(11, room.isHasFurniture());
            ps.setString(12, room.getImageUrl());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) room.setRoomId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[RoomDAO] addRoom error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<Room> findById(int roomId) {
        String sql = """
            SELECT r.*, u.full_name AS owner_name, u.phone AS owner_phone
            FROM rooms r JOIN users u ON r.owner_id = u.user_id
            WHERE r.room_id = ?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, roomId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RoomDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Room> findAll() {
        return queryRooms("SELECT r.*, u.full_name AS owner_name, u.phone AS owner_phone " +
                          "FROM rooms r JOIN users u ON r.owner_id = u.user_id ORDER BY r.created_at DESC");
    }

    @Override
    public List<Room> findAvailable() {
        return queryRooms("SELECT r.*, u.full_name AS owner_name, u.phone AS owner_phone " +
                          "FROM rooms r JOIN users u ON r.owner_id = u.user_id " +
                          "WHERE r.status='AVAILABLE' ORDER BY r.created_at DESC");
    }

    @Override
    public List<Room> findByOwner(int ownerId) {
        String sql = "SELECT r.*, u.full_name AS owner_name, u.phone AS owner_phone " +
                     "FROM rooms r JOIN users u ON r.owner_id = u.user_id WHERE r.owner_id=? ORDER BY r.created_at DESC";
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, ownerId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RoomDAO] findByOwner error: " + e.getMessage());
        }
        return rooms;
    }

    @Override
    public List<Room> searchRooms(String location, double minPrice, double maxPrice,
                                   String roomType, String keyword) {
        StringBuilder sql = new StringBuilder("""
            SELECT r.*, u.full_name AS owner_name, u.phone AS owner_phone
            FROM rooms r JOIN users u ON r.owner_id = u.user_id
            WHERE r.status='AVAILABLE'
        """);
        List<Object> params = new ArrayList<>();

        if (location != null && !location.isBlank()) {
            sql.append(" AND r.location LIKE ?");
            params.add("%" + location + "%");
        }
        if (minPrice > 0) { sql.append(" AND r.price_per_month >= ?"); params.add(minPrice); }
        if (maxPrice > 0) { sql.append(" AND r.price_per_month <= ?"); params.add(maxPrice); }
        if (roomType != null && !roomType.isBlank() && !"ALL".equals(roomType)) {
            sql.append(" AND r.room_type = ?"); params.add(roomType);
        }
        if (keyword != null && !keyword.isBlank()) {
            sql.append(" AND (r.title LIKE ? OR r.description LIKE ?)");
            params.add("%" + keyword + "%");
            params.add("%" + keyword + "%");
        }
        sql.append(" ORDER BY r.created_at DESC");

        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof String s) ps.setString(i + 1, s);
                else if (p instanceof Double d) ps.setDouble(i + 1, d);
            }
            ResultSet rs = ps.executeQuery();
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RoomDAO] searchRooms error: " + e.getMessage());
        }
        return rooms;
    }

    @Override
    public boolean update(Room room) {
        String sql = """
            UPDATE rooms SET title=?, description=?, location=?, price_per_month=?,
            room_type=?, max_occupants=?, has_wifi=?, has_parking=?, has_furniture=?, image_url=?
            WHERE room_id=?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, room.getTitle());
            ps.setString(2, room.getDescription());
            ps.setString(3, room.getLocation());
            ps.setDouble(4, room.getPricePerMonth());
            ps.setString(5, room.getRoomType().name());
            ps.setInt(6, room.getMaxOccupants());
            ps.setBoolean(7, room.isHasWifi());
            ps.setBoolean(8, room.isHasParking());
            ps.setBoolean(9, room.isHasFurniture());
            ps.setString(10, room.getImageUrl());
            ps.setInt(11, room.getRoomId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int roomId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM rooms WHERE room_id=?")) {
            ps.setInt(1, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean updateStatus(int roomId, Room.RoomStatus status) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE rooms SET status=? WHERE room_id=?")) {
            ps.setString(1, status.name());
            ps.setInt(2, roomId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[RoomDAO] updateStatus error: " + e.getMessage());
        }
        return false;
    }

    private List<Room> queryRooms(String sql) {
        List<Room> rooms = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) rooms.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[RoomDAO] query error: " + e.getMessage());
        }
        return rooms;
    }

    private Room mapRow(ResultSet rs) throws SQLException {
        Room room = new Room();
        room.setRoomId(rs.getInt("room_id"));
        room.setTitle(rs.getString("title"));
        room.setDescription(rs.getString("description"));
        room.setLocation(rs.getString("location"));
        room.setPricePerMonth(rs.getDouble("price_per_month"));
        room.setRoomType(Room.RoomType.valueOf(rs.getString("room_type")));
        room.setStatus(Room.RoomStatus.valueOf(rs.getString("status")));
        room.setOwnerId(rs.getInt("owner_id"));
        room.setMaxOccupants(rs.getInt("max_occupants"));
        room.setHasWifi(rs.getBoolean("has_wifi"));
        room.setHasParking(rs.getBoolean("has_parking"));
        room.setHasFurniture(rs.getBoolean("has_furniture"));
        room.setImageUrl(rs.getString("image_url"));
        try { room.setOwnerName(rs.getString("owner_name")); } catch (SQLException ignored) {}
        try { room.setOwnerPhone(rs.getString("owner_phone")); } catch (SQLException ignored) {}
        return room;
    }
}
