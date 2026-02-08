package patient_dashboard.book_appointment;

import model.Visita;
import storage_db.DatabaseStorageStrategyVisita;
import java.time.LocalDate;
import java.util.List;

/**
 * Concrete implementation of AppointmentRepository for Database storage.
 * Follows SRP by focusing only on SQL-based persistence.
 */
public class DatabaseAppointmentDAO implements AppointmentRepository {
    private final DatabaseStorageStrategyVisita dbStrategy = new DatabaseStorageStrategyVisita();

    @Override
    public List<Visita> findByDateAndSpecialist(LocalDate date, int specialistId) {
        return dbStrategy.findByDateAndSpecialist(date, specialistId);
    }

    @Override
    public List<Visita> findBySpecialist(String specialistSurname) {
        return List.of(); // Not implemented as per current system design
    }

    @Override
    public List<Visita> findBySpecialistEmail(String email) {
        return List.of(); // Not implemented as per current system design
    }

    @Override
    public List<Visita> findBySpecialistId(int specialistId) {
        return dbStrategy.findBySpecialistId(specialistId);
    }

    @Override
    public boolean save(Visita visita) {
        return dbStrategy.salva(visita);
    }

    @Override
    public boolean delete(Visita visita) {
        return dbStrategy.elimina(visita);
    }
}
