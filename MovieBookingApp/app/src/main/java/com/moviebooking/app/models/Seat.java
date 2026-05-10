package com.moviebooking.app.models;

/**
 * Seat model representing an individual seat in the theater grid.
 * Seat states: AVAILABLE, BOOKED, SELECTED, UNAVAILABLE (aisle/gap).
 */
public class Seat {

    // Seat state constants
    public static final int STATE_AVAILABLE   = 0;
    public static final int STATE_BOOKED      = 1;
    public static final int STATE_SELECTED    = 2;
    public static final int STATE_UNAVAILABLE = 3; // Aisle or gap

    // Seat category
    public static final String CATEGORY_STANDARD  = "Standard";
    public static final String CATEGORY_PREMIUM   = "Premium";
    public static final String CATEGORY_RECLINER  = "Recliner";

    private String seatId;     // e.g., "A1", "B12"
    private int row;           // Row index (0-based)
    private int col;           // Column index (0-based)
    private int state;         // One of the STATE_* constants
    private String category;   // CATEGORY_* constant
    private int price;         // Price may vary by category

    // Default constructor
    public Seat() {}

    // Full constructor
    public Seat(String seatId, int row, int col, int state, String category, int price) {
        this.seatId = seatId;
        this.row = row;
        this.col = col;
        this.state = state;
        this.category = category;
        this.price = price;
    }

    // ── Convenience methods ────────────────────────────────────────────────────

    public boolean isAvailable()   { return state == STATE_AVAILABLE; }
    public boolean isBooked()      { return state == STATE_BOOKED; }
    public boolean isSelected()    { return state == STATE_SELECTED; }
    public boolean isUnavailable() { return state == STATE_UNAVAILABLE; }

    /**
     * Toggles between SELECTED and AVAILABLE states.
     * No-op if the seat is BOOKED or UNAVAILABLE.
     */
    public void toggleSelection() {
        if (state == STATE_AVAILABLE) {
            state = STATE_SELECTED;
        } else if (state == STATE_SELECTED) {
            state = STATE_AVAILABLE;
        }
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public String getSeatId() { return seatId; }
    public void setSeatId(String seatId) { this.seatId = seatId; }

    public int getRow() { return row; }
    public void setRow(int row) { this.row = row; }

    public int getCol() { return col; }
    public void setCol(int col) { this.col = col; }

    public int getState() { return state; }
    public void setState(int state) { this.state = state; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPrice() { return price; }
    public void setPrice(int price) { this.price = price; }
}
