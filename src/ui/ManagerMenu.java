package ui;

import dao.ShowTimeDAO;
import dao.BookingDAO;
import dao.MovieDAO; // Import MovieDAO
import model.Movie;
import model.Booking;
import model.ShowTime;
import util.InputValidator;
import util.FileLogger;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ManagerMenu {
    private static Scanner sc = new Scanner(System.in);
    private static ShowTimeDAO showTimeDAO = new ShowTimeDAO();
    private static BookingDAO bookingDAO = new BookingDAO();
    private static MovieDAO movieDAO = new MovieDAO(); // Initialize MovieDAO
    private static FileLogger fileLogger = new FileLogger();

    public static void meno() {
        while (true) {
            System.out.println("\n----- Manager Menu -----");
            System.out.println("1. Delete Showtime");
            System.out.println("2. Add Showtime");
            System.out.println("3. View All Bookings");
            System.out.println("4. Generate Booking Report");
            System.out.println("5. Add New Movie"); // NEW OPTION
            System.out.println("6. Logout"); // Adjusted option number
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
                case 4:
                    generateBookingReport();
                    break;
                case 5: // NEW CASE
                    addNewMovie();
                    break;
                case 6: // Adjusted option number
                    System.out.println("Logging out...");
                    return;
                default:
                    System.err.println("Invalid option.");
            }
        }
    }

    private static void deleteShowtime() {
        System.out.println("\n--- Delete Showtime ---");

        List<ShowTime> allShowtimes = showTimeDAO.getAllShowTimes();
        if (allShowtimes.isEmpty()) {
            System.out.println("No showtimes available to delete.");
            return;
        }

        System.out.println("Available Showtimes:");
        for (ShowTime st : allShowtimes) {
            System.out.println(st);
        }

        System.out.print("Enter Showtime ID to delete: ");
        int showtimeId;
        try {
            showtimeId = sc.nextInt();
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Showtime ID.");
            sc.nextLine();
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

        if (showTimeDAO.deleteShowtime(showtimeId)) {
            System.out.println("Showtime ID " + showtimeId + " and its associated bookings/seats deleted successfully.");
            fileLogger.logInfo("Manager: Showtime ID " + showtimeId + " deleted.");
        } else {
            System.err.println("Failed to delete showtime ID " + showtimeId + ". Please check logs.");
            fileLogger.logError("Manager: Failed to delete showtime ID " + showtimeId + ".");
        }
    }


    private static void addShowtime() {
        System.out.println("\n--- Add New Showtime ---");

        // Use movieDAO to get all movies
        List<Movie> allMovies = movieDAO.getAllMovies(); // UPDATED: Use movieDAO
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
            sc.nextLine();
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number for Movie ID.");
            sc.nextLine();
            return;
        }

        // Use movieDAO to get movie by ID
        Movie selectedMovie = movieDAO.getMovieById(movieId); // UPDATED: Use movieDAO
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
                dateTime = LocalDateTime.parse(dateTimeString);
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
                sc.nextLine();
                if (availableSeats > 0) {
                    break;
                } else {
                    System.err.println("Available seats must be a positive number.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number for available seats.");
                sc.nextLine();
            }
        }

        double price;
        while (true) {
            System.out.print("Enter Ticket Price: ");
            try {
                price = sc.nextDouble();
                sc.nextLine();
                if (price >= 0) {
                    break;
                } else {
                    System.err.println("Price cannot be negative.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number for price.");
                sc.nextLine();
            }
        }

        int showtimeId = showTimeDAO.addShowTime(movieId, dateTime, hall, availableSeats, price);

        if (showtimeId != -1) {
            System.out.println("Showtime added successfully for '" + selectedMovie.getTitle() + "' at " + dateTime + " in " + hall + " with ID: " + showtimeId);
            fileLogger.logInfo("Manager: Added showtime ID " + showtimeId + " for movie " + selectedMovie.getTitle());
        } else {
            System.err.println("Failed to add showtime. Please check logs for details.");
            fileLogger.logError("Manager: Failed to add showtime for movie " + selectedMovie.getTitle());
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
            System.out.println(booking);
        }
    }

    private static void generateBookingReport() {
        System.out.println("\n--- Generating Booking Report ---");
        List<Booking> allBookings = bookingDAO.getAllBookings();
        if (fileLogger.generateReport("ALL_BOOKINGS", allBookings)) {
            System.out.println("Booking report generated successfully to " + FileLogger.BOOKING_REPORT_PATH);
            fileLogger.logInfo("Manager: Booking report generated.");
        } else {
            System.err.println("Failed to generate booking report.");
            fileLogger.logError("Manager: Failed to generate booking report.");
        }
    }

    // NEW METHOD: Add New Movie
    private static void addNewMovie() {
        System.out.println("\n--- Add New Movie ---");
        String title, description, genre;
        int durationMinutes;

        while (true) {
            System.out.print("Enter Movie Title: ");
            title = sc.nextLine().trim();
            if (!title.isEmpty()) break;
            else System.err.println("Movie title cannot be empty.");
        }

        System.out.print("Enter Movie Description: ");
        description = sc.nextLine().trim(); // Description can be empty

        while (true) {
            System.out.print("Enter Movie Duration (minutes): ");
            try {
                durationMinutes = sc.nextInt();
                sc.nextLine(); // consume newline
                if (durationMinutes > 0) break;
                else System.err.println("Duration must be a positive number.");
            } catch (InputMismatchException e) {
                System.err.println("Invalid input. Please enter a number for duration.");
                sc.nextLine();
            }
        }

        while (true) {
            System.out.print("Enter Movie Genre: ");
            genre = sc.nextLine().trim();
            if (!genre.isEmpty()) break;
            else System.err.println("Movie genre cannot be empty.");
        }

        int movieId = movieDAO.addMovie(title, description, durationMinutes, genre); // Use movieDAO

        if (movieId != -1) {
            System.out.println("Movie '" + title + "' added successfully with ID: " + movieId);
            fileLogger.logInfo("Manager: Added movie ID " + movieId + " - " + title);
        } else {
            System.err.println("Failed to add movie. Please check logs for details.");
            fileLogger.logError("Manager: Failed to add movie - " + title);
        }
    }
}
