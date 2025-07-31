package ui;

import dao.ShowTimeDAO; // Assuming ShowTimeDAO handles movie and showtime management
import dao.BookingDAO; // Import BookingDAO for viewing all bookings
import model.Movie;    // Import Movie model
import model.Booking;  // Import Booking model
import model.ShowTime; // Import Showtime model
import util.InputValidator; // For input validation

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException; // For date/time parsing errors
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ManagerMenu {
    private static Scanner sc = new Scanner(System.in);
    private static ShowTimeDAO showTimeDAO = new ShowTimeDAO(); // Use ShowTimeDAO for movie/showtime operations
    private static BookingDAO bookingDAO = new BookingDAO(); // Initialize BookingDAO for viewing bookings

    public static void meno() {
        while (true) {
            System.out.println("\n----- Manager Menu -----");
            System.out.println("1. Delete Showtime");
            System.out.println("2. Add Showtime");
            System.out.println("3. View All Bookings");
            System.out.println("4. Logout"); // Adjusted option number
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    deleteShowtime();
                    break;
                case 2:
                    addShowtime();
                    break;
                case 3:
                    viewAllBookings();
                    break;
                case 4: // Adjusted option number
                    System.out.println("Logging out...");
                    return;
                default:
                    System.err.println("Invalid option.");
            }
        }
    }

    // Method to delete a showtime
    private static void deleteShowtime() {
        System.out.println("\n--- Delete Showtime ---");

        List<ShowTime> allShowtimes = showTimeDAO.getAllShowTimes();
        if (allShowtimes.isEmpty()) {
            System.out.println("No showtimes available to delete.");
            return;
        }

        System.out.println("Available Showtimes:");
        for (ShowTime st : allShowtimes) {
            System.out.println(st); // Use ShowTime's toString()
        }

        System.out.print("Enter Showtime ID to delete: ");
        int showtimeId;
        try {
            showtimeId = sc.nextInt();
            sc.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Showtime ID.");
            sc.nextLine(); // Clear invalid input
            return;
        }

        ShowTime showtimeToDelete = showTimeDAO.getShowTimeById(showtimeId);
        if (showtimeToDelete == null) {
            System.err.println("Showtime with ID " + showtimeId + " not found.");
            return;
        }

        System.out.println("Are you sure you want to delete showtime: " + showtimeToDelete.getMovie().getTitle() + " at " + showtimeToDelete.getDateTime() + "? (yes/no)");
        String confirmation = sc.nextLine().trim().toLowerCase();

        if (!confirmation.equals("yes")) {
            System.out.println("Showtime deletion cancelled.");
            return;
        }

        if (showTimeDAO.deleteShowtime(showtimeId)) { // Call new delete method in DAO
            System.out.println("Showtime ID " + showtimeId + " and its associated bookings/seats deleted successfully.");
        } else {
            System.err.println("Failed to delete showtime ID " + showtimeId + ". Please check logs.");
        }
    }


    private static void addShowtime() {
        System.out.println("\n--- Add New Showtime ---");

        List<Movie> allMovies = showTimeDAO.getAllMovies(); // Assuming ShowTimeDAO has getAllMovies()
        if (allMovies.isEmpty()) {
            System.out.println("No movies available to schedule showtimes for. Please add movies first.");
            return;
        }

        System.out.println("Available Movies:");
        for (Movie movie : allMovies) {
            System.out.println("ID: " + movie.getId() + ", Title: " + movie.getTitle() + ", Genre: " + movie.getGenre());
        }

        System.out.print("Enter Movie ID for the showtime: ");
        int movieId;
        try {
            movieId = sc.nextInt();
            sc.nextLine(); // Consume newline
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Movie ID.");
            sc.nextLine(); // Clear invalid input
            return;
        }

        Movie selectedMovie = showTimeDAO.getMovieById(movieId);
        if (selectedMovie == null) {
            System.err.println("Movie with ID " + movieId + " not found.");
            return;
        }

        System.out.println("Scheduling showtime for: " + selectedMovie.getTitle());

        LocalDateTime dateTime;
        while (true) {
            System.out.print("Enter Date and Time (YYYY-MM-DDTHH:MM, e.g., 2025-08-15T19:30): ");
            String dateTimeString = sc.nextLine().trim();
            try {
                dateTime = LocalDateTime.parse(dateTimeString); // Uses ISO_LOCAL_DATE_TIME format by default
                break;
            } catch (DateTimeParseException e) {
                System.err.println("Invalid date/time format. Please use YYYY-MM-DDTHH:MM (e.g., 2025-08-15T19:30).");
            }
        }

        String hall;
        while (true) {
            System.out.print("Enter Hall Name (e.g., Hall 1, Auditorium B): ");
            hall = sc.nextLine().trim();
            if (!hall.isEmpty()) {
                break;
            } else {
                System.err.println("Hall name cannot be empty.");
            }
        }

        int availableSeats;
        while (true) {
            System.out.print("Enter Number of Available Seats: ");
            try {
                availableSeats = sc.nextInt();
                sc.nextLine(); // Consume newline
                if (availableSeats > 0) {
                    break;
                } else {
                    System.err.println("Available seats must be a positive number.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number for available seats.");
                sc.nextLine(); // Clear invalid input
            }
        }

        double price;
        while (true) {
            System.out.print("Enter Ticket Price: ");
            try {
                price = sc.nextDouble();
                sc.nextLine(); // Consume newline
                if (price >= 0) {
                    break;
                } else {
                    System.err.println("Price cannot be negative.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number for price.");
                sc.nextLine(); // Clear invalid input
            }
        }

        int showtimeId = showTimeDAO.addShowTime(movieId, dateTime, hall, availableSeats, price);

        if (showtimeId != -1) {
            System.out.println("Showtime added successfully for '" + selectedMovie.getTitle() + "' at " + dateTime + " in " + hall + " with ID: " + showtimeId);
        } else {
            System.err.println("Failed to add showtime. Please check logs for details.");
        }
    }

    private static void viewAllBookings() {
        System.out.println("\n--- All Bookings ---");
        List<Booking> allBookings = bookingDAO.getAllBookings();

        if (allBookings.isEmpty()) {
            System.out.println("No bookings found in the system.");
            return;
        }

        for (Booking booking : allBookings) {
            System.out.println(booking); // Assuming Booking.toString() provides good details
        }
    }
}
