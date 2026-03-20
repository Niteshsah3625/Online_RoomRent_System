package com.roomrent.controller;

import com.roomrent.model.Room;

/**
 * RoomEditContext — simple static holder to pass a Room object between controllers.
 * Used when navigating from the dashboard to the Add/Edit Room form.
 *
 * Note: In larger applications, dependency injection (e.g., Spring) would be preferred.
 * For a JavaFX desktop app, this is a clean and practical solution.
 */
public class RoomEditContext {

    private static Room selectedRoom = null;

    private RoomEditContext() {}

    public static Room getRoom() { return selectedRoom; }
    public static void setRoom(Room room) { selectedRoom = room; }
}
