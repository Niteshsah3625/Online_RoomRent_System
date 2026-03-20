package com.roomrent.model;

/**
 * Owner extends User — demonstrates Inheritance.
 * An Owner can list and manage rooms.
 */
public class Owner extends User {

    private String address;

    public Owner(int userId, String fullName, String email, String phone, String password, String address) {
        super(userId, fullName, email, phone, password, "OWNER");
        this.address = address;
    }

    public Owner() {
        super();
        setRole("OWNER");
    }

    /**
     * Polymorphic method override
     */
    @Override
    public String getDashboardTitle() {
        return "Owner Dashboard — " + getFullName();
    }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "Owner{userId=" + getUserId() + ", name='" + getFullName() + "', email='" + getEmail() + "'}";
    }
}
