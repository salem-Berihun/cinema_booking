package service;

import dao.BookingDAO;
import dao.ShowTimeDAO;
import exceptions.BookingException; 
import interfaces.Bookable; 
import model.Booking;
import model.Customer; 
import model.Seat;
import model.ShowTime;
import util.FileLogger; 

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class BookingService implements Bookable { 

    private BookingDAO bookingDAO;
    private ShowTimeDAO showTimeDAO;
    private FileLogger fileLogger; 

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.showTimeDAO = new ShowTimeDAO();
        this.fileLogger = new FileLogger();
    }


    @Override
    public boolean bookSeats(Customer customer, ShowTime showTime, List<Seat> seatsToBook) throws BookingException {
        
        if (customer == null) {
            fileLogger.logError("BookingService: Customer object is null during booking attempt.");
            throw new IllegalArgumentException("Customer object cannot be null.");
        }
        System.out.println("BookingService: Attempting to book for customer: " + customer.getFullName() + " (ID: " + customer.getId() + ")");


        if (showTime == null || seatsToBook == null || seatsToBook.isEmpty()) {
            throw new IllegalArgumentException("ShowTime, and seats to book cannot be null or empty.");
        }

        
        for (Seat requestedSeat : seatsToBook) {
    
            Seat actualSeat = showTime.getSeat(requestedSeat.getSeatNumber());
            if (actualSeat == null) {
                fileLogger.logError("BookingService: Requested seat '" + requestedSeat.getSeatNumber() + "' not found for showtime ID " + showTime.getId());
                throw new IllegalStateException("Seat '" + requestedSeat.getSeatNumber() + "' not found for this showtime.");
            }
            if (actualSeat.isBooked()) {
                fileLogger.logError("BookingService: Seat '" + requestedSeat.getSeatNumber() + "' already booked for showtime ID " + showTime.getId());
                throw new IllegalStateException("Seat '" + requestedSeat.getSeatNumber() + "' is already booked.");
            }
            
        
            requestedSeat.setId(actualSeat.getId());
        }

        
    
        double totalPrice = seatsToBook.size() * 10.0; 

        
        
        Booking newBooking = new Booking(0, customer, showTime, seatsToBook, LocalDateTime.now(), totalPrice);

        try {
            
            boolean bookingSaved = bookingDAO.saveBooking(newBooking);

            if (bookingSaved) {
                
                for (Seat seat : seatsToBook) {
                    
                    showTimeDAO.updateSeatStatus(seat.getId(), true);
                }
                
                showTimeDAO.updateShowtimeAvailableSeats(showTime.getId(), showTime.getAvailableSeatsCount() - seatsToBook.size());

                fileLogger.logInfo("BookingService: Booking successful for customer " + customer.getFullName() + " (ID: " + newBooking.getId() + ")");
                return true;
            } else {
                fileLogger.logError("BookingService: Failed to save booking to database for showtime ID " + showTime.getId());
                throw new BookingException("Failed to save booking to database.");
            }
        } catch (BookingException e) {
            fileLogger.logError("BookingService: Booking failed: " + e.getMessage());
            throw e; 
        } catch (IllegalArgumentException | IllegalStateException e) { 
            System.err.println("Booking Validation Error: " + e.getMessage());
            throw e; 
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
            
            requestedSeat.setId(actualSeat.getId());
        }


        fileLogger.logInfo("BookingService: Attempting to cancel seats for showtime ID " + showTime.getId() + ". This feature is not fully implemented yet.");
        throw new UnsupportedOperationException("Cancel Seats not yet fully implemented. This is a placeholder for future development.");
    }
}
