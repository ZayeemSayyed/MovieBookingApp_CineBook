package com.moviebooking.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moviebooking.app.R;

import java.util.List;

/**
 * Horizontal RecyclerView adapter for show-time chips on the seat selection screen.
 */
public class ShowTimeAdapter extends RecyclerView.Adapter<ShowTimeAdapter.TimeViewHolder> {

    public interface OnTimeSelectedListener {
        void onTimeSelected(String time);
    }

    private final List<String>         times;
    private final OnTimeSelectedListener listener;
    private int                        selectedPosition = 3; // Default: 07:30 PM slot

    public ShowTimeAdapter(List<String> times, OnTimeSelectedListener listener) {
        this.times    = times;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TimeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_show_time, parent, false);
        return new TimeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        String time     = times.get(position);
        boolean selected = (position == selectedPosition);

        holder.tvTime.setText(time);
        holder.itemView.setBackgroundResource(
            selected ? R.drawable.bg_time_selected : R.drawable.bg_time_normal
        );
        holder.tvTime.setTextColor(holder.itemView.getContext().getColor(
            selected ? R.color.white : R.color.text_primary
        ));

        holder.itemView.setOnClickListener(v -> {
            int prev = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(prev);
            notifyItemChanged(selectedPosition);
            listener.onTimeSelected(time);
        });
    }

    @Override
    public int getItemCount() { return times == null ? 0 : times.size(); }

    // ── ViewHolder ─────────────────────────────────────────────────────────────

    static class TimeViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTime;
        TimeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_show_time);
        }
    }
}
