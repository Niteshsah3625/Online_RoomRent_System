package com.roomrent.dao;

import com.roomrent.model.Owner;
import com.roomrent.model.Tenant;
import com.roomrent.model.User;
import com.roomrent.util.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of UserDAO.
 * Demonstrates: DAO Pattern, Polymorphism (returns Owner or Tenant based on role).
 */
public class UserDAOImpl implements UserDAO {

    @Override
    public boolean register(User user) {
        String sql = """
            INSERT INTO users (full_name, email, phone, password, role, address, occupation, num_people)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getFullName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPhone());
            ps.setString(4, user.getPassword());
            ps.setString(5, user.getRole());

            if (user instanceof Owner owner) {
                ps.setString(6, owner.getAddress());
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.INTEGER);
            } else if (user instanceof Tenant tenant) {
                ps.setNull(6, Types.VARCHAR);
                ps.setString(7, tenant.getOccupation());
                ps.setInt(8, tenant.getNumberOfPeople());
            } else {
                ps.setNull(6, Types.VARCHAR);
                ps.setNull(7, Types.VARCHAR);
                ps.setNull(8, Types.INTEGER);
            }

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) user.setUserId(keys.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] register error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRowToUser(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findByEmail error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(int userId) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return Optional.of(mapRowToUser(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findById error: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<User> findAll() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) users.add(mapRowToUser(rs));
        } catch (SQLException e) {
            System.err.println("[UserDAO] findAll error: " + e.getMessage());
        }
        return users;
    }

    @Override
    public boolean update(User user) {
        String sql = """
            UPDATE users SET full_name=?, phone=?, address=?, occupation=?, num_people=?
            WHERE user_id=?
        """;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, user.getFullName());
            ps.setString(2, user.getPhone());
            if (user instanceof Owner o) { ps.setString(3, o.getAddress()); ps.setNull(4, Types.VARCHAR); ps.setNull(5, Types.INTEGER); }
            else if (user instanceof Tenant t) { ps.setNull(3, Types.VARCHAR); ps.setString(4, t.getOccupation()); ps.setInt(5, t.getNumberOfPeople()); }
            else { ps.setNull(3, Types.VARCHAR); ps.setNull(4, Types.VARCHAR); ps.setNull(5, Types.INTEGER); }
            ps.setInt(6, user.getUserId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] update error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean delete(int userId) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("DELETE FROM users WHERE user_id=?")) {
            ps.setInt(1, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] delete error: " + e.getMessage());
        }
        return false;
    }

    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("[UserDAO] emailExists error: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to the appropriate User subtype.
     * Demonstrates Polymorphism — returns Owner or Tenant at runtime.
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        String role = rs.getString("role");
        User user;
        if ("OWNER".equals(role)) {
            Owner o = new Owner();
            o.setAddress(rs.getString("address"));
            user = o;
        } else {
            Tenant t = new Tenant();
            t.setOccupation(rs.getString("occupation"));
            t.setNumberOfPeople(rs.getInt("num_people"));
            user = t;
        }
        user.setUserId(rs.getInt("user_id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPhone(rs.getString("phone"));
        user.setPassword(rs.getString("password"));
        user.setRole(role);
        return user;
    }
}
