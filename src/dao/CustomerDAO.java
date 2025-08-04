package dao;
import model.Customer;
import model.User; // Import User model if Customer extends User or is related

import java.sql.*;
import util.DBUtil;

public class CustomerDAO {

    public Customer getCustomerById(int id) {
        // Corrected to select 'name' column, not 'full_name'
        String sql = "SELECT id, name, email, password, role FROM user WHERE id = ? AND role = 'customer'";

        try (Connection conn = DBUtil.getConnection(); // Get connection per method call
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // Corrected to use 'name' column from DB and pass 'role'
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"), // Use 'name' from DB
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("role") // Pass the role as well
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for more details
        }
        return null;
    }
}
