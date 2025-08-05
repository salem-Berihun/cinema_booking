package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer extends User {
    private List<Booking> bookings; 

   
    public Customer(int id, String fullName, String email, String password, String userType) {
        
        super(id, fullName, email, password, "customer");
        this.bookings = new ArrayList<>();
    }

    public Customer(String fullName, String email, String password, String userType) {
        super(fullName, email, password, "customer");
        this.bookings = new ArrayList<>();
    }

    public Customer(int id, String fullName, String email, String password, String userType, List<Booking> bookings) {
        super(id, fullName, email, password, "customer");
        this.bookings = bookings != null ? bookings : new ArrayList<>();
    }


    public List<Booking> getBookings() {
        return bookings;
    }

    public void setBookings(List<Booking> bookings) {
        this.bookings = bookings != null ? bookings : new ArrayList<>();
    }

    public void addBooking(Booking booking) {
        if (booking != null) {
            this.bookings.add(booking);
        }
    }

    public void voidremoveBooking(Booking booking) {
        if (booking != null) {
            this.bookings.remove(booking);
        }
    }

    @Override
    public void displayDashboard() {
        
        System.out.println("\n----- Customer Dashboard -----");
        System.out.println("Welcome, " + getFullName() + " (Customer)!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // Check superclass equality first
        if (!super.equals(o)) return false;
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
                super.toString() + 
                ", bookingsCount=" + (bookings != null ? bookings.size() : 0) +
                '}';
        //test comment for git

    }
}
