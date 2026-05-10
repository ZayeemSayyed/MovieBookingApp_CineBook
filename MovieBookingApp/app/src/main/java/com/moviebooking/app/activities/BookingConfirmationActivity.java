package com.moviebooking.app.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.moviebooking.app.R;
import com.moviebooking.app.models.Booking;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Displays the confirmed booking details and a QR code ticket.
 * Options: share ticket as text or download QR image.
 */
public class BookingConfirmationActivity extends AppCompatActivity {

    private TextView tvBookingId, tvMovieName, tvTheater, tvDateTime;
    private TextView tvSeats, tvAmount, tvStatus, tvPaymentMethod;
    private ImageView ivQrCode, ivConfirmIcon;
    private Button btnShareTicket, btnDownloadQr, btnGoHome, btnViewBookings;

    private Booking booking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_confirmation);

        booking = (Booking) getIntent().getSerializableExtra("booking");
        if (booking == null) {
            Toast.makeText(this, "Booking data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        bindBookingData();
        generateQrCode();
        setListeners();

        // Entrance animation for the check icon
        ivConfirmIcon.animate()
            .scaleX(1.2f).scaleY(1.2f)
            .setDuration(300)
            .withEndAction(() ->
                ivConfirmIcon.animate().scaleX(1f).scaleY(1f).setDuration(200).start()
            ).start();
    }

    private void initViews() {
        tvBookingId     = findViewById(R.id.tv_conf_booking_id);
        tvMovieName     = findViewById(R.id.tv_conf_movie_name);
        tvTheater       = findViewById(R.id.tv_conf_theater);
        tvDateTime      = findViewById(R.id.tv_conf_datetime);
        tvSeats         = findViewById(R.id.tv_conf_seats);
        tvAmount        = findViewById(R.id.tv_conf_amount);
        tvStatus        = findViewById(R.id.tv_conf_status);
        tvPaymentMethod = findViewById(R.id.tv_conf_payment_method);
        ivQrCode        = findViewById(R.id.iv_qr_code);
        ivConfirmIcon   = findViewById(R.id.iv_confirm_icon);
        btnShareTicket  = findViewById(R.id.btn_share_ticket);
        btnDownloadQr   = findViewById(R.id.btn_download_qr);
        btnGoHome       = findViewById(R.id.btn_go_home);
        btnViewBookings = findViewById(R.id.btn_view_bookings);
    }

    private void bindBookingData() {
        tvBookingId.setText(booking.getBookingId());
        tvMovieName.setText(booking.getMovieTitle());
        tvTheater.setText(booking.getTheater());
        tvDateTime.setText(booking.getShowDate() + "  •  " + booking.getShowTime());
        tvSeats.setText(booking.getSeatsDisplay());
        tvAmount.setText("₹" + booking.getTotalAmount());
        tvStatus.setText(booking.getStatus());
        tvPaymentMethod.setText(booking.getPaymentMethod());

        // Status chip colour
        tvStatus.setBackgroundResource(
            Booking.STATUS_CONFIRMED.equals(booking.getStatus())
                ? R.drawable.bg_status_confirmed
                : R.drawable.bg_status_cancelled
        );
    }

    /**
     * Generates a 400×400 QR code bitmap from the booking's QR data string.
     */
    private void generateQrCode() {
        try {
            BarcodeEncoder encoder = new BarcodeEncoder();
            Bitmap bitmap = encoder.encodeBitmap(
                booking.getQrCodeData(),
                BarcodeFormat.QR_CODE, 400, 400
            );
            ivQrCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            ivQrCode.setVisibility(View.GONE);
        }
    }

    private void setListeners() {
        // Share plain-text ticket details
        btnShareTicket.setOnClickListener(v -> shareTicket());

        // Download QR code to gallery
        btnDownloadQr.setOnClickListener(v -> downloadQrCode());

        // Go to home screen (clear back stack)
        btnGoHome.setOnClickListener(v -> {
            Intent intent = new Intent(BookingConfirmationActivity.this, HomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // Go to booking history
        btnViewBookings.setOnClickListener(v -> {
            startActivity(new Intent(BookingConfirmationActivity.this,
                BookingHistoryActivity.class));
        });
    }

    private void shareTicket() {
        String shareText =
            "🎬 Movie Ticket - " + booking.getBookingId() + "\n\n"
            + "Movie   : " + booking.getMovieTitle() + "\n"
            + "Theater : " + booking.getTheater() + "\n"
            + "Date    : " + booking.getShowDate() + "\n"
            + "Time    : " + booking.getShowTime() + "\n"
            + "Seats   : " + booking.getSeatsDisplay() + "\n"
            + "Amount  : ₹" + booking.getTotalAmount() + "\n"
            + "Status  : " + booking.getStatus() + "\n\n"
            + "Booked via CineBook App";

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Movie Ticket - " + booking.getMovieTitle());
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareText);
        startActivity(Intent.createChooser(shareIntent, "Share Ticket via"));
    }

    /**
     * Saves the QR code bitmap to the device gallery using MediaStore.
     */
    private void downloadQrCode() {
        ivQrCode.setDrawingCacheEnabled(true);
        Bitmap bitmap = ivQrCode.getDrawingCache();
        if (bitmap == null) {
            Toast.makeText(this, "QR code not ready yet", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "CineBook_" + booking.getBookingId() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/CineBook");

        Uri uri = getContentResolver().insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        if (uri != null) {
            try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                Toast.makeText(this, "QR code saved to gallery", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Failed to save QR code", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Cannot save to gallery", Toast.LENGTH_SHORT).show();
        }
    }
}
