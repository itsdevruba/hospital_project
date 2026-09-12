package projecthospital.models;

import java.util.Date;


public class Patient extends User implements Manageable {

    
    private int patientId;
    private Date dateOfBirth;
    private String bloodType;

    
    public Patient() {
        super();
        this.role = "PATIENT";
    }

    public Patient(int userId, String username, String password, String fullName,
                   String email, String phone, int patientId, Date dateOfBirth,
                   String bloodType) {
        super(userId, username, password, "PATIENT", fullName, email, phone);
        this.patientId = patientId;
        this.dateOfBirth = dateOfBirth;
        this.bloodType = bloodType;
    }

    
    @Override
    public String getUserInfo() {
        return "Patient: " + fullName + " (" + username + ")\n" +
               "Blood Type: " + bloodType + "\n" +
               "DOB: " + dateOfBirth;
    }

    
    @Override
    public boolean validate() {
        
        return username != null && !username.isEmpty() &&
               fullName != null && !fullName.isEmpty() &&
               bloodType != null && !bloodType.isEmpty();
    }

    @Override
    public int getId() {
        return patientId;
    }

    @Override
    public String getDisplayInfo() {
        return "Patient: " + fullName + " | Blood Type: " + bloodType;
    }

    // Getters and Setters
    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    @Override
    public String toString() {
        return "Patient{" +
                "patientId=" + patientId +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", username='" + username + '\'' +
                ", bloodType='" + bloodType + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                '}';
    }
}
