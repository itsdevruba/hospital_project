package projecthospital.models;

import java.util.Date;


public class VisitNote {

   
    private int noteId;
    private int appointmentId;
    private String notes;
    private String diagnosis;
    private String prescription;
    private Date createdDate;

    
    private Appointment appointment;

   
    public VisitNote() {
        this.createdDate = new Date();
    }

    public VisitNote(int noteId, int appointmentId, String notes,
                     String diagnosis, String prescription) {
        this.noteId = noteId;
        this.appointmentId = appointmentId;
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.createdDate = new Date();
    }

    
    public VisitNote(int noteId, Appointment appointment, String notes,
                     String diagnosis, String prescription) {
        this.noteId = noteId;
        this.appointment = appointment;
        this.appointmentId = appointment.getAppointmentId();
        this.notes = notes;
        this.diagnosis = diagnosis;
        this.prescription = prescription;
        this.createdDate = new Date();
    }

    
    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getPrescription() {
        return prescription;
    }

    public void setPrescription(String prescription) {
        this.prescription = prescription;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
        if (appointment != null) {
            this.appointmentId = appointment.getAppointmentId();
        }
    }

    @Override
    public String toString() {
        return "VisitNote{" +
                "noteId=" + noteId +
                ", appointmentId=" + appointmentId +
                ", diagnosis='" + diagnosis + '\'' +
                ", createdDate=" + createdDate +
                '}';
    }
}
