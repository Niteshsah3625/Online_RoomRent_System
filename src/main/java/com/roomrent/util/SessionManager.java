package com.roomrent.util;

import com.roomrent.model.User;

/**
 * SessionManager — Singleton to hold the currently logged-in user.
 * Allows passing user state across JavaFX controllers without constructor injection.
 */
public class SessionManager {

    private static SessionManager instance;
    private User currentUser;

    private SessionManager() {}

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public User getCurrentUser() { return currentUser; }

    public void setCurrentUser(User user) { this.currentUser = user; }

    public boolean isLoggedIn() { return currentUser != null; }

    public boolean isOwner() {
        return currentUser != null && "OWNER".equals(currentUser.getRole());
    }

    public boolean isTenant() {
        return currentUser != null && "TENANT".equals(currentUser.getRole());
    }

    public void logout() { currentUser = null; }
}
