package model;

import java.util.Objects; // Add this line

public class Seat {
    private int id;
    private String seatNumber; // e.g., "A1", "B2"
    private boolean isBooked;
    private int showtimeId; // Foreign key to link to ShowTime in DB

    // Full constructor
    public Seat(int id, String seatNumber, boolean isBooked, int showtimeId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
        this.showtimeId = showtimeId;
    }

    // Constructor without id (e.g., before saving to DB)
    public Seat(String seatNumber, boolean isBooked, int showtimeId) {
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
        this.showtimeId = showtimeId;
    }

    // Constructor from row and column, converts to seatNumber like "A1"
    public Seat(int row, int col, int showtimeId) {
        char rowChar = (char) ('A' + row - 1); // Adjust if your rows start from 0 for 'A'
        this.seatNumber = String.valueOf(rowChar) + col;
        this.id = 0; // Default or placeholder ID, as it's not known when just given row/col
        this.isBooked = false;
        this.showtimeId = showtimeId;
    }

    // NEW CONSTRUCTOR TO FIX THE ERROR IN SeatDAO.java
    // This constructor assumes showtimeId is not known/relevant at this point
    public Seat(int row, int col) {
        char rowChar = (char) ('A' + row - 1); // Adjust if your rows start from 0 for 'A'
        this.seatNumber = String.valueOf(rowChar) + col;
        this.id = 0; // Default ID if not yet persisted or known
        this.isBooked = false; // Default for a generic seat representation
        this.showtimeId = 0; // Default or -1 if not associated with a specific showtime yet
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
        if (!(o instanceof Seat)) return false;
        Seat seat = (Seat) o;
        // Equality based on seat number and showtimeId, since seat numbers are unique per showtime
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
