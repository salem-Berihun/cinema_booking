package ui;

import dao.UserDAO;
import model.User;
import exceptions.UserAuthException; // Import custom exception

public class Auth {

    private UserDAO userDAO; // Instantiated once for the lifecycle of Auth

    public Auth() {
        this.userDAO = new UserDAO(); // Initialize UserDAO
    }

    public User login(String email, String password) throws UserAuthException { // Declare throws clause
        // UserDAO.loginUser now throws UserAuthException directly
        return userDAO.loginUser(email, password);
    }

    public boolean register(String fullName, String email, String password, String role) {
        // UserDAO.registerUser returns boolean, no custom exception thrown from DAO directly here
        return userDAO.registerUser(fullName, email, password, role);
    }
}
