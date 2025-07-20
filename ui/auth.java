import java.util.InputMismatchException;
import java.util.Scanner;

public class auth {
    private static Scanner sc = new Scanner(System.in);

    private static boolean login(String email, String password) { //<-- for checking if user exist and input is correct
        UserDAO userdao = new UserDAO();//<-- this create new userdao to access the function insde
        User user = userdao.loginUser(email);//<-- this call the funtion,and if nessery we can change this to a static
        if (user != null) {
            if(user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private static boolean register(String name, String email, String password, String role) {
        //Dont worry about the id it will be override by the database id
        User newUser = new User(1,name,email,password,role);
        UserDAO userdao = new UserDAO();
        return userdao.regusterUser(newUser);
    }

    public static void meno(String role){
        while (true){
            try{
                System.out.println("             MAIN MENU            ");
                System.out.println("+--------------------------------+");
                System.out.println("[1] Login");
                System.out.println("[2] Register");
                System.out.println("[3] Go Back");
                System.out.print("Choice: ");
                int choice = sc.nextInt();
                sc.nextLine();
                //Login
                if(choice == 1) {
                    String LoginEmail;
                    String LoginPassword;

                    System.out.println("             Loing Page           ");
                    System.out.println("+--------------------------------+");
                    //email
                    System.out.print("\nEnter Your Email: ");
                    LoginEmail = sc.nextLine();
                    //password
                    System.out.print("\nEnter Your Password: ");
                    LoginPassword = sc.nextLine();

                    //check info form db
                    if(login(LoginEmail,LoginPassword)){
                        System.out.println("Login Successfully!");
                        if(role.equals("user")){
                            UserMenu.meno();
                        } else if (role.equals("manager")) {
                            ManagerMenu.meno();
                        }
                    }else{System.err.println("Login Failed:Please check your email and password, then try again.");}

                //Register
                }else if(choice == 2) {
                    String fullName;
                    String regEmail;
                    String regPassword;

                    System.out.println("           Register Page          ");
                    System.out.println("+--------------------------------+");

                    while(true){
                        System.out.print("\nEnter Your Full Name: ");
                        fullName = sc.nextLine();
                        if(InputValidator.isAlph(fullName)){
                            break;}else {System.err.println("Invalid Name!!");}
                    }
                    while (true){
                        System.out.print("\nEnter Your Email: ");
                        regEmail = sc.nextLine();
                        if(InputValidator.isValidEmail(regEmail)){
                            break;}else {System.err.println("Invalid Email");}
                    }
                    while (true){
                        System.out.print("\nEnter Your Password(min-4, max-16 ): ");
                        regPassword = sc.nextLine();
                        if(InputValidator.checkStringLength(4,16,regPassword)){
                            break;}else {System.err.println("Invalid Password");}
                    }
                    if(register(fullName,regEmail,regPassword,role)){
                        System.out.println("Registered Successfully!");
                    }else{System.err.println("Register Failed:try again.");}


                }else if(choice == 3) {
                    break;
                }
            }catch(InputMismatchException e){
                System.err.println("Please enter a valid option!");
                sc.nextLine();
            }

        }


    }
}
