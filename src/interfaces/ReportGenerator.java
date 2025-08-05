package interfaces;
import model.Booking;
public interface ReportGenerator {

    /**
     * Generates a generic report based on the report type and provided data
     * The implementation will determine how to interpret 'reportType' and 'data'.
     *
     * @param reportType A string indicating the type of report to generate (e.g., "DAILY_SALES", "MOVIE_PERFORMANCE").
     * @param data An object containing the data necessary for the report (e.g., List<Booking>, Movie, etc.).
     * @return true if the report was successfully generated and written, false otherwise.
     * @throws IllegalArgumentException if reportType is null or data is invalid for the given reportType.
     */
    boolean generateReport(String reportType, Object data);

    /**
     * Logs the details of a specific booking to a persistent storage (e.g., a log file).
     * This is a specialized logging method within the report generation context.
     *
     * @param booking The Booking object to be logged.
     * @return true if the booking was successfully logged, false otherwise.
     * @throws IllegalArgumentException if booking is null.
     */
    boolean logBooking(Booking booking);

    // You can add more specific report generation methods if desired,
    // e.g., generateMovieRevenueReport(Movie movie), generateOccupancyReport(ShowTime showTime)
}
