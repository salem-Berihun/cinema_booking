package service;

import java.util.ArrayList;
import java.util.List;
import model.Movie;
import model.ShowTime;
import model.Seat;

public class MovieService {
    private List<Movie> movies = new ArrayList<>();
    private List<ShowTime> showTimes = new ArrayList<>();

    public void addMovie(Movie movie) {
        movies.add(movie);
    }

    public void deleteMovie(String title) {
        movies.removeIf(movie -> movie.getTitle().equalsIgnoreCase(title));
    }

    public void addShowTime(ShowTime showTime) {
        showTimes.add(showTime);
    }

    public List<Movie> getMoviesList() {
        return movies;
    }

    public List<ShowTime> getShowTimesList() {
        return showTimes;
    }
}
