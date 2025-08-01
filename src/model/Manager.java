package model;

import java.util.Objects;

public class Manager extends User {


    public Manager(int id, String name, String email, String password) {
        super(id, name, email, password, "manager");
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
                super.toString() + // Include User's toString output
                '}';
    }
}