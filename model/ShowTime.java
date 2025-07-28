import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ShowTime {
    private int id;
    private Movie movie; // Linked Movie object
    private LocalDateTime dateTime;
    private String hall;
    private List<Seat> seats; // List of seats for this specific showtime

    public ShowTime(int id, Movie movie, LocalDateTime dateTime, String hall, List<Seat> seats) {
        this.id = id;
        this.movie = movie;
        this.dateTime = dateTime;
        this.hall = hall;
        this.seats = seats != null ? seats : new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public Movie getMovie() {
        return movie;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public String getHall() {
        return hall;
    }

    public List<Seat> getSeats() {
        return seats;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public void setHall(String hall) {
        this.hall = hall;
    }

    public void setSeats(List<Seat> seats) {
        this.seats = seats != null ? seats : new ArrayList<>();
    }

    /**
     * Calculates the number of available (not booked) seats for this showtime.
     * @return The count of available seats.
     */
    public int getAvailableSeatsCount() {
        return (int) seats.stream().filter(seat -> !seat.isBooked()).count();
    }

    /**
     * Retrieves a specific seat by its seat number.
     * @param seatNumber The unique identifier for the seat (e.g., "A1", "B2").
     * @return The Seat object if found, otherwise null.
     */
    public Seat getSeat(String seatNumber) {
        return seats.stream()
                .filter(seat -> seat.getSeatNumber().equalsIgnoreCase(seatNumber))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ShowTime showTime = (ShowTime) o;
        return id == showTime.id &&
                Objects.equals(movie, showTime.movie) &&
                Objects.equals(dateTime, showTime.dateTime) &&
                Objects.equals(hall, showTime.hall) &&
                Objects.equals(seats, showTime.seats); // Consider if comparing full seat list is desired for equals
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, movie, dateTime, hall, seats);
    }

    @Override
    public String toString() {
        return "ShowTime{" +
                "id=" + id +
                ", movieTitle='" + (movie != null ? movie.getTitle() : "N/A") + '\'' +
                ", dateTime=" + dateTime +
                ", hall='" + hall + '\'' +
                ", availableSeats=" + getAvailableSeatsCount() +
                '}';
    }
}