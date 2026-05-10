package com.moviebooking.app.utils;

import android.text.TextUtils;
import android.util.Patterns;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Input validation and password hashing utilities.
 */
public class ValidationUtils {

    private ValidationUtils() { /* utility class */ }

    // ── Input Validation ───────────────────────────────────────────────────────

    /**
     * Returns true if the string is non-null and non-empty after trimming.
     */
    public static boolean isNotEmpty(String value) {
        return !TextUtils.isEmpty(value) && !value.trim().isEmpty();
    }

    /**
     * Returns true if the email matches standard RFC pattern.
     */
    public static boolean isValidEmail(String email) {
        return isNotEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches();
    }

    /**
     * Returns true if the password meets minimum requirements:
     *   - At least 8 characters
     *   - At least one letter
     *   - At least one digit
     */
    public static boolean isValidPassword(String password) {
        if (!isNotEmpty(password) || password.length() < 8) return false;
        boolean hasLetter = false;
        boolean hasDigit  = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) hasLetter = true;
            if (Character.isDigit(c))  hasDigit  = true;
        }
        return hasLetter && hasDigit;
    }

    /**
     * Returns true if the Indian mobile number is 10 digits.
     */
    public static boolean isValidPhone(String phone) {
        return isNotEmpty(phone)
            && phone.trim().replaceAll("[^0-9]", "").length() == 10;
    }

    /**
     * Returns true if the name is at least 2 characters long.
     */
    public static boolean isValidName(String name) {
        return isNotEmpty(name) && name.trim().length() >= 2;
    }

    // ── Password Hashing ───────────────────────────────────────────────────────

    /**
     * Hashes a password using SHA-256.
     * NOTE: In production use BCrypt (via a native lib or Firebase Auth).
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (NoSuchAlgorithmException e) {
            // Should never happen on Android
            return password;
        }
    }

    /**
     * Verifies a plain-text password against a stored hash.
     */
    public static boolean verifyPassword(String plainPassword, String storedHash) {
        return hashPassword(plainPassword).equals(storedHash);
    }

    // ── Error Message Helpers ──────────────────────────────────────────────────

    public static String getPasswordRequirements() {
        return "Password must be at least 8 characters and contain letters & digits.";
    }
}
