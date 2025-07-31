package dao;

import model.Seat;
import util.DBUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SeatDAO {

    // Remove the 'private Connection conn;' field and its initialization from the constructor.
    // Each method will get its own connection via DBUtil.getConnection() in a try-with-resources block.
    public SeatDAO() {
        // No explicit connection needed here. Methods will get connections as they operate.
        // The check was useful for debugging, but now it's handled per operation.
    }

    public List<Seat> getSeatsByShowTimeId(int showTimeId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT id, seat_number, is_booked, showtime_id FROM seat WHERE showtime_id = ?";
        try (Connection currentConn = DBUtil.getConnection(); // Use a fresh connection
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, showTimeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String seatNumber = rs.getString("seat_number");
                    boolean booked = rs.getInt("is_booked") == 1;
                    int seatShowtimeId = rs.getInt("showtime_id");
                    seats.add(new Seat(id, seatNumber, booked, seatShowtimeId));
                }
            }
        } catch (SQLException e) {
            System.err.println("SeatDAO: Error getting seats by ShowTime ID: " + e.getMessage());
            e.printStackTrace();
        }
        return seats;
    }

    // REMOVED: public static Seat getSeatFromRowCol(int row, int col)
    // This method is not compatible with the current Seat model and database schema.
    // If you need to convert row/column integers to seat numbers (e.g., A1, B2),
    // that logic should be in the UI layer or a separate utility method that
    // returns a String seat number, which can then be used to create a Seat object
    // using new Seat(String seatNumber).

    // Method to add a batch of seats to a showtime
    public void addSeatsToShowTime(int showTimeId, List<Seat> seats) {
        String sql = "INSERT INTO seat (showtime_id, seat_number, is_booked) VALUES (?, ?, ?)";
        try (Connection currentConn = DBUtil.getConnection(); // Use a fresh connection
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            for (Seat seat : seats) {
                stmt.setInt(1, showTimeId);
                stmt.setString(2, seat.getSeatNumber());
                stmt.setInt(3, seat.isBooked() ? 1 : 0);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("SeatDAO: Add seats error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to update the booking status of a specific seat
    public boolean updateSeatStatus(int seatId, boolean isBooked) {
        String sql = "UPDATE seat SET is_booked = ? WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection(); // Use a fresh connection
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, isBooked ? 1 : 0);
            stmt.setInt(2, seatId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("SeatDAO: Error updating seat status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}