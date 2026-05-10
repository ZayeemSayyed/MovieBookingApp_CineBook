package com.moviebooking.app.models;

import java.io.Serializable;
import java.util.List;

/**
 * Movie model representing a film in the booking system.
 * Implements Serializable to pass between Activities via Intent.
 */
public class Movie implements Serializable {

    private int id;
    private String title;
    private String description;
    private String genre;
    private String duration;       // e.g., "2h 30m"
    private String language;
    private String releaseDate;
    private float rating;
    private int ratingCount;
    private String posterUrl;
    private String bannerUrl;
    private String director;
    private String cast;
    private String certificate;    // U, UA, A, etc.
    private int pricePerSeat;      // in INR
    private boolean isFavorite;
    private List<String> showTimes;
    private List<String> theaters;

    // Default constructor
    public Movie() {}

    // Full constructor
    public Movie(int id, String title, String description, String genre,
                 String duration, String language, String releaseDate,
                 float rating, int ratingCount, String posterUrl,
                 String bannerUrl, String director, String cast,
                 String certificate, int pricePerSeat) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.duration = duration;
        this.language = language;
        this.releaseDate = releaseDate;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.posterUrl = posterUrl;
        this.bannerUrl = bannerUrl;
        this.director = director;
        this.cast = cast;
        this.certificate = certificate;
        this.pricePerSeat = pricePerSeat;
        this.isFavorite = false;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────────

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }

    public String getPosterUrl() { return posterUrl; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public String getBannerUrl() { return bannerUrl; }
    public void setBannerUrl(String bannerUrl) { this.bannerUrl = bannerUrl; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }

    public String getCertificate() { return certificate; }
    public void setCertificate(String certificate) { this.certificate = certificate; }

    public int getPricePerSeat() { return pricePerSeat; }
    public void setPricePerSeat(int pricePerSeat) { this.pricePerSeat = pricePerSeat; }

    public boolean isFavorite() { return isFavorite; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }

    public List<String> getShowTimes() { return showTimes; }
    public void setShowTimes(List<String> showTimes) { this.showTimes = showTimes; }

    public List<String> getTheaters() { return theaters; }
    public void setTheaters(List<String> theaters) { this.theaters = theaters; }

    /**
     * Returns a formatted rating string like "8.5/10"
     */
    public String getFormattedRating() {
        return String.format("%.1f/10", rating);
    }

    /**
     * Returns formatted vote count like "12.5K votes"
     */
    public String getFormattedVotes() {
        if (ratingCount >= 1000) {
            return String.format("%.1fK votes", ratingCount / 1000.0);
        }
        return ratingCount + " votes";
    }
}
