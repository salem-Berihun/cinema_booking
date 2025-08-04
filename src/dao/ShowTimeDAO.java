package dao;

import util.DBUtil;
import model.Seat;
import model.ShowTime;
import model.Movie; // Import Movie model

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // Import Map

public class ShowTimeDAO {

    private SeatDAO seatDAO; // Use SeatDAO for seat operations
    private MovieDAO movieDAO; // NEW: Use MovieDAO for movie operations

    public ShowTimeDAO() {
        this.seatDAO = new SeatDAO(); // Initialize SeatDAO
        this.movieDAO = new MovieDAO(); // NEW: Initialize MovieDAO
    }

    // REMOVED: addMovie method (now in MovieDAO)
    // REMOVED: getMovieById method (now in MovieDAO)
    // REMOVED: getAllMovies method (now in MovieDAO)


    public int addShowTime(int movieId, LocalDateTime dateTime, String hall, int availableSeats, double price) {
        String sql = "INSERT INTO showtime (movie_id, date_time, hall, available_seats, price) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null; // Declare connection for transaction
        int showtimeId = -1;
        try {
            conn = DBUtil.getConnection(); // Get a NEW connection for this transaction
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert the showtime record
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, movieId);
                stmt.setString(2, dateTime.toString());
                stmt.setString(3, hall);
                stmt.setInt(4, availableSeats); // This correctly inserts the availableSeats count into the showtime table
                stmt.setDouble(5, price);
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    showtimeId = keys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve showtime ID after insertion.");
                }
            }

            // 2. Generate and insert individual seat records for the new showtime using SeatDAO
            if (showtimeId != -1 && availableSeats > 0) {
                List<Seat> newSeats = new ArrayList<>();
                int seatsGenerated = 0;
                // CRITICAL FIX: Generate seats based on the 'availableSeats' parameter
                for (char rowChar = 'A'; rowChar <= 'Z' && seatsGenerated < availableSeats; rowChar++) {
                    for (int col = 1; col <= 10 && seatsGenerated < availableSeats; col++) {
                        String seatNumber = String.valueOf(rowChar) + col;
                        newSeats.add(new Seat(0, seatNumber, false, showtimeId));
                        seatsGenerated++;
                    }
                }
                // If availableSeats is greater than 260 (26 rows * 10 columns), this loop will stop at Z10.
                // You might want to adjust the seat numbering logic for very large halls.

                seatDAO.addSeatsToShowTime(conn, showtimeId, newSeats); // Use SeatDAO to add seats
            }

            conn.commit(); // Commit the transaction if both operations succeed
            return showtimeId;

        } catch (SQLException e) {
            System.err.println("ShowTimeDAO: Add showtime error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("ShowTimeDAO: Rollback failed: " + rollbackEx.getMessage());
                }
            }
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close(); // Close the connection
                } catch (SQLException ex) {
                    System.err.println("ShowTimeDAO: Failed to close connection after addShowTime: " + ex.getMessage());
                }
            }
        }
    }


    public ShowTime getShowTimeById(int id) {
        String sql = "SELECT * FROM showtime WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int movieId = rs.getInt("movie_id");
                Movie movie = movieDAO.getMovieById(movieId); // NEW: Use MovieDAO to get movie details
                LocalDateTime dateTime = LocalDateTime.parse(rs.getString("date_time"));
                String hall = rs.getString("hall");

                // Fetch individual seats using SeatDAO
                List<Seat> seats = seatDAO.getSeatsByShowTimeId(id);

                return new ShowTime(id, movie, dateTime, hall, seats); // Pass the list of seats
            }
        } catch (SQLException e) {
            System.err.println("ShowTimeDAO: Get showtime error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public List<ShowTime> getAllShowTimes() {
        List<ShowTime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtime";

        try (Connection currentConn = DBUtil.getConnection();
             Statement stmt = currentConn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                ShowTime st = getShowTimeById(id); // getShowTimeById already fetches all details including seats
                if (st != null) {
                    list.add(st);
                } else {
                    System.err.println("ShowTimeDAO: getShowTimeById returned null for ID: " + id + ". Data inconsistency detected.");
                }
            }
        } catch (SQLException e) {
            System.err.println("ShowTimeDAO: Get all showtimes error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }


    public boolean updateSeatStatus(int seatId, boolean isBooked) {
        return seatDAO.updateSeatStatus(seatId, isBooked);
    }


    public boolean updateShowtimeAvailableSeats(int showtimeId, int newAvailableSeats) {
        String sql = "UPDATE showtime SET available_seats = ? WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, newAvailableSeats);
            stmt.setInt(2, showtimeId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("ShowTimeDAO: Error updating showtime available seats: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public boolean deleteShowtime(int showtimeId) {
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Delete associated booking_seats first (if any)
            String deleteBookingSeatsSql = "DELETE FROM booking_seats WHERE booking_id IN (SELECT id FROM bookings WHERE showtime_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(deleteBookingSeatsSql)) {
                ps.setInt(1, showtimeId);
                ps.executeUpdate();
            }

            // 2. Delete associated bookings
            String deleteBookingsSql = "DELETE FROM bookings WHERE showtime_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteBookingsSql)) {
                ps.setInt(1, showtimeId);
                ps.executeUpdate();
            }

            // 3. Delete associated seats
            String deleteSeatsSql = "DELETE FROM seat WHERE showtime_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSeatsSql)) {
                ps.setInt(1, showtimeId);
                ps.executeUpdate();
            }

            // 4. Delete the showtime itself
            String deleteShowtimeSql = "DELETE FROM showtime WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteShowtimeSql)) {
                ps.setInt(1, showtimeId);
                int affectedRows = ps.executeUpdate();
                if (affectedRows > 0) {
                    conn.commit(); // Commit transaction if showtime deleted
                    return true;
                }
            }
            conn.rollback(); // Rollback if showtime not found/deleted (affectedRows was 0)
            return false;

        } catch (SQLException e) {
            System.err.println("ShowTimeDAO: Error deleting showtime ID " + showtimeId + ": " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("ShowTimeDAO: Rollback failed during deleteShowtime: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close(); // Close the connection
                } catch (SQLException ex) {
                    System.err.println("ShowTimeDAO: Failed to close connection after deleteShowtime: " + ex.getMessage());
                }
            }
        }
    }


    public int deletePastShowtimes() {
        int deletedCount = 0;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Get current time for comparison
            String now = LocalDateTime.now().toString();

            // 1. Get IDs of past showtimes
            List<Integer> pastShowtimeIds = new ArrayList<>();
            String selectPastShowtimesSql = "SELECT id FROM showtime WHERE date_time < ?";
            try (PreparedStatement ps = conn.prepareStatement(selectPastShowtimesSql)) {
                ps.setString(1, now);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    pastShowtimeIds.add(rs.getInt("id"));
                }
            }

            if (pastShowtimeIds.isEmpty()) {
                System.out.println("ShowTimeDAO: No past showtimes found to delete.");
                return 0;
            }

            // 2. Delete associated booking_seats for past showtimes
            String deleteBookingSeatsSql = "DELETE FROM booking_seats WHERE booking_id IN (SELECT id FROM bookings WHERE showtime_id = ?)";
            try (PreparedStatement ps = conn.prepareStatement(deleteBookingSeatsSql)) {
                for (int id : pastShowtimeIds) {
                    ps.setInt(1, id);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 3. Delete associated bookings for past showtimes
            String deleteBookingsSql = "DELETE FROM bookings WHERE showtime_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteBookingsSql)) {
                for (int id : pastShowtimeIds) {
                    ps.setInt(1, id);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 4. Delete associated seats for past showtimes
            String deleteSeatsSql = "DELETE FROM seat WHERE showtime_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteSeatsSql)) {
                for (int id : pastShowtimeIds) {
                    ps.setInt(1, id);
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 5. Delete the past showtimes themselves
            String deleteShowtimesSql = "DELETE FROM showtime WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteShowtimesSql)) {
                for (int id : pastShowtimeIds) {
                    ps.setInt(1, id);
                    ps.addBatch();
                }
                int[] results = ps.executeBatch();
                for (int result : results) {
                    deletedCount += result;
                }
            }

            conn.commit();
            System.out.println("ShowTimeDAO: Successfully deleted " + deletedCount + " past showtimes.");
            return deletedCount;

        } catch (SQLException e) {
            System.err.println("ShowTimeDAO: Error deleting past showtimes: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("ShowTimeDAO: Rollback failed during deletePastShowtimes: " + rollbackEx.getMessage());
                }
            }
            return 0;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Failed to close connection after deletePastShowtimes: " + ex.getMessage());
                }
            }
        }
    }


    public int deleteMoviesWithoutShowtimes() {
        int deletedCount = 0;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            String deleteMoviesSql = "DELETE FROM movies WHERE id NOT IN (SELECT DISTINCT movie_id FROM showtime)";
            try (PreparedStatement ps = conn.prepareStatement(deleteMoviesSql)) {
                deletedCount = ps.executeUpdate();
            }

            conn.commit();
            System.out.println("ShowTimeDAO: Successfully deleted " + deletedCount + " movies without showtimes.");
            return deletedCount;

        } catch (SQLException e) {
            System.err.println("ShowTimeDAO: Error deleting movies without showtimes: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("ShowTimeDAO: Rollback failed during deleteMoviesWithoutShowtimes: " + rollbackEx.getMessage());
                }
            }
            return 0;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException ex) {
                    System.err.println("Failed to close connection after deleteMoviesWithoutShowtimes: " + ex.getMessage());
                }
            }
        }
    }
}
