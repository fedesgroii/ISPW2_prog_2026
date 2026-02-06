package specialist_dashboard.manage_agenda;

import java.time.LocalDate;
import java.time.LocalTime;

public class ManageAgendaBean {
    private LocalDate date;
    private LocalTime time;
    private String patientName;
    private String type;
    private String reason;
    private String status;

    public ManageAgendaBean() {
        // Default constructor
    }

    public ManageAgendaBean(LocalDate date, LocalTime time, String patientName, String type, String reason,
            String status) {
        this.date = date;
        this.time = time;
        this.patientName = patientName;
        this.type = type;
        this.reason = reason;
        this.status = status;
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

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "ManageAgendaBean{" +
                "date=" + date +
                ", time=" + time +
                ", patientName='" + patientName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
