package ui;

import dao.BookingDAO;
import model.Customer;
import dao.ShowTimeDAO;
import model.ShowTime;
import model.Booking;
import model.Seat; // Import Seat model
import util.InputValidator; // Assuming you have an InputValidator

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException; // Added this import
import java.util.stream.Collectors; // For stream operations

public class CustomerMenu {
    private static Scanner sc = new Scanner(System.in);
    private static BookingDAO bookingDAO = new BookingDAO();
    private static ShowTimeDAO showTimeDAO = new ShowTimeDAO(); // Ensure ShowTimeDAO is initialized

    // Renamed 'meno' to 'customerMainMenu' to match the call in main.java
    public static void customerMainMenu(Customer customer) {
        while (true) {
            System.out.println("\n----- Customer Menu -----");
            System.out.println("1. View Available ShowTimes");
            System.out.println("2. Make a Booking");
            System.out.println("3. View My Bookings");
            System.out.println("4. Logout");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAvailableShowTimes(); // Extracted to a separate method
                    break;
                case 2:
                    makeBooking(customer);
                    break;
                case 3:
                    viewBookings(customer);
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.err.println("Invalid option.");
            }
        }
    }

    // New method to view available showtimes
    private static void viewAvailableShowTimes() {
        List<ShowTime> showTimes = showTimeDAO.getAllShowTimes();
        if (showTimes.isEmpty()) {
            System.out.println("No showtimes available at the moment.");
            return;
        }
        System.out.println("\n--- Available ShowTimes ---");
        for (ShowTime st : showTimes) {
            System.out.println(st); // Uses ShowTime's toString()
        }
    }

    private static void makeBooking(Customer customer) {
        System.out.println("\n--- Make a Booking ---");
        viewAvailableShowTimes(); // Show available showtimes first

        if (showTimeDAO.getAllShowTimes().isEmpty()) {
            System.out.println("Cannot make a booking as no showtimes are available.");
            return;
        }

        System.out.print("Enter ShowTime ID to book: ");
        int showtimeId;
        try {
            showtimeId = sc.nextInt();
            sc.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for ShowTime ID.");
            sc.nextLine(); // Clear invalid input
            return;
        }

        ShowTime selectedShowTime = showTimeDAO.getShowTimeById(showtimeId);

        if (selectedShowTime == null) {
            System.err.println("ShowTime with ID " + showtimeId + " not found.");
            return;
        }

        if (selectedShowTime.getAvailableSeatsCount() <= 0) {
            System.out.println("Sorry, this showtime is fully booked.");
            return;
        }

        System.out.println("Selected ShowTime: " + selectedShowTime.getMovie().getTitle() + " on " + selectedShowTime.getDateTime() + " in " + selectedShowTime.getHall());
        System.out.println("Available Seats: " + selectedShowTime.getAvailableSeatsCount());

        // Display available seat numbers (optional, but helpful for user)
        List<Seat> availableSeats = selectedShowTime.getSeats().stream()
                .filter(seat -> !seat.isBooked())
                .collect(Collectors.toList());

        if (availableSeats.isEmpty()) {
            System.out.println("No individual seats are available for this showtime.");
            return;
        }

        System.out.println("Please select your seats (e.g., A1, B2). Separate multiple seats with commas:");
        System.out.print("Enter seat numbers: ");
        String seatInput = sc.nextLine();
        String[] seatNumbers = seatInput.split(",");

        List<Seat> seatsToBook = new ArrayList<>();
        for (String seatNum : seatNumbers) {
            seatNum = seatNum.trim().toUpperCase(); // Clean and standardize input
            Seat seat = selectedShowTime.getSeat(seatNum); // Get seat object from showtime
            if (seat == null) {
                System.err.println("Seat '" + seatNum + "' not found for this showtime. Booking cancelled.");
                return;
            }
            if (seat.isBooked()) {
                System.err.println("Seat '" + seatNum + "' is already booked. Booking cancelled.");
                return;
            }
            seatsToBook.add(seat);
        }

        if (seatsToBook.isEmpty()) {
            System.err.println("No valid seats selected. Booking cancelled.");
            return;
        }

        // Calculate total price (assuming fixed price per seat from Booking model)
        // You might want to get the price from the ShowTime object if it's dynamic
        double totalPrice = seatsToBook.size() * 10.0; // Using a placeholder price, ideally from ShowTime.price

        System.out.println("You are booking " + seatsToBook.size() + " tickets for " + selectedShowTime.getMovie().getTitle());
        System.out.println("Total Price: $" + String.format("%.2f", totalPrice));
        System.out.print("Confirm booking? (yes/no): ");
        String confirmation = sc.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Booking cancelled by user.");
            return;
        }

        // Create the Booking object
        Booking newBooking = new Booking(
                0, // ID will be set by DAO
                customer,
                selectedShowTime,
                seatsToBook,
                LocalDateTime.now(),
                totalPrice
        );

        // Save booking and update seat status
        if (bookingDAO.saveBooking(newBooking)) {
            // Update seat status in the database (mark as booked)
            for (Seat seat : seatsToBook) {
                showTimeDAO.updateSeatStatus(seat.getId(), true); // New method needed in ShowTimeDAO
            }
            // Update available seats count in showtime table
            showTimeDAO.updateShowtimeAvailableSeats(selectedShowTime.getId(), selectedShowTime.getAvailableSeatsCount() - seatsToBook.size()); // New method needed in ShowTimeDAO

            System.out.println("Booking successful! Your booking ID is: " + newBooking.getId());
        } else {
            System.err.println("Booking failed. Please try again.");
        }
    }

    private static void viewBookings(Customer customer) {
        List<Booking> bookings = bookingDAO.getAllBookings();
        System.out.println("\n--- Your Bookings ---");
        boolean foundBookings = false;
        for (Booking booking : bookings) {
            // Ensure customer object is not null and matches current customer
            if (booking.getCustomer() != null && booking.getCustomer().getId() == customer.getId()) {
                System.out.println(booking); // Uses Booking's toString()
                foundBookings = true;
            }
        }
        if (!foundBookings) {
            System.out.println("You have no bookings yet.");
        }
    }
}
