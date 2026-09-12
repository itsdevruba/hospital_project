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
import projecthospital.database.UserDAO;
import projecthospital.models.*;
import projecthospital.utils.SessionManager;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * LoginController - Controls the login screen
 * Demonstrates Exception Handling concept
 */
public class LoginController implements Initializable {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private ComboBox<String> cmbRole;

    @FXML
    private Label lblError;

    @FXML
    private Button btnLogin;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Populate role combo box
        cmbRole.setItems(FXCollections.observableArrayList("PATIENT", "DOCTOR", "ADMIN"));
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Clear previous error
        lblError.setText("");

        // Get input values
        String username = txtUsername.getText();
        String password = txtPassword.getText();
        String role = cmbRole.getValue();

        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            lblError.setText("Please enter username");
            return;
        }

        if (password == null || password.trim().isEmpty()) {
            lblError.setText("Please enter password");
            return;
        }

        if (role == null) {
            lblError.setText("Please select a role");
            return;
        }

        try {
            // Authenticate user
            User user = UserDAO.login(username, password, role);

            // Set current user in session
            SessionManager.setCurrentUser(user);

            // Navigate to appropriate dashboard based on role
            navigateToDashboard(user);

        } catch (IllegalArgumentException e) {
            lblError.setText("Error: " + e.getMessage());
        } catch (SQLException | ClassNotFoundException e) {
            lblError.setText("Database Error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            lblError.setText("Unexpected Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void navigateToDashboard(User user) {
        try {
            String fxmlFile = "";

            if (user instanceof Patient) {
                fxmlFile = "/resources/fxml/PatientDashboard.fxml";
            } else if (user instanceof Doctor) {
                fxmlFile = "/resources/fxml/DoctorDashboard.fxml";
            } else if (user instanceof Administrator) {
                fxmlFile = "/resources/fxml/AdminDashboard.fxml";
            }

            // Load new scene
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            // Get current stage
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Hospital Appointment System - " + user.getRole() + " Dashboard");
            stage.show();

        } catch (Exception e) {
            lblError.setText("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
