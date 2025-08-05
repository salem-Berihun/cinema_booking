package dao;

import model.Booking;
import model.Customer;
import model.Seat;
import model.ShowTime;
import util.DBUtil; // Ensure DBUtil is imported
import util.FileLogger; // Import FileLogger
import exceptions.BookingException; // Import custom exception

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private CustomerDAO customerDAO;
    private ShowTimeDAO showTimeDAO; // Corrected casing: ShowTimeDAO -> ShowtimeDAO
    private FileLogger fileLogger; // Instance of FileLogger

    public BookingDAO() {
        customerDAO = new CustomerDAO();
        showTimeDAO = new ShowTimeDAO(); // Corrected casing
        fileLogger = new FileLogger(); // Initialize FileLogger
    }

    public boolean saveBooking(Booking booking) throws BookingException { // Declare throws clause
        // Corrected table name to 'bookings' (plural) and column name to 'booking_date'
        String insertBookingSQL = "INSERT INTO bookings (customer_id, showtime_id, num_tickets, booking_date, total_price) VALUES (?, ?, ?, ?, ?)";
        // Corrected table name to 'booking_seats' (plural)
        String insertBookingSeatSQL = "INSERT INTO booking_seats (booking_id, seat_number) VALUES (?, ?)"; // Use seat_number as per DB schema

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert into bookings table
            try (PreparedStatement bookingStmt = conn.prepareStatement(insertBookingSQL, Statement.RETURN_GENERATED_KEYS)) {
                bookingStmt.setInt(1, booking.getCustomer().getId());
                bookingStmt.setInt(2, booking.getShowtime().getId());
                bookingStmt.setInt(3, booking.getSelectedSeats().size()); // num_tickets from selected seats count
                bookingStmt.setString(4, DBUtil.formatDateTime(booking.getBookingTime())); // Use DBUtil formatter
                bookingStmt.setDouble(5, booking.getTotalPrice());
                int affectedRows = bookingStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating booking failed, no rows affected.");
                }

                try (ResultSet generatedKeys = bookingStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getInt(1)); // Set the generated ID back to the Booking object
                    } else {
                        throw new SQLException("Creating booking failed, no ID obtained.");
                    }
                }
            }

            // 2. Insert into booking_seats table for each selected seat
            try (PreparedStatement seatStmt = conn.prepareStatement(insertBookingSeatSQL)) {
                for (Seat seat : booking.getSelectedSeats()) {
                    seatStmt.setInt(1, booking.getId());
                    seatStmt.setString(2, seat.getSeatNumber()); // Use seat_number for booking_seats
                    seatStmt.addBatch(); // Add to batch for efficiency
                }
                seatStmt.executeBatch(); // Execute all batched inserts
            }

            conn.commit(); // Commit the transaction
            fileLogger.logBooking(booking); // Log the successful booking
            System.out.println("Booking saved successfully!");
            return true;

        } catch (SQLException e) {
            System.err.println("Booking Save Error: " + e.getMessage());
            e.printStackTrace(); // Print full stack trace for debugging
            if (conn != null) {
                try {
                    System.err.println("Attempting rollback for booking save error.");
                    conn.rollback(); // Rollback transaction on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                    rollbackEx.printStackTrace();
                }
            }
            // Wrap SQLException in custom exception for higher-level handling
            throw new BookingException("Failed to save booking due to database error.", e);
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close(); // Close the connection
                } catch (SQLException ex) {
                    System.err.println("Failed to reset auto-commit or close connection: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();

        // Corrected table name to 'bookings' (plural) and column name to 'booking_date'
        String selectBookingsSQL = "SELECT id, customer_id, showtime_id, num_tickets, booking_date, total_price FROM bookings";
        // Corrected table name to 'booking_seats' (plural)
        // Join with 'seat' table to get seat_number and is_booked
        String selectSeatsForBookingSQL = "SELECT s.id, s.seat_number, s.is_booked, s.showtime_id FROM booking_seats bs JOIN seat s ON bs.seat_number = s.seat_number AND bs.booking_id = ? AND s.showtime_id = ?";


        try (Connection conn = DBUtil.getConnection(); // Get connection within the method
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectBookingsSQL)) {

            while (rs.next()) {
                int bookingId = rs.getInt("id");
                int customerId = rs.getInt("customer_id");
                int showtimeId = rs.getInt("showtime_id");
                int numTickets = rs.getInt("num_tickets"); // Retrieve num_tickets
                LocalDateTime bookingTime = DBUtil.parseDateTime(rs.getString("booking_date")); // Use DBUtil parser
                double totalPrice = rs.getDouble("total_price");

                // Get customer and showtime using DAOs
                Customer customer = customerDAO.getCustomerById(customerId); // Assuming CustomerDAO.getCustomerById exists
                ShowTime showTime = showTimeDAO.getShowTimeById(showtimeId); // Corrected: getShowTimeById

                // If customer or showtime might be null (e.g., if data is inconsistent), handle it
                if (customer == null || showTime == null) {
                    System.err.println("Warning: Could not load customer or showtime for booking ID: " + bookingId + ". Skipping this booking.");
                    continue; // Skip this booking and proceed to the next
                }

                // Get seats booked in this booking
                List<Seat> seatList = new ArrayList<>();
                try (PreparedStatement seatStmt = conn.prepareStatement(selectSeatsForBookingSQL)) {
                    seatStmt.setInt(1, bookingId);
                    seatStmt.setInt(2, showtimeId); // Pass showtimeId to filter seats correctly
                    try (ResultSet seatRS = seatStmt.executeQuery()) {
                        while (seatRS.next()) {
                            int seatId = seatRS.getInt("id");
                            String seatNumber = seatRS.getString("seat_number");
                            boolean isBooked = seatRS.getInt("is_booked") == 1; // SQLite uses INTEGER for boolean
                            int seatShowtimeId = seatRS.getInt("showtime_id");

                            // Corrected Seat constructor arguments based on model.Seat: (id, seatNumber, isBooked, showtimeId)
                            seatList.add(new Seat(seatId, seatNumber, isBooked, seatShowtimeId));
                        }
                    }
                }

                Booking booking = new Booking(bookingId, customer, showTime, seatList, bookingTime, totalPrice);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Connection opened in try-with-resources will be auto-closed
        }

        return bookings;
    }
}

