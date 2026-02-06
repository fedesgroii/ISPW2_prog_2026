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
     * @param specialistId The identifier of the specialist.
     * @return A list of matching Visita objects.
     */
    List<Visita> findByDateAndSpecialist(LocalDate date, int specialistId);

    /**
     * Finds all appointments for a specific specialist.
     *
     * @param specialistSurname The surname of the specialist.
     * @return A list of matching Visita objects.
     */
    List<Visita> findBySpecialist(String specialistSurname);

    /**
     * Finds all appointments for a specific specialist by email.
     *
     * @param email The email of the specialist.
     * @return A list of matching Visita objects.
     */
    List<Visita> findBySpecialistEmail(String email);

    /**
     * Finds all appointments for a specific specialist by their ID.
     *
     * @param specialistId The ID of the specialist.
     * @return A list of matching Visita objects.
     */
    List<Visita> findBySpecialistId(int specialistId);

    /**
     * Saves a new appointment.
     * 
     * @param visita The visit to save.
     * @return true if saved successfully.
     */
    boolean save(Visita visita);

    /**
     * Deletes an appointment.
     * 
     * @param visita The visit to delete.
     * @return true if deleted successfully.
     */
    boolean delete(Visita visita);
}
