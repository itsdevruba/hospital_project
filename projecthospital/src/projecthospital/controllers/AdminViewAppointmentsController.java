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
import projecthospital.models.Appointment;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AdminViewAppointmentsController implements Initializable {

    @FXML private TextField txtSearchPatient;
    @FXML private ComboBox<String> cmbStatusFilter;
    @FXML private Button btnSearch;
    @FXML private Button btnRefresh;

    @FXML private TableView<Appointment> tblAppointments;
    @FXML private TableColumn<Appointment, Integer> colAppointmentId;
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, Date> colDate;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private TableColumn<Appointment, String> colStatus;

    @FXML private Button btnBack;

    private ObservableList<Appointment> allAppointments;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupStatusFilter();
        setupTableColumns();
        loadAllAppointments();
    }

    private void setupStatusFilter() {
        cmbStatusFilter.setItems(FXCollections.observableArrayList(
            "All", "SCHEDULED", "COMPLETED", "CANCELLED"));
        cmbStatusFilter.setValue("All");
    }

    private void setupTableColumns() {
        colAppointmentId.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        colPatient.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getPatient() != null ?
                cellData.getValue().getPatient().getFullName() : "N/A"));
        colDoctor.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getDoctor() != null ?
                cellData.getValue().getDoctor().getFullName() : "N/A"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("appointmentDate"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("appointmentTime"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
    }

    private void loadAllAppointments() {
        try {
            ArrayList<Appointment> appointments = AppointmentDAO.getAllAppointments();
            allAppointments = FXCollections.observableArrayList(appointments);
            tblAppointments.setItems(allAppointments);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Failed to load appointments: " + e.getMessage());
        }
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String patientSearch = txtSearchPatient.getText().toLowerCase();
        String statusFilter = cmbStatusFilter.getValue();

        ObservableList<Appointment> filtered = allAppointments.stream()
            .filter(appt -> {
                boolean matchesPatient = patientSearch.isEmpty() ||
                    (appt.getPatient() != null &&
                     appt.getPatient().getFullName().toLowerCase().contains(patientSearch));
                boolean matchesStatus = "All".equals(statusFilter) ||
                    statusFilter.equals(appt.getStatus());
                return matchesPatient && matchesStatus;
            })
            .collect(Collectors.toCollection(FXCollections::observableArrayList));

        tblAppointments.setItems(filtered);
    }

    @FXML
    private void handleRefresh(ActionEvent event) {
        txtSearchPatient.clear();
        cmbStatusFilter.setValue("All");
        loadAllAppointments();
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
