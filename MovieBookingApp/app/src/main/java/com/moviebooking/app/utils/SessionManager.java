package com.moviebooking.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages user session state using SharedPreferences.
 * Stores the logged-in user's ID and basic profile info for quick access.
 */
public class SessionManager {

    private static final String PREF_NAME    = "MovieBookingSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID      = "userId";
    private static final String KEY_USER_NAME    = "userName";
    private static final String KEY_USER_EMAIL   = "userEmail";
    private static final String KEY_USER_PHONE   = "userPhone";
    private static final String KEY_DARK_MODE    = "darkMode";

    private static SessionManager instance;
    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    // Singleton
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }

    private SessionManager(Context context) {
        prefs  = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    // ── Session CRUD ───────────────────────────────────────────────────────────

    /**
     * Creates a new session for the given user.
     */
    public void createSession(int userId, String name, String email, String phone) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID,          userId);
        editor.putString(KEY_USER_NAME,     name);
        editor.putString(KEY_USER_EMAIL,    email);
        editor.putString(KEY_USER_PHONE,    phone);
        editor.apply();
    }

    /**
     * Clears the session (logout).
     */
    public void logout() {
        editor.remove(KEY_IS_LOGGED_IN);
        editor.remove(KEY_USER_ID);
        editor.remove(KEY_USER_NAME);
        editor.remove(KEY_USER_EMAIL);
        editor.remove(KEY_USER_PHONE);
        editor.apply();
    }

    /** Returns true if a user is currently logged in. */
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    /** Returns the logged-in user's database ID, or -1 if not logged in. */
    public int getUserId() {
        return prefs.getInt(KEY_USER_ID, -1);
    }

    public String getUserName()  { return prefs.getString(KEY_USER_NAME,  ""); }
    public String getUserEmail() { return prefs.getString(KEY_USER_EMAIL, ""); }
    public String getUserPhone() { return prefs.getString(KEY_USER_PHONE, ""); }

    /**
     * Updates cached display name (called after profile edit).
     */
    public void updateName(String name) {
        editor.putString(KEY_USER_NAME, name);
        editor.apply();
    }

    // ── Dark Mode ──────────────────────────────────────────────────────────────

    public boolean isDarkMode() {
        return prefs.getBoolean(KEY_DARK_MODE, false);
    }

    public void setDarkMode(boolean enabled) {
        editor.putBoolean(KEY_DARK_MODE, enabled);
        editor.apply();
    }
}
