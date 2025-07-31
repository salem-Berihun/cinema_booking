package interfaces;
import model.ShowTime;
import model.Seat;

import java.util.List; // For list of seats

public interface Bookable {
    /**
     * Attempts to book a list of seats for a given showtime.
     * @param showTime The ShowTime object for which seats are to be booked.
     * @param seatsToBook A list of Seat objects that the user wishes to book.
     * @return true if all seats were successfully booked, false otherwise.
     * @throws IllegalArgumentException if showTime or seatsToBook is null or empty.
     * @throws IllegalStateException if any of the requested seats are already booked.
     */
    boolean bookSeats(ShowTime showTime, List<Seat> seatsToBook);

    /**
     * Attempts to cancel a list of previously booked seats for a given showtime.
     * @param showTime The ShowTime object for which seats are to be cancelled.
     * @param seatsToCancel A list of Seat objects that are to be cancelled.
     * @return true if all seats were successfully cancelled, false otherwise.
     * @throws IllegalArgumentException if showTime or seatsToCancel is null or empty.
     * @throws IllegalStateException if any of the requested seats are not currently booked.
     */
    boolean cancelSeats(ShowTime showTime, List<Seat> seatsToCancel);
}