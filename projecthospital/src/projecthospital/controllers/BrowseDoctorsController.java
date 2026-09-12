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
import projecthospital.database.DoctorDAO;
import projecthospital.models.Doctor;
import projecthospital.utils.SessionManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class BrowseDoctorsController implements Initializable {

    @FXML
    private TableView<Doctor> tblDoctors;

    @FXML
    private TableColumn<Doctor, Integer> colDoctorId;

    @FXML
    private TableColumn<Doctor, String> colDoctorName;

    @FXML
    private TableColumn<Doctor, String> colSpecialization;

    @FXML
    private TableColumn<Doctor, Integer> colExperience;

    @FXML
    private Button btnBookAppointment;

    @FXML
    private Button btnBack;

    private ObservableList<Doctor> doctorsList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Setup table columns
        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colDoctorName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colExperience.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));

        // Load doctors
        loadDoctors();
    }

    private void loadDoctors() {
        try {
            ArrayList<Doctor> doctors = DoctorDAO.getAllDoctors();
            doctorsList = FXCollections.observableArrayList(doctors);
            tblDoctors.setItems(doctorsList);
        } catch (SQLException | ClassNotFoundException e) {
            showAlert("Error", "Failed to load doctors: " + e.getMessage());
        }
    }

    @FXML
    private void handleBookAppointment(ActionEvent event) {
        Doctor selectedDoctor = tblDoctors.getSelectionModel().getSelectedItem();
        if (selectedDoctor == null) {
            showAlert("Warning", "Please select a doctor first");
            return;
        }

        // Store selected doctor in session
        SessionManager.setSessionData("selectedDoctor", selectedDoctor);

        // Navigate to booking screen
        loadScene("/resources/fxml/BookAppointment.fxml", "Book Appointment");
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
