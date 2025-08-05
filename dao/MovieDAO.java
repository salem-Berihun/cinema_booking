package dao; // Ensure this matches your actual package

import model.Movie;
import util.DBUtil; // Assuming DBUtil is your connection utility
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList; // Added for getAllMovies
import java.util.List;     // Added for getAllMovies
import java.sql.Statement; // Added for getAllMovies and addMovie generated keys

public class MovieDAO {

    public MovieDAO() {
        // Constructor no longer needs to get a connection
    }

    // Add Movie and return its id
    public int addMovie(String title, String description, int durationMinutes, String genre) {
        String sql = "INSERT INTO movies (title, description, duration_minutes, genre) VALUES (?, ?, ?, ?)";
        try (Connection currentConn = DBUtil.getConnection();
             PreparedStatement stmt = currentConn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
            stmt.setString(2, description);
            stmt.setInt(3, durationMinutes);
            stmt.setString(4, genre);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            System.err.println("MovieDAO: Add movie error: " + e.getMessage());
            e.printStackTrace();
        }
        return -1; // Return -1 on failure
    }

    public Movie getMovieById(int id) {
        String query = "SELECT id, title, description, duration_minutes, genre FROM movies WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Movie(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("description"),
                            rs.getInt("duration_minutes"),
                            rs.getString("genre")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("MovieDAO: Error retrieving movie by ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null; // Return null if movie not found
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
                String description = rs.getString("description");
                int durationMinutes = rs.getInt("duration_minutes");
                String genre = rs.getString("genre");
                movies.add(new Movie(id, title, description, durationMinutes, genre));
            }
        } catch (SQLException e) {
            System.err.println("MovieDAO: Get all movies error: " + e.getMessage());
            e.printStackTrace();
        }
        return movies;
    }

    // You can add other CRUD operations here if needed, e.g., updateMovie, deleteMovie
}
