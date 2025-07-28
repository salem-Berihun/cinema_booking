import java.sql.*;

public class CustomerDAO {

    public static Customer getCustomerById(int id) {
        String sql = "SELECT * FROM user WHERE id = ? AND role = 'customer'";

        try (PreparedStatement stmt = DBUtil.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Customer(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }

        } catch (SQLException e) {
            System.err.println("Error fetching customer: " + e.getMessage());
        }
        return null;
    }
}
