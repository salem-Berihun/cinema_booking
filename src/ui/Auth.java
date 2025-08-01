package ui;

import dao.UserDAO;
import model.User;

public class Auth {

    // It's generally better practice to instantiate DAOs once if Auth is a singleton
    // or instantiated once per application lifecycle, rather than per method call.
    // private UserDAO userDAO = new UserDAO(); // Consider uncommenting this and removing per-method instantiation

    public User login(String email, String password) {
        UserDAO userdao = new UserDAO(); // Instantiate UserDAO for this operation
        // CORRECTED: Pass both email and password to loginUser
        User user = userdao.loginUser(email, password);

        // The password check is now handled by UserDAO.loginUser,
        // so if user is not null, login was successful.
        return user;
    }

    public boolean register(String fullName, String email, String password, String role) {
        UserDAO userdao = new UserDAO(); // Instantiate UserDAO for this operation

        // CORRECTED: Call registerUser with individual parameters as expected by UserDAO
        return userdao.registerUser(fullName, email, password, role);
    }
}
