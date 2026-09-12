package projecthospital.database;

import projecthospital.models.Patient;
import java.sql.*;
import java.util.ArrayList;


public class PatientDAO {

     
    public static Patient getPatientByUserId(int userId) throws SQLException, ClassNotFoundException {
        String query = "SELECT u.*, p.patient_id, p.date_of_birth, p.blood_type " +
                       "FROM users u JOIN patients p ON u.user_id = p.user_id " +
                       "WHERE u.user_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int patientId = rs.getInt("patient_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                Date dateOfBirth = rs.getDate("date_of_birth");
                String bloodType = rs.getString("blood_type");

                return new Patient(userId, username, password, fullName, email, phone,
                                   patientId, dateOfBirth, bloodType);
            }
            return null;

        } catch (SQLException e) {
            throw new SQLException("Failed to get patient: " + e.getMessage());
        }
    }

    
    public static Patient getPatientById(int patientId) throws SQLException, ClassNotFoundException {
        String query = "SELECT u.*, p.patient_id, p.date_of_birth, p.blood_type " +
                       "FROM users u JOIN patients p ON u.user_id = p.user_id " +
                       "WHERE p.patient_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String password = rs.getString("password");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                Date dateOfBirth = rs.getDate("date_of_birth");
                String bloodType = rs.getString("blood_type");

                return new Patient(userId, username, password, fullName, email, phone,
                                   patientId, dateOfBirth, bloodType);
            }
            return null;

        } catch (SQLException e) {
            throw new SQLException("Failed to get patient: " + e.getMessage());
        }
    }

    
    public static ArrayList<Patient> getAllPatients() throws SQLException, ClassNotFoundException {
        ArrayList<Patient> patients = new ArrayList<>();
        String query = "SELECT u.*, p.patient_id, p.date_of_birth, p.blood_type " +
                       "FROM users u JOIN patients p ON u.user_id = p.user_id " +
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
                int patientId = rs.getInt("patient_id");
                Date dateOfBirth = rs.getDate("date_of_birth");
                String bloodType = rs.getString("blood_type");

                Patient patient = new Patient(userId, username, password, fullName, email, phone,
                                              patientId, dateOfBirth, bloodType);
                patients.add(patient);
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to get patients: " + e.getMessage());
        }

        return patients;
    }

   
    public static int addPatient(Patient patient) throws SQLException, ClassNotFoundException {
        try {
            // First, add user record
            int userId = UserDAO.addUser(patient);
            patient.setUserId(userId);

            // Then, add patient-specific record
            String query = "INSERT INTO patients (patient_id, user_id, date_of_birth, blood_type) " +
                           "VALUES (seq_patient_id.NEXTVAL, ?, ?, ?)";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query, new String[]{"patient_id"})) {

                stmt.setInt(1, userId);
                stmt.setDate(2, patient.getDateOfBirth() != null ?
                             new java.sql.Date(patient.getDateOfBirth().getTime()) : null);
                stmt.setString(3, patient.getBloodType());

                stmt.executeUpdate();

                // Get generated patient_id
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    return rs.getInt(1);
                }

                throw new SQLException("Failed to get generated patient ID");
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to add patient: " + e.getMessage());
        }
    }

    
    public static boolean updatePatient(Patient patient) throws SQLException, ClassNotFoundException {
        try {
            // Update user record
            UserDAO.updateUser(patient);

            // Update patient-specific record
            String query = "UPDATE patients SET date_of_birth = ?, blood_type = ? WHERE patient_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setDate(1, patient.getDateOfBirth() != null ?
                             new java.sql.Date(patient.getDateOfBirth().getTime()) : null);
                stmt.setString(2, patient.getBloodType());
                stmt.setInt(3, patient.getPatientId());

                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to update patient: " + e.getMessage());
        }
    }

    
    public static boolean deletePatient(int patientId) throws SQLException, ClassNotFoundException {
        try {
            // Get user_id before deleting patient
            Patient patient = getPatientById(patientId);
            if (patient == null) {
                return false;
            }

            // Delete patient record (user record will be deleted by CASCADE)
            String query = "DELETE FROM patients WHERE patient_id = ?";

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(query)) {

                stmt.setInt(1, patientId);
                stmt.executeUpdate();
            }

            // Delete user record
            return UserDAO.deleteUser(patient.getUserId());

        } catch (SQLException e) {
            throw new SQLException("Failed to delete patient: " + e.getMessage());
        }
    }
}
