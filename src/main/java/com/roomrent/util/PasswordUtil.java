package com.roomrent.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;

/**
 * Utility class for password hashing.
 * Uses SHA-256 for demonstration. In production, use BCrypt.
 */
public class PasswordUtil {

    private PasswordUtil() {} // Utility class — no instantiation

    /**
     * Hashes a plain text password using SHA-256.
     * @param plainPassword the raw password string
     * @return hex-encoded hash string
     */
    public static String hashPassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(plainPassword.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 64) hexString.insert(0, '0');
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }

    /**
     * Verifies a plain password against a stored hash.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }

    /**
     * Validates password strength — minimum 6 characters.
     */
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    /**
     * Validates email format using simple regex.
     */
    public static boolean isValidEmail(String email) {
        return email != null && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }

    /**
     * Validates phone number — digits only, 7-15 chars.
     */
    public static boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^[0-9]{7,15}$");
    }
}
