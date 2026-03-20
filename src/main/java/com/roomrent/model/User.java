package com.roomrent.model;

/**
 * Abstract base class representing a system user.
 * Demonstrates OOP: Abstraction, Encapsulation, Inheritance.
 */
public abstract class User {
    // Encapsulated fields
    private int userId;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String role; // "OWNER" or "TENANT"

    // Constructor
    public User(int userId, String fullName, String email, String phone, String password, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
    }

    public User() {}

    // Abstract method — forces subclasses to define their dashboard title
    public abstract String getDashboardTitle();

    // Getters and Setters (Encapsulation)
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    @Override
    public String toString() {
        return "User{userId=" + userId + ", fullName='" + fullName + "', email='" + email + "', role='" + role + "'}";
    }
}
