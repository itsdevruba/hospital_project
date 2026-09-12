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
import projecthospital.utils.Validator;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class ManageDoctorsController implements Initializable {

    @FXML private TableView<Doctor> tblDoctors;
    @FXML private TableColumn<Doctor, Integer> colDoctorId;
    @FXML private TableColumn<Doctor, String> colFullName;
    @FXML private TableColumn<Doctor, String> colUsername;
    @FXML private TableColumn<Doctor, String> colSpecialization;
    @FXML private TableColumn<Doctor, Integer> colExperience;
    @FXML private TableColumn<Doctor, String> colEmail;

    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtSpecialization;
    @FXML private TextField txtLicenseNumber;
    @FXML private TextField txtYearsOfExperience;
    @FXML private Label lblMessage;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnBack;

    private ObservableList<Doctor> doctorsList;
    private Doctor selectedDoctor;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadDoctors();
        setupTableSelectionListener();
    }

    private void setupTableColumns() {
        colDoctorId.setCellValueFactory(new PropertyValueFactory<>("doctorId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colSpecialization.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colExperience.setCellValueFactory(new PropertyValueFactory<>("yearsOfExperience"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private void loadDoctors() {
        try {
            ArrayList<Doctor> doctors = DoctorDAO.getAllDoctors();
            doctorsList = FXCollections.observableArrayList(doctors);
            tblDoctors.setItems(doctorsList);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Failed to load doctors: " + e.getMessage());
        }
    }

    private void setupTableSelectionListener() {
        tblDoctors.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedDoctor = newSelection;
                    fillFormWithDoctor(newSelection);
                }
            }
        );
    }

    private void fillFormWithDoctor(Doctor doctor) {
        txtUsername.setText(doctor.getUsername());
        txtPassword.setText(doctor.getPassword());
        txtFullName.setText(doctor.getFullName());
        txtEmail.setText(doctor.getEmail());
        txtPhone.setText(doctor.getPhone());
        txtSpecialization.setText(doctor.getSpecialization());
        txtLicenseNumber.setText(doctor.getLicenseNumber());
        txtYearsOfExperience.setText(String.valueOf(doctor.getYearsOfExperience()));
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateInput()) return;

        try {
            Doctor doctor = createDoctorFromForm();
            DoctorDAO.addDoctor(doctor);
            showSuccess("Doctor added successfully!");
            loadDoctors();
            handleClear(null);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Failed to add doctor: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedDoctor == null) {
            showError("Please select a doctor to update");
            return;
        }
        if (!validateInput()) return;

        try {
            Doctor doctor = createDoctorFromForm();
            doctor.setDoctorId(selectedDoctor.getDoctorId());
            doctor.setUserId(selectedDoctor.getUserId());
            DoctorDAO.updateDoctor(doctor);
            showSuccess("Doctor updated successfully!");
            loadDoctors();
            handleClear(null);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Failed to update doctor: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedDoctor == null) {
            showError("Please select a doctor to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Doctor");
        confirm.setContentText("Are you sure you want to delete this doctor?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                DoctorDAO.deleteDoctor(selectedDoctor.getDoctorId());
                showSuccess("Doctor deleted successfully!");
                loadDoctors();
                handleClear(null);
            } catch (SQLException | ClassNotFoundException e) {
                showError("Failed to delete doctor: " + e.getMessage());
            }
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        txtFullName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtSpecialization.clear();
        txtLicenseNumber.clear();
        txtYearsOfExperience.clear();
        lblMessage.setText("");
        selectedDoctor = null;
        tblDoctors.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        loadScene("/resources/fxml/AdminDashboard.fxml", "Admin Dashboard");
    }

    private boolean validateInput() {
        if (!Validator.isNotEmpty(txtUsername.getText())) {
            showError("Username is required");
            return false;
        }
        if (!Validator.isNotEmpty(txtPassword.getText())) {
            showError("Password is required");
            return false;
        }
        if (!Validator.isNotEmpty(txtFullName.getText())) {
            showError("Full name is required");
            return false;
        }
        if (!Validator.isNotEmpty(txtSpecialization.getText())) {
            showError("Specialization is required");
            return false;
        }
        try {
            Integer.parseInt(txtYearsOfExperience.getText());
        } catch (NumberFormatException e) {
            showError("Years of experience must be a number");
            return false;
        }
        return true;
    }

    private Doctor createDoctorFromForm() {
        Doctor doctor = new Doctor();
        doctor.setUsername(txtUsername.getText().trim());
        doctor.setPassword(txtPassword.getText().trim());
        doctor.setFullName(txtFullName.getText().trim());
        doctor.setEmail(txtEmail.getText().trim());
        doctor.setPhone(txtPhone.getText().trim());
        doctor.setSpecialization(txtSpecialization.getText().trim());
        doctor.setLicenseNumber(txtLicenseNumber.getText().trim());
        doctor.setYearsOfExperience(Integer.parseInt(txtYearsOfExperience.getText().trim()));
        return doctor;
    }

    private void showError(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: red;");
    }

    private void showSuccess(String message) {
        lblMessage.setText(message);
        lblMessage.setStyle("-fx-text-fill: green;");
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
