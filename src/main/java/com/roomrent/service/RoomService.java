package com.roomrent.service;

import com.roomrent.dao.RoomDAO;
import com.roomrent.dao.RoomDAOImpl;
import com.roomrent.model.Room;

import java.util.List;
import java.util.Optional;

/**
 * RoomService — business logic for room listings.
 * Delegates persistence to RoomDAO.
 */
public class RoomService {

    private final RoomDAO roomDAO;

    public RoomService() {
        this.roomDAO = new RoomDAOImpl();
    }

    /**
     * Adds a new room after validation.
     */
    public boolean addRoom(Room room) {
        validateRoom(room);
        return roomDAO.addRoom(room);
    }

    /**
     * Updates an existing room.
     */
    public boolean updateRoom(Room room) {
        validateRoom(room);
        return roomDAO.update(room);
    }

    /**
     * Deletes a room if it belongs to the given owner.
     */
    public boolean deleteRoom(int roomId, int ownerId) {
        Optional<Room> existing = roomDAO.findById(roomId);
        if (existing.isEmpty()) throw new IllegalArgumentException("Room not found.");
        if (existing.get().getOwnerId() != ownerId)
            throw new SecurityException("You do not have permission to delete this room.");
        return roomDAO.delete(roomId);
    }

    /**
     * Returns all rooms owned by a specific user.
     */
    public List<Room> getOwnerRooms(int ownerId) {
        return roomDAO.findByOwner(ownerId);
    }

    /**
     * Returns all available rooms.
     */
    public List<Room> getAllAvailableRooms() {
        return roomDAO.findAvailable();
    }

    /**
     * Searches rooms based on filter criteria.
     */
    public List<Room> searchRooms(String location, double minPrice, double maxPrice,
                                   String roomType, String keyword) {
        return roomDAO.searchRooms(location, minPrice, maxPrice, roomType, keyword);
    }

    public Optional<Room> findById(int roomId) {
        return roomDAO.findById(roomId);
    }

    public boolean updateStatus(int roomId, Room.RoomStatus status) {
        return roomDAO.updateStatus(roomId, status);
    }

    // ─── Validation ──────────────────────────────────────────────────────────

    private void validateRoom(Room room) {
        if (room.getTitle() == null || room.getTitle().isBlank())
            throw new IllegalArgumentException("Room title is required.");
        if (room.getLocation() == null || room.getLocation().isBlank())
            throw new IllegalArgumentException("Location is required.");
        if (room.getPricePerMonth() <= 0)
            throw new IllegalArgumentException("Price must be greater than zero.");
        if (room.getMaxOccupants() < 1)
            throw new IllegalArgumentException("Max occupants must be at least 1.");
    }
}
