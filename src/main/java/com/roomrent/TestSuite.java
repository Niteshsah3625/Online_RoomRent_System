package com.roomrent;

import com.roomrent.model.*;
import com.roomrent.util.PasswordUtil;
import com.roomrent.util.SessionManager;

/**
 * ════════════════════════════════════════════════════════════
 *  TestSuite — Basic test cases for the Room Rent System
 *  Run with: javac TestSuite.java && java TestSuite
 *  (No framework required — pure Java assertions)
 * ════════════════════════════════════════════════════════════
 */
public class TestSuite {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        System.out.println("═══════════════════════════════════════");
        System.out.println("  Online Room Rent System — Test Suite");
        System.out.println("═══════════════════════════════════════\n");

        testPasswordUtil();
        testUserModel();
        testOwnerModel();
        testTenantModel();
        testRoomModel();
        testBookingModel();
        testSessionManager();
        testRoomValidation();

        System.out.println("\n═══════════════════════════════════════");
        System.out.printf("  Results: %d Passed | %d Failed%n", passed, failed);
        System.out.println("═══════════════════════════════════════");
    }

    // ─── PasswordUtil Tests ───────────────────────────────────────────────────

    private static void testPasswordUtil() {
        System.out.println("--- PasswordUtil Tests ---");

        // TC1: Hash should not equal plain text
        String hash = PasswordUtil.hashPassword("mypassword");
        assertTrue("TC1 - Hash differs from plaintext", !hash.equals("mypassword"));

        // TC2: Same input always produces same hash
        String hash2 = PasswordUtil.hashPassword("mypassword");
        assertTrue("TC2 - Hash is deterministic", hash.equals(hash2));

        // TC3: Verify correct password
        assertTrue("TC3 - Verify correct password", PasswordUtil.verifyPassword("mypassword", hash));

        // TC4: Verify wrong password fails
        assertTrue("TC4 - Verify wrong password fails", !PasswordUtil.verifyPassword("wrongpass", hash));

        // TC5: Valid email
        assertTrue("TC5 - Valid email format", PasswordUtil.isValidEmail("test@gmail.com"));

        // TC6: Invalid email
        assertTrue("TC6 - Invalid email rejected", !PasswordUtil.isValidEmail("notanemail"));

        // TC7: Valid phone
        assertTrue("TC7 - Valid phone number", PasswordUtil.isValidPhone("9800000001"));

        // TC8: Short phone rejected
        assertTrue("TC8 - Short phone rejected", !PasswordUtil.isValidPhone("123"));

        // TC9: Password min length
        assertTrue("TC9 - Short password rejected", !PasswordUtil.isValidPassword("abc"));

        // TC10: Password valid length
        assertTrue("TC10 - Valid password accepted", PasswordUtil.isValidPassword("abc123"));
    }

    // ─── User / Owner / Tenant Model Tests ───────────────────────────────────

    private static void testUserModel() {
        System.out.println("\n--- User Model Tests ---");
        Owner owner = new Owner(1, "Ram Sharma", "ram@test.com", "9800000001",
                "hashedpw", "Kathmandu");
        assertTrue("TC11 - Owner ID set", owner.getUserId() == 1);
        assertTrue("TC12 - Owner role is OWNER", "OWNER".equals(owner.getRole()));
        assertTrue("TC13 - Owner name set", "Ram Sharma".equals(owner.getFullName()));
    }

    private static void testOwnerModel() {
        System.out.println("\n--- Owner Model Tests ---");
        Owner owner = new Owner();
        owner.setFullName("Sita Rana");
        owner.setAddress("Lalitpur");
        assertTrue("TC14 - Owner address set", "Lalitpur".equals(owner.getAddress()));
        assertTrue("TC15 - Owner dashboard title contains name",
                owner.getDashboardTitle().contains("Sita Rana"));
        assertTrue("TC16 - Owner default role", "OWNER".equals(owner.getRole()));
    }

    private static void testTenantModel() {
        System.out.println("\n--- Tenant Model Tests ---");
        Tenant tenant = new Tenant(2, "Hari Thapa", "hari@test.com", "9800000002",
                "hashedpw", "Student", 1);
        assertTrue("TC17 - Tenant role is TENANT", "TENANT".equals(tenant.getRole()));
        assertTrue("TC18 - Tenant occupation set", "Student".equals(tenant.getOccupation()));
        assertTrue("TC19 - Tenant people count", tenant.getNumberOfPeople() == 1);
        assertTrue("TC20 - Tenant dashboard title", tenant.getDashboardTitle().contains("Hari Thapa"));
    }

    // ─── Room Model Tests ─────────────────────────────────────────────────────

    private static void testRoomModel() {
        System.out.println("\n--- Room Model Tests ---");
        Room room = new Room();
        room.setTitle("Cozy Single Room");
        room.setLocation("Baneshwor");
        room.setPricePerMonth(8000);
        room.setHasWifi(true);
        room.setHasParking(false);
        room.setHasFurniture(true);
        room.setRoomType(Room.RoomType.SINGLE);

        assertTrue("TC21 - Room title set", "Cozy Single Room".equals(room.getTitle()));
        assertTrue("TC22 - Room default status AVAILABLE",
                room.getStatus() == Room.RoomStatus.AVAILABLE);
        assertTrue("TC23 - Room has WiFi", room.isHasWifi());
        assertTrue("TC24 - Room no parking", !room.isHasParking());
        assertTrue("TC25 - Amenities string contains WiFi", room.getAmenitiesString().contains("WiFi"));
        assertTrue("TC26 - Amenities string contains Furnished",
                room.getAmenitiesString().contains("Furnished"));
        assertTrue("TC27 - Formatted price contains Rs.", room.getFormattedPrice().contains("Rs."));
        assertTrue("TC28 - Room type SINGLE", room.getRoomType() == Room.RoomType.SINGLE);
    }

    // ─── Booking Model Tests ──────────────────────────────────────────────────

    private static void testBookingModel() {
        System.out.println("\n--- Booking Model Tests ---");
        java.time.LocalDate start = java.time.LocalDate.now().plusDays(5);
        java.time.LocalDate end   = start.plusMonths(3);

        Booking booking = new Booking(1, 10, 20, start, end, 8000, "Hello owner");
        assertTrue("TC29 - Booking default status PENDING",
                booking.getStatus() == Booking.BookingStatus.PENDING);
        assertTrue("TC30 - Booking total = 3 months * 8000",
                booking.getTotalAmount() == 24000.0);
        assertTrue("TC31 - Booking duration string contains month",
                booking.getDurationString().contains("month"));
        assertTrue("TC32 - Formatted amount contains Rs.",
                booking.getFormattedAmount().contains("Rs."));
        assertTrue("TC33 - Booking message stored", "Hello owner".equals(booking.getMessage()));
    }

    // ─── SessionManager Tests ─────────────────────────────────────────────────

    private static void testSessionManager() {
        System.out.println("\n--- SessionManager Tests ---");
        SessionManager session = SessionManager.getInstance();

        // TC34: Singleton — same instance
        assertTrue("TC34 - Singleton returns same instance",
                session == SessionManager.getInstance());

        // TC35: Not logged in initially
        assertTrue("TC35 - Not logged in by default", !session.isLoggedIn());

        // TC36: Login sets user
        Owner owner = new Owner(1, "Test User", "t@t.com", "123", "pw", "addr");
        session.setCurrentUser(owner);
        assertTrue("TC36 - Is logged in after set", session.isLoggedIn());
        assertTrue("TC37 - isOwner returns true", session.isOwner());
        assertTrue("TC38 - isTenant returns false", !session.isTenant());

        // TC39: Logout clears user
        session.logout();
        assertTrue("TC39 - Not logged in after logout", !session.isLoggedIn());
    }

    // ─── Room Validation Tests (Service logic excerpted) ─────────────────────

    private static void testRoomValidation() {
        System.out.println("\n--- Room Validation Tests ---");

        Room validRoom = new Room();
        validRoom.setTitle("Nice Room");
        validRoom.setLocation("Thamel");
        validRoom.setPricePerMonth(6000);
        validRoom.setMaxOccupants(2);
        assertTrue("TC40 - Valid room title not blank", !validRoom.getTitle().isBlank());
        assertTrue("TC41 - Valid price > 0", validRoom.getPricePerMonth() > 0);
        assertTrue("TC42 - Max occupants >= 1", validRoom.getMaxOccupants() >= 1);

        Room invalidRoom = new Room();
        invalidRoom.setPricePerMonth(-500);
        assertTrue("TC43 - Invalid negative price detected", invalidRoom.getPricePerMonth() < 0);
        assertTrue("TC44 - Empty title detected",
                invalidRoom.getTitle() == null || (invalidRoom.getTitle().isBlank()));
    }

    // ─── Helper ──────────────────────────────────────────────────────────────

    private static void assertTrue(String testName, boolean condition) {
        if (condition) {
            System.out.printf("  ✅ PASS  %s%n", testName);
            passed++;
        } else {
            System.out.printf("  ❌ FAIL  %s%n", testName);
            failed++;
        }
    }
}
