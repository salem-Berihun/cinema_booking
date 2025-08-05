package model;

import java.util.List;

public class ShowTime {
    private String date;
    private String time;
    private Movie movie;
    private String hall;
    private List<Seat> seats;

    public ShowTime(String date, String time, Movie movie, String hall, List<Seat> seats) {
        this.date = date;
        this.time = time;
        this.movie = movie;
        this.hall = hall;
        this.seats = seats;
    }

    public String getDate() { return date; }
    public String getTime() { return time; }
    public Movie getMovie() { return movie; }
    public String getHall() { return hall; }
    public List<Seat> getSeats() { return seats; }

    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
    public void setMovie(Movie movie) { this.movie = movie; }
    public void setHall(String hall) { this.hall = hall; }
    public void setSeats(List<Seat> seats) { this.seats = seats; }

    @Override
    public String toString() {
        return movie.getTitle() + " at " + time + " on " + date + " in Hall " + hall;
        //tests
    }
}
