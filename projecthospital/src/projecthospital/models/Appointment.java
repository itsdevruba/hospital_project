package projecthospital.models;

import java.util.ArrayList;
import java.util.Date;


public class Appointment implements Manageable {

    
    private int appointmentId;
    private int patientId;
    private int doctorId;
    private Date appointmentDate;
    private String appointmentTime;
    private String status; // SCHEDULED, COMPLETED, CANCELLED
    private Date createdDate;

    
    private Patient patient;
    private Doctor doctor;

    
    public Appointment() {
        this.createdDate = new Date();
        this.status = "SCHEDULED";
    }

    public Appointment(int appointmentId, int patientId, int doctorId,
                       Date appointmentDate, String appointmentTime, String status) {
        this.appointmentId = appointmentId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.createdDate = new Date();
    }

   
    public Appointment(int appointmentId, Patient patient, Doctor doctor,
                       Date appointmentDate, String appointmentTime, String status) {
        this.appointmentId = appointmentId;
        this.patient = patient;
        this.doctor = doctor;
        this.patientId = patient.getPatientId();
        this.doctorId = doctor.getDoctorId();
        this.appointmentDate = appointmentDate;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.createdDate = new Date();
    }

   
    @Override
    public boolean validate() {
       
        return patientId > 0 && doctorId > 0 &&
               appointmentDate != null &&
               appointmentTime != null && !appointmentTime.isEmpty();
    }

    @Override
    public int getId() {
        return appointmentId;
    }

    @Override
    public String getDisplayInfo() {
        return "Appointment #" + appointmentId + " | Date: " + appointmentDate +
               " | Time: " + appointmentTime + " | Status: " + status;
    }

   
    public void scheduleAppointment() {
        this.status = "SCHEDULED";
    }

    public void completeAppointment() {
        this.status = "COMPLETED";
    }

    public void cancelAppointment() {
        this.status = "CANCELLED";
    }

    public boolean isScheduled() {
        return "SCHEDULED".equals(this.status);
    }

    
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    public Date getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Date appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(String appointmentTime) {
        this.appointmentTime = appointmentTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
        if (patient != null) {
            this.patientId = patient.getPatientId();
        }
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
        if (doctor != null) {
            this.doctorId = doctor.getDoctorId();
        }
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", appointmentDate=" + appointmentDate +
                ", appointmentTime='" + appointmentTime + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
