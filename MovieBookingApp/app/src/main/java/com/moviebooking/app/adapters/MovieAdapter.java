package com.moviebooking.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.moviebooking.app.R;
import com.moviebooking.app.models.Movie;

import java.util.List;

/**
 * RecyclerView adapter for the movie grid on the Home screen.
 * Displays poster, title, genre, rating, and a heart/favorite toggle.
 */
public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    public interface OnMovieClickListener {
        void onMovieClick(Movie movie);
        void onFavoriteClick(Movie movie, int position);
    }

    private final Context              context;
    private       List<Movie>          movies;
    private final OnMovieClickListener listener;

    public MovieAdapter(Context context, List<Movie> movies, OnMovieClickListener listener) {
        this.context  = context;
        this.movies   = movies;
        this.listener = listener;
    }

    public void updateMovies(List<Movie> newMovies) {
        this.movies = newMovies;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_movie_card, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie, position);
    }

    @Override
    public int getItemCount() {
        return movies == null ? 0 : movies.size();
    }

    // ── ViewHolder ─────────────────────────────────────────────────────────────

    class MovieViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivPoster, ivFavorite;
        private final TextView  tvTitle, tvGenre, tvRating, tvCertificate, tvDuration;

        MovieViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPoster      = itemView.findViewById(R.id.iv_movie_poster_card);
            ivFavorite    = itemView.findViewById(R.id.iv_favorite_card);
            tvTitle       = itemView.findViewById(R.id.tv_movie_title_card);
            tvGenre       = itemView.findViewById(R.id.tv_movie_genre_card);
            tvRating      = itemView.findViewById(R.id.tv_movie_rating_card);
            tvCertificate = itemView.findViewById(R.id.tv_movie_certificate_card);
            tvDuration    = itemView.findViewById(R.id.tv_movie_duration_card);
        }

        void bind(Movie movie, int position) {
            tvTitle.setText(movie.getTitle());
            tvGenre.setText(movie.getGenre());
            tvRating.setText("⭐ " + movie.getFormattedRating());
            tvCertificate.setText(movie.getCertificate());
            tvDuration.setText(movie.getDuration());

            // Favorite icon
            ivFavorite.setImageResource(
                movie.isFavorite()
                    ? R.drawable.ic_favorite_filled
                    : R.drawable.ic_favorite_outline
            );

            // Poster image
            Glide.with(context)
                .load(movie.getPosterUrl())
                .placeholder(R.drawable.placeholder_poster)
                .error(R.drawable.placeholder_poster)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .centerCrop()
                .into(ivPoster);

            // Click listeners
            itemView.setOnClickListener(v -> listener.onMovieClick(movie));
            ivFavorite.setOnClickListener(v -> listener.onFavoriteClick(movie, position));
        }
    }
}
