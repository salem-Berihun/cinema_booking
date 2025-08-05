package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer extends User {
    private List<Booking> bookings; // A customer has a list of their bookings

    // Constructor for existing customers (with ID from DB)
    public Customer(int id, String fullName, String email, String password, String userType) {
        // Call the constructor of the parent class (User)
        // We explicitly pass "customer" as the userType, ensuring this object is always identified as a customer.
        super(id, fullName, email, password, "customer");
        this.bookings = new ArrayList<>(); // Initialize an empty list of bookings
    }

    // Constructor for new customers (without ID, before DB insert)
    public Customer(String fullName, String email, String password, String userType) {
        // Call the constructor of the parent class (User)
        super(fullName, email, password, "customer");
        this.bookings = new ArrayList<>(); // Initialize an empty list of bookings
    }

    // Constructor with existing bookings (used when loading a customer and their bookings)
    public Customer(int id, String fullName, String email, String password, String userType, List<Booking> bookings) {
        super(id, fullName, email, password, "customer");
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

    // Method to add a single booking to the customer's list
    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    // Method to remove a single booking from the customer's list
    public void voidremoveBooking(Booking booking) {
        if (booking != null) {
            this.bookings.remove(booking);
        }
    }

    // --- IMPORTANT: Implement the abstract displayDashboard() method from User ---
    @Override
    public void displayDashboard() {
        // This method will typically lead to the CustomerMenu.customerMainMenu()
        // For now, it prints a message.
        // The main method will then call CustomerMenu.customerMainMenu(this);
        System.out.println("\n----- Customer Dashboard -----");
        System.out.println("Welcome, " + getFullName() + " (Customer)!");
        // We'll handle the actual menu navigation in main.java or the calling UI class.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // Check superclass equality first
        if (!super.equals(o)) return false;
        Customer customer = (Customer) o;
        // Only compare bookings if necessary for equality. Often, identity (ID/email) from User is enough.
        // Comparing lists can be tricky and might not be what you want for 'equality'.
        // For now, let's keep it simple and rely on User's equality, or explicitly compare list contents.
        return Objects.equals(bookings, customer.bookings);
    }

    @Override
    public int hashCode() {
        // Combine superclass hash code with bookings hash code
        return Objects.hash(super.hashCode(), bookings);
    }

    @Override
    public String toString() {
        return "Customer{" +
                super.toString() + 
                ", bookingsCount=" + (bookings != null ? bookings.size() : 0) +
                '}';
    }
}

