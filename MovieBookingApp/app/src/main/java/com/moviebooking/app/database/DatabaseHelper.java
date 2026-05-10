package com.moviebooking.app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.moviebooking.app.models.Booking;
import com.moviebooking.app.models.Movie;
import com.moviebooking.app.models.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * SQLite database helper for local data persistence.
 * Handles Users, Movies (cached), Bookings, and Favorites.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database info
    private static final String DATABASE_NAME = "movie_booking.db";
    private static final int    DATABASE_VERSION = 1;

    // Singleton instance
    private static DatabaseHelper instance;

    // ── Table Names ────────────────────────────────────────────────────────────
    private static final String TABLE_USERS     = "users";
    private static final String TABLE_MOVIES    = "movies";
    private static final String TABLE_BOOKINGS  = "bookings";
    private static final String TABLE_FAVORITES = "favorites";

    // ── Common Columns ─────────────────────────────────────────────────────────
    private static final String COL_ID = "id";

    // ── Users Columns ──────────────────────────────────────────────────────────
    private static final String COL_NAME          = "name";
    private static final String COL_EMAIL         = "email";
    private static final String COL_PHONE         = "phone";
    private static final String COL_PASSWORD_HASH = "password_hash";
    private static final String COL_PROFILE_PIC   = "profile_pic";
    private static final String COL_CITY          = "city";
    private static final String COL_CREATED_AT    = "created_at";

    // ── Movies Columns ─────────────────────────────────────────────────────────
    private static final String COL_TITLE        = "title";
    private static final String COL_DESCRIPTION  = "description";
    private static final String COL_GENRE        = "genre";
    private static final String COL_DURATION     = "duration";
    private static final String COL_LANGUAGE     = "language";
    private static final String COL_RELEASE_DATE = "release_date";
    private static final String COL_RATING       = "rating";
    private static final String COL_RATING_COUNT = "rating_count";
    private static final String COL_POSTER_URL   = "poster_url";
    private static final String COL_BANNER_URL   = "banner_url";
    private static final String COL_DIRECTOR     = "director";
    private static final String COL_CAST         = "cast";
    private static final String COL_CERTIFICATE  = "certificate";
    private static final String COL_PRICE        = "price_per_seat";

    // ── Bookings Columns ───────────────────────────────────────────────────────
    private static final String COL_BOOKING_ID     = "booking_id";
    private static final String COL_USER_ID        = "user_id";
    private static final String COL_MOVIE_ID       = "movie_id";
    private static final String COL_MOVIE_TITLE    = "movie_title";
    private static final String COL_MOVIE_POSTER   = "movie_poster";
    private static final String COL_THEATER        = "theater";
    private static final String COL_SHOW_DATE      = "show_date";
    private static final String COL_SHOW_TIME      = "show_time";
    private static final String COL_SEATS          = "seats";        // Comma-separated
    private static final String COL_TOTAL_SEATS    = "total_seats";
    private static final String COL_PRICE_PER_SEAT = "price_per_seat";
    private static final String COL_TOTAL_AMOUNT   = "total_amount";
    private static final String COL_PAYMENT_METHOD = "payment_method";
    private static final String COL_STATUS         = "status";
    private static final String COL_BOOKED_AT      = "booked_at";
    private static final String COL_QR_DATA        = "qr_data";

    // ── CREATE TABLE Statements ────────────────────────────────────────────────

    private static final String CREATE_TABLE_USERS =
        "CREATE TABLE " + TABLE_USERS + " ("
        + COL_ID          + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COL_NAME        + " TEXT NOT NULL, "
        + COL_EMAIL       + " TEXT NOT NULL UNIQUE, "
        + COL_PHONE       + " TEXT, "
        + COL_PASSWORD_HASH + " TEXT NOT NULL, "
        + COL_PROFILE_PIC + " TEXT, "
        + COL_CITY        + " TEXT, "
        + COL_CREATED_AT  + " INTEGER DEFAULT 0"
        + ");";

    private static final String CREATE_TABLE_MOVIES =
        "CREATE TABLE " + TABLE_MOVIES + " ("
        + COL_ID           + " INTEGER PRIMARY KEY, "
        + COL_TITLE        + " TEXT NOT NULL, "
        + COL_DESCRIPTION  + " TEXT, "
        + COL_GENRE        + " TEXT, "
        + COL_DURATION     + " TEXT, "
        + COL_LANGUAGE     + " TEXT, "
        + COL_RELEASE_DATE + " TEXT, "
        + COL_RATING       + " REAL DEFAULT 0, "
        + COL_RATING_COUNT + " INTEGER DEFAULT 0, "
        + COL_POSTER_URL   + " TEXT, "
        + COL_BANNER_URL   + " TEXT, "
        + COL_DIRECTOR     + " TEXT, "
        + COL_CAST         + " TEXT, "
        + COL_CERTIFICATE  + " TEXT, "
        + COL_PRICE        + " INTEGER DEFAULT 150"
        + ");";

    private static final String CREATE_TABLE_BOOKINGS =
        "CREATE TABLE " + TABLE_BOOKINGS + " ("
        + COL_ID            + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COL_BOOKING_ID    + " TEXT NOT NULL UNIQUE, "
        + COL_USER_ID       + " INTEGER NOT NULL, "
        + COL_MOVIE_ID      + " INTEGER, "
        + COL_MOVIE_TITLE   + " TEXT, "
        + COL_MOVIE_POSTER  + " TEXT, "
        + COL_THEATER       + " TEXT, "
        + COL_SHOW_DATE     + " TEXT, "
        + COL_SHOW_TIME     + " TEXT, "
        + COL_SEATS         + " TEXT, "
        + COL_TOTAL_SEATS   + " INTEGER DEFAULT 0, "
        + COL_PRICE_PER_SEAT + " INTEGER DEFAULT 0, "
        + COL_TOTAL_AMOUNT  + " INTEGER DEFAULT 0, "
        + COL_PAYMENT_METHOD + " TEXT, "
        + COL_STATUS        + " TEXT DEFAULT 'CONFIRMED', "
        + COL_BOOKED_AT     + " INTEGER DEFAULT 0, "
        + COL_QR_DATA       + " TEXT"
        + ");";

    private static final String CREATE_TABLE_FAVORITES =
        "CREATE TABLE " + TABLE_FAVORITES + " ("
        + COL_ID       + " INTEGER PRIMARY KEY AUTOINCREMENT, "
        + COL_USER_ID  + " INTEGER NOT NULL, "
        + COL_MOVIE_ID + " INTEGER NOT NULL, "
        + "UNIQUE(" + COL_USER_ID + ", " + COL_MOVIE_ID + ")"
        + ");";

    // ── Singleton ──────────────────────────────────────────────────────────────

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context.getApplicationContext());
        }
        return instance;
    }

    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
        db.execSQL(CREATE_TABLE_MOVIES);
        db.execSQL(CREATE_TABLE_BOOKINGS);
        db.execSQL(CREATE_TABLE_FAVORITES);
        Log.d(TAG, "Database tables created.");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FAVORITES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // USER OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Inserts a new user. Returns the row ID on success, -1 on failure.
     */
    public long insertUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME,          user.getName());
        cv.put(COL_EMAIL,         user.getEmail().toLowerCase().trim());
        cv.put(COL_PHONE,         user.getPhone());
        cv.put(COL_PASSWORD_HASH, user.getPasswordHash());
        cv.put(COL_PROFILE_PIC,   user.getProfilePicPath());
        cv.put(COL_CITY,          user.getCity());
        cv.put(COL_CREATED_AT,    user.getCreatedAt());
        return db.insert(TABLE_USERS, null, cv);
    }

    /**
     * Retrieves a user by email.  Returns null if not found.
     */
    public User getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
            COL_EMAIL + "=?", new String[]{email.toLowerCase().trim()},
            null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    /**
     * Retrieves a user by ID.  Returns null if not found.
     */
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
            COL_ID + "=?", new String[]{String.valueOf(userId)},
            null, null, null);

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = cursorToUser(cursor);
            cursor.close();
        }
        return user;
    }

    /**
     * Updates user profile fields. Returns number of rows affected.
     */
    public int updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME,        user.getName());
        cv.put(COL_PHONE,       user.getPhone());
        cv.put(COL_CITY,        user.getCity());
        cv.put(COL_PROFILE_PIC, user.getProfilePicPath());
        return db.update(TABLE_USERS, cv, COL_ID + "=?",
            new String[]{String.valueOf(user.getId())});
    }

    /**
     * Checks if the given email is already registered.
     */
    public boolean isEmailRegistered(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COL_ID},
            COL_EMAIL + "=?", new String[]{email.toLowerCase().trim()},
            null, null, null);
        boolean exists = (cursor != null && cursor.getCount() > 0);
        if (cursor != null) cursor.close();
        return exists;
    }

    private User cursorToUser(Cursor c) {
        User user = new User();
        user.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        user.setName(c.getString(c.getColumnIndexOrThrow(COL_NAME)));
        user.setEmail(c.getString(c.getColumnIndexOrThrow(COL_EMAIL)));
        user.setPhone(c.getString(c.getColumnIndexOrThrow(COL_PHONE)));
        user.setPasswordHash(c.getString(c.getColumnIndexOrThrow(COL_PASSWORD_HASH)));
        user.setProfilePicPath(c.getString(c.getColumnIndexOrThrow(COL_PROFILE_PIC)));
        user.setCity(c.getString(c.getColumnIndexOrThrow(COL_CITY)));
        user.setCreatedAt(c.getLong(c.getColumnIndexOrThrow(COL_CREATED_AT)));
        return user;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // MOVIE OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Inserts or replaces a movie in local cache.
     */
    public long insertOrReplaceMovie(Movie movie) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = movieToContentValues(movie);
        return db.insertWithOnConflict(TABLE_MOVIES, null, cv,
            SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Bulk inserts a list of movies (used for seeding sample data).
     */
    public void insertMovies(List<Movie> movies) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            for (Movie m : movies) {
                db.insertWithOnConflict(TABLE_MOVIES, null,
                    movieToContentValues(m), SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Returns all movies from cache.
     */
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MOVIES, null, null, null, null, null,
                COL_RATING + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    movies.add(cursorToMovie(cursor));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return movies;
    }

    /**
     * Returns movies filtered by genre (partial match).
     */
    public List<Movie> getMoviesByGenre(String genre) {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MOVIES, null,
                COL_GENRE + " LIKE ?", new String[]{"%" + genre + "%"},
                null, null, COL_RATING + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    movies.add(cursorToMovie(cursor));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return movies;
    }

    /**
     * Searches movies by title.
     */
    public List<Movie> searchMovies(String query) {
        List<Movie> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String likeQuery = "%" + query + "%";
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_MOVIES, null,
                COL_TITLE + " LIKE ? OR " + COL_GENRE + " LIKE ? OR " + COL_CAST + " LIKE ?",
                new String[]{likeQuery, likeQuery, likeQuery},
                null, null, COL_RATING + " DESC");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    movies.add(cursorToMovie(cursor));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return movies;
    }

    /**
     * Returns true if the movies table is empty (first launch).
     */
    public boolean isMoviesEmpty() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean empty = true;
        try {
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MOVIES, null);
            if (cursor != null && cursor.moveToFirst()) {
                empty = (cursor.getInt(0) == 0);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return empty;
    }

    private ContentValues movieToContentValues(Movie m) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID,           m.getId());
        cv.put(COL_TITLE,        m.getTitle());
        cv.put(COL_DESCRIPTION,  m.getDescription());
        cv.put(COL_GENRE,        m.getGenre());
        cv.put(COL_DURATION,     m.getDuration());
        cv.put(COL_LANGUAGE,     m.getLanguage());
        cv.put(COL_RELEASE_DATE, m.getReleaseDate());
        cv.put(COL_RATING,       m.getRating());
        cv.put(COL_RATING_COUNT, m.getRatingCount());
        cv.put(COL_POSTER_URL,   m.getPosterUrl());
        cv.put(COL_BANNER_URL,   m.getBannerUrl());
        cv.put(COL_DIRECTOR,     m.getDirector());
        cv.put(COL_CAST,         m.getCast());
        cv.put(COL_CERTIFICATE,  m.getCertificate());
        cv.put(COL_PRICE,        m.getPricePerSeat());
        return cv;
    }

    private Movie cursorToMovie(Cursor c) {
        Movie m = new Movie();
        m.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        m.setTitle(c.getString(c.getColumnIndexOrThrow(COL_TITLE)));
        m.setDescription(c.getString(c.getColumnIndexOrThrow(COL_DESCRIPTION)));
        m.setGenre(c.getString(c.getColumnIndexOrThrow(COL_GENRE)));
        m.setDuration(c.getString(c.getColumnIndexOrThrow(COL_DURATION)));
        m.setLanguage(c.getString(c.getColumnIndexOrThrow(COL_LANGUAGE)));
        m.setReleaseDate(c.getString(c.getColumnIndexOrThrow(COL_RELEASE_DATE)));
        m.setRating(c.getFloat(c.getColumnIndexOrThrow(COL_RATING)));
        m.setRatingCount(c.getInt(c.getColumnIndexOrThrow(COL_RATING_COUNT)));
        m.setPosterUrl(c.getString(c.getColumnIndexOrThrow(COL_POSTER_URL)));
        m.setBannerUrl(c.getString(c.getColumnIndexOrThrow(COL_BANNER_URL)));
        m.setDirector(c.getString(c.getColumnIndexOrThrow(COL_DIRECTOR)));
        m.setCast(c.getString(c.getColumnIndexOrThrow(COL_CAST)));
        m.setCertificate(c.getString(c.getColumnIndexOrThrow(COL_CERTIFICATE)));
        m.setPricePerSeat(c.getInt(c.getColumnIndexOrThrow(COL_PRICE)));
        return m;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // BOOKING OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Inserts a new booking. Returns row ID on success, -1 on failure.
     */
    public long insertBooking(Booking booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_BOOKING_ID,     booking.getBookingId());
        cv.put(COL_USER_ID,        booking.getUserId());
        cv.put(COL_MOVIE_ID,       booking.getMovieId());
        cv.put(COL_MOVIE_TITLE,    booking.getMovieTitle());
        cv.put(COL_MOVIE_POSTER,   booking.getMoviePosterUrl());
        cv.put(COL_THEATER,        booking.getTheater());
        cv.put(COL_SHOW_DATE,      booking.getShowDate());
        cv.put(COL_SHOW_TIME,      booking.getShowTime());
        // Store seats as comma-separated string
        cv.put(COL_SEATS,          String.join(",", booking.getSelectedSeats()));
        cv.put(COL_TOTAL_SEATS,    booking.getTotalSeats());
        cv.put(COL_PRICE_PER_SEAT, booking.getPricePerSeat());
        cv.put(COL_TOTAL_AMOUNT,   booking.getTotalAmount());
        cv.put(COL_PAYMENT_METHOD, booking.getPaymentMethod());
        cv.put(COL_STATUS,         booking.getStatus());
        cv.put(COL_BOOKED_AT,      booking.getBookedAt());
        cv.put(COL_QR_DATA,        booking.getQrCodeData());

        return db.insert(TABLE_BOOKINGS, null, cv);
    }

    /**
     * Returns all bookings for a given user, ordered newest first.
     */
    public List<Booking> getBookingsByUserId(int userId) {
        List<Booking> bookings = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_BOOKINGS, null,
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, COL_BOOKED_AT + " DESC");

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bookings.add(cursorToBooking(cursor));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return bookings;
    }

    /**
     * Returns a single booking by its human-readable booking ID string.
     */
    public Booking getBookingByBookingId(String bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        Booking booking = null;
        try {
            cursor = db.query(TABLE_BOOKINGS, null,
                COL_BOOKING_ID + "=?", new String[]{bookingId},
                null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                booking = cursorToBooking(cursor);
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return booking;
    }

    /**
     * Cancels a booking by updating its status.
     */
    public int cancelBooking(String bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_STATUS, Booking.STATUS_CANCELLED);
        return db.update(TABLE_BOOKINGS, cv, COL_BOOKING_ID + "=?",
            new String[]{bookingId});
    }

    private Booking cursorToBooking(Cursor c) {
        Booking b = new Booking();
        b.setId(c.getInt(c.getColumnIndexOrThrow(COL_ID)));
        b.setBookingId(c.getString(c.getColumnIndexOrThrow(COL_BOOKING_ID)));
        b.setUserId(c.getInt(c.getColumnIndexOrThrow(COL_USER_ID)));
        b.setMovieId(c.getInt(c.getColumnIndexOrThrow(COL_MOVIE_ID)));
        b.setMovieTitle(c.getString(c.getColumnIndexOrThrow(COL_MOVIE_TITLE)));
        b.setMoviePosterUrl(c.getString(c.getColumnIndexOrThrow(COL_MOVIE_POSTER)));
        b.setTheater(c.getString(c.getColumnIndexOrThrow(COL_THEATER)));
        b.setShowDate(c.getString(c.getColumnIndexOrThrow(COL_SHOW_DATE)));
        b.setShowTime(c.getString(c.getColumnIndexOrThrow(COL_SHOW_TIME)));

        // Parse comma-separated seats back to list
        String seatsStr = c.getString(c.getColumnIndexOrThrow(COL_SEATS));
        if (seatsStr != null && !seatsStr.isEmpty()) {
            b.setSelectedSeats(new ArrayList<>(Arrays.asList(seatsStr.split(","))));
        } else {
            b.setSelectedSeats(new ArrayList<>());
        }

        b.setTotalSeats(c.getInt(c.getColumnIndexOrThrow(COL_TOTAL_SEATS)));
        b.setPricePerSeat(c.getInt(c.getColumnIndexOrThrow(COL_PRICE_PER_SEAT)));
        b.setTotalAmount(c.getInt(c.getColumnIndexOrThrow(COL_TOTAL_AMOUNT)));
        b.setPaymentMethod(c.getString(c.getColumnIndexOrThrow(COL_PAYMENT_METHOD)));
        b.setStatus(c.getString(c.getColumnIndexOrThrow(COL_STATUS)));
        b.setBookedAt(c.getLong(c.getColumnIndexOrThrow(COL_BOOKED_AT)));
        b.setQrCodeData(c.getString(c.getColumnIndexOrThrow(COL_QR_DATA)));
        return b;
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // FAVORITES OPERATIONS
    // ═══════════════════════════════════════════════════════════════════════════

    /**
     * Adds a movie to user's favorites. Returns -1 if already a favorite.
     */
    public long addFavorite(int userId, int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_USER_ID,  userId);
        cv.put(COL_MOVIE_ID, movieId);
        return db.insertWithOnConflict(TABLE_FAVORITES, null, cv,
            SQLiteDatabase.CONFLICT_IGNORE);
    }

    /**
     * Removes a movie from user's favorites.
     */
    public int removeFavorite(int userId, int movieId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_FAVORITES,
            COL_USER_ID + "=? AND " + COL_MOVIE_ID + "=?",
            new String[]{String.valueOf(userId), String.valueOf(movieId)});
    }

    /**
     * Checks if a movie is in user's favorites.
     */
    public boolean isFavorite(int userId, int movieId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        boolean fav = false;
        try {
            cursor = db.query(TABLE_FAVORITES, new String[]{COL_ID},
                COL_USER_ID + "=? AND " + COL_MOVIE_ID + "=?",
                new String[]{String.valueOf(userId), String.valueOf(movieId)},
                null, null, null);
            fav = (cursor != null && cursor.getCount() > 0);
        } finally {
            if (cursor != null) cursor.close();
        }
        return fav;
    }

    /**
     * Returns the list of favorite movie IDs for a user.
     */
    public List<Integer> getFavoriteMovieIds(int userId) {
        List<Integer> ids = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.query(TABLE_FAVORITES, new String[]{COL_MOVIE_ID},
                COL_USER_ID + "=?", new String[]{String.valueOf(userId)},
                null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    ids.add(cursor.getInt(0));
                }
            }
        } finally {
            if (cursor != null) cursor.close();
        }
        return ids;
    }
}
