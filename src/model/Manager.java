package model;

import java.util.Objects;

public class Manager extends User {

    // Constructor for existing managers (with ID from DB)
    public Manager(int id, String fullName, String email, String password, String userType) {
        // Call the constructor of the parent class (User)
        // We explicitly pass "manager" as the userType, ensuring this object is always identified as a manager.
        super(id, fullName, email, password, "manager");
    }

    // Constructor for new managers (without ID, before DB insert)
    public Manager(String fullName, String email, String password, String userType) {
        // Call the constructor of the parent class (User)
        super(fullName, email, password, "manager");
    }

    // --- IMPORTANT: Implement the abstract displayDashboard() method from User ---
    @Override
    public void displayDashboard() {
        // This method will typically lead to the ManagerMenu.meno()
        // For now, it prints a message.
        // The main method will then call ManagerMenu.meno();
        System.out.println("\n----- Manager Dashboard -----");
        System.out.println("Welcome, " + getFullName() + " (Manager)!");
        // We'll handle the actual menu navigation in main.java or the calling UI class.
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // Use super.equals() to compare the User part of the object
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        // Use super.hashCode() to generate hash code based on the User part
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Manager{" +
                // Call the toString() method of the parent class (User) to get its details
                super.toString() +
                '}';
    }
}
