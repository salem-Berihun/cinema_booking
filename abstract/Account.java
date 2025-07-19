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
    //getter
    public int getId() {return id;}
    public String getName() {return name;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public String getRole() {return role;}

    //setter
    public void setId(int id) {this.id = id;}
    public void setName(String name) {this.name = name;}
    public void setEmail(String email) {this.email = email;}
    public void setPassword(String password) {this.password = password;}
    public void setRole(String role) {this.role = role;}




}
