package com.moviebooking.app.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.chip.Chip;
import com.moviebooking.app.R;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.models.Movie;
import com.moviebooking.app.utils.SessionManager;

/**
 * Shows full details of a movie: poster, synopsis, cast, ratings, etc.
 * Provides a "Book Now" button to proceed to seat selection.
 */
public class MovieDetailsActivity extends AppCompatActivity {

    private ImageView ivBanner, ivPoster;
    private TextView  tvTitle, tvGenre, tvDuration, tvLanguage, tvCertificate;
    private TextView  tvRating, tvVotes, tvDirector, tvCast, tvDescription;
    private TextView  tvReleaseDate, tvPricePerSeat;
    private RatingBar ratingBar;
    private Button    btnBookNow;
    private ImageView ivFavorite;
    private Toolbar   toolbar;

    private Movie movie;
    private DatabaseHelper db;
    private SessionManager session;
    private boolean isFavorite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_details);

        db      = DatabaseHelper.getInstance(this);
        session = SessionManager.getInstance(this);

        // Retrieve movie passed via Intent
        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie == null) {
            Toast.makeText(this, "Movie not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupToolbar();
        bindMovieData();
        checkFavoriteStatus();
        setListeners();
    }

    private void initViews() {
        ivBanner       = findViewById(R.id.iv_movie_banner);
        ivPoster       = findViewById(R.id.iv_movie_poster);
        tvTitle        = findViewById(R.id.tv_movie_title);
        tvGenre        = findViewById(R.id.tv_movie_genre);
        tvDuration     = findViewById(R.id.tv_movie_duration);
        tvLanguage     = findViewById(R.id.tv_movie_language);
        tvCertificate  = findViewById(R.id.tv_movie_certificate);
        tvRating       = findViewById(R.id.tv_movie_rating);
        tvVotes        = findViewById(R.id.tv_movie_votes);
        tvDirector     = findViewById(R.id.tv_movie_director);
        tvCast         = findViewById(R.id.tv_movie_cast);
        tvDescription  = findViewById(R.id.tv_movie_description);
        tvReleaseDate  = findViewById(R.id.tv_movie_release_date);
        tvPricePerSeat = findViewById(R.id.tv_price_per_seat);
        ratingBar      = findViewById(R.id.rating_bar);
        btnBookNow     = findViewById(R.id.btn_book_now);
        ivFavorite     = findViewById(R.id.iv_favorite);
        toolbar        = findViewById(R.id.toolbar_movie_details);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }
    }

    private void bindMovieData() {
        // Banner
        Glide.with(this)
            .load(movie.getBannerUrl())
            .placeholder(R.drawable.placeholder_banner)
            .error(R.drawable.placeholder_banner)
            .centerCrop()
            .into(ivBanner);

        // Poster
        Glide.with(this)
            .load(movie.getPosterUrl())
            .placeholder(R.drawable.placeholder_poster)
            .error(R.drawable.placeholder_poster)
            .centerCrop()
            .into(ivPoster);

        tvTitle.setText(movie.getTitle());
        tvGenre.setText(movie.getGenre());
        tvDuration.setText(movie.getDuration());
        tvLanguage.setText(movie.getLanguage());
        tvCertificate.setText(movie.getCertificate());
        tvRating.setText(movie.getFormattedRating());
        tvVotes.setText(movie.getFormattedVotes());
        tvDirector.setText("Director: " + movie.getDirector());
        tvCast.setText("Cast: " + movie.getCast());
        tvDescription.setText(movie.getDescription());
        tvReleaseDate.setText("Release: " + movie.getReleaseDate());
        tvPricePerSeat.setText("₹" + movie.getPricePerSeat() + " / seat");

        // RatingBar: convert 10-point rating to 5-star scale
        ratingBar.setRating(movie.getRating() / 2f);
    }

    private void checkFavoriteStatus() {
        new Thread(() -> {
            isFavorite = db.isFavorite(session.getUserId(), movie.getId());
            runOnUiThread(() -> updateFavoriteIcon());
        }).start();
    }

    private void updateFavoriteIcon() {
        ivFavorite.setImageResource(
            isFavorite ? R.drawable.ic_favorite_filled : R.drawable.ic_favorite_outline
        );
    }

    private void setListeners() {
        btnBookNow.setOnClickListener(v -> {
            Intent intent = new Intent(MovieDetailsActivity.this, SeatSelectionActivity.class);
            intent.putExtra("movie", movie);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        ivFavorite.setOnClickListener(v -> toggleFavorite());
    }

    private void toggleFavorite() {
        new Thread(() -> {
            if (isFavorite) {
                db.removeFavorite(session.getUserId(), movie.getId());
                isFavorite = false;
            } else {
                db.addFavorite(session.getUserId(), movie.getId());
                isFavorite = true;
            }
            runOnUiThread(() -> {
                updateFavoriteIcon();
                Toast.makeText(this,
                    isFavorite ? "Added to favorites" : "Removed from favorites",
                    Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
