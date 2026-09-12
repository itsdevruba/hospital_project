package projecthospital.utils;

import java.util.regex.Pattern;


public class Validator {

    // Regex patterns
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    private static final Pattern PHONE_PATTERN =
        Pattern.compile("^[0-9]{10}$");

    private static final Pattern USERNAME_PATTERN =
        Pattern.compile("^[a-zA-Z0-9._]{3,20}$");

    
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

   
    public static boolean isValidEmail(String email) {
        if (!isNotEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    
    public static boolean isValidPhone(String phone) {
        if (!isNotEmpty(phone)) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    
    public static boolean isValidUsername(String username) {
        if (!isNotEmpty(username)) {
            return false;
        }
        return USERNAME_PATTERN.matcher(username).matches();
    }

    
    public static boolean isValidPassword(String password) {
        return isNotEmpty(password) && password.length() >= 6;
    }

    
    public static boolean isPositiveNumber(int number) {
        return number > 0;
    }

   
    public static boolean isAlphabeticWithSpaces(String value) {
        if (!isNotEmpty(value)) {
            return false;
        }
        return value.matches("^[a-zA-Z ]+$");
    }

    
    public static String validateRequiredField(String fieldName, String value) {
        if (!isNotEmpty(value)) {
            return fieldName + " is required";
        }
        return null;
    }

   
    public static String validateEmailField(String email) {
        if (!isNotEmpty(email)) {
            return "Email is required";
        }
        if (!isValidEmail(email)) {
            return "Invalid email format";
        }
        return null;
    }

    
    public static String validatePhoneField(String phone) {
        if (!isNotEmpty(phone)) {
            return "Phone is required";
        }
        if (!isValidPhone(phone)) {
            return "Invalid phone format (must be 10 digits)";
        }
        return null;
    }

   
    public static String validateUsernameField(String username) {
        if (!isNotEmpty(username)) {
            return "Username is required";
        }
        if (!isValidUsername(username)) {
            return "Username must be 3-20 characters (letters, numbers, dots, underscores only)";
        }
        return null;
    }

    
    public static String validatePasswordField(String password) {
        if (!isNotEmpty(password)) {
            return "Password is required";
        }
        if (!isValidPassword(password)) {
            return "Password must be at least 6 characters";
        }
        return null;
    }
}
