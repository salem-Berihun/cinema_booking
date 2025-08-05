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

    public SeatDAO() {
        // Constructor no longer needs to get a connection, as methods will get them.
    }


    public List<Seat> getSeatsByShowTimeId(int showTimeId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT id, seat_number, is_booked, showtime_id FROM seat WHERE showtime_id = ?";
        try (Connection currentConn = DBUtil.getConnection(); // Get a fresh connection
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, showTimeId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String seatNumber = rs.getString("seat_number");
                    boolean booked = rs.getInt("is_booked") == 1; // SQLite stores boolean as INTEGER (0 or 1)
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


    public void addSeatsToShowTime(Connection conn, int showTimeId, List<Seat> seats) throws SQLException { // NEW: Accepts Connection
        // This comment is added to force a recompile if the file hasn't been recognized as changed.
        String sql = "INSERT INTO seat (showtime_id, seat_number, is_booked) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) { // Use the provided connection
            for (Seat seat : seats) {
                stmt.setInt(1, showTimeId);
                stmt.setString(2, seat.getSeatNumber());
                stmt.setInt(3, seat.isBooked() ? 1 : 0); // Convert boolean to int (1 for true, 0 for false)
                stmt.addBatch(); // Add to batch for efficient bulk insertion
            }
            stmt.executeBatch(); // Execute all batched inserts
        }
        // No catch block here, as SQLException should be handled by the calling transactional method (ShowTimeDAO)
    }


    public boolean updateSeatStatus(int seatId, boolean isBooked) {
        String sql = "UPDATE seat SET is_booked = ? WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection(); // Get a fresh connection
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, isBooked ? 1 : 0); // Convert boolean to int
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

