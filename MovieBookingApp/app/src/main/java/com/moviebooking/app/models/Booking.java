package com.moviebooking.app.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Booking model representing a confirmed ticket reservation.
 */
public class Booking implements Serializable {

    // Booking statuses
    public static final String STATUS_CONFIRMED = "CONFIRMED";
    public static final String STATUS_CANCELLED = "CANCELLED";
    public static final String STATUS_PENDING    = "PENDING";

    private int id;
    private String bookingId;       // Human-readable: MB-XXXXXXXX
    private int userId;
    private int movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private String theater;
    private String showDate;        // e.g., "25 Dec 2024"
    private String showTime;        // e.g., "07:30 PM"
    private List<String> selectedSeats;
    private int totalSeats;
    private int pricePerSeat;
    private int totalAmount;
    private String paymentMethod;
    private String status;
    private long bookedAt;          // Unix timestamp
    private String qrCodeData;

    // Default constructor
    public Booking() {
        this.bookingId = generateBookingId();
        this.bookedAt = System.currentTimeMillis();
        this.status = STATUS_CONFIRMED;
    }

    // Full constructor used after seat selection + payment
    public Booking(int userId, Movie movie, String theater, String showDate,
                   String showTime, List<String> selectedSeats, String paymentMethod) {
        this.bookingId = generateBookingId();
        this.bookedAt = System.currentTimeMillis();
        this.status = STATUS_CONFIRMED;

        this.userId = userId;
        this.movieId = movie.getId();
        this.movieTitle = movie.getTitle();
        this.moviePosterUrl = movie.getPosterUrl();
        this.theater = theater;
        this.showDate = showDate;
        this.showTime = showTime;
        this.selectedSeats = selectedSeats;
        this.totalSeats = selectedSeats.size();
        this.pricePerSeat = movie.getPricePerSeat();
        this.totalAmount = pricePerSeat * totalSeats;
        this.paymentMethod = paymentMethod;

        // Build QR code data string
        this.qrCodeData = buildQrData();
    }

    // ── Helper Methods ─────────────────────────────────────────────────────────

    /**
     * Generates a unique booking ID in format MB-XXXXXXXX (uppercase hex).
     */
    private String generateBookingId() {
        String uuid = UUID.randomUUID().toString().replace("-", "").toUpperCase();
        return "MB-" + uuid.substring(0, 8);
    }

    /**
     * Builds the QR code payload string containing all booking details.
     */
    private String buildQrData() {
        return "BookingID:" + bookingId
                + "|Movie:" + movieTitle
                + "|Theater:" + theater
                + "|Date:" + showDate
                + "|Time:" + showTime
                + "|Seats:" + String.join(",", selectedSeats)
                + "|Amount:₹" + totalAmount;
    }

    /**
     * Returns the booking date/time formatted for display.
     */
    public String getFormattedBookedAt() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(bookedAt));
    }

    /**
     * Returns comma-separated seat labels, e.g. "A1, A2, B3".
     */
    public String getSeatsDisplay() {
        if (selectedSeats == null || selectedSeats.isEmpty()) return "-";
        return String.join(", ", selectedSeats);
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getBookingId() { return bookingId; }
    public void setBookingId(String bookingId) { this.bookingId = bookingId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getMovieId() { return movieId; }
    public void setMovieId(int movieId) { this.movieId = movieId; }

    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }

    public String getMoviePosterUrl() { return moviePosterUrl; }
    public void setMoviePosterUrl(String moviePosterUrl) { this.moviePosterUrl = moviePosterUrl; }

    public String getTheater() { return theater; }
    public void setTheater(String theater) { this.theater = theater; }

    public String getShowDate() { return showDate; }
    public void setShowDate(String showDate) { this.showDate = showDate; }

    public String getShowTime() { return showTime; }
    public void setShowTime(String showTime) { this.showTime = showTime; }

    public List<String> getSelectedSeats() { return selectedSeats; }
    public void setSelectedSeats(List<String> selectedSeats) {
        this.selectedSeats = selectedSeats;
        this.totalSeats = (selectedSeats != null) ? selectedSeats.size() : 0;
    }

    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }

    public int getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(int pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public int getTotalAmount() { return totalAmount; }
    public void setTotalAmount(int totalAmount) { this.totalAmount = totalAmount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public long getBookedAt() { return bookedAt; }
    public void setBookedAt(long bookedAt) { this.bookedAt = bookedAt; }

    public String getQrCodeData() { return qrCodeData; }
    public void setQrCodeData(String qrCodeData) { this.qrCodeData = qrCodeData; }
}
