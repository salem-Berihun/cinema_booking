// Assuming your main class is in the 'main' package

// Assuming these are the correct packages
import model.User; // This typically would be for general user (could be parent of Customer/Manager)
import model.Customer; // Specific customer model (now extends User)
import model.Manager;  // Specific manager model (now extends User)
import ui.CustomerMenu; // Your customer UI menu
import ui.ManagerMenu; // Your manager UI menu
import util.DBUtil; // Make sure DBUtil is imported
import util.FileLogger; // Import FileLogger for application startup/shutdown logging

import ui.Auth; // Your authentication logic
import util.InputValidator; // Your input validation utility
import exceptions.UserAuthException; // Import custom UserAuthException
import java.util.InputMismatchException;
import java.util.Scanner;

public class main {
    private static Scanner sc = new Scanner(System.in);
    private static Auth authInstance = new Auth();
    private static FileLogger fileLogger = new FileLogger(); // Initialize FileLogger for main

    public static void main(String[] args) {

        DBUtil.initTable(); // Ensures all tables are created correctly based on the latest DBUtil
        DBUtil.initData();  // Inserts sample data based on the updated DBUtil
        fileLogger.logInfo("Application started successfully."); // Log application startup
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
                    fileLogger.logInfo("Application is shutting down."); // Log application shutdown
                    break;
                } else {
                    System.err.println("Invalid choice. Please enter 1, 2 or 3.");
                }
            } catch (InputMismatchException e) {
                System.err.println("Please enter a valid number!");
                sc.nextLine(); // clear invalid input
                fileLogger.logError("Invalid input in main menu: " + e.getMessage());
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

                    User loggedInUser = null;
                    try {
                        loggedInUser = authInstance.login(email, password); // Auth.login can now throw UserAuthException
                    } catch (UserAuthException e) { // Catch custom exception
                        System.err.println("Login Failed: " + e.getMessage());
                        fileLogger.logError("Login failed for email: " + email + " - " + e.getMessage());
                        continue; // Stay in the login/register loop
                    }

                    if (loggedInUser != null) {
                        // Ensure the logged-in user's type matches the expected role
                        if (!loggedInUser.getUserType().equalsIgnoreCase(role)) {
                            System.err.println("Login Failed: User role mismatch. You logged in as " + loggedInUser.getUserType() + " but selected " + role + " menu.");
                            fileLogger.logError("Login failed for email: " + email + " - Role mismatch. Logged in as " + loggedInUser.getUserType() + ", tried to access " + role + " menu.");
                            continue; // Stay in the login/register loop
                        }

                        System.out.println("Login Successful!");
                        fileLogger.logInfo("User " + loggedInUser.getFullName() + " (ID: " + loggedInUser.getId() + ", Role: " + loggedInUser.getUserType() + ") logged in.");

                        // --- CRITICAL CHANGE: Leverage polymorphism directly ---
                        // Call the abstract displayDashboard method.
                        // At runtime, Java will call the specific implementation
                        // in either Customer or Manager based on the actual object type.
                        loggedInUser.displayDashboard(); // This will print the welcome message
                        // Now, explicitly call the menu based on the actual type of loggedInUser
                        if (loggedInUser instanceof Customer) {
                            CustomerMenu.customerMainMenu((Customer) loggedInUser);
                        } else if (loggedInUser instanceof Manager) {
                            ManagerMenu.meno(); // Assuming meno is the main method for ManagerMenu
                        }
                        break; // After the user exits their specific menu, go back to main menu
                    } else {
                        // This else block might not be reached if UserAuthException is always thrown for null
                        System.err.println("Login Failed: Invalid credentials or user not found.");
                        fileLogger.logError("Login failed for email: " + email + " - User not found (unexpected null return).");
                    }

                } else if (choice == 2) {
                    System.out.println("\n---- Register Page ----");
                    String fullName, email, password;

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

                    if (authInstance.register(fullName, email, password, role)) {
                        System.out.println("Registered Successfully!");
                        fileLogger.logInfo("User " + fullName + " (Role: " + role + ") registered successfully.");
                        if (role.equals("customer")) {
                            System.out.println("Please login with your new account.");
                        }
                    } else {
                        System.err.println("Register Failed. Try again.");
                        fileLogger.logError("Registration failed for email: " + email + " (Role: " + role + ").");
                    }
                } else if (choice == 3) {
                    break;  // Go back to main menu
                } else {
                    System.err.println("Invalid menu choice.");
                    fileLogger.logError("Invalid choice in userFlow menu: " + choice);
                }
            } catch (InputMismatchException e) {
                System.err.println("Please enter a number.");
                sc.nextLine();
                fileLogger.logError("Invalid input in userFlow menu: " + e.getMessage());
            }
        }
    }
}
