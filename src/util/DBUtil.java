package util;

import java.io.*;
import java.util.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DBUtil {
    // CRITICAL CHANGE: Removed 'private static Connection conn;'
    // Each call to getConnection() will now return a new, independent connection.

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static Connection getConnection() {
        Connection newConn = null; // Declare local connection variable
        try {
            Properties prop = new Properties();
            try (InputStream input = DBUtil.class.getResourceAsStream("/config/db_config.txt")) {
                if (input == null) {
                    System.err.println("Error: db_config.txt not found on classpath at /config/db_config.txt.");
                    System.err.println("Please ensure 'src' is marked as 'Sources Root' in IntelliJ's Project Structure,");
                    System.err.println("and that 'db_config.txt' is located directly inside the 'src/config' folder.");
                    throw new FileNotFoundException("Database configuration file not found on classpath.");
                }
                prop.load(input);
            }

            String dbUrl = prop.getProperty("db.url");
            if (dbUrl == null || dbUrl.isEmpty()) {
                System.err.println("Error: 'db.url' property not found or is empty in db_config.txt.");
                throw new SQLException("Database URL not configured.");
            }

            Class.forName("org.sqlite.JDBC");

            newConn = DriverManager.getConnection(dbUrl); // Create a new connection
            // System.out.println("Database connection established: " + dbUrl); // Can be noisy, keep for initial debug

        } catch (FileNotFoundException e) {
            System.err.println("Configuration file error: " + e.getMessage());
            e.printStackTrace();
            newConn = null;
        } catch (SQLException e) {
            System.err.println("SQL connection error: " + e.getMessage());
            e.printStackTrace();
            newConn = null;
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC Driver not found: " + e.getMessage());
            System.err.println("Please ensure the SQLite JDBC driver JAR is added to your project dependencies.");
            e.printStackTrace();
            newConn = null;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during database connection: " + e.getMessage());
            e.printStackTrace();
            newConn = null;
        }
        return newConn; // Return the new connection
    }

    // This method is now largely obsolete as connections are managed per-method in DAOs
    public static void closeConnection() {
        // With getConnection() returning new connections, DAOs are responsible for closing them.
        // This method can be removed from main.java calls.
        System.out.println("DBUtil: closeConnection() called. (Note: Connections are now managed per-DAO-method.)");
    }

    public static void initTable() {
        // Get a connection specifically for table initialization
        try (Connection currentConn = getConnection();
             Statement stmt = currentConn.createStatement()) {

            if (currentConn == null) {
                System.err.println("Cannot initialize tables: No database connection available.");
                return;
            }

            System.out.println("DBUtil: Forcing drop of all tables for schema refresh...");
            dropTabel(); // Call the drop method (note: still has typo 'Tabel')
            System.out.println("DBUtil: Tables dropped.");

            // user table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS user(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                );
            """);

            // Manager table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS manager(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    role TEXT NOT NULL
                );
            """);

            // movies table
            String createMoviesTableSql = """
                CREATE TABLE IF NOT EXISTS movies(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    title TEXT NOT NULL,
                    description TEXT,
                    duration_minutes INTEGER,
                    genre TEXT
                );
            """;
            System.out.println("DBUtil: Executing SQL for movies table:\n" + createMoviesTableSql);
            stmt.executeUpdate(createMoviesTableSql);

            // showtime table
            String createShowtimeTableSql = """
                CREATE TABLE IF NOT EXISTS showtime(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    movie_id INTEGER NOT NULL,
                    date_time TEXT NOT NULL,
                    hall TEXT NOT NULL,
                    available_seats INTEGER NOT NULL,
                    price REAL NOT NULL,
                    FOREIGN KEY (movie_id) REFERENCES movies(id)
                );
            """;
            System.out.println("DBUtil: Executing SQL for showtime table:\n" + createShowtimeTableSql);
            stmt.executeUpdate(createShowtimeTableSql);

            // seat table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS seat (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    showtime_id INTEGER,
                    seat_number TEXT,
                    is_booked INTEGER DEFAULT 0,
                    FOREIGN KEY(showtime_id) REFERENCES showtime(id)
                );
            """);

            // bookings table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS bookings(
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    customer_id INTEGER NOT NULL,
                    showtime_id INTEGER NOT NULL,
                    num_tickets INTEGER NOT NULL,
                    booking_date TEXT NOT NULL,
                    total_price REAL NOT NULL,
                    FOREIGN KEY (customer_id) REFERENCES user(id),
                    FOREIGN KEY (showtime_id) REFERENCES showtime(id)
                );
            """);

            // booking_seats table
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS booking_seats (
                    booking_id INTEGER,
                    seat_number TEXT,
                    FOREIGN KEY(booking_id) REFERENCES bookings(id)
                );
            """);

            System.out.println("Database Tables initialized!");
        } catch (SQLException e) {
            System.err.println("Error initializing tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void initData() {
        // Get a connection specifically for data initialization
        try (Connection currentConn = getConnection();
             Statement stmt = currentConn.createStatement()) {

            if (currentConn == null) {
                System.err.println("Cannot initialize data: No database connection available.");
                return;
            }

            ResultSet rsMovies = stmt.executeQuery("SELECT COUNT(*) FROM movies");
            if (rsMovies.next() && rsMovies.getInt(1) == 0) {
                System.out.println("Inserting sample movies and showtimes...");

                String insertMovieSql = "INSERT INTO movies (title, description, duration_minutes, genre) VALUES (?, ?, ?, ?)";
                int movieId1 = -1, movieId2 = -1, movieId3 = -1;

                try (PreparedStatement ps = currentConn.prepareStatement(insertMovieSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "The Great Adventure");
                    ps.setString(2, "An epic journey");
                    ps.setInt(3, 120);
                    ps.setString(4, "Action");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) movieId1 = keys.getInt(1);
                    }

                    ps.setString(1, "Mystery of the Old House");
                    ps.setString(2, "A thrilling detective story");
                    ps.setInt(3, 95);
                    ps.setString(4, "Mystery");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) movieId2 = keys.getInt(1);
                    }

                    ps.setString(1, "Comedy Night Live");
                    ps.setString(2, "Laugh out loud!");
                    ps.setInt(3, 80);
                    ps.setString(4, "Comedy");
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        if (keys.next()) movieId3 = keys.getInt(1);
                    }
                }

                String insertShowtimeSql = "INSERT INTO showtime (movie_id, date_time, hall, available_seats, price) VALUES (?, ?, ?, ?, ?)";
                int showtimeId1 = -1, showtimeId2 = -1, showtimeId3 = -1;

                try (PreparedStatement ps = currentConn.prepareStatement(insertShowtimeSql, Statement.RETURN_GENERATED_KEYS)) {
                    if (movieId1 != -1) {
                        ps.setInt(1, movieId1);
                        ps.setString(2, LocalDateTime.of(2025, 8, 1, 10, 0).toString());
                        ps.setString(3, "Hall 1");
                        ps.setInt(4, 50);
                        ps.setDouble(5, 10.50);
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) showtimeId1 = keys.getInt(1);
                        }
                    }

                    if (movieId3 != -1) {
                        ps.setInt(1, movieId3);
                        ps.setString(2, LocalDateTime.of(2025, 8, 1, 14, 0).toString());
                        ps.setString(3, "Hall 3");
                        ps.setInt(4, 50);
                        ps.setDouble(5, 9.00);
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) showtimeId2 = keys.getInt(1);
                        }
                    }

                    if (movieId2 != -1) {
                        ps.setInt(1, movieId2);
                        ps.setString(2, LocalDateTime.of(2025, 8, 2, 18, 0).toString());
                        ps.setString(3, "Hall 2");
                        ps.setInt(4, 40);
                        ps.setDouble(5, 12.00);
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) showtimeId3 = keys.getInt(1);
                        }
                    }
                }

                // Insert sample seats for showtimeId1 (The Great Adventure 10:00)
                if (showtimeId1 != -1) {
                    String insertSeatSql = "INSERT INTO seat (showtime_id, seat_number, is_booked) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = currentConn.prepareStatement(insertSeatSql)) {
                        for (int row = 1; row <= 5; row++) {
                            for (int col = 1; col <= 10; col++) {
                                char rowChar = (char) ('A' + row - 1);
                                String seatNumber = String.valueOf(rowChar) + col;
                                ps.setInt(1, showtimeId1);
                                ps.setString(2, seatNumber);
                                ps.setInt(3, 0); // Not booked
                                ps.addBatch();
                            }
                        }
                        ps.executeBatch();
                    }
                }
                System.out.println("Sample movies and showtimes inserted!");
            } else {
                System.out.println("Database already contains movies. Skipping movie/showtime initialization.");
            }

            ResultSet rsUsers = stmt.executeQuery("SELECT COUNT(*) FROM user");
            if (rsUsers.next() && rsUsers.getInt(1) == 0) {
                System.out.println("Inserting sample user...");
                String insertUserSql = "INSERT INTO user (name, email, password, role) VALUES (?, ?, ?, ?)"; // Use 'name'
                try (PreparedStatement ps = currentConn.prepareStatement(insertUserSql, Statement.RETURN_GENERATED_KEYS)) {
                    ps.setString(1, "Sample User");
                    ps.setString(2, "sample@example.com");
                    ps.setString(3, "password123");
                    ps.setString(4, "customer");
                    ps.executeUpdate();
                }
                System.out.println("Sample user inserted!");
            } else {
                System.out.println("Database already contains users. Skipping user initialization.");
            }

            ResultSet rsBookings = stmt.executeQuery("SELECT COUNT(*) FROM bookings");
            if (rsBookings.next() && rsBookings.getInt(1) == 0) {
                System.out.println("Inserting sample booking...");

                int sampleUserId = -1;
                // Use 'name' instead of 'full_name' in SELECT
                ResultSet userRs = stmt.executeQuery("SELECT id FROM user WHERE email = 'sample@example.com'");
                if (userRs.next()) {
                    sampleUserId = userRs.getInt("id");
                }

                int sampleShowtimeId = -1;
                ResultSet showtimeRs = stmt.executeQuery("SELECT id FROM showtime LIMIT 1");
                if (showtimeRs.next()) {
                    sampleShowtimeId = showtimeRs.getInt("id");
                }

                if (sampleUserId != -1 && sampleShowtimeId != -1) {
                    String insertBookingSql = "INSERT INTO bookings (customer_id, showtime_id, num_tickets, booking_date, total_price) VALUES (?, ?, ?, ?, ?)";
                    int bookingId = -1;
                    try (PreparedStatement ps = currentConn.prepareStatement(insertBookingSql, Statement.RETURN_GENERATED_KEYS)) {
                        String bookingDate = LocalDateTime.now().toString();
                        int numTickets = 2;
                        double totalPrice = numTickets * 10.50;
                        ps.setInt(1, sampleUserId);
                        ps.setInt(2, sampleShowtimeId);
                        ps.setInt(3, numTickets);
                        ps.setString(4, bookingDate);
                        ps.setDouble(5, totalPrice);
                        ps.executeUpdate();
                        try (ResultSet keys = ps.getGeneratedKeys()) {
                            if (keys.next()) bookingId = keys.getInt(1);
                        }
                    }

                    if (bookingId != -1) {
                        String insertBookingSeatSql = "INSERT INTO booking_seats (booking_id, seat_number) VALUES (?, ?)";
                        try (PreparedStatement ps = currentConn.prepareStatement(insertBookingSeatSql)) {
                            ps.setInt(1, bookingId);
                            ps.setString(2, "A1");
                            ps.addBatch();
                            ps.setInt(1, bookingId);
                            ps.setString(2, "A2");
                            ps.addBatch();
                            ps.executeBatch();
                        }
                    }
                    System.out.println("Sample booking inserted!");
                } else {
                    System.err.println("Could not insert sample booking: Missing sample user or showtime data.");
                }
            } else {
                System.out.println("Database already contains bookings. Skipping booking initialization.");
            }

        } catch (SQLException e) {
            System.err.println("Error initializing sample data: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void dropTabel() { // Typo: Should be dropTable
        Connection currentConn = getConnection();
        if (currentConn == null) {
            System.err.println("Cannot drop tables: No database connection available.");
            return;
        }
        try (Statement stmt = currentConn.createStatement()) {
            // Drop in reverse order of dependency
            stmt.executeUpdate("DROP TABLE IF EXISTS booking_seats");
            stmt.executeUpdate("DROP TABLE IF EXISTS bookings");
            stmt.executeUpdate("DROP TABLE IF EXISTS seat");
            stmt.executeUpdate("DROP TABLE IF EXISTS showtime");
            stmt.executeUpdate("DROP TABLE IF EXISTS movies");
            stmt.executeUpdate("DROP TABLE IF EXISTS manager");
            stmt.executeUpdate("DROP TABLE IF EXISTS user");
            System.out.println("Database Tables dropped!");
        } catch (SQLException e) {
            System.err.println("Error dropping tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Helper methods for LocalDateTime formatting and parsing
    public static String formatDateTime(LocalDateTime dateTime) {
        return dateTime.format(FORMATTER);
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        return LocalDateTime.parse(dateTimeString, FORMATTER);
    }
}
