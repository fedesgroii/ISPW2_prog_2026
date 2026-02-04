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
    public List<Visita> findByDateAndSpecialist(LocalDate date, String specialistId) {
        try {
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
                            v.getSpecialista() != null && v.getSpecialista().equals(specialistId))
                    .toList();
        } catch (Exception e) {
            LOGGER.severe(() -> "Error in findByDateAndSpecialist: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public boolean save(Visita visita) {
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
}
