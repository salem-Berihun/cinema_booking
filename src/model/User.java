package model;

import java.util.Objects; // Keep this if you use it for equals/hashCode, otherwise it can be removed if not needed

public class User {
    private int id;
    private String fullName; // <-- Renamed 'name' to 'fullName' for consistency with DB 'full_name'
    private String email;
    private String password;
    private String userType; // <-- Renamed 'role' to 'userType' for consistency with DB 'user_type'
    // In a real app, store hashed passwords!

    // Full constructor - updated parameter names
    public User(int id, String fullName, String email, String password, String userType) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    // Constructor without ID (for new users before DB insert)
    public User(String fullName, String email, String password, String userType) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }


    // --- Getters ---
    public int getId() {
        return id;
    }

    public String getFullName() { // <-- Renamed from getName()
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getUserType() { // <-- Renamed from getRole()
        return userType;
    }

    // --- Setters ---
    public void setId(int id) {
        this.id = id;
    }

    public void setFullName(String fullName) { // <-- Renamed from setName()
        this.fullName = fullName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUserType(String userType) { // <-- Renamed from setRole()
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' + // <-- Updated field name
                ", email='" + email + '\'' +
                ", userType='" + userType + '\'' + // <-- Updated field name
                '}';
    }

    // It's good practice to implement equals and hashCode for model objects
    // @Override
    // public boolean equals(Object o) {
    //     if (this == o) return true;
    //     if (o == null || getClass() != o.getClass()) return false;
    //     User user = (User) o;
    //     return id == user.id && Objects.equals(email, user.email);
    // }
    //
    // @Override
    // public int hashCode() {
    //     return Objects.hash(id, email);
    // }
}