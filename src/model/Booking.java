package model;
import model.ShowTime;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Booking {
    private int id;
    private Customer customer;
    private ShowTime showtime;
    private List<Seat> selectedSeats;
    private LocalDateTime bookingTime;
    private double totalPrice;
    private static final double SEAT_PRICE = 10.0; // Example fixed seat price

    // Constructor with ID, Customer, Showtime, Seats, BookingTime
    public Booking(int id, Customer customer, ShowTime showtime, List<Seat> selectedSeats, LocalDateTime bookingTime) {
        this.id = id;
        this.customer = customer;
        this.showtime = showtime;
        this.selectedSeats = selectedSeats;
        this.bookingTime = bookingTime;
        this.totalPrice = calculateTotalPrice(); // Calculate price upon creation
    }

    // Constructor with existing total price (e.g., when loading from DB)
    public Booking(int id, Customer customer, ShowTime showtime, List<Seat> selectedSeats, LocalDateTime bookingTime, double totalPrice) {
        this.id = id;
        this.customer = customer;
        this.showtime = showtime;
        this.selectedSeats = selectedSeats;
        this.bookingTime = bookingTime;
        this.totalPrice = totalPrice;
    }

    // Constructor for new bookings (without ID, before DB insert)
    public Booking(Customer customer, ShowTime showtime, List<Seat> selectedSeats, LocalDateTime bookingTime) {
        this.customer = customer;
        this.showtime = showtime;
        this.selectedSeats = selectedSeats;
        this.bookingTime = bookingTime;
        this.totalPrice = calculateTotalPrice(); // Calculate price upon creation
    }


    // Getters
    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ShowTime getShowtime() {
        return showtime;
    }

    public List<Seat> getSelectedSeats() {
        return selectedSeats;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public void setShowtime(ShowTime showtime) {
        this.showtime = showtime;
    }

    public void setSelectedSeats(List<Seat> selectedSeats) {
        this.selectedSeats = selectedSeats;
        this.totalPrice = calculateTotalPrice(); // Recalculate if seats change
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }


    public double calculateTotalPrice() {
        // You might want to use the price from the ShowTime object instead of a fixed SEAT_PRICE
        // return selectedSeats != null ? selectedSeats.size() * showtime.getPrice() : 0.0;
        return selectedSeats != null ? selectedSeats.size() * SEAT_PRICE : 0.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return id == booking.id &&
                Double.compare(booking.totalPrice, totalPrice) == 0 &&
                Objects.equals(customer, booking.customer) &&
                Objects.equals(showtime, booking.showtime) &&
                Objects.equals(selectedSeats, booking.selectedSeats) &&
                Objects.equals(bookingTime, booking.bookingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, showtime, selectedSeats, bookingTime, totalPrice);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customerId=" + (customer != null ? customer.getId() : "N/A") +
                ", showtimeId=" + (showtime != null ? showtime.getId() : "N/A") +
                ", selectedSeatsCount=" + (selectedSeats != null ? selectedSeats.size() : 0) +
                ", bookingTime=" + bookingTime +
                ", totalPrice=" + String.format("%.2f", totalPrice) +
                '}';
    }
}
