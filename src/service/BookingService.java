package service;
import util.Tc;

import java.util.Scanner;

public class BookingService {
    private static final int ROWS = 5;
    private static final int COLUMNS = 10;
    private boolean[][] seats = new boolean[ROWS][COLUMNS]; // false = available, true = booked
    private Scanner sc = new Scanner(System.in);

    // Render the seat map
    public void showSeats() {
        System.out.println("\n" + Tc.bold + "         Cinema Seat Map         " + Tc.f);
        System.out.print("   ");
        for (int c = 0; c < COLUMNS; c++) {
            System.out.printf(" %2d ", c + 1);
        }
        System.out.println();

        for (int r = 0; r < ROWS; r++) {
            System.out.printf("R%d ", r + 1);
            for (int c = 0; c < COLUMNS; c++) {
                if (seats[r][c]) {
                    // booked
                    System.out.print(" " + Tc.r + "[X]" + Tc.f);
                } else {
                    // available
                    System.out.print(" " + Tc.g + "[ ]" + Tc.f);
                }
            }
            System.out.println();
        }
    }

    // Book a seat
    public void bookSeat() {
        showSeats();
        System.out.println("\n" + Tc.bold + "Booking a Seat" + Tc.f);

        try {
            System.out.print("Enter row (1-" + ROWS + "): ");
            int row = sc.nextInt();
            System.out.print("Enter seat number (1-" + COLUMNS + "): ");
            int col = sc.nextInt();

            if (row < 1 || row > ROWS || col < 1 || col > COLUMNS) {
                System.err.println("Invalid seat position.");
                return;
            }

            if (seats[row - 1][col - 1]) {
                System.err.println("Seat already booked!");
            } else {
                seats[row - 1][col - 1] = true;
                System.out.println(Tc.g + "Seat booked successfully!" + Tc.f);
            }

        } catch (Exception e) {
            System.err.println("Invalid input.");
            sc.nextLine(); // clear buffer
        }
    }

    // Optional: Reset seats (e.g., for testing)
    public void resetSeats() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                seats[i][j] = false;
            }
        }
    }
}
// Helper method added for logging — safe and traceable
public void logSeatMap() {
    System.out.println("Logging current seat status:");
    for (int r = 0; r < ROWS; r++) {
        for (int c = 0; c < COLUMNS; c++) {
            System.out.print(seats[r][c] ? "[X]" : "[ ]");
        }
        System.out.println();
    }
}