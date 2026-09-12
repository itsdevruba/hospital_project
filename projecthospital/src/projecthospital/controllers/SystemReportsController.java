package projecthospital.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import projecthospital.database.AppointmentDAO;
import projecthospital.database.DoctorDAO;
import projecthospital.database.PatientDAO;
import projecthospital.models.Appointment;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SystemReportsController implements Initializable {

    @FXML private Label lblTotalPatients;
    @FXML private Label lblTotalDoctors;
    @FXML private Label lblTotalAppointments;
    @FXML private Label lblScheduledAppointments;
    @FXML private Label lblCompletedAppointments;
    @FXML private Label lblCancelledAppointments;

    @FXML private Button btnRefresh;
    @FXML private Button btnBack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadStatistics();
    }

    private void loadStatistics() {
        try {
            // Get total patients
            int totalPatients = PatientDAO.getAllPatients().size();
            lblTotalPatients.setText(String.valueOf(totalPatients));

            // Get total doctors
            int totalDoctors = DoctorDAO.getAllDoctors().size();
            lblTotalDoctors.setText(String.valueOf(totalDoctors));

            // Get all appointments
            ArrayList<Appointment> allAppointments = AppointmentDAO.getAllAppointments();
            lblTotalAppointments.setText(String.valueOf(allAppointments.size()));

            // Count by status
            long scheduled = allAppointments.stream()
                .filter(a -> "SCHEDULED".equals(a.getStatus())).count();
            long completed = allAppointments.stream()
                .filter(a -> "COMPLETED".equals(a.getStatus())).count();
            long cancelled = allAppointments.stream()
                .filter(a -> "CANCELLED".equals(a.getStatus())).count();

            lblScheduledAppointments.setText(String.valueOf(scheduled));
            lblCompletedAppointments.setText(String.valueOf(completed));
            lblCancelledAppointments.setText(String.valueOf(cancelled));

        } catch (SQLException | ClassNotFoundException e) {
            System.err.println("Failed to load statistics: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        loadStatistics();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        loadScene("/resources/fxml/AdminDashboard.fxml", "Admin Dashboard");
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
}
