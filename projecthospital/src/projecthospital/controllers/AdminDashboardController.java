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
import projecthospital.utils.SessionManager;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminDashboardController implements Initializable {

    @FXML
    private Label lblWelcome;

    @FXML
    private Button btnManagePatients;

    @FXML
    private Button btnManageDoctors;

    @FXML
    private Button btnViewAppointments;

    @FXML
    private Button btnSystemReports;

    @FXML
    private Button btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblWelcome.setText("Welcome, " + SessionManager.getCurrentUserFullName());
    }

    @FXML
    private void handleManagePatients(ActionEvent event) {
        loadScene("/resources/fxml/ManagePatients.fxml", "Manage Patients");
    }

    @FXML
    private void handleManageDoctors(ActionEvent event) {
        loadScene("/resources/fxml/ManageDoctors.fxml", "Manage Doctors");
    }

    @FXML
    private void handleViewAppointments(ActionEvent event) {
        loadScene("/resources/fxml/AdminViewAppointments.fxml", "View Appointments");
    }

    @FXML
    private void handleSystemReports(ActionEvent event) {
        loadScene("/resources/fxml/SystemReports.fxml", "System Reports");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        SessionManager.logout();
        loadScene("/resources/fxml/Login.fxml", "Login");
    }

    private void loadScene(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) btnManagePatients.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hospital Appointment System - " + title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
