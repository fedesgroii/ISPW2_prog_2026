package patient_dashboard.book_appointment;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Bean DTO for transporting appointment booking data.
 * Used by both GUI and CLI to send data to the Application Controller.
 */
public class BookAppointmentBean {
    // Patient Data (can be used for confirmation or new patients)
    private String name;
    private String surname;
    private String dateOfBirth; // String for manual input validation
    private String phone;
    private String email;

    // Appointment Data
    private String specialist;
    private int specialistId;
    private LocalDate date;
    private LocalTime time;
    private String serviceType; // "Online" or "In presenza"
    private String reason;

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSpecialist() {
        return specialist;
    }

    public void setSpecialist(String specialist) {
        this.specialist = specialist;
    }

    public int getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(int specialistId) {
        this.specialistId = specialistId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
