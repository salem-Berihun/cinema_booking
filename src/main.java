

import model.User; 
import model.Customer;
import ui.CustomerMenu; 
import ui.ManagerMenu; 
import util.DBUtil; 

import ui.Auth; 
import util.InputValidator; 
import java.util.InputMismatchException;
import java.util.Scanner;

public class main { 
    private static Scanner sc = new Scanner(System.in);
    private static Auth authInstance = new Auth();

    public static void main(String[] args) {
        
        DBUtil.initTable(); 
        DBUtil.initData();  
        
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

                    User loggedInUser = authInstance.login(email, password);
                    if (loggedInUser != null) {
                      
                        if (!loggedInUser.getUserType().equalsIgnoreCase(role)) {
                            System.err.println("Login Failed: User role mismatch. You logged in as " + loggedInUser.getUserType() + " but selected " + role + " menu.");
                            continue; // Stay in the login/register loop
                        }

                        System.out.println("Login Successful!");
                        if (role.equals("customer")) {
                           
                            Customer customer = new Customer(
                                    loggedInUser.getId(),
                                    loggedInUser.getFullName(), 
                                    loggedInUser.getEmail(),
                                    loggedInUser.getPassword()
                            );
                           
                            CustomerMenu.customerMainMenu(customer); 
                        } else if (role.equals("manager")) {
                            ManagerMenu.meno(); 
                        }
                   
                        break;
                    } else {
                        System.err.println("Login Failed: Invalid credentials or user not found.");
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
                        if (role.equals("customer")) {
                            System.out.println("Please login with your new account.");
                        }
                    } else {
                        System.err.println("Register Failed. Try again.");
                    }
                } else if (choice == 3) {
                    break; 
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
