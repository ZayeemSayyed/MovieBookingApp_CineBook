package com.moviebooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.navigation.NavigationBarView;
import com.moviebooking.app.R;
import com.moviebooking.app.adapters.MovieAdapter;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.models.Movie;
import com.moviebooking.app.utils.MovieDataProvider;
import com.moviebooking.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Main home screen showing movie listings, genre filter chips, and search.
 * Hosts the bottom navigation bar.
 */
public class HomeActivity extends AppCompatActivity
        implements MovieAdapter.OnMovieClickListener {

    private RecyclerView      rvMovies;
    private MovieAdapter      movieAdapter;
    private ChipGroup         chipGroupGenres;
    private EditText          etSearch;
    private TextView          tvGreeting, tvNowShowing;
    private BottomNavigationView bottomNav;

    private DatabaseHelper    db;
    private SessionManager    session;

    private List<Movie> allMovies = new ArrayList<>();
    private String      currentGenre = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db      = DatabaseHelper.getInstance(this);
        session = SessionManager.getInstance(this);

        initViews();
        setupGenreChips();
        setupRecyclerView();
        setupBottomNav();
        loadMovies();
        setSearchListener();

        // Personalised greeting
        String name = session.getUserName();
        tvGreeting.setText("Hi, " + (name.isEmpty() ? "Guest" : name.split(" ")[0]) + " 👋");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh movies in case favorites changed
        loadMovies();
    }

    private void initViews() {
        rvMovies       = findViewById(R.id.rv_movies);
        chipGroupGenres = findViewById(R.id.chip_group_genres);
        etSearch       = findViewById(R.id.et_search);
        tvGreeting     = findViewById(R.id.tv_greeting);
        tvNowShowing   = findViewById(R.id.tv_now_showing);
        bottomNav      = findViewById(R.id.bottom_nav);
    }

    private void setupGenreChips() {
        chipGroupGenres.removeAllViews();
        for (String genre : MovieDataProvider.getGenres()) {
            Chip chip = new Chip(this);
            chip.setText(genre);
            chip.setCheckable(true);
            chip.setChecked(genre.equals("All"));
            chip.setChipBackgroundColorResource(R.color.chip_state_list);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_state_list, getTheme()));
            chip.setOnClickListener(v -> {
                currentGenre = genre;
                etSearch.setText(""); // Clear search when genre changes
                filterByGenre(genre);
            });
            chipGroupGenres.addView(chip);
        }
    }

    private void setupRecyclerView() {
        movieAdapter = new MovieAdapter(this, new ArrayList<>(), this);
        // Use GridLayout with 2 columns
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        rvMovies.setLayoutManager(layoutManager);
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setHasFixedSize(true);
    }

    private void setupBottomNav() {
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true; // Already here
            } else if (id == R.id.nav_profile) {
                startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
                return true;
            } else if (id == R.id.nav_bookings) {
                startActivity(new Intent(HomeActivity.this, BookingHistoryActivity.class));
                return true;
            }
            return false;
        });
    }

    private void loadMovies() {
        new Thread(() -> {
            List<Movie> movies = db.getAllMovies();
            // Mark favorites
            List<Integer> favIds = db.getFavoriteMovieIds(session.getUserId());
            for (Movie m : movies) {
                m.setFavorite(favIds.contains(m.getId()));
            }
            runOnUiThread(() -> {
                allMovies = movies;
                movieAdapter.updateMovies(movies);
                tvNowShowing.setText("Now Showing (" + movies.size() + ")");
            });
        }).start();
    }

    private void filterByGenre(String genre) {
        new Thread(() -> {
            List<Movie> filtered;
            if ("All".equals(genre)) {
                filtered = new ArrayList<>(allMovies);
            } else {
                filtered = new ArrayList<>();
                for (Movie m : allMovies) {
                    if (m.getGenre() != null && m.getGenre().contains(genre)) {
                        filtered.add(m);
                    }
                }
            }
            runOnUiThread(() -> movieAdapter.updateMovies(filtered));
        }).start();
    }

    private void setSearchListener() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    filterByGenre(currentGenre);
                } else {
                    // Filter from the full list
                    List<Movie> results = new ArrayList<>();
                    for (Movie m : allMovies) {
                        if (m.getTitle().toLowerCase().contains(query.toLowerCase())
                            || m.getGenre().toLowerCase().contains(query.toLowerCase())
                            || m.getCast().toLowerCase().contains(query.toLowerCase())) {
                            results.add(m);
                        }
                    }
                    movieAdapter.updateMovies(results);
                }
            }
        });
    }

    // ── MovieAdapter.OnMovieClickListener ──────────────────────────────────────

    @Override
    public void onMovieClick(Movie movie) {
        Intent intent = new Intent(HomeActivity.this, MovieDetailsActivity.class);
        intent.putExtra("movie", movie);
        startActivity(intent);
        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
    }

    @Override
    public void onFavoriteClick(Movie movie, int position) {
        new Thread(() -> {
            boolean isFav = db.isFavorite(session.getUserId(), movie.getId());
            if (isFav) {
                db.removeFavorite(session.getUserId(), movie.getId());
                movie.setFavorite(false);
            } else {
                db.addFavorite(session.getUserId(), movie.getId());
                movie.setFavorite(true);
            }
            runOnUiThread(() -> movieAdapter.notifyItemChanged(position));
        }).start();
    }
}
