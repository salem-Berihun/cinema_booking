 // Assuming your main class is in the 'main' package

// Assuming these are the correct packages
import model.User; // This typically would be for general user (could be parent of Customer/Manager)
import model.Customer; // Specific customer model
import ui.CustomerMenu; // Your customer UI menu
import ui.ManagerMenu; // Your manager UI menu
import util.DBUtil; // Make sure DBUtil is imported

import ui.Auth; // Your authentication logic
import util.InputValidator; // Your input validation utility
import java.util.InputMismatchException;
import java.util.Scanner;

public class main { // Renamed from 'main' pac2kage to just 'main' class based on common practice
    private static Scanner sc = new Scanner(System.in);
    private static Auth authInstance = new Auth();

    public static void main(String[] args) {
        // --- CRITICAL CHANGE: Initialize database tables AND data here ---
        // Call initTable() to create schema, then initData() to populate.
        DBUtil.initTable(); // Ensures all tables are created correctly based on the latest DBUtil
        DBUtil.initData();  // Inserts sample data based on the updated DBUtil
        // --- END CRITICAL CHANGE ---

        while (true) {
            try {
                System.out.println("\n+++++ Welcome To Cinema Booking Application +++++");
                System.out.println("You are:");
                System.out.println("    1. Customer");
                System.out.println("    2. Manager");
                System.out.println("    3. Exit");
                System.out.print("Enter: ");

                int choice = sc.nextInt();
                sc.nextLine(); // clear newline

                if (choice == 1) {
                    userFlow("customer"); // Pass "customer" role
                } else if (choice == 2) {
                    userFlow("manager"); // Pass "manager" role
                } else if (choice == 3) {
                    System.out.println("Exiting...");
                    DBUtil.closeConnection(); // Close the static connection on exit
                    break;
                } else {
                    System.err.println("Invalid choice. Please enter 1, 2 or 3.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid number!");
                sc.nextLine(); // clear invalid input
            }
        }
    }

    private static void userFlow(String role) {
        while (true) {
            try {
                System.out.println("\n             MAIN MENU            ");
                System.out.println("+--------------------------------+");
                System.out.println("[1] Login");
                System.out.println("[2] Register");
                System.out.println("[3] Go Back");
                System.out.print("Choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                if (choice == 1) {
                    System.out.println("\n---- Login Page ----");
                    System.out.print("Enter Your Email: ");
                    String email = sc.nextLine();
                    System.out.print("Enter Your Password: ");
                    String password = sc.nextLine();

                    User loggedInUser = authInstance.login(email, password); // Auth.login returns User
                    if (loggedInUser != null) {
                        // Ensure the logged-in user's type matches the expected role
                        // Assuming User has a getUserType() method that returns "customer" or "manager"
                        if (!loggedInUser.getUserType().equalsIgnoreCase(role)) {
                            System.err.println("Login Failed: User role mismatch. You logged in as " + loggedInUser.getUserType() + " but selected " + role + " menu.");
                            continue; // Stay in the login/register loop
                        }

                        System.out.println("Login Successful!");
                        if (role.equals("customer")) { // Corrected: Check for "customer" role
                            // If Auth.login returns a generic User, convert it to a Customer object
                            // Assuming Customer model has a constructor that takes User attributes.
                            // Assuming User.getName() is now User.getFullName() for consistency with DBUtil.
                            Customer customer = new Customer(
                                    loggedInUser.getId(),
                                    loggedInUser.getFullName(), // Use getFullName() as per DBUtil.user table
                                    loggedInUser.getEmail(),
                                    loggedInUser.getPassword()
                            );
                            // --- CRITICAL CHANGE: Call the correctly named method from CustomerMenu ---
                            CustomerMenu.customerMainMenu(customer); // Corrected method name
                        } else if (role.equals("manager")) {
                            ManagerMenu.meno(); // Assuming ManagerMenu still has 'meno'
                        }
                        // After successful login and menu display, break from this userFlow loop
                        // to go back to the main application loop if desired, or handle further navigation
                        break;
                    } else {
                        System.err.println("Login Failed: Invalid credentials or user not found.");
                    }

                } else if (choice == 2) {
                    System.out.println("\n---- Register Page ----");
                    String fullName, email, password; // Renamed 'name' to 'fullName' for consistency

                    while (true) {
                        System.out.print("Full Name: ");
                        fullName = sc.nextLine();
                        if (InputValidator.isAlph(fullName)) break;
                        else System.err.println("Invalid Name! (Only alphabetic characters allowed)");
                    }

                    while (true) {
                        System.out.print("Email: ");
                        email = sc.nextLine();
                        if (InputValidator.isValidEmail(email)) break;
                        else System.err.println("Invalid Email format!");
                    }

                    while (true) {
                        System.out.print("Password (4-16 chars): ");
                        password = sc.nextLine();
                        if (InputValidator.checkStringLength(4, 16, password)) break;
                        else System.err.println("Password must be between 4 and 16 characters!");
                    }

                    // --- CRITICAL CHANGE: Pass fullName instead of name ---
                    if (authInstance.register(fullName, email, password, role)) {
                        System.out.println("Registered Successfully!");
                        // If a customer registered, maybe automatically log them in or guide them to login
                        if (role.equals("customer")) {
                            System.out.println("Please login with your new account.");
                        }
                    } else {
                        System.err.println("Register Failed. Try again.");
                    }
                } else if (choice == 3) {
                    break;  // Go back to main menu
                } else {
                    System.err.println("Invalid menu choice.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Please enter a number.");
                sc.nextLine();
            }
        }
    }
}
