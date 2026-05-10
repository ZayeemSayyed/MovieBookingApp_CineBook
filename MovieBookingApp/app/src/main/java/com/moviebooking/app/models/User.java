package com.moviebooking.app.models;

import java.io.Serializable;

/**
 * User model representing an authenticated user in the booking system.
 */
public class User implements Serializable {

    private int id;
    private String name;
    private String email;
    private String phone;
    private String passwordHash;   // Stored as a BCrypt/SHA hash — never plain text
    private String profilePicPath; // Local file path or URL
    private String city;
    private long createdAt;        // Unix timestamp

    // Default constructor
    public User() {}

    // Registration constructor
    public User(String name, String email, String phone, String passwordHash) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.createdAt = System.currentTimeMillis();
    }

    // Full constructor
    public User(int id, String name, String email, String phone,
                String passwordHash, String profilePicPath, String city) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.passwordHash = passwordHash;
        this.profilePicPath = profilePicPath;
        this.city = city;
        this.createdAt = System.currentTimeMillis();
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getProfilePicPath() { return profilePicPath; }
    public void setProfilePicPath(String profilePicPath) { this.profilePicPath = profilePicPath; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }

    /**
     * Returns the first letter of the user's name for avatar placeholder.
     */
    public String getInitial() {
        if (name != null && !name.isEmpty()) {
            return String.valueOf(name.charAt(0)).toUpperCase();
        }
        return "?";
    }
}
