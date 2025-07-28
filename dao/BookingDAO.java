import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO {
    private Connection conn;
    private CustomerDAO customerDAO;
    private ShowTimeDAO showTimeDAO;

    public BookingDAO() {
        try {
            conn = DBUtil.getConnection();
            createTablesIfNotExist();
            customerDAO = new CustomerDAO();
            showTimeDAO = new ShowTimeDAO();
        } catch (SQLException e) {
            System.err.println("DB Connection Error: " + e.getMessage());
        }
    }

    private void createTablesIfNotExist() throws SQLException {
        String bookingsTable = """
            CREATE TABLE IF NOT EXISTS bookings (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                customer_id INTEGER,
                showtime_id INTEGER,
                booking_time TEXT,
                total_price REAL
            );
        """;

        String seatsTable = """
            CREATE TABLE IF NOT EXISTS booking_seats (
                booking_id INTEGER,
                seat_number TEXT
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(bookingsTable);
            stmt.execute(seatsTable);
        }
    }

    public boolean saveBooking(Booking booking) {
        String insertBooking = "INSERT INTO bookings (customer_id, showtime_id, booking_time, total_price) VALUES (?, ?, ?, ?)";
        String insertSeat = "INSERT INTO booking_seats (booking_id, seat_number) VALUES (?, ?)";

        try {
            conn.setAutoCommit(false);

            // Insert booking
            try (PreparedStatement bookingStmt = conn.prepareStatement(insertBooking, Statement.RETURN_GENERATED_KEYS)) {
                bookingStmt.setInt(1, booking.getCustomer().getId());
                bookingStmt.setInt(2, booking.getShowTime().getId());
                bookingStmt.setString(3, booking.getBookingTime().toString());
                bookingStmt.setDouble(4, booking.getTotalPrice());
                int affectedRows = bookingStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Creating booking failed, no rows affected.");
                }

                try (ResultSet generatedKeys = bookingStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        booking.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating booking failed, no ID obtained.");
                    }
                }
            }

            // Insert booking seats
            try (PreparedStatement seatStmt = conn.prepareStatement(insertSeat)) {
                for (Seat seat : booking.getSelectedSeats()) {
                    seatStmt.setInt(1, booking.getId());
                    seatStmt.setString(2, seat.getSeatNumber());
                    seatStmt.addBatch();
                }
                seatStmt.executeBatch();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.err.println("Booking Save Error: " + e.getMessage());
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            return false;
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException ex) {
                System.err.println("Failed to reset auto-commit: " + ex.getMessage());
            }
        }
    }

    public List<Booking> getAllBookings() {
        List<Booking> bookings = new ArrayList<>();

        String selectBookings = "SELECT * FROM bookings";
        String selectSeats = "SELECT seat_number FROM booking_seats WHERE booking_id = ?";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(selectBookings)) {

            while (rs.next()) {
                int bookingId = rs.getInt("id");
                int customerId = rs.getInt("customer_id");
                int showtimeId = rs.getInt("showtime_id");
                LocalDateTime bookingTime = LocalDateTime.parse(rs.getString("booking_time"));
                double totalPrice = rs.getDouble("total_price");

                // Get customer and showtime using DAOs
                Customer customer = customerDAO.getCustomerById(customerId);
                ShowTime showTime = showTimeDAO.getShowTimeById(showtimeId);

                // Get seats booked in this booking
                List<Seat> seatList = new ArrayList<>();
                try (PreparedStatement seatStmt = conn.prepareStatement(selectSeats)) {
                    seatStmt.setInt(1, bookingId);
                    try (ResultSet seatRS = seatStmt.executeQuery()) {
                        while (seatRS.next()) {
                            String seatNumber = seatRS.getString("seat_number");
                            // We do not have seat id or booking status here, use 0 or false as default
                            seatList.add(new Seat(0, seatNumber, true, showtimeId));
                        }
                    }
                }

                Booking booking = new Booking(bookingId, customer, showTime, seatList, bookingTime, totalPrice);
                bookings.add(booking);
            }

        } catch (SQLException e) {
            System.err.println("Error loading bookings: " + e.getMessage());
        }

        return bookings;
    }
}
