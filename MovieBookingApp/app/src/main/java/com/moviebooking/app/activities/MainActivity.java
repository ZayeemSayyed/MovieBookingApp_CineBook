package com.moviebooking.app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.moviebooking.app.R;
import com.moviebooking.app.database.DatabaseHelper;
import com.moviebooking.app.utils.MovieDataProvider;
import com.moviebooking.app.utils.SessionManager;

/**
 * Splash screen activity.
 *
 * Responsibilities:
 *  1. Apply saved dark-mode preference.
 *  2. Seed the database with sample movies on first launch.
 *  3. After a brief animated delay, route to HomeActivity (logged in)
 *     or LoginActivity (not logged in).
 */
public class MainActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Apply dark-mode preference before setContentView
        SessionManager session = SessionManager.getInstance(this);
        if (session.isDarkMode()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }

        setContentView(R.layout.activity_main);

        // Seed sample movie data on first launch
        DatabaseHelper db = DatabaseHelper.getInstance(this);
        if (db.isMoviesEmpty()) {
            db.insertMovies(MovieDataProvider.getSampleMovies());
        }

        // Animate logo and tagline
        animateSplash();

        // Navigate after delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent;
            if (session.isLoggedIn()) {
                intent = new Intent(MainActivity.this, HomeActivity.class);
            } else {
                intent = new Intent(MainActivity.this, LoginActivity.class);
            }
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        }, SPLASH_DELAY_MS);
    }

    private void animateSplash() {
        ImageView logo    = findViewById(R.id.iv_splash_logo);
        TextView  appName = findViewById(R.id.tv_splash_name);
        TextView  tagline = findViewById(R.id.tv_splash_tagline);

        // Scale + fade logo
        ScaleAnimation scale = new ScaleAnimation(
            0.3f, 1f, 0.3f, 1f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        );
        scale.setDuration(600);

        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(600);

        AnimationSet logoAnim = new AnimationSet(true);
        logoAnim.addAnimation(scale);
        logoAnim.addAnimation(fadeIn);
        logo.startAnimation(logoAnim);

        // Fade in text with delay
        AlphaAnimation textFade = new AlphaAnimation(0f, 1f);
        textFade.setDuration(700);
        textFade.setStartOffset(600);
        textFade.setFillAfter(true);
        appName.startAnimation(textFade);

        AlphaAnimation tagFade = new AlphaAnimation(0f, 1f);
        tagFade.setDuration(700);
        tagFade.setStartOffset(900);
        tagFade.setFillAfter(true);
        tagline.startAnimation(tagFade);
    }
}
