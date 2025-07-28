import java.util.List;
import java.util.Scanner;

public class CustomerMenu {
    private static Scanner sc = new Scanner(System.in);
    private static BookingDAO bookingDAO = new BookingDAO();

    public static void meno(Customer customer) {
        while (true) {
            System.out.println("\n----- Customer Menu -----");
            System.out.println("1. View Available ShowTimes");
            System.out.println("2. Make a Booking");
            System.out.println("3. View My Bookings");
            System.out.println("4. Logout");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    ShowTimeDAO showTimeDAO = new ShowTimeDAO();
                    List<ShowTime> showTimes = showTimeDAO.getAllShowTimes();
                    for (ShowTime st : showTimes) {
                        System.out.println(st);
                    }
                    break;
                case 2:
                    makeBooking(customer);
                    break;
                case 3:
                    viewBookings(customer);
                    break;
                case 4:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.err.println("Invalid option.");
            }
        }
    }

    private static void makeBooking(Customer customer) {
        System.out.println("Booking feature is under construction...");
        // Here you can add detailed booking steps (select showtime, select seats, save booking)
    }

    private static void viewBookings(Customer customer) {
        List<Booking> bookings = bookingDAO.getAllBookings();
        System.out.println("Your Bookings:");
        for (Booking booking : bookings) {
            if (booking.getCustomer().getId() == customer.getId()) {
                System.out.println(booking);
            }
        }
    }
}
