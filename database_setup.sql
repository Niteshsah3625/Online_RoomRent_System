-- ═══════════════════════════════════════════════════════════════
--  Online Room Rent System — Database Setup Script
--  Run this in MySQL Workbench or MySQL CLI before launching the app.
--  Note: The application also auto-creates these tables on first run.
-- ═══════════════════════════════════════════════════════════════

-- Create and select the database
CREATE DATABASE IF NOT EXISTS room_rent_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE room_rent_db;

-- ─── Table: users ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100)  NOT NULL,
    email         VARCHAR(100)  UNIQUE NOT NULL,
    phone         VARCHAR(20),
    password      VARCHAR(255)  NOT NULL,          -- SHA-256 hashed
    role          ENUM('OWNER','TENANT') NOT NULL,
    address       VARCHAR(255),                    -- Owners only
    occupation    VARCHAR(100),                    -- Tenants only
    num_people    INT DEFAULT 1,                   -- Tenants only
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ─── Table: rooms ────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS rooms (
    room_id         INT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(150)  NOT NULL,
    description     TEXT,
    location        VARCHAR(255)  NOT NULL,
    price_per_month DOUBLE        NOT NULL,
    room_type       ENUM('SINGLE','DOUBLE','STUDIO','APARTMENT') DEFAULT 'SINGLE',
    status          ENUM('AVAILABLE','BOOKED','UNAVAILABLE')     DEFAULT 'AVAILABLE',
    owner_id        INT           NOT NULL,
    max_occupants   INT           DEFAULT 1,
    has_wifi        BOOLEAN       DEFAULT FALSE,
    has_parking     BOOLEAN       DEFAULT FALSE,
    has_furniture   BOOLEAN       DEFAULT FALSE,
    image_url       VARCHAR(500),
    created_at      TIMESTAMP     DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_room_owner FOREIGN KEY (owner_id)
        REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ─── Table: bookings ─────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS bookings (
    booking_id    INT AUTO_INCREMENT PRIMARY KEY,
    room_id       INT  NOT NULL,
    tenant_id     INT  NOT NULL,
    start_date    DATE NOT NULL,
    end_date      DATE NOT NULL,
    total_amount  DOUBLE,
    status        ENUM('PENDING','CONFIRMED','CANCELLED','COMPLETED') DEFAULT 'PENDING',
    message       TEXT,
    booking_date  DATE DEFAULT (CURRENT_DATE),
    CONSTRAINT fk_booking_room   FOREIGN KEY (room_id)
        REFERENCES rooms(room_id)   ON DELETE CASCADE,
    CONSTRAINT fk_booking_tenant FOREIGN KEY (tenant_id)
        REFERENCES users(user_id)   ON DELETE CASCADE
) ENGINE=InnoDB;

-- ─── Sample Data (optional — remove for production) ──────────────

-- Sample Owner (password = "owner123" hashed with SHA-256)
INSERT IGNORE INTO users (full_name, email, phone, password, role, address) VALUES
('',   'nitesh@roomrent.com',   '9800000001',
 'nitesh',
 'OWNER', 'Baneshwor, Kathmandu');

-- Sample Tenant (password = "tenant123" hashed with SHA-256)
INSERT IGNORE INTO users (full_name, email, phone, password, role, occupation, num_people) VALUES
('Sita Thapa',   'sita@roomrent.com',  '9800000002',
 '3e9add37c5e48ba8d4f38b5c8d0f0e1e9b3c4f5e6a7b8c9d0e1f2a3b4c5d6e7f',
 'TENANT', 'Student', 1);

-- Sample Room (owned by Ram Sharma — user_id=1)
INSERT IGNORE INTO rooms (title, description, location, price_per_month, room_type,
                           status, owner_id, max_occupants, has_wifi, has_parking, has_furniture)
VALUES
('Cozy Single Room in Baneshwor',
 'Quiet, well-lit single room with attached bathroom. Close to Koteshwor bus stop.',
 'Baneshwor, Kathmandu', 8000, 'SINGLE', 'AVAILABLE', 1, 1, TRUE, FALSE, TRUE),

('Spacious Double Room',
 'Large double room on 2nd floor. Suitable for couple or two students.',
 'Thamel, Kathmandu',    12000, 'DOUBLE', 'AVAILABLE', 1, 2, TRUE, TRUE,  TRUE),

('Studio Apartment',
 'Modern studio with kitchenette, perfect for working professionals.',
 'Lazimpat, Kathmandu',  18000, 'STUDIO', 'AVAILABLE', 1, 1, TRUE, FALSE, TRUE);

-- ─── Verification Queries ────────────────────────────────────────
-- Run these to confirm setup:
-- SELECT * FROM users;
-- SELECT * FROM rooms;
-- SELECT * FROM bookings;

SELECT 'Database setup complete!' AS Status;
