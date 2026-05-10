package com.moviebooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;
import com.moviebooking.app.R;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.models.User;
import com.moviebooking.app.utils.SessionManager;

/**
 * User profile screen.
 *
 * Shows user details, allows inline editing of name/phone/city,
 * provides dark-mode toggle, navigation to booking history, and logout.
 */
public class ProfileActivity extends AppCompatActivity {

    private Toolbar         toolbar;
    private TextView        tvInitialAvatar, tvProfileName, tvProfileEmail, tvTotalBookings;
    private TextInputEditText etEditName, etEditPhone, etEditCity;
    private Button          btnSaveProfile, btnLogout;
    private View            btnViewBookings; // Changed from Button to View to support LinearLayout
    private Switch          switchDarkMode;

    private DatabaseHelper  db;
    private SessionManager  session;
    private User            currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        db      = DatabaseHelper.getInstance(this);
        session = SessionManager.getInstance(this);

        initViews();
        setupToolbar();
        loadUserData();
        setListeners();
    }

    private void initViews() {
        toolbar          = findViewById(R.id.toolbar_profile);
        tvInitialAvatar  = findViewById(R.id.tv_avatar_initial);
        tvProfileName    = findViewById(R.id.tv_profile_name);
        tvProfileEmail   = findViewById(R.id.tv_profile_email);
        tvTotalBookings  = findViewById(R.id.tv_total_bookings);
        etEditName       = findViewById(R.id.et_edit_name);
        etEditPhone      = findViewById(R.id.et_edit_phone);
        etEditCity       = findViewById(R.id.et_edit_city);
        btnSaveProfile   = findViewById(R.id.btn_save_profile);
        btnViewBookings  = findViewById(R.id.btn_view_bookings);
        btnLogout        = findViewById(R.id.btn_logout);
        switchDarkMode   = findViewById(R.id.switch_dark_mode);
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Profile");
        }
    }

    private void loadUserData() {
        new Thread(() -> {
            currentUser = db.getUserById(session.getUserId());
            int totalBookings = db.getBookingsByUserId(session.getUserId()).size();

            runOnUiThread(() -> {
                if (currentUser == null) return;

                tvInitialAvatar.setText(currentUser.getInitial());
                tvProfileName.setText(currentUser.getName());
                tvProfileEmail.setText(currentUser.getEmail());
                tvTotalBookings.setText(totalBookings + " Bookings");

                etEditName.setText(currentUser.getName());
                etEditPhone.setText(currentUser.getPhone());
                etEditCity.setText(currentUser.getCity());

                // Dark mode switch state (no listener yet to avoid false trigger)
                switchDarkMode.setChecked(session.isDarkMode());
            });
        }).start();
    }

    private void setListeners() {
        // Save profile
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        // View booking history
        btnViewBookings.setOnClickListener(v ->
            startActivity(new Intent(ProfileActivity.this, BookingHistoryActivity.class)));

        // Dark mode toggle
        switchDarkMode.setOnCheckedChangeListener((btn, isChecked) -> {
            session.setDarkMode(isChecked);
            AppCompatDelegate.setDefaultNightMode(
                isChecked
                    ? AppCompatDelegate.MODE_NIGHT_YES
                    : AppCompatDelegate.MODE_NIGHT_NO
            );
        });

        // Logout with confirmation
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    private void saveProfile() {
        String name  = etEditName.getText() != null  ? etEditName.getText().toString().trim()  : "";
        String phone = etEditPhone.getText() != null ? etEditPhone.getText().toString().trim() : "";
        String city  = etEditCity.getText() != null  ? etEditCity.getText().toString().trim()  : "";

        if (name.length() < 2) {
            etEditName.setError("Name must be at least 2 characters");
            return;
        }

        currentUser.setName(name);
        currentUser.setPhone(phone);
        currentUser.setCity(city);

        new Thread(() -> {
            int rows = db.updateUser(currentUser);
            runOnUiThread(() -> {
                if (rows > 0) {
                    session.updateName(name);
                    tvProfileName.setText(name);
                    tvInitialAvatar.setText(currentUser.getInitial());
                    Toast.makeText(this, "Profile updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Update failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Logout", (dialog, which) -> {
                session.logout();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                finish();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) { onBackPressed(); return true; }
        return super.onOptionsItemSelected(item);
    }
}
