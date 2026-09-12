package projecthospital.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import projecthospital.database.VisitNoteDAO;
import projecthospital.models.Appointment;
import projecthospital.models.VisitNote;
import projecthospital.utils.SessionManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AddVisitNotesController implements Initializable {

    @FXML
    private Label lblPatientInfo;

    @FXML
    private Label lblAppointmentInfo;

    @FXML
    private TextArea txtNotes;

    @FXML
    private TextArea txtDiagnosis;

    @FXML
    private TextArea txtPrescription;

    @FXML
    private Label lblMessage;

    @FXML
    private Button btnSave;

    @FXML
    private Button btnCancel;

    private Appointment selectedAppointment;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Get selected appointment from session
        selectedAppointment = (Appointment) SessionManager.getSessionData("selectedAppointment");

        if (selectedAppointment != null) {
            // Display patient and appointment info
            lblPatientInfo.setText(selectedAppointment.getPatient().getFullName());
            lblAppointmentInfo.setText(selectedAppointment.getAppointmentDate() + " at " +
                                       selectedAppointment.getAppointmentTime());

            // Try to load existing notes if any
            try {
                VisitNote existingNote = VisitNoteDAO.getVisitNoteByAppointment(
                    selectedAppointment.getAppointmentId());

                if (existingNote != null) {
                    txtNotes.setText(existingNote.getNotes());
                    txtDiagnosis.setText(existingNote.getDiagnosis());
                    txtPrescription.setText(existingNote.getPrescription());
                }
            } catch (SQLException | ClassNotFoundException e) {
                // No existing notes - that's fine
            }
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        lblMessage.setText("");

        // Validate inputs
        if (txtNotes.getText() == null || txtNotes.getText().trim().isEmpty()) {
            lblMessage.setText("Please enter visit notes");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        if (txtDiagnosis.getText() == null || txtDiagnosis.getText().trim().isEmpty()) {
            lblMessage.setText("Please enter diagnosis");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            // Create visit note
            VisitNote visitNote = new VisitNote();
            visitNote.setAppointmentId(selectedAppointment.getAppointmentId());
            visitNote.setNotes(txtNotes.getText().trim());
            visitNote.setDiagnosis(txtDiagnosis.getText().trim());
            visitNote.setPrescription(txtPrescription.getText() != null ?
                                      txtPrescription.getText().trim() : "");

            // Check if note already exists
            VisitNote existingNote = VisitNoteDAO.getVisitNoteByAppointment(
                selectedAppointment.getAppointmentId());

            if (existingNote != null) {
                // Update existing note
                visitNote.setNoteId(existingNote.getNoteId());
                boolean updated = VisitNoteDAO.updateVisitNote(visitNote);
                if (updated) {
                    showSuccessAndReturn("Visit notes updated successfully!");
                } else {
                    lblMessage.setText("Failed to update visit notes");
                    lblMessage.setStyle("-fx-text-fill: red;");
                }
            } else {
                // Add new note
                int noteId = VisitNoteDAO.addVisitNote(visitNote);
                if (noteId > 0) {
                    showSuccessAndReturn("Visit notes saved successfully!");
                } else {
                    lblMessage.setText("Failed to save visit notes");
                    lblMessage.setStyle("-fx-text-fill: red;");
                }
            }

        } catch (SQLException | ClassNotFoundException e) {
            lblMessage.setText("Error: " + e.getMessage());
            lblMessage.setStyle("-fx-text-fill: red;");
            e.printStackTrace();
        }
    }

    private void showSuccessAndReturn(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();

        // Clear session data and return to schedule
        SessionManager.setSessionData("selectedAppointment", null);
        loadScene("/resources/fxml/DoctorSchedule.fxml", "Today's Schedule");
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Clear session data
        SessionManager.setSessionData("selectedAppointment", null);
        loadScene("/resources/fxml/DoctorSchedule.fxml", "Today's Schedule");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnCancel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hospital Appointment System - " + title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
