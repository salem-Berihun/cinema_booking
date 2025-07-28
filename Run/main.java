import java.util.InputMismatchException;
import java.util.Scanner;

public class main {
    private static Scanner sc = new Scanner(System.in);
    private static auth authInstance = new auth();

    public static void main(String[] args) {
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
                    userFlow("user");
                } else if (choice == 2) {
                    userFlow("manager");
                } else if (choice == 3) {
                    System.out.println("Exiting...");
                    break;
                } else {
                    System.out.println("Invalid choice. Please enter 1, 2 or 3.");
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
                        System.out.println("Login Successful!");
                        if (role.equals("user")) {
                            // Cast or create Customer from User
                            if (loggedInUser instanceof Customer) {
                                CustomerMenu.meno((Customer) loggedInUser);
                            } else {
                                // or convert User to Customer if needed, e.g.,
                                Customer customer = new Customer(
                                        loggedInUser.getId(),
                                        loggedInUser.getName(),
                                        loggedInUser.getEmail(),
                                        loggedInUser.getPassword()
                                );
                                CustomerMenu.meno(customer);
                            }
                        } else if (role.equals("manager")) {
                            ManagerMenu.meno();
                        }
                        break;
                    } else {
                        System.err.println("Login Failed: Invalid credentials.");
                    }

                } else if (choice == 2) {
                    System.out.println("\n---- Register Page ----");
                    String name, email, password;

                    while (true) {
                        System.out.print("Full Name: ");
                        name = sc.nextLine();
                        if (InputValidator.isAlph(name)) break;
                        else System.err.println("Invalid Name!");
                    }

                    while (true) {
                        System.out.print("Email: ");
                        email = sc.nextLine();
                        if (InputValidator.isValidEmail(email)) break;
                        else System.err.println("Invalid Email!");
                    }

                    while (true) {
                        System.out.print("Password (4-16 chars): ");
                        password = sc.nextLine();
                        if (InputValidator.checkStringLength(4, 16, password)) break;
                        else System.err.println("Invalid Password Length!");
                    }

                    if (authInstance.register(name, email, password, role)) {
                        System.out.println("Registered Successfully!");
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
