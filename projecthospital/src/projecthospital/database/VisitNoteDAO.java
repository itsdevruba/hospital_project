package projecthospital.database;

import projecthospital.models.Appointment;
import projecthospital.models.VisitNote;
import java.sql.*;
import java.util.ArrayList;


public class VisitNoteDAO {

    
    public static VisitNote getVisitNoteById(int noteId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM visit_notes WHERE note_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, noteId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int appointmentId = rs.getInt("appointment_id");
                String notes = rs.getString("notes");
                String diagnosis = rs.getString("diagnosis");
                String prescription = rs.getString("prescription");

                // Fetch Appointment object (Composition)
                Appointment appointment = AppointmentDAO.getAppointmentById(appointmentId);

                return new VisitNote(noteId, appointment, notes, diagnosis, prescription);
            }
            return null;

        } catch (SQLException e) {
            throw new SQLException("Failed to get visit note: " + e.getMessage());
        }
    }

    
    public static VisitNote getVisitNoteByAppointment(int appointmentId) throws SQLException, ClassNotFoundException {
        String query = "SELECT * FROM visit_notes WHERE appointment_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, appointmentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                int noteId = rs.getInt("note_id");
                String notes = rs.getString("notes");
                String diagnosis = rs.getString("diagnosis");
                String prescription = rs.getString("prescription");

                Appointment appointment = AppointmentDAO.getAppointmentById(appointmentId);

                return new VisitNote(noteId, appointment, notes, diagnosis, prescription);
            }
            return null;

        } catch (SQLException e) {
            throw new SQLException("Failed to get visit note: " + e.getMessage());
        }
    }

  
    public static ArrayList<VisitNote> getVisitNotesByPatient(int patientId) throws SQLException, ClassNotFoundException {
        ArrayList<VisitNote> visitNotes = new ArrayList<>();
        String query = "SELECT vn.* FROM visit_notes vn " +
                       "JOIN appointments a ON vn.appointment_id = a.appointment_id " +
                       "WHERE a.patient_id = ? ORDER BY vn.created_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, patientId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int noteId = rs.getInt("note_id");
                int appointmentId = rs.getInt("appointment_id");
                String notes = rs.getString("notes");
                String diagnosis = rs.getString("diagnosis");
                String prescription = rs.getString("prescription");

                Appointment appointment = AppointmentDAO.getAppointmentById(appointmentId);
                VisitNote visitNote = new VisitNote(noteId, appointment, notes, diagnosis, prescription);
                visitNotes.add(visitNote);
            }

        } catch (SQLException e) {
            throw new SQLException("Failed to get visit notes: " + e.getMessage());
        }

        return visitNotes;
    }

   
    public static int addVisitNote(VisitNote visitNote) throws SQLException, ClassNotFoundException {
        String query = "INSERT INTO visit_notes (note_id, appointment_id, notes, diagnosis, prescription) " +
                       "VALUES (seq_note_id.NEXTVAL, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, new String[]{"note_id"})) {

            stmt.setInt(1, visitNote.getAppointmentId());
            stmt.setString(2, visitNote.getNotes());
            stmt.setString(3, visitNote.getDiagnosis());
            stmt.setString(4, visitNote.getPrescription());

            stmt.executeUpdate();

            // Get generated note_id
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }

            throw new SQLException("Failed to get generated note ID");

        } catch (SQLException e) {
            throw new SQLException("Failed to add visit note: " + e.getMessage());
        }
    }

    /**
     * Update visit note
     * @param visitNote VisitNote object
     * @return true if successful
     * @throws SQLException if database error occurs
     * @throws ClassNotFoundException if driver class is not found
     */
    public static boolean updateVisitNote(VisitNote visitNote) throws SQLException, ClassNotFoundException {
        String query = "UPDATE visit_notes SET notes = ?, diagnosis = ?, prescription = ? " +
                       "WHERE note_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, visitNote.getNotes());
            stmt.setString(2, visitNote.getDiagnosis());
            stmt.setString(3, visitNote.getPrescription());
            stmt.setInt(4, visitNote.getNoteId());

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new SQLException("Failed to update visit note: " + e.getMessage());
        }
    }

    
    public static boolean deleteVisitNote(int noteId) throws SQLException, ClassNotFoundException {
        String query = "DELETE FROM visit_notes WHERE note_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, noteId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            throw new SQLException("Failed to delete visit note: " + e.getMessage());
        }
    }
}
