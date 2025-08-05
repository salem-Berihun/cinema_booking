package model;

import java.util.Objects; e

public class Seat {
    private int id;
    private String seatNumber; 
    private boolean isBooked;
    private int showtimeId;

    public Seat(int id, String seatNumber, boolean isBooked, int showtimeId) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
        this.showtimeId = showtimeId;
    }

    public Seat(String seatNumber, boolean isBooked, int showtimeId) {
        this.seatNumber = seatNumber;
        this.isBooked = isBooked;
        this.showtimeId = showtimeId;
    }

    public Seat(int row, int col, int showtimeId) {
        char rowChar = (char) ('A' + row - 1); 
        this.seatNumber = String.valueOf(rowChar) + col;
        this.id = 0; 
        this.isBooked = false;
        this.showtimeId = showtimeId;
    }

    
    public Seat(int row, int col) {
        char rowChar = (char) ('A' + row - 1); 
        this.seatNumber = String.valueOf(rowChar) + col;
        this.id = 0; 
        this.isBooked = false; 
        this.showtimeId = 0; 
    }

    
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
