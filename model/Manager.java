import java.util.Objects;

public class Manager extends User {
    // No specific new fields required beyond User's for basic management,
    // but you could add String department or int employeeId if desired for more complexity.
    // For now, it just inherits User's properties and methods.

    public Manager(int id, String name, String email, String password) {
        super(id, name, email, password);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        // Manager doesn't have unique fields beyond User, so we can rely on super.equals()
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        // Manager doesn't have unique fields beyond User, so we can rely on super.hashCode()
        return super.hashCode();
    }

    @Override
    public String toString() {
        return "Manager{" +
                super.toString() + // Include User's toString output
                '}';
    }
}