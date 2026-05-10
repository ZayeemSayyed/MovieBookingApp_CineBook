package com.moviebooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;
import com.moviebooking.app.R;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.models.Booking;
import com.moviebooking.app.models.Movie;
import com.moviebooking.app.utils.NotificationHelper;
import com.moviebooking.app.utils.SessionManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Mock payment screen.
 *
 * Displays booking summary and simulates a payment flow with UPI / Card / Wallet options.
 * On "Pay Now" the app shows a loading spinner for 2 s, then saves the booking to SQLite
 * and navigates to BookingConfirmationActivity.
 */
public class PaymentActivity extends AppCompatActivity {

    private Toolbar     toolbar;
    private ImageView   ivMoviePoster;
    private TextView    tvMovieTitle, tvTheater, tvDatetime, tvSeats;
    private TextView    tvSubtotal, tvConvenienceFee, tvGrandTotal;
    private RadioGroup  rgPaymentMethod;
    private Button      btnPay;
    private ProgressBar progressPay;
    private CardView    cvOrderSummary;

    // Data from previous screen
    private Movie        movie;
    private String       theater, showDate, showTime;
    private List<String> selectedSeats;
    private int          totalAmount;

    private DatabaseHelper    db;
    private SessionManager    session;
    private NotificationHelper notifHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // ── Retrieve extras ──────────────────────────────────────────────────
        movie         = (Movie) getIntent().getSerializableExtra("movie");
        theater       = getIntent().getStringExtra("theater");
        showDate      = getIntent().getStringExtra("date");
        showTime      = getIntent().getStringExtra("time");
        selectedSeats = getIntent().getStringArrayListExtra("seats");
        totalAmount   = getIntent().getIntExtra("totalAmount", 0);

        if (movie == null || selectedSeats == null) { finish(); return; }

        db          = DatabaseHelper.getInstance(this);
        session     = SessionManager.getInstance(this);
        notifHelper = new NotificationHelper(this);

        initViews();
        setupToolbar();
        bindSummary();
        setListeners();
    }

    private void initViews() {
        toolbar         = findViewById(R.id.toolbar_payment);
        ivMoviePoster   = findViewById(R.id.iv_payment_poster);
        tvMovieTitle    = findViewById(R.id.tv_payment_movie_title);
        tvTheater       = findViewById(R.id.tv_payment_theater);
        tvDatetime      = findViewById(R.id.tv_payment_datetime);
        tvSeats         = findViewById(R.id.tv_payment_seats);
        tvSubtotal      = findViewById(R.id.tv_subtotal);
        tvConvenienceFee = findViewById(R.id.tv_convenience_fee);
        tvGrandTotal    = findViewById(R.id.tv_grand_total);
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        btnPay          = findViewById(R.id.btn_pay_now);
        progressPay     = findViewById(R.id.progress_pay);
        cvOrderSummary  = findViewById(R.id.cv_order_summary);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Payment");
        }
    }

    private void bindSummary() {
        Glide.with(this)
            .load(movie.getPosterUrl())
            .placeholder(R.drawable.placeholder_poster)
            .centerCrop()
            .into(ivMoviePoster);

        tvMovieTitle.setText(movie.getTitle());
        tvTheater.setText(theater);
        tvDatetime.setText(showDate + "  •  " + showTime);
        tvSeats.setText(String.join(", ", selectedSeats));

        int convenienceFee = (int) (totalAmount * 0.05); // 5% convenience fee
        int grandTotal     = totalAmount + convenienceFee;

        tvSubtotal.setText("₹" + totalAmount);
        tvConvenienceFee.setText("₹" + convenienceFee);
        tvGrandTotal.setText("₹" + grandTotal);

        // Update totalAmount to include fee
        totalAmount = grandTotal;
    }

    private void setListeners() {
        btnPay.setOnClickListener(v -> processPayment());
    }

    /**
     * Simulates a 2-second payment processing delay, then saves the booking.
     */
    private void processPayment() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        String paymentMethod;
        if      (selectedId == R.id.rb_upi)    paymentMethod = "UPI";
        else if (selectedId == R.id.rb_card)   paymentMethod = "Credit/Debit Card";
        else if (selectedId == R.id.rb_wallet) paymentMethod = "Wallet";
        else {
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoading(true);

        final String finalPaymentMethod = paymentMethod;

        // Simulate network/payment delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Save booking on background thread
            new Thread(() -> {
                Booking booking = new Booking(
                    session.getUserId(),
                    movie,
                    theater,
                    showDate,
                    showTime,
                    selectedSeats,
                    finalPaymentMethod
                );
                // Override total to include convenience fee
                booking.setTotalAmount(totalAmount);

                long rowId = db.insertBooking(booking);

                runOnUiThread(() -> {
                    showLoading(false);
                    if (rowId != -1) {
                        // Send local notification
                        notifHelper.showBookingConfirmationNotification(booking);

                        // Navigate to confirmation screen
                        Intent intent = new Intent(PaymentActivity.this,
                            BookingConfirmationActivity.class);
                        intent.putExtra("booking", booking);
                        // Clear back stack up to Home
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in,
                            android.R.anim.fade_out);
                        finish();
                    } else {
                        Toast.makeText(this,
                            "Booking failed. Please try again.", Toast.LENGTH_LONG).show();
                    }
                });
            }).start();
        }, 2200); // 2.2 s simulated delay
    }

    private void showLoading(boolean show) {
        progressPay.setVisibility(show ? View.VISIBLE : View.GONE);
        btnPay.setEnabled(!show);
        btnPay.setText(show ? "Processing..." : "Pay Now");
        cvOrderSummary.setAlpha(show ? 0.5f : 1f);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
