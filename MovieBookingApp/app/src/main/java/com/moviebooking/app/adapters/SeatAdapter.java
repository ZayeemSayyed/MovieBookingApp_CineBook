package com.moviebooking.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviebooking.app.R;
import com.moviebooking.app.models.Seat;

import java.util.List;

/**
 * RecyclerView adapter for the seat grid.
 * Each seat is rendered as a small square with a background tint based on its state:
 *   AVAILABLE   → grey/white
 *   SELECTED    → accent colour
 *   BOOKED      → dark / cross-hatched
 *   UNAVAILABLE → transparent (spacer)
 */
public class SeatAdapter extends RecyclerView.Adapter<SeatAdapter.SeatViewHolder> {

    public interface OnSeatClickListener {
        void onSeatClick(Seat seat, int position);
    }

    private final Context            context;
    private       List<Seat>         seats;
    private final int                cols;
    private final OnSeatClickListener listener;

    public SeatAdapter(Context context, List<Seat> seats, int cols, OnSeatClickListener listener) {
        this.context  = context;
        this.seats    = seats;
        this.cols     = cols;
        this.listener = listener;
    }

    public void updateSeats(List<Seat> newSeats) {
        this.seats = newSeats;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SeatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_seat, parent, false);
        return new SeatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SeatViewHolder holder, int position) {
        Seat seat = seats.get(position);
        holder.bind(seat, position);
    }

    @Override
    public int getItemCount() {
        return seats == null ? 0 : seats.size();
    }

    // ── ViewHolder ─────────────────────────────────────────────────────────────

    class SeatViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvSeatLabel;
        private final View     seatView;

        SeatViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSeatLabel = itemView.findViewById(R.id.tv_seat_label);
            seatView    = itemView.findViewById(R.id.view_seat);
        }

        void bind(Seat seat, int position) {
            if (seat.isUnavailable()) {
                // Invisible spacer (aisle)
                seatView.setVisibility(View.INVISIBLE);
                tvSeatLabel.setVisibility(View.INVISIBLE);
                itemView.setClickable(false);
                return;
            }

            seatView.setVisibility(View.VISIBLE);
            tvSeatLabel.setVisibility(View.VISIBLE);

            // Show short label (only column number)
            String col = seat.getSeatId().replaceAll("[^0-9]", "");
            tvSeatLabel.setText(col);

            // Background resource based on state + category
            switch (seat.getState()) {
                case Seat.STATE_BOOKED:
                    seatView.setBackgroundResource(R.drawable.bg_seat_booked);
                    tvSeatLabel.setTextColor(context.getColor(R.color.seat_text_booked));
                    itemView.setClickable(false);
                    break;
                case Seat.STATE_SELECTED:
                    seatView.setBackgroundResource(R.drawable.bg_seat_selected);
                    tvSeatLabel.setTextColor(context.getColor(R.color.white));
                    itemView.setClickable(true);
                    break;
                default: // AVAILABLE
                    switch (seat.getCategory()) {
                        case Seat.CATEGORY_PREMIUM:
                            seatView.setBackgroundResource(R.drawable.bg_seat_premium);
                            break;
                        case Seat.CATEGORY_RECLINER:
                            seatView.setBackgroundResource(R.drawable.bg_seat_recliner);
                            break;
                        default:
                            seatView.setBackgroundResource(R.drawable.bg_seat_available);
                    }
                    tvSeatLabel.setTextColor(context.getColor(R.color.seat_text_available));
                    itemView.setClickable(true);
                    break;
            }

            itemView.setOnClickListener(v -> listener.onSeatClick(seat, position));
        }
    }
}
