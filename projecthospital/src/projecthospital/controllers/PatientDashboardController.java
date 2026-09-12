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

public class PatientDashboardController implements Initializable {

    @FXML
    private Label lblWelcome;

    @FXML
    private Button btnBrowseDoctors;

    @FXML
    private Button btnMyAppointments;

    @FXML
    private Button btnLogout;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Set welcome message
        lblWelcome.setText("Welcome, " + SessionManager.getCurrentUserFullName());
    }

    @FXML
    private void handleBrowseDoctors(ActionEvent event) {
        loadScene("/resources/fxml/BrowseDoctors.fxml", "Browse Doctors");
    }

    @FXML
    private void handleMyAppointments(ActionEvent event) {
        loadScene("/resources/fxml/PatientAppointments.fxml", "My Appointments");
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
            Stage stage = (Stage) btnBrowseDoctors.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hospital Appointment System - " + title);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
