package com.roomrent.service;

import com.roomrent.dao.UserDAO;
import com.roomrent.dao.UserDAOImpl;
import com.roomrent.model.Owner;
import com.roomrent.model.Tenant;
import com.roomrent.model.User;
import com.roomrent.util.PasswordUtil;
import com.roomrent.util.SessionManager;

import java.util.Optional;

/**
 * AuthService — business logic for registration and authentication.
 * Demonstrates Service Layer pattern separating business logic from UI.
 */
public class AuthService {

    private final UserDAO userDAO;

    public AuthService() {
        this.userDAO = new UserDAOImpl();
    }

    /**
     * Registers a new Owner account.
     * @return true on success, false if email is taken or validation fails
     */
    public boolean registerOwner(String fullName, String email, String phone,
                                  String password, String address) {
        if (!validateRegistration(fullName, email, phone, password)) return false;
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        Owner owner = new Owner();
        owner.setFullName(fullName.trim());
        owner.setEmail(email.trim().toLowerCase());
        owner.setPhone(phone.trim());
        owner.setPassword(PasswordUtil.hashPassword(password));
        owner.setAddress(address.trim());
        return userDAO.register(owner);
    }

    /**
     * Registers a new Tenant account.
     */
    public boolean registerTenant(String fullName, String email, String phone,
                                   String password, String occupation, int numPeople) {
        if (!validateRegistration(fullName, email, phone, password)) return false;
        if (userDAO.emailExists(email)) {
            throw new IllegalArgumentException("An account with this email already exists.");
        }
        Tenant tenant = new Tenant();
        tenant.setFullName(fullName.trim());
        tenant.setEmail(email.trim().toLowerCase());
        tenant.setPhone(phone.trim());
        tenant.setPassword(PasswordUtil.hashPassword(password));
        tenant.setOccupation(occupation.trim());
        tenant.setNumberOfPeople(numPeople);
        return userDAO.register(tenant);
    }

    /**
     * Authenticates a user and stores them in the session.
     * @return the authenticated User, or null if credentials are wrong
     */
    public User login(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new IllegalArgumentException("Email and password are required.");
        }
        Optional<User> userOpt = userDAO.findByEmail(email.trim().toLowerCase());
        if (userOpt.isEmpty()) return null;
        User user = userOpt.get();
        if (!PasswordUtil.verifyPassword(password, user.getPassword())) return null;
        SessionManager.getInstance().setCurrentUser(user);
        return user;
    }

    /**
     * Logs out the current session user.
     */
    public void logout() {
        SessionManager.getInstance().logout();
    }

    /**
     * Checks if the current session has an authenticated user.
     */
    public boolean isLoggedIn() {
        return SessionManager.getInstance().isLoggedIn();
    }

    // ─── Private helpers ──────────────────────────────────────────────────────

    private boolean validateRegistration(String fullName, String email, String phone, String password) {
        if (fullName == null || fullName.isBlank())
            throw new IllegalArgumentException("Full name is required.");
        if (!PasswordUtil.isValidEmail(email))
            throw new IllegalArgumentException("Please enter a valid email address.");
        if (!PasswordUtil.isValidPhone(phone))
            throw new IllegalArgumentException("Phone number must be 7-15 digits.");
        if (!PasswordUtil.isValidPassword(password))
            throw new IllegalArgumentException("Password must be at least 6 characters.");
        return true;
    }
}
