package patient_dashboard.book_appointment;

import model.Visita;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for managing medical appointments (Visita).
 */
public interface AppointmentRepository {
    /**
     * Finds all appointments for a specific date and specialist.
     *
     * @param date         The date of the appointments.
     * @param specialistId The identifier/name of the specialist.
     * @return A list of matching Visita objects.
     */
    List<Visita> findByDateAndSpecialist(LocalDate date, String specialistId);

    /**
     * Saves a new appointment.
     * 
     * @param visita The visit to save.
     * @return true if saved successfully.
     */
    boolean save(Visita visita);
}
