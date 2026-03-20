# 🏠 Online Room Rent System

A professional desktop application built with **Java 21** and **JavaFX** that digitises the room rental process — connecting property owners with tenants, eliminating brokers and manual record-keeping.

---

## 📋 Project Overview

| Item | Details |
|---|---|
| **Language** | Java 21 (LTS) |
| **UI Framework** | JavaFX 21 + FXML + CSS |
| **Database** | MySQL 8.x via JDBC |
| **Build Tool** | Apache Maven 3.9+ |
| **Architecture** | MVC + Service Layer + DAO Pattern |
| **OOP Pillars** | Abstraction, Encapsulation, Inheritance, Polymorphism |

---

## ✨ Features

### Owner
- Register and log in securely (SHA-256 hashed passwords)
- List new rooms with title, location, price, type, amenities
- Edit and delete room listings
- View all incoming booking requests
- Confirm or cancel tenant bookings
- Dashboard with room stats (total, available, pending bookings)

### Tenant
- Register and log in
- Browse all available rooms
- Search/filter by location, price range, room type, and keyword
- View room details and owner contact info
- Send booking requests with move-in/move-out dates
- View and cancel their own bookings

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────┐
│          View Layer (FXML + CSS)                 │
│  login · register · owner-dashboard              │
│  tenant-dashboard · add-room · book-room         │
├─────────────────────────────────────────────────┤
│       Controller Layer (JavaFX Controllers)      │
│  LoginController · RegisterController            │
│  OwnerDashboardController · TenantDashboard...   │
│  AddRoomController · BookRoomController          │
├─────────────────────────────────────────────────┤
│         Service Layer (Business Rules)           │
│  AuthService · RoomService · BookingService      │
├─────────────────────────────────────────────────┤
│         DAO Layer (Data Access)                  │
│  UserDAO/Impl · RoomDAO/Impl · BookingDAO        │
├─────────────────────────────────────────────────┤
│         Model Layer (Entities)                   │
│  User (abstract) · Owner · Tenant · Room · Booking│
├─────────────────────────────────────────────────┤
│         Utility Layer                            │
│  DatabaseConnection (Singleton)                  │
│  SessionManager (Singleton) · PasswordUtil       │
├─────────────────────────────────────────────────┤
│         MySQL Database                           │
│  users · rooms · bookings                        │
└─────────────────────────────────────────────────┘
```

---

## 🎓 OOP Concepts Demonstrated

| Concept | Implementation |
|---|---|
| **Abstraction** | `User` abstract class; `UserDAO`, `RoomDAO` interfaces |
| **Encapsulation** | All model fields private with getters/setters |
| **Inheritance** | `Owner extends User`, `Tenant extends User` |
| **Polymorphism** | `getDashboardTitle()` override; `mapRowToUser()` returns Owner or Tenant at runtime |
| **Singleton** | `DatabaseConnection`, `SessionManager` |
| **DAO Pattern** | All SQL isolated in DAO classes; services contain no SQL |
| **Service Layer** | Business rule validation separated from persistence |
| **MVC** | FXML=View, Controllers=Controller, Services+DAOs=Model |

---

## 🗄️ Database Schema

```sql
CREATE DATABASE room_rent_db;

-- Auto-created by DatabaseConnection.initializeTables() on first run
-- Tables: users, rooms, bookings
-- See DatabaseConnection.java for full DDL
```

---

## 🚀 Getting Started

### Prerequisites
- Java JDK 21+
- JavaFX SDK 21.0.2 → https://openjfx.io
- MySQL 8.x running on localhost:3306
- Maven 3.9+ (or manually manage JARs)

### 1. Database Setup
```sql
CREATE DATABASE room_rent_db;
-- Tables are auto-created on first app launch
```

### 2. Configure DB Password
Open `src/main/java/com/roomrent/util/DatabaseConnection.java`:
```java
private static final String PASSWORD = "your_mysql_password_here";
```

### 3. Build & Run with Maven
```bash
mvn clean compile
mvn javafx:run
```

### 4. Run without Maven (IntelliJ)
1. Add `mysql-connector-java-8.0.33.jar` to project libraries
2. Add all JARs from `javafx-sdk-21/lib/` to project libraries
3. Set VM options:
   ```
   --module-path /path/to/javafx-sdk-21/lib
   --add-modules javafx.controls,javafx.fxml
   ```
4. Right-click `MainApp.java` → Run

---

## 📁 Project Structure

```
RoomRentSystem/
├── pom.xml
├── README.md
└── src/
    ├── main/
    │   ├── java/
    │   │   ├── module-info.java
    │   │   └── com/roomrent/
    │   │       ├── app/           MainApp.java
    │   │       ├── model/         User, Owner, Tenant, Room, Booking
    │   │       ├── dao/           UserDAO, RoomDAO, BookingDAO + Impls
    │   │       ├── service/       AuthService, RoomService, BookingService
    │   │       ├── controller/    6 JavaFX Controllers + RoomEditContext
    │   │       └── util/          DatabaseConnection, PasswordUtil, SessionManager
    │   └── resources/
    │       └── com/roomrent/views/
    │           ├── login.fxml
    │           ├── register.fxml
    │           ├── owner-dashboard.fxml
    │           ├── tenant-dashboard.fxml
    │           ├── add-room.fxml
    │           ├── book-room.fxml
    │           └── style.css
    └── test/
        └── java/com/roomrent/
            └── TestSuite.java     (44 unit tests, no framework needed)
```

---

## 🧪 Running Tests

```bash
# Compile and run the standalone test suite (no JUnit required)
javac -cp . TestSuite.java
java TestSuite

# Expected output:
# 44 Passed | 0 Failed
```

---

## 📸 Screen Flow

```
Launch → Login
           ├─ Owner  → Owner Dashboard → Add/Edit Room
           │                           → Confirm/Cancel Bookings
           └─ Tenant → Tenant Dashboard → Search Rooms → Book Room
                                        → My Bookings → Cancel
```

---

## 📄 Documentation

See `OOP_Architecture_Documentation.pdf` for full architecture diagrams, class definitions, database ERD, implementation details, and test results.

---

## 👨‍💻 Technologies

![Java](https://img.shields.io/badge/Java-21-orange)
![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)
![MySQL](https://img.shields.io/badge/MySQL-8.x-blue)
![Maven](https://img.shields.io/badge/Maven-3.9-red)
