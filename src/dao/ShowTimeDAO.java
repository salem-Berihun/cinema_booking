package dao;

import util.DBUtil;
import model.Seat;
import model.ShowTime;
import model.Movie; // Import Movie model

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowTimeDAO {

    public ShowTimeDAO() {
        // Constructor no longer needs to get a connection
    }

    // Add Movie and return its id
    public int addMovie(String title,String description,int durationMinutes, String genre) {
        String sql = "INSERT INTO movies (title,description,duration_minutes, genre) VALUES (?, ?, ?, ?)";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, durationMinutes);
            stmt.setString(4, genre);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("Add movie error: " + e.getMessage());
        }
        return -1;
    }

    // Get Movie by id
    public Movie getMovieById(int id) {
        String sql = "SELECT * FROM movies WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int movieId = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description");
                int durationMinutes = rs.getInt("duration_minutes");
                String genre = rs.getString("genre");
                return new Movie(movieId, title, description, durationMinutes, genre);
            }
        } catch (SQLException e) {
            System.err.println("Get movie error: " + e.getMessage());
        }
        return null;
    }

    // Get all Movies
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();
        String sql = "SELECT * FROM movies";
        try (Connection currentConn = DBUtil.getConnection();
             Statement stmt = currentConn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String description = rs.getString("description"); // Corrected: Should be "description" not "String"
                int durationMinutes = rs.getInt("duration_minutes");
                String genre = rs.getString("genre");
                movies.add(new Movie(id, title, description, durationMinutes, genre));
            }
        } catch (SQLException e) {
            System.err.println("Get all movies error: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }


    // Add ShowTime (with hall, available_seats, and price)
    public int addShowTime(int movieId, LocalDateTime dateTime, String hall, int availableSeats, double price) {
        String sql = "INSERT INTO showtime (movie_id, date_time, hall, available_seats, price) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null; // Declare connection for transaction
        int showtimeId = -1;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // Start transaction

            // 1. Insert the showtime record
            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, movieId);
                stmt.setString(2, dateTime.toString());
                stmt.setString(3, hall);
                stmt.setInt(4, availableSeats);
                stmt.setDouble(5, price);
                stmt.executeUpdate();
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    showtimeId = keys.getInt(1);
                } else {
                    throw new SQLException("Failed to retrieve showtime ID after insertion.");
                }
            }

            // 2. Generate and insert individual seat records for the new showtime
            if (showtimeId != -1 && availableSeats > 0) {
                List<Seat> newSeats = new ArrayList<>();
                for (int i = 1; i <= availableSeats; i++) {
                    // Simple sequential seat numbering for now (e.g., "Seat 1", "Seat 2")
                    // If you need A1, B2 etc., a more complex loop would be needed.
                    newSeats.add(new Seat(0, "Seat " + i, false, showtimeId)); // ID 0 as it's new, false for not booked
                }
                // Use the same connection for addSeatsToShowTime within the transaction
                String insertSeatSql = "INSERT INTO seat (showtime_id, seat_number, is_booked) VALUES (?, ?, ?)";
                try (PreparedStatement seatStmt = conn.prepareStatement(insertSeatSql)) {
                    for (Seat seat : newSeats) {
                        seatStmt.setInt(1, showtimeId);
                        seatStmt.setString(2, seat.getSeatNumber());
                        seatStmt.setInt(3, seat.isBooked() ? 1 : 0);
                        seatStmt.addBatch();
                    }
                    seatStmt.executeBatch();
                }
            }

            conn.commit(); // Commit the transaction if both operations succeed
            return showtimeId;

        } catch (SQLException e) {
            System.err.println("Add showtime error: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // Rollback on error
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            return -1;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close(); // Close the connection
                } catch (SQLException ex) {
                    System.err.println("Failed to close connection after addShowTime: " + ex.getMessage());
                }
            }
        }
    }

    // Get ShowTime by id (including seats)
    public ShowTime getShowTimeById(int id) {
        String sql = "SELECT * FROM showtime WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int movieId = rs.getInt("movie_id");
                Movie movie = getMovieById(movieId);
                LocalDateTime dateTime = LocalDateTime.parse(rs.getString("date_time"));
                String hall = rs.getString("hall");
                // int availableSeats = rs.getInt("available_seats"); // This is the count, not individual seats
                // double price = rs.getDouble("price");

                List<Seat> seats = getSeatsByShowTimeId(id); // Fetch individual seats

                return new ShowTime(id, movie, dateTime, hall, seats);
            }
        } catch (SQLException e) {
            System.err.println("Get showtime error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    // Get all ShowTimes
    public List<ShowTime> getAllShowTimes() {
        List<ShowTime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtime";

        System.out.println("ShowTimeDAO: Attempting to retrieve all showtimes...");
        try (Connection currentConn = DBUtil.getConnection();
             Statement stmt = currentConn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                System.out.println("ShowTimeDAO: Found showtime ID: " + id);
                ShowTime st = getShowTimeById(id); // getShowTimeById already fetches all details
                if (st != null) {
                    list.add(st);
                    System.out.println("ShowTimeDAO: Added showtime to list: " + st.getMovie().getTitle() + " at " + st.getDateTime());
                } else {
                    System.out.println("ShowTimeDAO: getShowTimeById returned null for ID: " + id);
                }
            }
            System.out.println("ShowTimeDAO: Finished retrieving showtimes. Total found: " + list.size());
        } catch (SQLException e) {
            System.err.println("Get all showtimes error: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    // Add Seat(s) to a ShowTime (now used internally by addShowTime)
    public void addSeatsToShowTime(int showTimeId, List<Seat> seats) {
        String sql = "INSERT INTO seat (showtime_id, seat_number, is_booked) VALUES (?, ?, ?)";
        // This method is now called internally from addShowTime, which manages the connection.
        // So, we pass the connection to it, or ensure it gets its own if called externally.
        // For simplicity, let's assume it gets its own connection if called directly,
        // but within addShowTime, we'll pass the transaction's connection.
        // For now, I'll keep it as it is, assuming addShowTime's transaction handles it.
        try (Connection currentConn = DBUtil.getConnection(); // Gets a new connection if called externally
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            for (Seat seat : seats) {
                stmt.setInt(1, showTimeId);
                stmt.setString(2, seat.getSeatNumber());
                stmt.setInt(3, seat.isBooked() ? 1 : 0);
                stmt.addBatch();
            }
            stmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Add seats error: " + e.getMessage());
        }
    }

    // Get seats by ShowTime ID
    public List<Seat> getSeatsByShowTimeId(int showTimeId) {
        List<Seat> seats = new ArrayList<>();
        String sql = "SELECT * FROM seat WHERE showtime_id = ?";
        try (Connection currentConn = DBUtil.getConnection(); // Get connection here
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, showTimeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String seatNumber = rs.getString("seat_number");
                boolean booked = rs.getInt("is_booked") == 1;
                int seatShowtimeId = rs.getInt("showtime_id");
                seats.add(new Seat(id, seatNumber, booked,seatShowtimeId));
            }
        } catch (SQLException e) {
            System.err.println("Get seats error: " + e.getMessage());
        }
        return seats;
    }

    // Method to update a seat's booked status
    public boolean updateSeatStatus(int seatId, boolean isBooked) {
        String sql = "UPDATE seat SET is_booked = ? WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, isBooked ? 1 : 0);
            stmt.setInt(2, seatId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating seat status: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to update available seats count for a showtime
    public boolean updateShowtimeAvailableSeats(int showtimeId, int newAvailableSeats) {
        String sql = "UPDATE showtime SET available_seats = ? WHERE id = ?";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql)) {
            stmt.setInt(1, newAvailableSeats);
            stmt.setInt(2, showtimeId);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("Error updating showtime available seats: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to delete a showtime and its associated data
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
            conn.rollback(); // Rollback if showtime not found/deleted
            return false;

        } catch (SQLException e) {
            System.err.println("Error deleting showtime ID " + showtimeId + ": " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); // Reset auto-commit
                    conn.close(); // Close the connection
                } catch (SQLException ex) {
                    System.err.println("Failed to close connection after deleteShowtime: " + ex.getMessage());
                }
            }
        }
    }

    // Method to delete past showtimes
    public int deletePastShowtimes() {
        int deletedCount = 0;
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false);

            // Get current time
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
                System.out.println("No past showtimes found to delete.");
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
            return deletedCount;

        } catch (SQLException e) {
            System.err.println("Error deleting past showtimes: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
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

    // Method to delete movies that no longer have any showtimes
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
            return deletedCount;

        } catch (SQLException e) {
            System.err.println("Error deleting movies without showtimes: " + e.getMessage());
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackEx) {
                    System.err.println("Rollback failed: " + rollbackEx.getMessage());
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
