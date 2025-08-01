public abstract class Account {
    private int id;
    private String name;
    private String email;
    private String password;
    private String role;

    public Account(int id, String name, String email, String password, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    // Setters with basic logging added
    public void setId(int id) {
        System.out.println("Setting ID to: " + id);  // Added log
        this.id = id;
    }

    public void setName(String name) {
        System.out.println("Setting name to: " + name);  // Added log
        this.name = name;
    }

    public void setEmail(String email) {
        System.out.println("Setting email to: " + email);  // Added log
        this.email = email;
    }

    public void setPassword(String password) {
        System.out.println("Setting password.");  // Logging without showing real password
        this.password = password;
    }

    public void setRole(String role) {
        System.out.println("Setting role to: " + role);  // Added log
        this.role = role;
    }
}




