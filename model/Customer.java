import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer extends User {
    private List<Booking> bookings;

    public Customer(int id, String name, String email, String password) {
        super(id, name, email, password);
        this.bookings = new ArrayList<>(); // Initialize an empty list of bookings
    }

    // Constructor with existing bookings
    public Customer(int id, String name, String email, String password, List<Booking> bookings) {
        super(id, name, email, password);
        this.bookings = bookings != null ? bookings : new ArrayList<>();
    }

    // Getter for bookings
    public List<Booking> getBookings() {
        return bookings;
    }

    // Setter for bookings
    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
    }

    // Method to add a single booking
    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    // Method to remove a single booking
    public void removeBooking(Booking booking) {
        if (booking != null) {
            this.bookings.remove(booking);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false; // Check superclass equality
        Customer customer = (Customer) o;
        return Objects.equals(bookings, customer.bookings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), bookings);
    }

    @Override
    public String toString() {
        return "Customer{" +
                super.toString() + // Include User's toString output
                ", bookingsCount=" + (bookings != null ? bookings.size() : 0) +
                '}';
    }
}