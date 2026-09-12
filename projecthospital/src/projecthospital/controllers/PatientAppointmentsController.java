package projecthospital.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import projecthospital.database.AppointmentDAO;
import projecthospital.database.PatientDAO;
import projecthospital.database.VisitNoteDAO;
import projecthospital.models.Appointment;
import projecthospital.models.Patient;
import projecthospital.models.VisitNote;
import projecthospital.utils.SessionManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class PatientAppointmentsController implements Initializable {

    @FXML
    private TableView<Appointment> tblAppointments;

    @FXML
    private TableColumn<Appointment, Integer> colAppointmentId;

    @FXML
    private TableColumn<Appointment, String> colDoctor;

    @FXML
    private TableColumn<Appointment, Date> colDate;

    @FXML
    private TableColumn<Appointment, String> colTime;

    @FXML
    private TableColumn<Appointment, String> colStatus;

    @FXML
    private Button btnCancelAppointment;

    @FXML
    private Button btnViewNotes;

    @FXML
    private Button btnBack;

    private ObservableList<Appointment> appointmentsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colDoctor.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDoctor() != null ?
                cellData.getValue().getDoctor().getFullName() : "N/A"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load appointments
        loadAppointments();
    }

    private void loadAppointments() {
        try {
            int userId = SessionManager.getCurrentUserId();
            Patient patient = PatientDAO.getPatientByUserId(userId);

            ArrayList<Appointment> appointments =
                AppointmentDAO.getAppointmentsByPatient(patient.getPatientId());
            appointmentsList = FXCollections.observableArrayList(appointments);
            tblAppointments.setItems(appointmentsList);

        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelAppointment(ActionEvent event) {
        Appointment selected = tblAppointments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an appointment");
            return;
        }

        if (!"SCHEDULED".equals(selected.getStatus())) {
            showAlert("Warning", "Only scheduled appointments can be cancelled");
            return;
        }

        // Confirm cancellation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancellation");
        confirm.setHeaderText("Cancel Appointment");
        confirm.setContentText("Are you sure you want to cancel this appointment?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                AppointmentDAO.cancelAppointment(selected.getAppointmentId());
                showAlert("Success", "Appointment cancelled successfully");
                loadAppointments();  // Refresh table
            } catch (SQLException | ClassNotFoundException e) {
                showAlert("Error", "Failed to cancel appointment: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleViewNotes(ActionEvent event) {
        Appointment selected = tblAppointments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an appointment");
            return;
        }

        if (!"COMPLETED".equals(selected.getStatus())) {
            showAlert("Info", "Visit notes are only available for completed appointments");
            return;
        }

        try {
            VisitNote note = VisitNoteDAO.getVisitNoteByAppointment(selected.getAppointmentId());
            if (note == null) {
                showAlert("Info", "No visit notes found for this appointment");
                return;
            }

            // Display notes in alert
            String noteDetails = "Visit Notes\n\n" +
                               "Notes: " + note.getNotes() + "\n\n" +
                               "Diagnosis: " + note.getDiagnosis() + "\n\n" +
                               "Prescription: " + note.getPrescription();

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Visit Notes");
            alert.setHeaderText("Appointment #" + selected.getAppointmentId());
            alert.setContentText(noteDetails);
            alert.showAndWait();

        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Failed to load visit notes: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        loadScene("/resources/fxml/PatientDashboard.fxml", "Patient Dashboard");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnBack.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hospital Appointment System - " + title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
