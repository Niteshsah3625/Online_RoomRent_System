package com.roomrent.dao;

import com.roomrent.model.User;
import java.util.List;
import java.util.Optional;

/**
 * Generic DAO interface for User entities.
 * Demonstrates Interface-based abstraction (OOP Interface Segregation).
 */
public interface UserDAO {
    boolean register(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(int userId);
    List<User> findAll();
    boolean update(User user);
    boolean delete(int userId);
    boolean emailExists(String email);
}
