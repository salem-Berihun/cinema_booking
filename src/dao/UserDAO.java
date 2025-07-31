package dao;

import model.User; // Assuming you have a User model
import util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // For Statement.RETURN_GENERATED_KEYS

public class UserDAO {

    public User loginUser(String email, String password) {
        // Corrected: Select 'name' column, not 'full_name'
        String sql = "SELECT id, name, email, password, role FROM user WHERE email = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection(); // Get a new connection for this operation
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Corrected: Use 'name' from ResultSet, and 'role' for userType
                    return new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO Login Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public boolean registerUser(String name, String email, String password, String role) {
        // Corrected: Insert into 'name' column, not 'full_name'
        String sql = "INSERT INTO user (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection(); // Get a new connection for this operation
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name); // Use 'name' here
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO Register Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Method to get a generic User by ID (useful for CustomerDAO and other DAOs)
    public User getUserById(int id) {
        String sql = "SELECT id, name, email, password, role FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new User(rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("password"), rs.getString("role"));
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO Get User By ID Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
