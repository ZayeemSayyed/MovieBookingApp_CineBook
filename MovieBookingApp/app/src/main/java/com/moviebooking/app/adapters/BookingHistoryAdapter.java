package com.moviebooking.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.moviebooking.app.R;
import com.moviebooking.app.models.Booking;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * RecyclerView adapter for the booking history list.
 * Each card shows: poster thumbnail, movie name, date/time, seats, amount, status.
 * Confirmed bookings show a "Cancel" button.
 */
public class BookingHistoryAdapter
        extends RecyclerView.Adapter<BookingHistoryAdapter.BookingViewHolder> {

    public interface OnBookingActionListener {
        void onCancelBooking(Booking booking, int position);
    }

    private final Context                 context;
    private       List<Booking>           bookings;
    private final OnBookingActionListener listener;

    public BookingHistoryAdapter(Context context, List<Booking> bookings,
                                 OnBookingActionListener listener) {
        this.context  = context;
        this.bookings = bookings;
        this.listener = listener;
    }

    public void updateBookings(List<Booking> newBookings) {
        this.bookings = newBookings;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BookingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_booking_history, parent, false);
        return new BookingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BookingViewHolder holder, int position) {
        holder.bind(bookings.get(position), position);
    }

    @Override
    public int getItemCount() { return bookings == null ? 0 : bookings.size(); }

    // ── ViewHolder ─────────────────────────────────────────────────────────────

    class BookingViewHolder extends RecyclerView.ViewHolder {

        private final CircleImageView ivPoster;
        private final TextView tvBookingId, tvMovieName, tvDateTime, tvTheater;
        private final TextView tvSeats, tvAmount, tvStatus, tvBookedOn;
        private final Button   btnCancel;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster    = itemView.findViewById(R.id.iv_history_poster);
            tvBookingId = itemView.findViewById(R.id.tv_history_booking_id);
            tvMovieName = itemView.findViewById(R.id.tv_history_movie_name);
            tvDateTime  = itemView.findViewById(R.id.tv_history_datetime);
            tvTheater   = itemView.findViewById(R.id.tv_history_theater);
            tvSeats     = itemView.findViewById(R.id.tv_history_seats);
            tvAmount    = itemView.findViewById(R.id.tv_history_amount);
            tvStatus    = itemView.findViewById(R.id.tv_history_status);
            tvBookedOn  = itemView.findViewById(R.id.tv_history_booked_on);
            btnCancel   = itemView.findViewById(R.id.btn_cancel_booking);
        }

        void bind(Booking booking, int position) {
            tvBookingId.setText(booking.getBookingId());
            tvMovieName.setText(booking.getMovieTitle());
            tvDateTime.setText(booking.getShowDate() + "  •  " + booking.getShowTime());
            tvTheater.setText(booking.getTheater());
            tvSeats.setText("Seats: " + booking.getSeatsDisplay());
            tvAmount.setText("₹" + booking.getTotalAmount());
            tvStatus.setText(booking.getStatus());
            tvBookedOn.setText("Booked: " + booking.getFormattedBookedAt());

            // Status colour
            tvStatus.setBackgroundResource(
                Booking.STATUS_CONFIRMED.equals(booking.getStatus())
                    ? R.drawable.bg_status_confirmed
                    : R.drawable.bg_status_cancelled
            );

            // Poster thumbnail
            Glide.with(context)
                .load(booking.getMoviePosterUrl())
                .placeholder(R.drawable.placeholder_poster)
                .circleCrop()
                .into(ivPoster);

            // Cancel button only for confirmed bookings
            boolean canCancel = Booking.STATUS_CONFIRMED.equals(booking.getStatus());
            btnCancel.setVisibility(canCancel ? View.VISIBLE : View.GONE);

            if (canCancel) {
                btnCancel.setOnClickListener(v -> {
                    new AlertDialog.Builder(context)
                        .setTitle("Cancel Booking")
                        .setMessage("Cancel booking " + booking.getBookingId() + "?")
                        .setPositiveButton("Yes, Cancel", (dialog, which) ->
                            listener.onCancelBooking(booking, position))
                        .setNegativeButton("No", null)
                        .show();
                });
            }
        }
    }
}
