package util;

public class InputValidator {
    public static boolean isAlph(String text){
        //prevents null pointer crash,ensures it's not blank,confirms only letters
        if(text != null && !text.isEmpty() && text.matches("[a-zA-Z_ ]+")){
            return true;
        }
        return false;
    }
    public static boolean isInteger(String num){
        if(num != null && !num.isEmpty() && num.matches("\\d+")){
            return true;
        }
        return false;
    }
    public static boolean isValidEmail(String email) {
        if (email != null && !email.isEmpty() && email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")){
            return true;
        }
        return false;
    }
    public static boolean checkIntLength(int min , int max, int num){
        int length = String.valueOf(Math.abs(num)).length();
        if(length >= min && length <= max){
            return true;
        }
        return false;
    }

    public static boolean checkStringLength(int min , int max, String text){
        if(text.length() >= min && text.length() <= max){
            return true;
        }
        return false;
    }

}
