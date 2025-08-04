package model;

import java.util.Objects;

// Make User an abstract class. This means you cannot create a direct 'User' object,
// only objects of classes that extend User (like Customer or Manager).
public abstract class User {
    private int id;
    private String fullName;
    private String email;
    private String password; // In a real app, store hashed passwords!
    private String userType; // e.g., "customer", "manager"

    // Constructor for existing users (with ID from DB)
    public User(int id, String fullName, String email, String password, String userType) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // Constructor for new users (ID not yet assigned by DB)
    public User(String fullName, String email, String password, String userType) {
        this(0, fullName, email, password, userType); // Call the full constructor with default ID 0
    }

    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() {
        return userType;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    // --- Abstract Method ---
    // This is an abstract method. It has no body here.
    // Any concrete class that extends User (like Customer or Manager) MUST provide an implementation for this method.
    // This demonstrates polymorphism (method overriding).
    public abstract void displayDashboard();

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' +
                '}';
    }

    // It's good practice to implement equals and hashCode for model objects
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id == user.id && Objects.equals(email, user.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }
}
