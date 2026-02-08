package patient_dashboard.book_appointment;

import model.Visita;
import storage_file.FileManagerVisite;
import java.time.LocalDate;
import java.util.List;

/**
 * Concrete implementation of AppointmentRepository for JSON file storage.
 * Follows SRP by focusing only on file-based persistence.
 */
public class FileAppointmentDAO implements AppointmentRepository {
    private final FileManagerVisite fileManager = new FileManagerVisite();

    @Override
    public List<Visita> findByDateAndSpecialist(LocalDate date, int specialistId) {
        return fileManager.getAllInstanceOfActor().stream()
                .filter(v -> v.getData() != null && v.getData().equals(date) &&
                        v.getSpecialistaId() == specialistId)
                .toList();
    }

    @Override
    public List<Visita> findBySpecialist(String specialistSurname) {
        return List.of();
    }

    @Override
    public List<Visita> findBySpecialistEmail(String email) {
        return List.of();
    }

    @Override
    public List<Visita> findBySpecialistId(int specialistId) {
        return fileManager.getAllInstanceOfActor().stream()
                .filter(v -> v.getSpecialistaId() == specialistId)
                .toList();
    }

    @Override
    public boolean save(Visita visita) {
        return fileManager.salva(visita);
    }

    @Override
    public boolean delete(Visita visita) {
        return fileManager.elimina(visita);
    }
}
