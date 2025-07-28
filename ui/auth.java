import java.util.InputMismatchException;
import java.util.Scanner;

public class auth {
    private static Scanner sc = new Scanner(System.in);

    public User login(String email, String password) { //<-- for checking if user exist and input is correct
        UserDAO userdao = new UserDAO();//<-- this create new userdao to access the function insde
        User user = userdao.loginUser(email);//<-- this call the funtion,and if nessery we can change this to a static
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }return null;
    }

    public boolean register(String name, String email, String password, String role) {
        //Dont worry about the id it will be override by the database id
        User newUser = new User(1,name,email,password,role);
        UserDAO userdao = new UserDAO();
        return userdao.regusterUser(newUser);
    }

}
