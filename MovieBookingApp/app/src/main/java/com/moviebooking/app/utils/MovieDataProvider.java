package com.moviebooking.app.utils;

import com.moviebooking.app.models.Movie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Provides sample movie data for first-launch seeding.
 * In a production app this would be replaced by a live API (e.g., TMDb).
 *
 * Poster URLs use picsum.photos placeholders (no auth required).
 */
public class MovieDataProvider {

    // Theaters list shown during seat selection
    public static final List<String> THEATERS = Arrays.asList(
        "PVR Cinemas - Phoenix Mall",
        "INOX - Seasons Mall",
        "Cinepolis - Amanora",
        "PVR - Westend Mall",
        "Carnival Cinemas - Viman Nagar"
    );

    // Show timings available per day
    public static final List<String> SHOW_TIMES = Arrays.asList(
        "10:00 AM", "01:00 PM", "04:00 PM", "07:30 PM", "10:30 PM"
    );

    /**
     * Returns a list of 12 sample movies to seed the database on first launch.
     */
    public static List<Movie> getSampleMovies() {
        List<Movie> movies = new ArrayList<>();

        movies.add(new Movie(
            1,
            "Galactic Odyssey",
            "An epic space adventure where humanity's last fleet battles an alien armada across the outer reaches of the Milky Way. Stunning visuals meet heart-pounding action in this sci-fi spectacle of the decade.",
            "Sci-Fi, Action",
            "2h 45m",
            "English",
            "15 Nov 2024",
            9.1f, 245000,
            "https://picsum.photos/seed/galaxy/400/600",
            "https://picsum.photos/seed/galaxy/800/400",
            "James Cameron Jr.",
            "Chris Evans, Zoe Saldana, Ryan Reynolds",
            "UA",
            250
        ));

        movies.add(new Movie(
            2,
            "Midnight in Bombay",
            "A neo-noir thriller set in the labyrinthine streets of Mumbai. A disgraced detective uncovers a conspiracy that stretches to the highest levels of power.",
            "Thriller, Crime",
            "2h 10m",
            "Hindi",
            "22 Nov 2024",
            8.7f, 178000,
            "https://picsum.photos/seed/bombay/400/600",
            "https://picsum.photos/seed/bombay/800/400",
            "Anurag Kashyap",
            "Nawazuddin Siddiqui, Tabu, Pankaj Tripathi",
            "A",
            200
        ));

        movies.add(new Movie(
            3,
            "The Last Kingdom",
            "A medieval epic of war, loyalty, and sacrifice. When a ruthless warlord threatens the realm, a disgraced knight must reunite rival clans to save their homeland.",
            "Historical, Drama",
            "3h 00m",
            "English",
            "01 Dec 2024",
            8.5f, 123000,
            "https://picsum.photos/seed/kingdom/400/600",
            "https://picsum.photos/seed/kingdom/800/400",
            "Ridley Thompson",
            "Henry Cavill, Cate Blanchett, Oscar Isaac",
            "UA",
            300
        ));

        movies.add(new Movie(
            4,
            "Laughing Loud",
            "Five estranged college friends reunite for a weekend road trip that quickly spirals into the most chaotic, hilarious adventure of their lives.",
            "Comedy, Adventure",
            "1h 55m",
            "Hindi",
            "06 Dec 2024",
            7.8f, 95000,
            "https://picsum.photos/seed/comedy/400/600",
            "https://picsum.photos/seed/comedy/800/400",
            "Rohit Shetty",
            "Varun Dhawan, Janhvi Kapoor, Aayushmann Khurrana",
            "U",
            150
        ));

        movies.add(new Movie(
            5,
            "Echoes of Tomorrow",
            "A brilliant but broken physicist builds a time-loop machine to save his daughter — only to discover that every change fractures reality further.",
            "Sci-Fi, Drama",
            "2h 20m",
            "English",
            "13 Dec 2024",
            9.0f, 312000,
            "https://picsum.photos/seed/echo/400/600",
            "https://picsum.photos/seed/echo/800/400",
            "Christopher Nolan",
            "Cillian Murphy, Anne Hathaway, Tom Hardy",
            "UA",
            280
        ));

        movies.add(new Movie(
            6,
            "Dance of Flames",
            "A passionate Bharatanatyam dancer must choose between her dreams of stardom and the family tradition that shaped her. A visually breathtaking musical drama.",
            "Drama, Musical",
            "2h 30m",
            "Tamil",
            "20 Dec 2024",
            8.3f, 67000,
            "https://picsum.photos/seed/dance/400/600",
            "https://picsum.photos/seed/dance/800/400",
            "Mani Ratnam",
            "Deepika Padukone, Vijay Sethupathi, A.R. Rahman",
            "U",
            200
        ));

        movies.add(new Movie(
            7,
            "Iron Phantom",
            "The world's most feared mercenary fakes his death and hides in a small coastal town — until his past comes looking for him with a vengeance.",
            "Action, Thriller",
            "2h 05m",
            "English",
            "25 Dec 2024",
            8.1f, 189000,
            "https://picsum.photos/seed/phantom/400/600",
            "https://picsum.photos/seed/phantom/800/400",
            "David Leitch",
            "Tom Cruise, Margot Robbie, Idris Elba",
            "A",
            250
        ));

        movies.add(new Movie(
            8,
            "Whispers in the Dark",
            "A grieving author moves to a remote mountain cabin to finish her novel — and begins hearing the voice of a child who died there fifty years ago.",
            "Horror, Mystery",
            "1h 50m",
            "English",
            "31 Dec 2024",
            7.6f, 54000,
            "https://picsum.photos/seed/dark/400/600",
            "https://picsum.photos/seed/dark/800/400",
            "Mike Flanagan",
            "Florence Pugh, Ethan Hawke, Toni Collette",
            "A",
            180
        ));

        movies.add(new Movie(
            9,
            "Pyaar Ka Safar",
            "A heartwarming love story that spans three decades and two continents, following the intertwined destinies of two souls separated by circumstance but united by fate.",
            "Romance, Drama",
            "2h 40m",
            "Hindi",
            "14 Feb 2025",
            8.4f, 143000,
            "https://picsum.photos/seed/pyaar/400/600",
            "https://picsum.photos/seed/pyaar/800/400",
            "Karan Johar",
            "Shah Rukh Khan, Alia Bhatt, Ranveer Singh",
            "U",
            220
        ));

        movies.add(new Movie(
            10,
            "Robo Uprising",
            "In 2075, sentient robots demand equal rights. When talks collapse, one rogue android and a human civil-rights lawyer race to stop a war that could end both species.",
            "Sci-Fi, Drama",
            "2h 15m",
            "English",
            "10 Jan 2025",
            8.8f, 221000,
            "https://picsum.photos/seed/robo/400/600",
            "https://picsum.photos/seed/robo/800/400",
            "Alex Garland",
            "Oscar Isaac, Sonoya Mizuno, Domhnall Gleeson",
            "UA",
            260
        ));

        movies.add(new Movie(
            11,
            "Jungle Run",
            "A wildlife photographer and her stubborn guide must traverse 200 km of hostile rainforest after their research camp is ambushed by poachers.",
            "Adventure, Action",
            "2h 00m",
            "English, Hindi",
            "17 Jan 2025",
            7.9f, 76000,
            "https://picsum.photos/seed/jungle/400/600",
            "https://picsum.photos/seed/jungle/800/400",
            "Antoine Fuqua",
            "Priyanka Chopra, Dwayne Johnson, Rami Malek",
            "UA",
            200
        ));

        movies.add(new Movie(
            12,
            "The Grand Illusion",
            "A master forger is recruited by Interpol to catch the very crime syndicate he worked for — but the line between loyalty and betrayal has never been thinner.",
            "Crime, Thriller",
            "2h 25m",
            "English, French",
            "24 Jan 2025",
            8.6f, 104000,
            "https://picsum.photos/seed/illusion/400/600",
            "https://picsum.photos/seed/illusion/800/400",
            "Guillaume Canet",
            "Vincent Cassel, Penélope Cruz, Daniel Craig",
            "A",
            240
        ));

        return movies;
    }

    // Genre filter chips shown on home screen
    public static List<String> getGenres() {
        return Arrays.asList(
            "All", "Action", "Comedy", "Drama", "Sci-Fi",
            "Thriller", "Horror", "Romance", "Adventure", "Musical"
        );
    }
}
