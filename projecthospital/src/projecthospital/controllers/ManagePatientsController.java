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
import projecthospital.database.PatientDAO;
import projecthospital.models.Patient;
import projecthospital.utils.Validator;

import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class ManagePatientsController implements Initializable {

    @FXML private TableView<Patient> tblPatients;
    @FXML private TableColumn<Patient, Integer> colPatientId;
    @FXML private TableColumn<Patient, String> colFullName;
    @FXML private TableColumn<Patient, String> colUsername;
    @FXML private TableColumn<Patient, String> colEmail;
    @FXML private TableColumn<Patient, String> colPhone;
    @FXML private TableColumn<Patient, String> colBloodType;

    @FXML private TextField txtUsername;
    @FXML private TextField txtPassword;
    @FXML private TextField txtFullName;
    @FXML private TextField txtEmail;
    @FXML private TextField txtPhone;
    @FXML private TextField txtBloodType;
    @FXML private DatePicker dpDateOfBirth;
    @FXML private Label lblMessage;
    @FXML private Button btnAdd;
    @FXML private Button btnUpdate;
    @FXML private Button btnDelete;
    @FXML private Button btnClear;
    @FXML private Button btnBack;

    private ObservableList<Patient> patientsList;
    private Patient selectedPatient;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        loadPatients();
        setupTableSelectionListener();
    }

    private void setupTableColumns() {
        colPatientId.setCellValueFactory(new PropertyValueFactory<>("patientId"));
        colFullName.setCellValueFactory(new PropertyValueFactory<>("fullName"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colBloodType.setCellValueFactory(new PropertyValueFactory<>("bloodType"));
    }

    private void loadPatients() {
        try {
            ArrayList<Patient> patients = PatientDAO.getAllPatients();
            patientsList = FXCollections.observableArrayList(patients);
            tblPatients.setItems(patientsList);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Failed to load patients: " + e.getMessage());
        }
    }

    private void setupTableSelectionListener() {
        tblPatients.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    selectedPatient = newSelection;
                    fillFormWithPatient(newSelection);
                }
            }
        );
    }

    private void fillFormWithPatient(Patient patient) {
        txtUsername.setText(patient.getUsername());
        txtPassword.setText(patient.getPassword());
        txtFullName.setText(patient.getFullName());
        txtEmail.setText(patient.getEmail());
        txtPhone.setText(patient.getPhone());
        txtBloodType.setText(patient.getBloodType());
        if (patient.getDateOfBirth() != null) {
            // Convert java.util.Date to LocalDate
            java.util.Date dob = patient.getDateOfBirth();
            dpDateOfBirth.setValue(new java.sql.Date(dob.getTime()).toLocalDate());
        }
    }

    @FXML
    private void handleAdd(ActionEvent event) {
        if (!validateInput()) return;

        try {
            Patient patient = createPatientFromForm();
            PatientDAO.addPatient(patient);
            showSuccess("Patient added successfully!");
            loadPatients();
            handleClear(null);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Failed to add patient: " + e.getMessage());
        }
    }

    @FXML
    private void handleUpdate(ActionEvent event) {
        if (selectedPatient == null) {
            showError("Please select a patient to update");
            return;
        }
        if (!validateInput()) return;

        try {
            Patient patient = createPatientFromForm();
            patient.setPatientId(selectedPatient.getPatientId());
            patient.setUserId(selectedPatient.getUserId());
            PatientDAO.updatePatient(patient);
            showSuccess("Patient updated successfully!");
            loadPatients();
            handleClear(null);
        } catch (SQLException | ClassNotFoundException e) {
            showError("Failed to update patient: " + e.getMessage());
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (selectedPatient == null) {
            showError("Please select a patient to delete");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Delete");
        confirm.setHeaderText("Delete Patient");
        confirm.setContentText("Are you sure you want to delete this patient?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            try {
                PatientDAO.deletePatient(selectedPatient.getPatientId());
                showSuccess("Patient deleted successfully!");
                loadPatients();
                handleClear(null);
            } catch (SQLException | ClassNotFoundException e) {
                showError("Failed to delete patient: " + e.getMessage());
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
        txtBloodType.clear();
        dpDateOfBirth.setValue(null);
        lblMessage.setText("");
        selectedPatient = null;
        tblPatients.getSelectionModel().clearSelection();
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
        if (!Validator.isNotEmpty(txtBloodType.getText())) {
            showError("Blood type is required");
            return false;
        }
        return true;
    }

    private Patient createPatientFromForm() {
        Patient patient = new Patient();
        patient.setUsername(txtUsername.getText().trim());
        patient.setPassword(txtPassword.getText().trim());
        patient.setFullName(txtFullName.getText().trim());
        patient.setEmail(txtEmail.getText().trim());
        patient.setPhone(txtPhone.getText().trim());
        patient.setBloodType(txtBloodType.getText().trim());
        if (dpDateOfBirth.getValue() != null) {
            patient.setDateOfBirth(Date.from(dpDateOfBirth.getValue()
                .atStartOfDay(ZoneId.systemDefault()).toInstant()));
        }
        return patient;
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
