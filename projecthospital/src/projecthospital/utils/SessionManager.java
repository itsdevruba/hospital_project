package projecthospital.utils;

import projecthospital.models.User;
import java.util.HashMap;


public class SessionManager {

    // HashMap to store session data (Generic Collection)
    private static HashMap<String, Object> sessionData = new HashMap<>();
    private static User currentUser = null;

    
    public static void setCurrentUser(User user) {
        currentUser = user;
        if (user != null) {
            sessionData.put("userId", user.getUserId());
            sessionData.put("username", user.getUsername());
            sessionData.put("role", user.getRole());
            sessionData.put("fullName", user.getFullName());
        }
    }

    
    public static User getCurrentUser() {
        return currentUser;
    }

    
    public static int getCurrentUserId() {
        if (currentUser != null) {
            return currentUser.getUserId();
        }
        return -1;
    }

    
    public static String getCurrentUserRole() {
        if (currentUser != null) {
            return currentUser.getRole();
        }
        return null;
    }

    
    public static String getCurrentUserFullName() {
        if (currentUser != null) {
            return currentUser.getFullName();
        }
        return "";
    }

    
    public static boolean isLoggedIn() {
        return currentUser != null;
    }

    
    public static boolean hasRole(String role) {
        return currentUser != null && role.equals(currentUser.getRole());
    }

   
    public static void setSessionData(String key, Object value) {
        sessionData.put(key, value);
    }

    
    public static Object getSessionData(String key) {
        return sessionData.get(key);
    }

   
    public static void logout() {
        currentUser = null;
        sessionData.clear();
        System.out.println("User logged out successfully");
    }

   
    public static HashMap<String, Object> getAllSessionData() {
        return new HashMap<>(sessionData);
    }
}
