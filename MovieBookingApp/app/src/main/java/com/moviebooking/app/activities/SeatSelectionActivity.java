package com.moviebooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.moviebooking.app.R;
import com.moviebooking.app.adapters.SeatAdapter;
import com.moviebooking.app.adapters.ShowTimeAdapter;
import com.moviebooking.app.models.Movie;
import com.moviebooking.app.models.Seat;
import com.moviebooking.app.utils.MovieDataProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * Seat selection screen.
 *
 * Displays:
 *  - Date picker chips (next 7 days)
 *  - Theater selector chips
 *  - Show-time chips
 *  - A grid of seats (Standard / Premium / Recliner) with AVAILABLE / BOOKED / SELECTED states
 *  - Dynamic price summary at the bottom
 */
public class SeatSelectionActivity extends AppCompatActivity
        implements SeatAdapter.OnSeatClickListener {

    // Grid config
    private static final int COLS             = 10;
    private static final int ROWS_STANDARD    = 6;
    private static final int ROWS_PREMIUM     = 3;
    private static final int ROWS_RECLINER    = 2;
    private static final int MAX_SEATS        = 8;

    private Toolbar        toolbar;
    private RecyclerView   rvSeats;
    private RecyclerView   rvShowTimes;
    private ChipGroup      chipGroupDates;
    private ChipGroup      chipGroupTheaters;
    private TextView       tvMovieTitle, tvSelectedSeats, tvTotalPrice;
    private Button         btnProceedPayment;

    private SeatAdapter    seatAdapter;
    private ShowTimeAdapter showTimeAdapter;

    private Movie          movie;
    private List<Seat>     seats    = new ArrayList<>();
    private List<String>   selectedSeatIds = new ArrayList<>();

    private String         selectedDate;
    private String         selectedTheater;
    private String         selectedTime;

    private final Random   random = new Random(42); // Fixed seed for reproducible "booked" pattern

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seat_selection);

        movie = (Movie) getIntent().getSerializableExtra("movie");
        if (movie == null) { finish(); return; }

        // Default selections
        selectedDate    = getTodayDate();
        selectedTheater = MovieDataProvider.THEATERS.get(0);
        selectedTime    = MovieDataProvider.SHOW_TIMES.get(3);

        initViews();
        setupToolbar();
        setupDateChips();
        setupTheaterChips();
        setupShowTimes();
        buildSeats();
        setupSeatRecyclerView();
        updatePriceSummary();
        setListeners();
    }

    // ── Init ───────────────────────────────────────────────────────────────────

    private void initViews() {
        toolbar           = findViewById(R.id.toolbar_seats);
        rvSeats           = findViewById(R.id.rv_seats);
        rvShowTimes       = findViewById(R.id.rv_show_times);
        chipGroupDates    = findViewById(R.id.chip_group_dates);
        chipGroupTheaters = findViewById(R.id.chip_group_theaters);
        tvMovieTitle      = findViewById(R.id.tv_seat_movie_title);
        tvSelectedSeats   = findViewById(R.id.tv_selected_seats);
        tvTotalPrice      = findViewById(R.id.tv_total_price);
        btnProceedPayment = findViewById(R.id.btn_proceed_payment);

        tvMovieTitle.setText(movie.getTitle());
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Select Seats");
        }
    }

    // ── Date chips ─────────────────────────────────────────────────────────────

    private void setupDateChips() {
        chipGroupDates.removeAllViews();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dayFmt  = new SimpleDateFormat("EEE", Locale.getDefault());
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd MMM", Locale.getDefault());
        SimpleDateFormat fullFmt = new SimpleDateFormat("dd MMM yyyy", Locale.getDefault());

        for (int i = 0; i < 7; i++) {
            String label    = dayFmt.format(cal.getTime()) + "\n" + dateFmt.format(cal.getTime());
            String fullDate = fullFmt.format(cal.getTime());

            Chip chip = new Chip(this);
            chip.setText(label);
            chip.setCheckable(true);
            chip.setChecked(i == 0);
            chip.setChipBackgroundColorResource(R.color.chip_state_list);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_state_list, getTheme()));
            chip.setTag(fullDate);
            chip.setOnClickListener(v -> {
                selectedDate = (String) v.getTag();
                // Rebuild seats for new date (different random booked pattern)
                random.setSeed(fullDate.hashCode());
                buildSeats();
                seatAdapter.updateSeats(seats);
                clearSelections();
            });
            chipGroupDates.addView(chip);
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
    }

    // ── Theater chips ──────────────────────────────────────────────────────────

    private void setupTheaterChips() {
        chipGroupTheaters.removeAllViews();
        List<String> theaters = MovieDataProvider.THEATERS;
        for (int i = 0; i < theaters.size(); i++) {
            String theater = theaters.get(i);
            Chip chip = new Chip(this);
            chip.setText(theater);
            chip.setCheckable(true);
            chip.setChecked(i == 0);
            chip.setChipBackgroundColorResource(R.color.chip_state_list);
            chip.setTextColor(getResources().getColorStateList(R.color.chip_text_state_list, getTheme()));
            chip.setOnClickListener(v -> {
                selectedTheater = theater;
                random.setSeed(theater.hashCode());
                buildSeats();
                seatAdapter.updateSeats(seats);
                clearSelections();
            });
            chipGroupTheaters.addView(chip);
        }
    }

    // ── Show times ─────────────────────────────────────────────────────────────

    private void setupShowTimes() {
        showTimeAdapter = new ShowTimeAdapter(MovieDataProvider.SHOW_TIMES, time -> {
            selectedTime = time;
            random.setSeed(time.hashCode());
            buildSeats();
            seatAdapter.updateSeats(seats);
            clearSelections();
        });
        rvShowTimes.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvShowTimes.setAdapter(showTimeAdapter);
    }

    // ── Seat grid builder ──────────────────────────────────────────────────────

    /**
     * Builds the seat list with random pre-booked seats.
     * Rows A-F → Standard | G-I → Premium | J-K → Recliner
     */
    private void buildSeats() {
        seats.clear();
        char rowLabel = 'A';

        // ── Standard rows ──
        for (int r = 0; r < ROWS_STANDARD; r++) {
            for (int c = 0; c < COLS; c++) {
                String id = rowLabel + String.valueOf(c + 1);
                int state  = random.nextInt(5) == 0 ? Seat.STATE_BOOKED : Seat.STATE_AVAILABLE;
                seats.add(new Seat(id, r, c, state, Seat.CATEGORY_STANDARD, movie.getPricePerSeat()));
            }
            rowLabel++;
        }

        // ── Premium rows ──
        for (int r = 0; r < ROWS_PREMIUM; r++) {
            for (int c = 0; c < COLS; c++) {
                String id  = rowLabel + String.valueOf(c + 1);
                int state  = random.nextInt(4) == 0 ? Seat.STATE_BOOKED : Seat.STATE_AVAILABLE;
                int price  = (int) (movie.getPricePerSeat() * 1.5);
                seats.add(new Seat(id, ROWS_STANDARD + r, c, state, Seat.CATEGORY_PREMIUM, price));
            }
            rowLabel++;
        }

        // ── Recliner rows ──
        for (int r = 0; r < ROWS_RECLINER; r++) {
            for (int c = 0; c < COLS; c++) {
                String id  = rowLabel + String.valueOf(c + 1);
                int state  = random.nextInt(6) == 0 ? Seat.STATE_BOOKED : Seat.STATE_AVAILABLE;
                int price  = movie.getPricePerSeat() * 2;
                seats.add(new Seat(id, ROWS_STANDARD + ROWS_PREMIUM + r, c, state,
                    Seat.CATEGORY_RECLINER, price));
            }
            rowLabel++;
        }
    }

    private void setupSeatRecyclerView() {
        seatAdapter = new SeatAdapter(this, seats, COLS, this);
        GridLayoutManager glm = new GridLayoutManager(this, COLS);
        rvSeats.setLayoutManager(glm);
        rvSeats.setAdapter(seatAdapter);
    }

    // ── Seat click ─────────────────────────────────────────────────────────────

    @Override
    public void onSeatClick(Seat seat, int position) {
        if (seat.isBooked() || seat.isUnavailable()) {
            Toast.makeText(this, "This seat is not available", Toast.LENGTH_SHORT).show();
            return;
        }

        if (seat.isAvailable() && selectedSeatIds.size() >= MAX_SEATS) {
            Toast.makeText(this, "Max " + MAX_SEATS + " seats per booking", Toast.LENGTH_SHORT).show();
            return;
        }

        seat.toggleSelection();

        if (seat.isSelected()) {
            selectedSeatIds.add(seat.getSeatId());
        } else {
            selectedSeatIds.remove(seat.getSeatId());
        }

        seatAdapter.notifyItemChanged(position);
        updatePriceSummary();
    }

    // ── Price summary ──────────────────────────────────────────────────────────

    private void updatePriceSummary() {
        int total = 0;
        List<String> labels = new ArrayList<>();
        for (Seat s : seats) {
            if (s.isSelected()) {
                total += s.getPrice();
                labels.add(s.getSeatId());
            }
        }

        if (labels.isEmpty()) {
            tvSelectedSeats.setText("No seats selected");
            tvTotalPrice.setText("₹0");
        } else {
            tvSelectedSeats.setText(String.join(", ", labels));
            tvTotalPrice.setText("₹" + total);
        }

        btnProceedPayment.setEnabled(!labels.isEmpty());
    }

    private void clearSelections() {
        selectedSeatIds.clear();
        for (Seat s : seats) {
            if (s.isSelected()) s.setState(Seat.STATE_AVAILABLE);
        }
        updatePriceSummary();
    }

    // ── Navigation ─────────────────────────────────────────────────────────────

    private void setListeners() {
        btnProceedPayment.setOnClickListener(v -> {
            if (selectedSeatIds.isEmpty()) {
                Toast.makeText(this, "Please select at least one seat", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate total price from selected seats
            int total = 0;
            for (Seat s : seats) {
                if (s.isSelected()) total += s.getPrice();
            }

            Intent intent = new Intent(SeatSelectionActivity.this, PaymentActivity.class);
            intent.putExtra("movie",           movie);
            intent.putExtra("theater",         selectedTheater);
            intent.putExtra("date",            selectedDate);
            intent.putExtra("time",            selectedTime);
            intent.putStringArrayListExtra("seats", new ArrayList<>(selectedSeatIds));
            intent.putExtra("totalAmount",     total);
            startActivity(intent);
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private String getTodayDate() {
        return new SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            .format(Calendar.getInstance().getTime());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
