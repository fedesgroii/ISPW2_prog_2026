package patient_dashboard.book_appointment;

import model.Visita;
import storage_db.DatabaseStorageStrategyVisita;
import storage_file.FileManagerVisite;
import storage_liste.ListaVisite;
import java.time.LocalDate;
import java.util.List;

/**
 * Implementation of AppointmentRepository that acts as a bridge to
 * existing storage strategies (RAM, File, DB).
 */
public class AppointmentDAO implements AppointmentRepository {

    private final int storageOption;
    private DatabaseStorageStrategyVisita dbStrategy;
    private FileManagerVisite fileManager;
    private ListaVisite ramList;

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(AppointmentDAO.class.getName());

    public AppointmentDAO(int storageOption) {
        this.storageOption = storageOption;
    }

    private synchronized DatabaseStorageStrategyVisita getDbStrategy() {
        if (dbStrategy == null)
            dbStrategy = new DatabaseStorageStrategyVisita();
        return dbStrategy;
    }

    private synchronized FileManagerVisite getFileManager() {
        if (fileManager == null)
            fileManager = new FileManagerVisite();
        return fileManager;
    }

    private synchronized ListaVisite getRamList() {
        if (ramList == null)
            ramList = ListaVisite.getIstanzaListaVisite();
        return ramList;
    }

    @Override
    public List<Visita> findByDateAndSpecialist(LocalDate date, int specialistId) {
        List<Visita> allVisite;

        switch (storageOption) {
            case 0: // RAM
                allVisite = getRamList().getObservableListaVisite();
                break;
            case 1: // DB
                return getDbStrategy().findByDateAndSpecialist(date, specialistId);
            case 2: // FILE
                allVisite = getFileManager().getAllInstanceOfActor();
                break;
            default:
                throw new IllegalStateException("Unexpected storage option: " + storageOption);
        }

        return allVisite.stream()
                .filter(v -> v.getData() != null && v.getData().equals(date) &&
                        v.getSpecialistaId() == specialistId)
                .toList();
    }

    @Override
    public List<Visita> findBySpecialist(String specialistSurname) {
        // Not implemented case - requires joining with Specialist DAO which is not
        // allowed to be modified here
        return List.of();
    }

    @Override
    public List<Visita> findBySpecialistId(int specialistId) {
        LOGGER.info(() -> "[DEBUG-DAO] findBySpecialistId called for ID: " + specialistId + " (Storage: "
                + storageOption + ")");
        List<Visita> allVisite;

        switch (storageOption) {
            case 0: // RAM
                allVisite = getRamList().getObservableListaVisite();
                break;
            case 1: // DB
                return getDbStrategy().findBySpecialistId(specialistId);
            case 2: // FILE
                allVisite = getFileManager().getAllInstanceOfActor();
                break;
            default:
                throw new IllegalStateException("Unexpected storage option: " + storageOption);
        }

        List<Visita> filtered = allVisite.stream()
                .filter(v -> v.getSpecialistaId() == specialistId)
                .toList();
        LOGGER.info(() -> "[DEBUG-DAO] Found " + filtered.size() + " visits for specialist ID: " + specialistId);
        return filtered;
    }

    @Override
    public List<Visita> findBySpecialistEmail(String email) {
        return List.of();
    }

    @Override
    public boolean save(Visita visita) {
        LOGGER.info(() -> "[DEBUG-DAO] Saving Visita: " + visita + " (Storage: " + storageOption + ")");
        try {
            switch (storageOption) {
                case 0: // RAM
                    return getRamList().aggiungiVisita(visita);
                case 1: // DB
                    return getDbStrategy().salva(visita);
                case 2: // FILE
                    return getFileManager().salva(visita);
                default:
                    throw new IllegalStateException("Unexpected storage option: " + storageOption);
            }
        } catch (Exception e) {
            LOGGER.severe(() -> "Error in save appointment: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean delete(Visita visita) {
        try {
            switch (storageOption) {
                case 0: // RAM
                    return getRamList().rimuoviVisita(
                            visita.getPazienteCodiceFiscale(),
                            visita.getData(),
                            visita.getOrario());
                case 1: // DB
                    return getDbStrategy().elimina(visita);
                case 2: // FILE
                    return getFileManager().elimina(visita);
                default:
                    throw new IllegalStateException("Unexpected storage option: " + storageOption);
            }
        } catch (Exception e) {
            LOGGER.severe(() -> "Error in delete appointment: " + e.getMessage());
            return false;
        }
    }
}
