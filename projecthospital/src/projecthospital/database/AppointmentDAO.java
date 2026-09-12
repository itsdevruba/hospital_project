package projecthospital.database;

import projecthospital.models.Appointment;
import projecthospital.models.Doctor;
import projecthospital.models.Patient;
import java.sql.*;
import java.util.ArrayList;


public class AppointmentDAO {

   
    public static Appointment getAppointmentById(int appointmentId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM appointments WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");
                Date appointmentDate = rs.getDate("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String status = rs.getString("status");

                // Fetch Patient and Doctor objects (Composition)
                Patient patient = PatientDAO.getPatientById(patientId);
                Doctor doctor = DoctorDAO.getDoctorById(doctorId);

                return new Appointment(appointmentId, patient, doctor,
                                       appointmentDate, appointmentTime, status);
            }
            return null;

        } catch (SQLException e) {
            throw new SQLException("Failed to get appointment: " + e.getMessage());
        }
    }

   
    public static ArrayList<Appointment> getAllAppointments() throws SQLException, ClassNotFoundException {
        ArrayList<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments ORDER BY appointment_date, appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int patientId = rs.getInt("patient_id");
                int doctorId = rs.getInt("doctor_id");
                Date appointmentDate = rs.getDate("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String status = rs.getString("status");

                Patient patient = PatientDAO.getPatientById(patientId);
                Doctor doctor = DoctorDAO.getDoctorById(doctorId);

                Appointment appointment = new Appointment(appointmentId, patient, doctor,
                                                          appointmentDate, appointmentTime, status);
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to get appointments: " + e.getMessage());
        }

        return appointments;
    }

    
    public static ArrayList<Appointment> getAppointmentsByPatient(int patientId) throws SQLException, ClassNotFoundException {
        ArrayList<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments WHERE patient_id = ? ORDER BY appointment_date DESC, appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int doctorId = rs.getInt("doctor_id");
                Date appointmentDate = rs.getDate("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String status = rs.getString("status");

                Patient patient = PatientDAO.getPatientById(patientId);
                Doctor doctor = DoctorDAO.getDoctorById(doctorId);

                Appointment appointment = new Appointment(appointmentId, patient, doctor,
                                                          appointmentDate, appointmentTime, status);
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to get patient appointments: " + e.getMessage());
        }

        return appointments;
    }

    
    public static ArrayList<Appointment> getAppointmentsByDoctor(int doctorId) throws SQLException, ClassNotFoundException {
        ArrayList<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments WHERE doctor_id = ? ORDER BY appointment_date, appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int patientId = rs.getInt("patient_id");
                Date appointmentDate = rs.getDate("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String status = rs.getString("status");

                Patient patient = PatientDAO.getPatientById(patientId);
                Doctor doctor = DoctorDAO.getDoctorById(doctorId);

                Appointment appointment = new Appointment(appointmentId, patient, doctor,
                                                          appointmentDate, appointmentTime, status);
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to get doctor appointments: " + e.getMessage());
        }

        return appointments;
    }

   
    public static ArrayList<Appointment> getTodayAppointmentsByDoctor(int doctorId) throws SQLException, ClassNotFoundException {
        ArrayList<Appointment> appointments = new ArrayList<>();
        String query = "SELECT * FROM appointments WHERE doctor_id = ? AND " +
                       "TRUNC(appointment_date) = TRUNC(SYSDATE) ORDER BY appointment_time";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                int patientId = rs.getInt("patient_id");
                Date appointmentDate = rs.getDate("appointment_date");
                String appointmentTime = rs.getString("appointment_time");
                String status = rs.getString("status");

                Patient patient = PatientDAO.getPatientById(patientId);
                Doctor doctor = DoctorDAO.getDoctorById(doctorId);

                Appointment appointment = new Appointment(appointmentId, patient, doctor,
                                                          appointmentDate, appointmentTime, status);
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to get today's appointments: " + e.getMessage());
        }

        return appointments;
    }

  
    public static int addAppointment(Appointment appointment)
            throws SQLException, ClassNotFoundException, IllegalStateException {

        // Check for conflicts
        if (checkAppointmentConflict(appointment.getDoctorId(),
                                      new java.sql.Date(appointment.getAppointmentDate().getTime()),
                                      appointment.getAppointmentTime())) {
            throw new IllegalStateException(
                "This time slot is already booked. Please choose a different time.");
        }

        String query = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, " +
                       "appointment_date, appointment_time, status) " +
                       "VALUES (seq_appointment_id.NEXTVAL, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, new String[]{"appointment_id"})) {

            stmt.setInt(1, appointment.getPatientId());
            stmt.setInt(2, appointment.getDoctorId());
            stmt.setDate(3, new java.sql.Date(appointment.getAppointmentDate().getTime()));
            stmt.setString(4, appointment.getAppointmentTime());
            stmt.setString(5, "SCHEDULED");

            stmt.executeUpdate();

            // Get generated appointment_id
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new SQLException("Failed to get generated appointment ID");

        } catch (SQLException e) {
            throw new SQLException("Failed to add appointment: " + e.getMessage());
        }
    }

    public static boolean updateAppointmentStatus(int appointmentId, String status)
            throws SQLException, ClassNotFoundException {
        String query = "UPDATE appointments SET status = ? WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, status);
            stmt.setInt(2, appointmentId);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new SQLException("Failed to update appointment: " + e.getMessage());
        }
    }

    public static boolean cancelAppointment(int appointmentId) throws SQLException, ClassNotFoundException {
        return updateAppointmentStatus(appointmentId, "CANCELLED");
    }

   
    private static boolean checkAppointmentConflict(int doctorId, Date appointmentDate,
                                                     String appointmentTime) throws SQLException, ClassNotFoundException {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND " +
                       "appointment_date = ? AND appointment_time = ? AND status = 'SCHEDULED'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, doctorId);
            stmt.setDate(2, appointmentDate);
            stmt.setString(3, appointmentTime);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            return false;

        } catch (SQLException e) {
            throw new SQLException("Failed to check appointment conflict: " + e.getMessage());
        }
    }
}
