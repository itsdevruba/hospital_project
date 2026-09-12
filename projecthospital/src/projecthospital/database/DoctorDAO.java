package projecthospital.database;

import projecthospital.models.Doctor;
import java.sql.*;
import java.util.ArrayList;


public class DoctorDAO {

    public static Doctor getDoctorByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT u.*, d.doctor_id, d.specialization, d.license_number, d.years_of_experience " +
                       "FROM users u JOIN doctors d ON u.user_id = d.user_id " +
                       "WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int doctorId = rs.getInt("doctor_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String specialization = rs.getString("specialization");
                String licenseNumber = rs.getString("license_number");
                int yearsOfExperience = rs.getInt("years_of_experience");

                return new Doctor(userId, username, password, fullName, email, phone,
                                  doctorId, specialization, licenseNumber, yearsOfExperience);
            }
            return null;

        } catch (SQLException e) {
            throw new SQLException("Failed to get doctor: " + e.getMessage());
        }
    }

    
    public static Doctor getDoctorById(int doctorId) throws SQLException, ClassNotFoundException {
        String query = "SELECT u.*, d.doctor_id, d.specialization, d.license_number, d.years_of_experience " +
                       "FROM users u JOIN doctors d ON u.user_id = d.user_id " +
                       "WHERE d.doctor_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String specialization = rs.getString("specialization");
                String licenseNumber = rs.getString("license_number");
                int yearsOfExperience = rs.getInt("years_of_experience");

                return new Doctor(userId, username, password, fullName, email, phone,
                                  doctorId, specialization, licenseNumber, yearsOfExperience);
            }
            return null;

        } catch (SQLException e) {
            throw new SQLException("Failed to get doctor: " + e.getMessage());
        }
    }

    
    public static ArrayList<Doctor> getAllDoctors() throws SQLException, ClassNotFoundException {
        ArrayList<Doctor> doctors = new ArrayList<>();
        String query = "SELECT u.*, d.doctor_id, d.specialization, d.license_number, d.years_of_experience " +
                       "FROM users u JOIN doctors d ON u.user_id = d.user_id " +
                       "ORDER BY u.full_name";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                int doctorId = rs.getInt("doctor_id");
                String specialization = rs.getString("specialization");
                String licenseNumber = rs.getString("license_number");
                int yearsOfExperience = rs.getInt("years_of_experience");

                Doctor doctor = new Doctor(userId, username, password, fullName, email, phone,
                                           doctorId, specialization, licenseNumber, yearsOfExperience);
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to get doctors: " + e.getMessage());
        }

        return doctors;
    }

    public static int addDoctor(Doctor doctor) throws SQLException, ClassNotFoundException {
        try {
            // First, add user record
            int userId = UserDAO.addUser(doctor);
            doctor.setUserId(userId);

            // Then, add doctor-specific record
            String query = "INSERT INTO doctors (doctor_id, user_id, specialization, license_number, years_of_experience) " +
                           "VALUES (seq_doctor_id.NEXTVAL, ?, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query, new String[]{"doctor_id"})) {

                stmt.setInt(1, userId);
                stmt.setString(2, doctor.getSpecialization());
                stmt.setString(3, doctor.getLicenseNumber());
                stmt.setInt(4, doctor.getYearsOfExperience());

                stmt.executeUpdate();

                // Get generated doctor_id
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                throw new SQLException("Failed to get generated doctor ID");
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to add doctor: " + e.getMessage());
        }
    }

    
    public static boolean updateDoctor(Doctor doctor) throws SQLException, ClassNotFoundException {
        try {
            // Update user record
            UserDAO.updateUser(doctor);

            // Update doctor-specific record
            String query = "UPDATE doctors SET specialization = ?, license_number = ?, " +
                           "years_of_experience = ? WHERE doctor_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setString(1, doctor.getSpecialization());
                stmt.setString(2, doctor.getLicenseNumber());
                stmt.setInt(3, doctor.getYearsOfExperience());
                stmt.setInt(4, doctor.getDoctorId());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to update doctor: " + e.getMessage());
        }
    }

    
    public static boolean deleteDoctor(int doctorId) throws SQLException, ClassNotFoundException {
        try {
            // Get user_id before deleting doctor
            Doctor doctor = getDoctorById(doctorId);
            if (doctor == null) {
                return false;
            }

            // Delete doctor record (user record will be deleted by CASCADE)
            String query = "DELETE FROM doctors WHERE doctor_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, doctorId);
                stmt.executeUpdate();
            }

            // Delete user record
            return UserDAO.deleteUser(doctor.getUserId());

        } catch (SQLException e) {
            throw new SQLException("Failed to delete doctor: " + e.getMessage());
        }
    }
}
