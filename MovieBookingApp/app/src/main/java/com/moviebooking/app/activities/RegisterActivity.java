package com.moviebooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.moviebooking.app.R;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.models.User;
import com.moviebooking.app.utils.SessionManager;
import com.moviebooking.app.utils.ValidationUtils;

/**
 * New user registration screen.
 * Collects name, email, phone, and password; validates input; saves to SQLite.
 */
public class RegisterActivity extends AppCompatActivity {

    private EditText        etName, etEmail, etPhone, etPassword, etConfirmPassword;
    private TextInputLayout tilName, tilEmail, tilPhone, tilPassword, tilConfirmPassword;
    private Button          btnRegister;
    private TextView        tvGoToLogin;
    private ProgressBar     progressBar;

    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        db      = DatabaseHelper.getInstance(this);
        session = SessionManager.getInstance(this);

        initViews();
        setListeners();
    }

    private void initViews() {
        etName             = findViewById(R.id.et_reg_name);
        etEmail            = findViewById(R.id.et_reg_email);
        etPhone            = findViewById(R.id.et_reg_phone);
        etPassword         = findViewById(R.id.et_reg_password);
        etConfirmPassword  = findViewById(R.id.et_reg_confirm_password);
        tilName            = findViewById(R.id.til_reg_name);
        tilEmail           = findViewById(R.id.til_reg_email);
        tilPhone           = findViewById(R.id.til_reg_phone);
        tilPassword        = findViewById(R.id.til_reg_password);
        tilConfirmPassword = findViewById(R.id.til_reg_confirm_password);
        btnRegister        = findViewById(R.id.btn_register);
        tvGoToLogin        = findViewById(R.id.tv_go_to_login);
        progressBar        = findViewById(R.id.progress_register);
    }

    private void setListeners() {
        btnRegister.setOnClickListener(v -> attemptRegistration());
        tvGoToLogin.setOnClickListener(v -> {
            finish();
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });
    }

    private void attemptRegistration() {
        // Clear errors
        tilName.setError(null);
        tilEmail.setError(null);
        tilPhone.setError(null);
        tilPassword.setError(null);
        tilConfirmPassword.setError(null);

        String name            = etName.getText().toString().trim();
        String email           = etEmail.getText().toString().trim();
        String phone           = etPhone.getText().toString().trim();
        String password        = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        // Validate
        boolean valid = true;

        if (!ValidationUtils.isValidName(name)) {
            tilName.setError("Name must be at least 2 characters");
            valid = false;
        }
        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError("Enter a valid email address");
            valid = false;
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            tilPhone.setError("Enter a valid 10-digit phone number");
            valid = false;
        }
        if (!ValidationUtils.isValidPassword(password)) {
            tilPassword.setError(ValidationUtils.getPasswordRequirements());
            valid = false;
        }
        if (!password.equals(confirmPassword)) {
            tilConfirmPassword.setError("Passwords do not match");
            valid = false;
        }
        if (!valid) return;

        showLoading(true);

        // Database operations on background thread
        new Thread(() -> {
            // Check for duplicate email
            if (db.isEmailRegistered(email)) {
                runOnUiThread(() -> {
                    showLoading(false);
                    tilEmail.setError("An account with this email already exists");
                });
                return;
            }

            // Create user object
            User user = new User(name, email, phone, ValidationUtils.hashPassword(password));
            long userId = db.insertUser(user);

            runOnUiThread(() -> {
                showLoading(false);
                if (userId != -1) {
                    // Auto-login after successful registration
                    user.setId((int) userId);
                    session.createSession((int) userId, name, email, phone);

                    Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    tilEmail.setError("Registration failed. Please try again.");
                }
            });
        }).start();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnRegister.setEnabled(!show);
    }
}
