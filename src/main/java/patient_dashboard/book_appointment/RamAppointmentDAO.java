package patient_dashboard.book_appointment;

import model.Visita;
import storage_liste.ListaVisite;
import java.time.LocalDate;
import java.util.List;

/**
 * Concrete implementation of AppointmentRepository for RAM (in-memory) storage.
 * Follows SRP by focusing only on in-memory persistence.
 */
public class RamAppointmentDAO implements AppointmentRepository {
    private final ListaVisite ramList = ListaVisite.getIstanzaListaVisite();

    @Override
    public List<Visita> findByDateAndSpecialist(LocalDate date, int specialistId) {
        return ramList.getObservableListaVisite().stream()
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
        return ramList.getObservableListaVisite().stream()
                .filter(v -> v.getSpecialistaId() == specialistId)
                .toList();
    }

    @Override
    public boolean save(Visita visita) {
        return ramList.aggiungiVisita(visita);
    }

    @Override
    public boolean delete(Visita visita) {
        return ramList.rimuoviVisita(
                visita.getPazienteCodiceFiscale(),
                visita.getData(),
                visita.getOrario());
    }
}
