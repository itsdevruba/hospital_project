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
import projecthospital.database.DoctorDAO;
import projecthospital.models.Appointment;
import projecthospital.models.Doctor;
import projecthospital.utils.SessionManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class DoctorScheduleController implements Initializable {

    @FXML
    private TableView<Appointment> tblAppointments;

    @FXML
    private TableColumn<Appointment, Integer> colAppointmentId;

    @FXML
    private TableColumn<Appointment, String> colPatient;

    @FXML
    private TableColumn<Appointment, String> colTime;

    @FXML
    private TableColumn<Appointment, String> colStatus;

    @FXML
    private TableColumn<Appointment, String> colBloodType;

    @FXML
    private Button btnAddNotes;

    @FXML
    private Button btnCompleteAppointment;

    @FXML
    private Button btnBack;

    private ObservableList<Appointment> appointmentsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colPatient.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPatient() != null ?
                cellData.getValue().getPatient().getFullName() : "N/A"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colBloodType.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPatient() != null ?
                cellData.getValue().getPatient().getBloodType() : "N/A"));

        // Load today's appointments
        loadTodayAppointments();
    }

    private void loadTodayAppointments() {
        try {
            int userId = SessionManager.getCurrentUserId();
            Doctor doctor = DoctorDAO.getDoctorByUserId(userId);

            ArrayList<Appointment> appointments =
                AppointmentDAO.getTodayAppointmentsByDoctor(doctor.getDoctorId());
            appointmentsList = FXCollections.observableArrayList(appointments);
            tblAppointments.setItems(appointmentsList);

        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddNotes(ActionEvent event) {
        Appointment selected = tblAppointments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an appointment");
            return;
        }

        // Store selected appointment in session
        SessionManager.setSessionData("selectedAppointment", selected);

        // Navigate to add notes screen
        loadScene("/resources/fxml/AddVisitNotes.fxml", "Add Visit Notes");
    }

    @FXML
    private void handleCompleteAppointment(ActionEvent event) {
        Appointment selected = tblAppointments.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Warning", "Please select an appointment");
            return;
        }

        if (!"SCHEDULED".equals(selected.getStatus())) {
            showAlert("Warning", "Only scheduled appointments can be marked as completed");
            return;
        }

        try {
            AppointmentDAO.updateAppointmentStatus(selected.getAppointmentId(), "COMPLETED");
            showAlert("Success", "Appointment marked as completed");
            loadTodayAppointments();  // Refresh table
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Failed to update appointment: " + e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        loadScene("/resources/fxml/DoctorDashboard.fxml", "Doctor Dashboard");
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
