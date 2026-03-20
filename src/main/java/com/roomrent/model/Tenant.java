package com.roomrent.model;

/**
 * Tenant extends User — demonstrates Inheritance.
 * A Tenant can search and book rooms.
 */
public class Tenant extends User {

    private String occupation;
    private int numberOfPeople;

    public Tenant(int userId, String fullName, String email, String phone, String password,
                  String occupation, int numberOfPeople) {
        super(userId, fullName, email, phone, password, "TENANT");
        this.occupation = occupation;
        this.numberOfPeople = numberOfPeople;
    }

    public Tenant() {
        super();
        setRole("TENANT");
    }

    /**
     * Polymorphic method override
     */
    @Override
    public String getDashboardTitle() {
        return "Tenant Dashboard — " + getFullName();
    }

    public String getOccupation() { return occupation; }
    public void setOccupation(String occupation) { this.occupation = occupation; }

    public int getNumberOfPeople() { return numberOfPeople; }
    public void setNumberOfPeople(int numberOfPeople) { this.numberOfPeople = numberOfPeople; }

    @Override
    public String toString() {
        return "Tenant{userId=" + getUserId() + ", name='" + getFullName() + "', email='" + getEmail() + "'}";
    }
}
