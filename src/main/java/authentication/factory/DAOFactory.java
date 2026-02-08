package authentication.factory;

import authentication.UserDAO;
import authentication.dao.DatabaseUserDAO;
import authentication.dao.FileUserDAO;
import authentication.dao.InMemoryUserDAO;
import patient_dashboard.book_appointment.DatabaseAppointmentDAO;
import patient_dashboard.book_appointment.FileAppointmentDAO;
import patient_dashboard.book_appointment.RamAppointmentDAO;
import patient_dashboard.book_appointment.AppointmentRepository;
import model.Paziente;
import model.Specialista;
import startupconfig.StartupConfigBean;
import storage_db.DatabaseStorageStrategyPaziente;
import storage_db.DatabaseStorageStrategySpecialista;
import storage_file.FileManagerPazienti;
import storage_file.FileManagerSpecialisti;
import storage_liste.ListaPazienti;
import storage_liste.ListaSpecialisti;

/**
 * Factory per la creazione dei DAO corretti in base alla configurazione di
 * storage.
 * Supporta tre tipi di storage: RAM (0), Database (1), File (2).
 */
public class DAOFactory {

        private DAOFactory() {
                // Utility class, costruttore privato
        }

        private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
                        .getLogger(DAOFactory.class.getName());

        /**
         * Crea i DAO per Paziente e Specialista in base alla configurazione.
         * 
         * @param config Il bean di configurazione con l'opzione di storage
         * @return DAOPair contenente i DAO per Paziente e Specialista
         * @throws IllegalArgumentException se l'opzione di storage non è valida
         */
        public static DAOPair createDAOs(StartupConfigBean config) {
                int storageOption = config.getStorageOption();
                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering DAOFactory.createDAOs: storageOption=%d",
                                Thread.currentThread().getName(), storageOption));

                switch (storageOption) {
                        case 0: // RAM
                                LOGGER.info(
                                                () -> String.format("[DEBUG][Thread: %s] Creating RAM DAOs",
                                                                Thread.currentThread().getName()));
                                return new DAOPair(
                                                new InMemoryUserDAO<>(ListaPazienti.getIstanzaListaPazienti()
                                                                .getObservableListaPazienti()),
                                                new InMemoryUserDAO<>(
                                                                ListaSpecialisti.getIstanzaListaSpecialisti()
                                                                                .getObservableListaSpecialisti()),
                                                new RamAppointmentDAO());

                        case 1: // Database
                                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Creating Database DAOs",
                                                Thread.currentThread().getName()));
                                return new DAOPair(
                                                new DatabaseUserDAO<>(new DatabaseStorageStrategyPaziente()),
                                                new DatabaseUserDAO<>(new DatabaseStorageStrategySpecialista()),
                                                new DatabaseAppointmentDAO());

                        case 2: // File
                                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Creating File DAOs",
                                                Thread.currentThread().getName()));
                                return new DAOPair(
                                                new FileUserDAO<>(new FileManagerPazienti()),
                                                new FileUserDAO<>(new FileManagerSpecialisti()),
                                                new FileAppointmentDAO());

                        default:
                                LOGGER.severe(() -> String.format("[DEBUG][Thread: %s] Invalid storage option: %d",
                                                Thread.currentThread().getName(), storageOption));
                                throw new IllegalArgumentException("Opzione di storage non valida: " + storageOption
                                                + ". Valori accettati: 0 (RAM), 1 (Database), 2 (File)");
                }
        }

        /**
         * Classe helper per ritornare entrambi i DAO (Paziente e Specialista).
         */
        public static class DAOPair {
                public final UserDAO<Paziente> pazienteDAO;
                public final UserDAO<Specialista> specialistaDAO;
                public final AppointmentRepository appointmentRepository;

                public DAOPair(UserDAO<Paziente> pDAO, UserDAO<Specialista> sDAO, AppointmentRepository aRepo) {
                        this.pazienteDAO = pDAO;
                        this.specialistaDAO = sDAO;
                        this.appointmentRepository = aRepo;
                }
        }
}
