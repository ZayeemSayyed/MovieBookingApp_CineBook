package com.moviebooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.moviebooking.app.R;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.models.User;
import com.moviebooking.app.utils.SessionManager;
import com.moviebooking.app.utils.ValidationUtils;

/**
 * Handles user login with email + password.
 * Validates input, hashes the password, and compares against the stored hash.
 */
public class LoginActivity extends AppCompatActivity {

    private EditText      etEmail, etPassword;
    private TextInputLayout tilEmail, tilPassword;
    private Button        btnLogin;
    private TextView      tvRegister, tvForgotPassword;
    private CheckBox      cbRememberMe;
    private ProgressBar   progressBar;
    private ImageButton   ibTogglePassword;

    private boolean isPasswordVisible = false;

    private DatabaseHelper db;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db      = DatabaseHelper.getInstance(this);
        session = SessionManager.getInstance(this);

        initViews();
        setListeners();
    }

    private void initViews() {
        etEmail          = findViewById(R.id.et_login_email);
        etPassword       = findViewById(R.id.et_login_password);
        tilEmail         = findViewById(R.id.til_login_email);
        tilPassword      = findViewById(R.id.til_login_password);
        btnLogin         = findViewById(R.id.btn_login);
        tvRegister       = findViewById(R.id.tv_go_to_register);
        tvForgotPassword = findViewById(R.id.tv_forgot_password);
        cbRememberMe     = findViewById(R.id.cb_remember_me);
        progressBar      = findViewById(R.id.progress_login);
        ibTogglePassword = findViewById(R.id.ib_toggle_password);
    }

    private void setListeners() {
        // Login button
        btnLogin.setOnClickListener(v -> attemptLogin());

        // Go to Register
        tvRegister.setOnClickListener(v -> {
            startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        });

        // Forgot Password (mock)
        tvForgotPassword.setOnClickListener(v ->
            Toast.makeText(this, "Password reset link sent to your email.", Toast.LENGTH_LONG).show()
        );

        // Toggle password visibility
        ibTogglePassword.setOnClickListener(v -> togglePasswordVisibility());
    }

    private void attemptLogin() {
        // Clear previous errors
        tilEmail.setError(null);
        tilPassword.setError(null);

        String email    = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString();

        // Validation
        boolean valid = true;
        if (!ValidationUtils.isValidEmail(email)) {
            tilEmail.setError("Enter a valid email address");
            valid = false;
        }
        if (!ValidationUtils.isNotEmpty(password)) {
            tilPassword.setError("Password cannot be empty");
            valid = false;
        }
        if (!valid) return;

        // Show loading
        showLoading(true);

        // Run database lookup off the UI thread
        new Thread(() -> {
            User user = db.getUserByEmail(email);
            boolean passwordMatch = (user != null)
                && ValidationUtils.verifyPassword(password, user.getPasswordHash());

            runOnUiThread(() -> {
                showLoading(false);
                if (passwordMatch) {
                    // Create session and navigate home
                    session.createSession(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getPhone()
                    );
                    navigateToHome();
                } else {
                    // Generic error message (security best practice)
                    tilPassword.setError("Invalid email or password");
                    etPassword.requestFocus();
                }
            });
        }).start();
    }

    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        btnLogin.setEnabled(!show);
        btnLogin.setText(show ? "" : getString(R.string.login));
    }

    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;
        etPassword.setTransformationMethod(
            isPasswordVisible
                ? HideReturnsTransformationMethod.getInstance()
                : PasswordTransformationMethod.getInstance()
        );
        ibTogglePassword.setImageResource(
            isPasswordVisible ? R.drawable.ic_eye_off : R.drawable.ic_eye
        );
        // Move cursor to end
        etPassword.setSelection(etPassword.getText().length());
    }
}
