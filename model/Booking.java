import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class Booking {
    private int id;
    private Customer customer;
    private ShowTime showTime;
    private List<Seat> selectedSeats;
    private LocalDateTime bookingTime;
    private double totalPrice;
    private static final double SEAT_PRICE = 10.0; // Example fixed seat price

    public Booking(int id, Customer customer, ShowTime showTime, List<Seat> selectedSeats, LocalDateTime bookingTime) {
        this.id = id;
        this.customer = customer;
        this.showTime = showTime;
        this.selectedSeats = selectedSeats;
        this.bookingTime = bookingTime;
        this.totalPrice = calculateTotalPrice(); // Calculate price upon creation
    }

    // Constructor with existing total price (e.g., when loading from DB)
    public Booking(int id, Customer customer, ShowTime showTime, List<Seat> selectedSeats, LocalDateTime bookingTime, double totalPrice) {
        this.id = id;
        this.customer = customer;
        this.showTime = showTime;
        this.selectedSeats = selectedSeats;
        this.bookingTime = bookingTime;
        this.totalPrice = totalPrice;
    }

    // Getters
    public int getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public ShowTime getShowTime() {
        return showTime;
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

    public void setShowTime(ShowTime showTime) {
        this.showTime = showTime;
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

    /**
     * Calculates the total price of the booking based on the number of selected seats and a fixed seat price.
     * @return The calculated total price.
     */
    public double calculateTotalPrice() {
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
                Objects.equals(showTime, booking.showTime) &&
                Objects.equals(selectedSeats, booking.selectedSeats) &&
                Objects.equals(bookingTime, booking.bookingTime);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, customer, showTime, selectedSeats, bookingTime, totalPrice);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", customerId=" + (customer != null ? customer.getId() : "N/A") +
                ", showTimeId=" + (showTime != null ? showTime.getId() : "N/A") +
                ", selectedSeatsCount=" + (selectedSeats != null ? selectedSeats.size() : 0) +
                ", bookingTime=" + bookingTime +
                ", totalPrice=" + String.format("%.2f", totalPrice) +
                '}';
    }
}