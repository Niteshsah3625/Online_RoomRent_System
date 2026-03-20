package com.roomrent.dao;

import com.roomrent.model.Room;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for Room CRUD and search operations.
 */
public interface RoomDAO {
    boolean addRoom(Room room);
    Optional<Room> findById(int roomId);
    List<Room> findAll();
    List<Room> findAvailable();
    List<Room> findByOwner(int ownerId);
    List<Room> searchRooms(String location, double minPrice, double maxPrice,
                           String roomType, String keyword);
    boolean update(Room room);
    boolean delete(int roomId);
    boolean updateStatus(int roomId, Room.RoomStatus status);
}
