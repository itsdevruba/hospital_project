package projecthospital.database;

import projecthospital.models.*;
import java.sql.*;


public class UserDAO {

    
    public static User login(String username, String password, String role)
            throws IllegalArgumentException, SQLException, ClassNotFoundException {

        String query = "SELECT * FROM users WHERE username = ? AND password = ? AND user_type = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, role);

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                // Create appropriate user object based on role
                if ("PATIENT".equals(role)) {
                    return PatientDAO.getPatientByUserId(userId);
                } else if ("DOCTOR".equals(role)) {
                    return DoctorDAO.getDoctorByUserId(userId);
                } else if ("ADMIN".equals(role)) {
                    Administrator admin = new Administrator(userId, username, password, fullName, email, phone);
                    return admin;
                }
            }

            throw new IllegalArgumentException("Invalid username, password, or role");
        }
    }

    
    public static User getUserById(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String username = rs.getString("username");
                String password = rs.getString("password");
                String role = rs.getString("user_type");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");

                if ("ADMIN".equals(role)) {
                    return new Administrator(userId, username, password, fullName, email, phone);
                }
            }
            return null;
        }
    }

    
    public static int addUser(User user) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO users (user_id, username, password, user_type, full_name, email, phone) " +
                       "VALUES (seq_user_id.NEXTVAL, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, new String[]{"user_id"})) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getFullName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getPhone());

            stmt.executeUpdate();

            // Get generated user_id
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new SQLException("Failed to get generated user ID");
        }
    }

    
    public static boolean updateUser(User user) throws SQLException, ClassNotFoundException {
        String query = "UPDATE users SET username = ?, password = ?, full_name = ?, " +
                       "email = ?, phone = ? WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getPhone());
            stmt.setInt(6, user.getUserId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

   
    public static boolean deleteUser(int userId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM users WHERE user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    
    public static boolean usernameExists(String username) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;
        }
    }
}
