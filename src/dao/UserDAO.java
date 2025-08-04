package dao;

import model.Customer; // Import our new Customer class
import model.Manager;  // Import our new Manager class
import model.User;     // Import the abstract User class
import util.DBUtil;
import exceptions.UserAuthException; // Import custom exception

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UserDAO {

    public User loginUser(String email, String password) throws UserAuthException { // Declare throws clause
        String sql = "SELECT id, name, email, password, role FROM user WHERE email = ? AND password = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.setString(2, password);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String userEmail = rs.getString("email");
                    String userPassword = rs.getString("password");
                    String role = rs.getString("role");

                    if ("customer".equalsIgnoreCase(role)) {
                        return new Customer(id, name, userEmail, userPassword, role);
                    } else if ("manager".equalsIgnoreCase(role)) {
                        return new Manager(id, name, userEmail, userPassword, role);
                    } else {
                        // Handle unexpected roles
                        throw new UserAuthException("Login Failed: Unknown user role encountered.");
                    }
                } else {
                    // No user found with provided credentials
                    throw new UserAuthException("Login Failed: Invalid email or password.");
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO Login Error: " + e.getMessage());
            e.printStackTrace();
            // Wrap SQLException in custom exception for higher-level handling
            throw new UserAuthException("Database error during login.", e);
        }
    }

    public boolean registerUser(String name, String email, String password, String role) {
        String sql = "INSERT INTO user (name, email, password, role) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, password);
            stmt.setString(4, role); // Save the role in the database
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.err.println("UserDAO Register Error: " + e.getMessage());
            e.printStackTrace();
            // In a real app, you might throw a custom RegistrationException here
            return false;
        }
    }

    public User getUserById(int id) {
        String sql = "SELECT id, name, email, password, role FROM user WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int userId = rs.getInt("id");
                    String name = rs.getString("name");
                    String userEmail = rs.getString("email");
                    String userPassword = rs.getString("password");
                    String role = rs.getString("role");

                    if ("customer".equalsIgnoreCase(role)) {
                        return new Customer(userId, name, userEmail, userPassword, role);
                    } else if ("manager".equalsIgnoreCase(role)) {
                        return new Manager(userId, name, userEmail, userPassword, role);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("UserDAO Get User By ID Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
