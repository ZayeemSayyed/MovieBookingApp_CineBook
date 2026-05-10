package com.moviebooking.app.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moviebooking.app.R;
import com.moviebooking.app.adapters.BookingHistoryAdapter;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.models.Booking;
import com.moviebooking.app.utils.SessionManager;

import java.util.List;

/**
 * Screen displaying the user's past and upcoming movie bookings.
 */
public class BookingHistoryActivity extends AppCompatActivity {

    private Toolbar               toolbar;
    private RecyclerView          rvBookings;
    private View                  emptyStateView;
    private BookingHistoryAdapter adapter;

    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_history);

        db      = DatabaseHelper.getInstance(this);
        session = SessionManager.getInstance(this);

        initViews();
        setupToolbar();
        setupRecyclerView();
        loadBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings(); // Refresh in case a booking was cancelled
    }

    private void initViews() {
        toolbar        = findViewById(R.id.toolbar_history);
        rvBookings     = findViewById(R.id.rv_bookings_history);
        emptyStateView = findViewById(R.id.tv_history_empty); // Matches ID in XML
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("My Bookings");
        }
    }

    private void setupRecyclerView() {
        rvBookings.setLayoutManager(new LinearLayoutManager(this));
        // No adapter yet, will set in loadBookings()
    }

    private void loadBookings() {
        int userId = session.getUserId();
        List<Booking> bookings = db.getBookingsByUserId(userId);

        if (bookings.isEmpty()) {
            rvBookings.setVisibility(View.GONE);
            emptyStateView.setVisibility(View.VISIBLE);
        } else {
            rvBookings.setVisibility(View.VISIBLE);
            emptyStateView.setVisibility(View.GONE);

            adapter = new BookingHistoryAdapter(this, bookings, (booking, position) -> {
                // Potential cancel action
                db.cancelBooking(booking.getBookingId());
                loadBookings();
            });
            rvBookings.setAdapter(adapter);
        }
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
