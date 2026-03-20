/**
 * Module descriptor for Online Room Rent System.
 * Required by Java 9+ module system to work with JavaFX and FXML.
 */
module com.roomrent {
    // JavaFX dependencies
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;

    // JDBC
    requires java.sql;

    // Open packages to JavaFX FXML reflective access
    opens com.roomrent.app        to javafx.fxml, javafx.graphics;
    opens com.roomrent.controller to javafx.fxml;
    opens com.roomrent.model      to javafx.base, javafx.fxml;

    // Export packages
    exports com.roomrent.app;
    exports com.roomrent.model;
    exports com.roomrent.service;
    exports com.roomrent.dao;
    exports com.roomrent.util;
    exports com.roomrent.controller;
}
