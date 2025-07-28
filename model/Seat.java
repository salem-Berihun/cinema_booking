import java.util.Objects;

public class Seat {
    private int id;
    private String seatNumber; // e.g., "A1", "B2"
    private boolean isBooked;
    private int showtimeId; // Foreign key to link to ShowTime in DB

    public Seat(int id, String seatNumber, boolean isBooked, int showtimeId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
        this.showtimeId = showtimeId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public boolean isBooked() {
        return isBooked;
    }

    public int getShowtimeId() {
        return showtimeId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public void setBooked(boolean booked) {
        isBooked = booked;
    }

    public void setShowtimeId(int showtimeId) {
        this.showtimeId = showtimeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Seat seat = (Seat) o;
        // Equality based on seat number and showtime, as seat numbers are unique per showtime
        return id == seat.id &&
                showtimeId == seat.showtimeId &&
                Objects.equals(seatNumber, seat.seatNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, seatNumber, showtimeId);
    }

    @Override
    public String toString() {
        return "Seat{" +
                "id=" + id +
                ", seatNumber='" + seatNumber + '\'' +
                ", isBooked=" + isBooked +
                ", showtimeId=" + showtimeId +
                '}';
    }
}