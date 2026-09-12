package projecthospital.controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import projecthospital.database.AppointmentDAO;
import projecthospital.database.DoctorDAO;
import projecthospital.database.PatientDAO;
import projecthospital.models.Appointment;
import projecthospital.models.Doctor;
import projecthospital.models.Patient;
import projecthospital.utils.SessionManager;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BookAppointmentController implements Initializable {

    @FXML
    private ComboBox<Doctor> cmbDoctor;

    @FXML
    private DatePicker dpAppointmentDate;

    @FXML
    private ComboBox<String> cmbTime;

    @FXML
    private Label lblMessage;

    @FXML
    private Button btnConfirm;

    @FXML
    private Button btnCancel;

    private ArrayList<Doctor> doctors;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Load doctors
        loadDoctors();

        // Populate time slots
        cmbTime.setItems(FXCollections.observableArrayList(
            "09:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
            "02:00 PM", "03:00 PM", "04:00 PM", "05:00 PM"
        ));

        // Check if doctor was pre-selected from browse screen
        Doctor selectedDoctor = (Doctor) SessionManager.getSessionData("selectedDoctor");
        if (selectedDoctor != null) {
            cmbDoctor.setValue(selectedDoctor);
            SessionManager.setSessionData("selectedDoctor", null);  // Clear
        }
    }

    private void loadDoctors() {
        try {
            doctors = DoctorDAO.getAllDoctors();
            cmbDoctor.setItems(FXCollections.observableArrayList(doctors));
        } catch (SQLException | ClassNotFoundException e) {
            lblMessage.setText("Error loading doctors: " + e.getMessage());
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleConfirm(ActionEvent event) {
        lblMessage.setText("");

        // Validate inputs
        if (cmbDoctor.getValue() == null) {
            lblMessage.setText("Please select a doctor");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        if (dpAppointmentDate.getValue() == null) {
            lblMessage.setText("Please select a date");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        if (cmbTime.getValue() == null) {
            lblMessage.setText("Please select a time");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        // Check if date is in the future
        if (dpAppointmentDate.getValue().isBefore(LocalDate.now())) {
            lblMessage.setText("Please select a future date");
            lblMessage.setStyle("-fx-text-fill: red;");
            return;
        }

        try {
            // Get current patient
            int userId = SessionManager.getCurrentUserId();
            Patient patient = PatientDAO.getPatientByUserId(userId);

            // Create appointment
            Appointment appointment = new Appointment();
            appointment.setPatientId(patient.getPatientId());
            appointment.setDoctorId(cmbDoctor.getValue().getDoctorId());
            appointment.setAppointmentDate(java.sql.Date.valueOf(dpAppointmentDate.getValue()));
            appointment.setAppointmentTime(cmbTime.getValue());

            // Save appointment
            AppointmentDAO.addAppointment(appointment);

            lblMessage.setText("Appointment booked successfully!");
            lblMessage.setStyle("-fx-text-fill: green;");

            // Clear form
            cmbDoctor.setValue(null);
            dpAppointmentDate.setValue(null);
            cmbTime.setValue(null);

        } catch (IllegalStateException e) {
            lblMessage.setText(e.getMessage());
            lblMessage.setStyle("-fx-text-fill: red;");
        } catch (SQLException | ClassNotFoundException e) {
            lblMessage.setText("Error: " + e.getMessage());
            lblMessage.setStyle("-fx-text-fill: red;");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        loadScene("/resources/fxml/BrowseDoctors.fxml", "Browse Doctors");
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
