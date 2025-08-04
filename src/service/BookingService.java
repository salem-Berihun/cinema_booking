package service;

import dao.BookingDAO;
import dao.ShowTimeDAO;
import exceptions.BookingException; // Import custom exception
import interfaces.Bookable; // Import the Bookable interface
import model.Booking;
import model.Customer; // Import Customer model
import model.Seat;
import model.ShowTime;
import util.FileLogger; // For logging within the service

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService implements Bookable { // Implements the Bookable interface

    private BookingDAO bookingDAO;
    private ShowTimeDAO showTimeDAO;
    private FileLogger fileLogger; // For logging service operations

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.showTimeDAO = new ShowTimeDAO();
        this.fileLogger = new FileLogger();
    }


    @Override
    public boolean bookSeats(Customer customer, ShowTime showTime, List<Seat> seatsToBook) throws BookingException {
        // Diagnostic print to ensure customer object is not null
        if (customer == null) {
            fileLogger.logError("BookingService: Customer object is null during booking attempt.");
            throw new IllegalArgumentException("Customer object cannot be null.");
        }
        System.out.println("BookingService: Attempting to book for customer: " + customer.getFullName() + " (ID: " + customer.getId() + ")");


        if (showTime == null || seatsToBook == null || seatsToBook.isEmpty()) {
            throw new IllegalArgumentException("ShowTime, and seats to book cannot be null or empty.");
        }

        // Business logic: Validate if seats are actually available and exist for this showtime
        for (Seat requestedSeat : seatsToBook) {
            // Use ShowTime's getSeat(String) which uses the internal Map for efficient lookup
            Seat actualSeat = showTime.getSeat(requestedSeat.getSeatNumber());
            if (actualSeat == null) {
                fileLogger.logError("BookingService: Requested seat '" + requestedSeat.getSeatNumber() + "' not found for showtime ID " + showTime.getId());
                throw new IllegalStateException("Seat '" + requestedSeat.getSeatNumber() + "' not found for this showtime.");
            }
            if (actualSeat.isBooked()) {
                fileLogger.logError("BookingService: Seat '" + requestedSeat.getSeatNumber() + "' already booked for showtime ID " + showTime.getId());
                throw new IllegalStateException("Seat '" + requestedSeat.getSeatNumber() + "' is already booked.");
            }
            // Ensure the Seat object in seatsToBook has the correct ID from the actualSeat
            // This is important for ShowTimeDAO.updateSeatStatus later, which uses seat ID.
            requestedSeat.setId(actualSeat.getId());
        }

        // Calculate total price. Assuming Booking model's constructor calculates totalPrice.
        // If ShowTime had a price per seat, you'd use showTime.getPrice() here.
        double totalPrice = seatsToBook.size() * 10.0; // Using a placeholder price for now

        // Create the Booking object (ID will be set by DAO)
        // CRITICAL CHANGE: Explicitly pass 0 for the ID to match the constructor signature
        Booking newBooking = new Booking(0, customer, showTime, seatsToBook, LocalDateTime.now(), totalPrice);

        try {
            // Orchestrate DAO calls
            boolean bookingSaved = bookingDAO.saveBooking(newBooking);

            if (bookingSaved) {
                // Update seat status in the database (mark as booked)
                for (Seat seat : seatsToBook) {
                    // Use the seat's ID which was updated from actualSeat
                    showTimeDAO.updateSeatStatus(seat.getId(), true);
                }
                // Update available seats count in showtime table
                showTimeDAO.updateShowtimeAvailableSeats(showTime.getId(), showTime.getAvailableSeatsCount() - seatsToBook.size());

                fileLogger.logInfo("BookingService: Booking successful for customer " + customer.getFullName() + " (ID: " + newBooking.getId() + ")");
                return true;
            } else {
                fileLogger.logError("BookingService: Failed to save booking to database for showtime ID " + showTime.getId());
                throw new BookingException("Failed to save booking to database.");
            }
        } catch (BookingException e) {
            fileLogger.logError("BookingService: Booking failed: " + e.getMessage());
            throw e; // Re-throw custom exception
        } catch (IllegalArgumentException | IllegalStateException e) { // Catch validation exceptions from service
            System.err.println("Booking Validation Error: " + e.getMessage());
            throw e; // Re-throw the exception after logging/printing
        } catch (Exception e) {
            fileLogger.logError("BookingService: An unexpected error occurred during booking: " + e.getMessage());
            throw new BookingException("An unexpected error occurred during booking.", e);
        }
    }


    @Override
    public boolean cancelSeats(ShowTime showTime, List<Seat> seatsToCancel) throws BookingException {
        if (showTime == null || seatsToCancel == null || seatsToCancel.isEmpty()) {
            throw new IllegalArgumentException("ShowTime and seats to cancel cannot be null or empty.");
        }

        // Business logic: Validate if seats are actually booked for this showtime
        for (Seat requestedSeat : seatsToCancel) {
            Seat actualSeat = showTime.getSeat(requestedSeat.getSeatNumber());
            if (actualSeat == null) {
                fileLogger.logError("BookingService: Requested seat '" + requestedSeat.getSeatNumber() + "' not found for showtime ID " + showTime.getId() + " during cancellation.");
                throw new IllegalStateException("Seat '" + requestedSeat.getSeatNumber() + "' not found for this showtime.");
            }
            if (!actualSeat.isBooked()) {
                fileLogger.logError("BookingService: Seat '" + requestedSeat.getSeatNumber() + "' is not booked for showtime ID " + showTime.getId() + " during cancellation.");
                throw new IllegalStateException("Seat '" + requestedSeat.getSeatNumber() + "' is not currently booked.");
            }
            // Ensure the Seat object in seatsToCancel has the correct ID from the actualSeat
            requestedSeat.setId(actualSeat.getId());
        }

        // This would involve:
        // 1. Finding the associated booking(s) for these seats.
        // 2. Deleting entries from booking_seats table.
        // 3. Updating seat status to unbooked in the seat table.
        // 4. Updating showtime available seats count.
        // 5. Potentially deleting the main booking if all seats are cancelled.
        // 6. Using transactions for atomicity.

        fileLogger.logInfo("BookingService: Attempting to cancel seats for showtime ID " + showTime.getId() + ". This feature is not fully implemented yet.");
        throw new UnsupportedOperationException("Cancel Seats not yet fully implemented. This is a placeholder for future development.");
    }
}
