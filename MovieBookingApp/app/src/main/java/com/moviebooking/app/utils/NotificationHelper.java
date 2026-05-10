package com.moviebooking.app.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.moviebooking.app.R;
import com.moviebooking.app.activities.BookingHistoryActivity;
import com.moviebooking.app.models.Booking;

/**
 * Helper for posting local push notifications (booking confirmations, etc.).
 */
public class NotificationHelper {

    private static final String CHANNEL_ID   = "movie_booking_channel";
    private static final String CHANNEL_NAME = "Booking Notifications";
    private static final int    NOTIF_ID     = 1001;

    private final Context context;

    public NotificationHelper(Context context) {
        this.context = context.getApplicationContext();
        createNotificationChannel();
    }

    /**
     * Creates the notification channel (required on Android O+).
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Notifications for movie ticket bookings");
            channel.enableVibration(true);
            NotificationManager manager =
                context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Posts a booking confirmation notification.
     *
     * @param booking The confirmed booking to notify about.
     */
    public void showBookingConfirmationNotification(Booking booking) {
        Intent intent = new Intent(context, BookingHistoryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        String contentText = "🎬 " + booking.getMovieTitle()
            + " | " + booking.getShowDate()
            + " " + booking.getShowTime()
            + " | Seats: " + booking.getSeatsDisplay();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Booking Confirmed! " + booking.getBookingId())
            .setContentText(contentText)
            .setStyle(new NotificationCompat.BigTextStyle().bigText(contentText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVibrate(new long[]{0, 300, 100, 300});

        try {
            NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(context);
            notificationManager.notify(NOTIF_ID, builder.build());
        } catch (SecurityException e) {
            // POST_NOTIFICATIONS permission not granted on Android 13+
            // Silently ignore — booking is still stored in the database
        }
    }
}
