package com.roomrent.model;

/**
 * Room entity class — represents a rental room listing.
 * Demonstrates Encapsulation and Composition (contains Owner reference).
 */
public class Room {

    public enum RoomStatus { AVAILABLE, BOOKED, UNAVAILABLE }
    public enum RoomType   { SINGLE, DOUBLE, STUDIO, APARTMENT }

    private int roomId;
    private String title;
    private String description;
    private String location;
    private double pricePerMonth;
    private RoomType roomType;
    private RoomStatus status;
    private int ownerId;          // FK reference
    private String ownerName;     // Denormalized for display
    private String ownerPhone;
    private int maxOccupants;
    private boolean hasWifi;
    private boolean hasParking;
    private boolean hasFurniture;
    private String imageUrl;

    // Default constructor
    public Room() {
        this.status = RoomStatus.AVAILABLE;
    }

    // Full constructor
    public Room(int roomId, String title, String description, String location,
                double pricePerMonth, RoomType roomType, int ownerId,
                int maxOccupants, boolean hasWifi, boolean hasParking, boolean hasFurniture) {
        this.roomId = roomId;
        this.title = title;
        this.description = description;
        this.location = location;
        this.pricePerMonth = pricePerMonth;
        this.roomType = roomType;
        this.status = RoomStatus.AVAILABLE;
        this.ownerId = ownerId;
        this.maxOccupants = maxOccupants;
        this.hasWifi = hasWifi;
        this.hasParking = hasParking;
        this.hasFurniture = hasFurniture;
    }

    // Getters and Setters
    public int getRoomId()                   { return roomId; }
    public void setRoomId(int roomId)        { this.roomId = roomId; }

    public String getTitle()                 { return title; }
    public void setTitle(String title)       { this.title = title; }

    public String getDescription()           { return description; }
    public void setDescription(String d)     { this.description = d; }

    public String getLocation()              { return location; }
    public void setLocation(String location) { this.location = location; }

    public double getPricePerMonth()         { return pricePerMonth; }
    public void setPricePerMonth(double p)   { this.pricePerMonth = p; }

    public RoomType getRoomType()            { return roomType; }
    public void setRoomType(RoomType t)      { this.roomType = t; }

    public RoomStatus getStatus()            { return status; }
    public void setStatus(RoomStatus s)      { this.status = s; }

    public int getOwnerId()                  { return ownerId; }
    public void setOwnerId(int ownerId)      { this.ownerId = ownerId; }

    public String getOwnerName()             { return ownerName; }
    public void setOwnerName(String n)       { this.ownerName = n; }

    public String getOwnerPhone()            { return ownerPhone; }
    public void setOwnerPhone(String p)      { this.ownerPhone = p; }

    public int getMaxOccupants()             { return maxOccupants; }
    public void setMaxOccupants(int m)       { this.maxOccupants = m; }

    public boolean isHasWifi()               { return hasWifi; }
    public void setHasWifi(boolean w)        { this.hasWifi = w; }

    public boolean isHasParking()            { return hasParking; }
    public void setHasParking(boolean p)     { this.hasParking = p; }

    public boolean isHasFurniture()          { return hasFurniture; }
    public void setHasFurniture(boolean f)   { this.hasFurniture = f; }

    public String getImageUrl()              { return imageUrl; }
    public void setImageUrl(String u)        { this.imageUrl = u; }

    /**
     * Returns formatted amenities string for display
     */
    public String getAmenitiesString() {
        StringBuilder sb = new StringBuilder();
        if (hasWifi)      sb.append("WiFi  ");
        if (hasParking)   sb.append("Parking  ");
        if (hasFurniture) sb.append("Furnished");
        return sb.toString().trim().isEmpty() ? "No amenities listed" : sb.toString().trim();
    }

    /**
     * Returns formatted price string
     */
    public String getFormattedPrice() {
        return String.format("Rs. %.0f / month", pricePerMonth);
    }

    @Override
    public String toString() {
        return "Room{id=" + roomId + ", title='" + title + "', location='" + location
                + "', price=" + pricePerMonth + ", status=" + status + "}";
    }
}
