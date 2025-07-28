import java.util.Scanner;

public class ManagerMenu {
    private static Scanner sc = new Scanner(System.in);
    private static BookingDAO bookingDAO = new BookingDAO();

    public static void meno() {
        while (true) {
            System.out.println("\n----- Manager Menu -----");
            System.out.println("1. Add Movie");
            System.out.println("2. View All Bookings");
            System.out.println("3. Logout");
            System.out.print("Choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    addMovie();
                    break;
                case 2:
                    viewAllBookings();
                    break;
                case 3:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.err.println("Invalid option.");
            }
        }
    }

    private static void addMovie() {
        System.out.println("Add movie feature is under construction...");
        // You can implement adding movie functionality here.
    }

    private static void viewAllBookings() {
        System.out.println("All Bookings:");
        for (Booking booking : bookingDAO.getAllBookings()) {
            System.out.println(booking);
        }
    }
}
