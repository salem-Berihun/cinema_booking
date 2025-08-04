package util;

import interfaces.ReportGenerator; // Import the interface
import model.Booking; // Import Booking model
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List; // For generateReport

public class FileLogger implements ReportGenerator { // It implements the interface

    public static final String LOG_FILE_PATH = "application_log.txt";
    public static final String BOOKING_REPORT_PATH = "booking_report.txt";
    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // Private helper method to write a line to a specified file
    private boolean writeToFile(String filePath, String content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) { // 'true' for append mode
            writer.write(content);
            writer.newLine(); // Add a new line after each entry
            return true;
        } catch (IOException e) {
            System.err.println("FileLogger: Error writing to file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // --- Implementation of ReportGenerator interface methods ---

    @Override
    public boolean generateReport(String reportType, Object data) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("--- Report Generated: ").append(timestamp).append(" ---\n");
        reportContent.append("Report Type: ").append(reportType).append("\n");

        if ("ALL_BOOKINGS".equalsIgnoreCase(reportType) && data instanceof List) {
            @SuppressWarnings("unchecked") // Suppress warning for unchecked cast
            List<Booking> bookings = (List<Booking>) data;
            reportContent.append("Total Bookings: ").append(bookings.size()).append("\n");
            reportContent.append("----------------------------------\n");
            for (Booking booking : bookings) {
                reportContent.append(booking.toString()).append("\n"); // Use Booking's toString
            }
            reportContent.append("----------------------------------\n");
            return writeToFile(BOOKING_REPORT_PATH, reportContent.toString());
        } else {
            reportContent.append("Unsupported report type or invalid data for reportType: ").append(reportType).append("\n");
            return writeToFile(LOG_FILE_PATH, reportContent.toString()); // Log error to general log
        }
    }

    @Override
    public boolean logBooking(Booking booking) {
        if (booking == null) {
            // Throwing IllegalArgumentException as per interface contract
            throw new IllegalArgumentException("Booking object cannot be null for logging.");
        }
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String logEntry = String.format("[%s] BOOKING_SUCCESS: ID=%d, Customer=%s, Showtime=%d, Seats=%d, Total=%.2f",
                timestamp,
                booking.getId(),
                booking.getCustomer() != null ? booking.getCustomer().getFullName() : "N/A",
                booking.getShowtime() != null ? booking.getShowtime().getId() : "N/A",
                booking.getSelectedSeats() != null ? booking.getSelectedSeats().size() : 0,
                booking.getTotalPrice());
        return writeToFile(LOG_FILE_PATH, logEntry);
    }

    // You could add other generic logging methods if needed
    public boolean logInfo(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String logEntry = String.format("[%s] INFO: %s", timestamp, message);
        return writeToFile(LOG_FILE_PATH, logEntry);
    }

    public boolean logError(String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String logEntry = String.format("[%s] ERROR: %s", timestamp, message);
        return writeToFile(LOG_FILE_PATH, logEntry);
    }
}
