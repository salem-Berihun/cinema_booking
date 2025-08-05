package model;

import java.util.Objects;

public class Manager extends User {

       public Manager(int id, String fullName, String email, String password, String userType) {
       
        super(id, fullName, email, password, "manager");
    }

    
    public Manager(String fullName, String email, String password, String userType) {
        super(fullName, email, password, "manager");
    }

    @Override
    public void displayDashboard() {
       
        System.out.println("\n----- Manager Dashboard -----");
        System.out.println("Welcome, " + getFullName() + " (Manager)!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Manager{" +
                super.toString() +
                '}';
    }
}
