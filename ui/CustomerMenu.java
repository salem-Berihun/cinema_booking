package ui;

import dao.BookingDAO; // Still needed for viewBookings
import model.Customer;
import dao.ShowTimeDAO;
import model.ShowTime;
import model.Booking;
import model.Seat;
import util.InputValidator;
import service.BookingService; // Import the new BookingServi
import exceptions.BookingException; // Import custom exception

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.stream.Collectors;

public class CustomerMenu {
    private static Scanner sc = new Scanner(System.in);
    private static BookingDAO bookingDAO = new BookingDAO(); // Still needed for viewBookings
    private static ShowTimeDAO showTimeDAO = new ShowTimeDAO();
    private static BookingService bookingService = new BookingService(); // New: Use BookingService

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
                    viewAvailableShowTimes();
                    break;
                case 2:
                    makeBooking(customer); // Pass customer to makeBooking
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

    private static void viewAvailableShowTimes() {
        List<ShowTime> showTimes = showTimeDAO.getAllShowTimes();
        if (showTimes.isEmpty()) {
            System.out.println("No showtimes available at the moment.");
            return;
        }
        System.out.println("\n--- Available ShowTimes ---");
        for (ShowTime st : showTimes) {
            System.out.println(st);
        }
    }

    private static void makeBooking(Customer customer) { // Now accepts Customer
        System.out.println("\n--- Make a Booking ---");
        viewAvailableShowTimes();

        if (showTimeDAO.getAllShowTimes().isEmpty()) {
            System.out.println("Cannot make a booking as no showtimes are available.");
            return;
        }

        System.out.print("Enter ShowTime ID to book: ");
        int showtimeId;
        try {
            showtimeId = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for ShowTime ID.");
            sc.nextLine();
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

        // Display available seat numbers using the Map
        System.out.println("Available Seats (by number):");
        selectedShowTime.getSeatsMap().values().stream()
                .filter(seat -> !seat.isBooked())
                .forEach(seat -> System.out.print(seat.getSeatNumber() + " "));
        System.out.println();


        System.out.println("Please select your seats (e.g., A1, B2). Separate multiple seats with commas:");
        System.out.print("Enter seat numbers: ");
        String seatInput = sc.nextLine();
        String[] seatNumbers = seatInput.split(",");

        List<Seat> seatsToBook = new ArrayList<>();
        for (String seatNum : seatNumbers) {
            seatNum = seatNum.trim().toUpperCase();
            Seat seat = selectedShowTime.getSeat(seatNum); // Uses ShowTime's getSeat(String) which uses the Map
            if (seat == null) {
                System.err.println("Seat '" + seatNum + "' not found for this showtime. Please re-enter.");
                seatsToBook.clear(); // Clear list to prevent partial booking
                return; // Exit and let user try again
            }
            if (seat.isBooked()) {
                System.err.println("Seat '" + seatNum + "' is already booked. Please re-enter.");
                seatsToBook.clear(); // Clear list to prevent partial booking
                return; // Exit and let user try again
            }
            seatsToBook.add(seat);
        }

        if (seatsToBook.isEmpty()) {
            System.err.println("No valid seats selected. Booking cancelled.");
            return;
        }

        double totalPrice = seatsToBook.size() * 10.0; // Using a placeholder price for now

        System.out.println("You are booking " + seatsToBook.size() + " tickets for " + selectedShowTime.getMovie().getTitle());
        System.out.println("Total Price: $" + String.format("%.2f", totalPrice));
        System.out.print("Confirm booking? (yes/no): ");
        String confirmation = sc.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Booking cancelled by user.");
            return;
        }

        // --- CRITICAL CHANGE: Delegate to BookingService ---
        try {
            // Pass the actual customer object to the service layer
            if (bookingService.bookSeats(customer, selectedShowTime, seatsToBook)) { // Updated method call
                System.out.println("Booking successful! (Booking ID will be assigned by DAO)"); // ID is set in Booking object by DAO
            } else {
                System.err.println("Booking failed. Please try again.");
            }
        } catch (BookingException e) { // Catch custom exception
            System.err.println("Booking Error: " + e.getMessage());
            // Optionally, log this error via FileLogger here if not already logged by service
        } catch (IllegalArgumentException | IllegalStateException e) { // Catch validation exceptions from service
            System.err.println("Booking Validation Error: " + e.getMessage());
        }
        // --- END CRITICAL CHANGE ---
    }

    private static void viewBookings(Customer customer) {
        List<Booking> bookings = bookingDAO.getAllBookings();
        System.out.println("\n--- Your Bookings ---");
        boolean foundBookings = false;
        for (Booking booking : bookings) {
            if (booking.getCustomer() != null && booking.getCustomer().getId() == customer.getId()) {
                System.out.println(booking);
                foundBookings = true;
            }
        }
        if (!foundBookings) {
            System.out.println("You have no bookings yet.");
        }
    }
}
