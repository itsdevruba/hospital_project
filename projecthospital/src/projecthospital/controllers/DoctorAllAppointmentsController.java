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
import java.util.stream.Collectors;

public class DoctorAllAppointmentsController implements Initializable {

    @FXML
    private ComboBox<String> cmbStatusFilter;

    @FXML
    private Button btnFilter;

    @FXML
    private TableView<Appointment> tblAppointments;

    @FXML
    private TableColumn<Appointment, Integer> colAppointmentId;

    @FXML
    private TableColumn<Appointment, String> colPatient;

    @FXML
    private TableColumn<Appointment, Date> colDate;

    @FXML
    private TableColumn<Appointment, String> colTime;

    @FXML
    private TableColumn<Appointment, String> colStatus;

    @FXML
    private Button btnBack;

    private ObservableList<Appointment> allAppointments;
    private int doctorId;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup status filter
        cmbStatusFilter.setItems(FXCollections.observableArrayList(
            "All", "SCHEDULED", "COMPLETED", "CANCELLED"));
        cmbStatusFilter.setValue("All");

        // Setup table columns
        colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colPatient.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPatient() != null ?
                cellData.getValue().getPatient().getFullName() : "N/A"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Load appointments
        loadAllAppointments();
    }

    private void loadAllAppointments() {
        try {
            int userId = SessionManager.getCurrentUserId();
            Doctor doctor = DoctorDAO.getDoctorByUserId(userId);
            doctorId = doctor.getDoctorId();

            ArrayList<Appointment> appointments =
                AppointmentDAO.getAppointmentsByDoctor(doctorId);
            allAppointments = FXCollections.observableArrayList(appointments);
            tblAppointments.setItems(allAppointments);

        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilter(ActionEvent event) {
        String selectedStatus = cmbStatusFilter.getValue();

        if ("All".equals(selectedStatus)) {
            tblAppointments.setItems(allAppointments);
        } else {
            ObservableList<Appointment> filtered = allAppointments.stream()
                .filter(appt -> selectedStatus.equals(appt.getStatus()))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
            tblAppointments.setItems(filtered);
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
