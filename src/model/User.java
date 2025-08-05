package model;

import java.util.Objects;


public abstract class User {
    private int id;
    private String fullName;
    private String email;
    private String password; 
    private String userType; 

  
    public User(int id, String fullName, String email, String password, String userType) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.userType = userType;
    }

    public User(String fullName, String email, String password, String userType) {
        this(0, fullName, email, password, userType);
    }

   
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
