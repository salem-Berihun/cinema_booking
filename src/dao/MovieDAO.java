// In dao/MovieDAO.java
package dao; // Ensure this matches your actual package

import model.Movie;
import util.DBUtil; // Assuming DBUtil is your connection utility
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MovieDAO {

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
    // ... potentially other methods like insertMovie, updateMovie, deleteMovie
}