import java.util.InputMismatchException;
import java.util.Scanner;

public class auth {
    private static Scanner sc = new Scanner(System.in);
    private boolean login(String email, String password) { //<-- for checking if user exist and input is correct
        UserDAO userdao = new UserDAO();
        User user = userdao.loginUser(email);
        if (user != null) {
            if(user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    private boolean register(String name, String email, String password, String role) {


        return false;
    }

    public static void User(){
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
                if(choice == 1) {
                    while(true){
                        System.out.println("             Loing Page           ");
                        System.out.println("+--------------------------------+");
                        System.out.print("Enter Your Full Name(min-4, max-8 ): ");
                        String fullName = sc.nextLine();

                    }




                }else if(choice == 2) {
                    System.out.println("           Register Page          ");
                    System.out.println("+--------------------------------+");
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
