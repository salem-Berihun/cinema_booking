package model;

public class Seat {
    private String seatNumber;
    private boolean isBooked;

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.isBooked = false;
    }

    public String getSeatNumber() { return seatNumber; }
    public boolean isBooked() { return isBooked; }

    public void book() { this.isBooked = true; }
    public void cancel() { this.isBooked = false; }

    @Override
    public String toString() {
        return seatNumber + (isBooked ? " [BOOKED]" : " [AVAILABLE]");
        //test
    }
}
