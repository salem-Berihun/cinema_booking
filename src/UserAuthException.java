package exception;

// Custom exception for user authentication failures
public class UserAuthException extends Exception {
    public UserAuthException(String message) {
        super(message);
    }

    public UserAuthException(String message, Throwable cause) {
        super(message, cause);
    }
}
