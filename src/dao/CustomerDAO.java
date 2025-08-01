package dao;
import model.Customer;
import model.User; // Import User model if Customer extends User or is related

import java.sql.*;
import util.DBUtil;

public class CustomerDAO {

    public Customer getCustomerById(int id) {
        String sql = "SELECT id, name, email, password, role FROM user WHERE id = ? AND role = 'customer'";

        // CORRECTED: Use try-with-resources for the Connection object as well
        try (Connection conn = DBUtil.getConnection(); // Get a new connection for this operation
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"), // Use 'name' as per DB schema
                        rs.getString("email"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer: " + e.getMessage());
            e.printStackTrace(); // Always print stack trace for detailed debugging
        }
        return null;
    }
}
