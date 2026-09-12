package projecthospital.models;

import java.util.ArrayList;


public class Doctor extends User implements Manageable {

    
    private int doctorId;
    private String specialization;
    private String licenseNumber;
    private int yearsOfExperience;

   
    public Doctor() {
        super();
        this.role = "DOCTOR";
    }

    public Doctor(int userId, String username, String password, String fullName,
                  String email, String phone, int doctorId, String specialization,
                  String licenseNumber, int yearsOfExperience) {
        super(userId, username, password, "DOCTOR", fullName, email, phone);
        this.doctorId = doctorId;
        this.specialization = specialization;
        this.licenseNumber = licenseNumber;
        this.yearsOfExperience = yearsOfExperience;
    }

    
    @Override
    public String getUserInfo() {
        return "Dr. " + fullName + "\n" +
               "Specialization: " + specialization + "\n" +
               "Experience: " + yearsOfExperience + " years\n" +
               "License: " + licenseNumber;
    }

    
    @Override
    public boolean validate() {
       
        return username != null && !username.isEmpty() &&
               fullName != null && !fullName.isEmpty() &&
               specialization != null && !specialization.isEmpty();
    }

    @Override
    public int getId() {
        return doctorId;
    }

    @Override
    public String getDisplayInfo() {
        return "Dr. " + fullName + " | " + specialization + " | " + yearsOfExperience + " years exp.";
    }

    
    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(String licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    @Override
    public String toString() {
        return "Doctor{" +
                "doctorId=" + doctorId +
                ", userId=" + userId +
                ", fullName='" + fullName + '\'' +
                ", specialization='" + specialization + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                '}';
    }
}
