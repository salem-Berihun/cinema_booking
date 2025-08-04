package interfaces;
import model.Customer; // NEW: Import Customer model
import model.ShowTime;
import model.Seat;

import java.util.List;

public interface Bookable {

    boolean bookSeats(Customer customer, ShowTime showTime, List<Seat> seatsToBook) throws exceptions.BookingException; // UPDATED SIGNATURE


    boolean cancelSeats(ShowTime showTime, List<Seat> seatsToCancel) throws exceptions.BookingException;
}
