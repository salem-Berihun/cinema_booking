import java.util.InputMismatchException;
import java.util.Scanner;

public class main {
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            System.out.println("+++++Welcome To Cinema Booking Application+++++");
            System.out.println("Your:");
            System.out.println("    1.Customer");
            System.out.println("    2.Manager");
            System.out.println("Enter: ");
            int choice = sc.nextInt();
            sc.nextLine();
            if(choice==1){
                
            }


        }catch(InputMismatchException e){
            System.err.println("Please enter a valid option!");
        }
    }
}
