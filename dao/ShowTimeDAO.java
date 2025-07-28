import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ShowTimeDAO {

    private Connection conn;

    public ShowTimeDAO() {
        try {
            conn = DBUtil.getConnection();
            createTablesIfNotExist();
        } catch (SQLException e) {
            System.err.println("ShowTimeDAO Init Error: " + e.getMessage());
        }
    }

    private void createTablesIfNotExist() throws SQLException {
        String movieTable = """
            CREATE TABLE IF NOT EXISTS movie (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL
            );
        """;

        String showtimeTable = """
            CREATE TABLE IF NOT EXISTS showtime (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                movie_id INTEGER,
                date_time TEXT,
                hall TEXT,
                FOREIGN KEY(movie_id) REFERENCES movie(id)
            );
        """;

        String seatTable = """
            CREATE TABLE IF NOT EXISTS seat (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                showtime_id INTEGER,
                seat_number TEXT,
                is_booked INTEGER DEFAULT 0,
                FOREIGN KEY(showtime_id) REFERENCES showtime(id)
            );
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(movieTable);
            stmt.execute(showtimeTable);
            stmt.execute(seatTable);
        }
    }

    // Add Movie and return its id
    public int addMovie(String title) {
        String sql = "INSERT INTO movie (title) VALUES (?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, title);
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
        String sql = "SELECT * FROM movie WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Movie(rs.getInt("id"), rs.getString("title"));
            }
        } catch (SQLException e) {
            System.err.println("Get movie error: " + e.getMessage());
        }
        return null;
    }

    // Add ShowTime (with hall)
    public int addShowTime(int movieId, LocalDateTime dateTime, String hall) {
        String sql = "INSERT INTO showtime (movie_id, date_time, hall) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, movieId);
            stmt.setString(2, dateTime.toString());
            stmt.setString(3, hall);
            stmt.executeUpdate();
            ResultSet keys = stmt.getGeneratedKeys();
            if (keys.next()) return keys.getInt(1);
        } catch (SQLException e) {
            System.err.println("Add showtime error: " + e.getMessage());
        }
        return -1;
    }

    // Get ShowTime by id (including seats)
    public ShowTime getShowTimeById(int id) {
        String sql = "SELECT * FROM showtime WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int movieId = rs.getInt("movie_id");
                Movie movie = getMovieById(movieId);
                LocalDateTime dateTime = LocalDateTime.parse(rs.getString("date_time"));
                String hall = rs.getString("hall");
                List<Seat> seats = getSeatsByShowTimeId(id);

                return new ShowTime(id, movie, dateTime, hall, seats);
            }
        } catch (SQLException e) {
            System.err.println("Get showtime error: " + e.getMessage());
        }
        return null;
    }

    // Get all ShowTimes
    public List<ShowTime> getAllShowTimes() {
        List<ShowTime> list = new ArrayList<>();
        String sql = "SELECT id FROM showtime";

        try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                ShowTime st = getShowTimeById(id);
                if (st != null) list.add(st);
            }
        } catch (SQLException e) {
            System.err.println("Get all showtimes error: " + e.getMessage());
        }
        return list;
    }

    // Add Seat(s) to a ShowTime
    public void addSeatsToShowTime(int showTimeId, List<Seat> seats) {
        String sql = "INSERT INTO seat (showtime_id, seat_number, is_booked) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
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
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, showTimeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String seatNumber = rs.getString("seat_number");
                boolean booked = rs.getInt("is_booked") == 1;
                seats.add(new Seat(id, seatNumber, booked));
            }
        } catch (SQLException e) {
            System.err.println("Get seats error: " + e.getMessage());
        }
        return seats;
    }
}
