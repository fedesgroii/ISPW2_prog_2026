package patient_dashboard.book_appointment;

import model.Paziente;
import model.Visita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import startupconfig.StartupConfigBean;
import startupconfig.StartupSettingsEntity;
import authentication.factory.DAOFactory;

/**
 * Application Controller for the "Book Appointment" use case.
 * Contains business logic and validation, independent of the UI.
 */
public class BookAppointmentControllerApp {
    private static final Logger LOGGER = Logger.getLogger(BookAppointmentControllerApp.class.getName());

    /**
     * Executes the booking process.
     * 
     * @param bean          The data transfer object containing booking details.
     * @param loggedPatient The patient currently logged in.
     * @return A message indicating success or the specific validation error.
     */
    public String bookAppointment(BookAppointmentBean bean, Paziente loggedPatient) {
        LOGGER.info(() -> "[DEBUG] Processing booking request for patient: " + loggedPatient.getEmail());

        // 1. Validation
        String validationError = validateBean(bean);
        if (validationError != null) {
            LOGGER.warning(() -> "[DEBUG] Validation failed: " + validationError);
            return validationError;
        }

        // 2. Business Logic: Create the Visita object
        try {
            Visita nuevaVisita = new Visita(
                    loggedPatient,
                    bean.getDate(),
                    bean.getTime(),
                    bean.getSpecialist(),
                    bean.getServiceType(),
                    bean.getReason(),
                    "Prenotata" // Initial state
            );

            // Fetch repository
            StartupSettingsEntity settings = StartupSettingsEntity.getInstance();
            StartupConfigBean config = new StartupConfigBean(settings.isInterfaceMode(), settings.getStorageOption());
            AppointmentRepository repo = DAOFactory.createDAOs(config).appointmentRepository;

            // Save to persistence
            if (repo.save(nuevaVisita)) {
                LOGGER.info(() -> "[DEBUG] Successfully saved Visita object: " + nuevaVisita);
                return "SUCCESS";
            } else {
                LOGGER.severe(() -> "[DEBUG] Failed to save Visita object.");
                return "Errore durante il salvataggio della prenotazione.";
            }
        } catch (Exception e) {
            LOGGER.severe(() -> "[DEBUG] Error creating or saving Visita: " + e.getMessage());
            return "Errore interno durante la creazione della visita.";
        }
    }

    private String validateBean(BookAppointmentBean bean) {
        if (bean.getSpecialist() == null || bean.getSpecialist().trim().isEmpty()) {
            return "Lo specialista è obbligatorio.";
        }

        String dateError = validateDate(bean);
        if (dateError != null) {
            return dateError;
        }

        if (bean.getTime() == null) {
            return "L'orario della visita è obbligatorio.";
        }
        if (bean.getServiceType() == null
                || (!bean.getServiceType().equals("Online") && !bean.getServiceType().equals("In presenza"))) {
            return "Tipo di prestazione non valido (deve essere 'Online' o 'In presenza').";
        }

        // Nuove validazioni richieste
        if (bean.getPhone() == null || !bean.getPhone().matches("\\d{8,10}")) {
            return "Il numero di telefono non è valido (inserire da 8 a 10 cifre).";
        }

        if (bean.getDateOfBirth() == null || !bean.getDateOfBirth().matches("\\d{2}/\\d{2}/\\d{4}")) {
            return "La data di nascita deve essere nel formato GG/MM/AAAA.";
        }

        return null; // No errors
    }

    /**
     * Validazione della data della visita secondo le regole di business.
     */
    public String validateDate(BookAppointmentBean bean) {
        if (bean == null || bean.getDate() == null) {
            return "La data della visita è obbligatoria.";
        }
        LocalDate date = bean.getDate();
        if (date.isBefore(LocalDate.now())) {
            return "La data della visita non può essere nel passato.";
        }
        if (date.getDayOfWeek().getValue() >= 6) {
            return "Non è possibile prenotare visite di sabato o domenica.";
        }
        if (ItalianHolidayCalendar.isItalianHoliday(date)) {
            return "Non è possibile prenotare visite in un giorno festivo.";
        }
        return null;
    }

    /**
     * Retrieves the list of available specialists based on the storage
     * configuration.
     * 
     * @param config The current application configuration.
     * @return A list of Specialista objects.
     */
    public java.util.List<model.Specialista> getAvailableSpecialists(startupconfig.StartupConfigBean config) {
        authentication.factory.DAOFactory.DAOPair daos = authentication.factory.DAOFactory.createDAOs(config);
        return daos.specialistaDAO.getAllInstanceOfActor();
    }

    public List<LocalTime> getAvailableSlots(BookAppointmentBean bean) {
        LOGGER.info(() -> String.format("[DEBUG] getAvailableSlots called for bean: %s", bean));

        LocalDate date = (bean != null) ? bean.getDate() : null;
        String specialistId = (bean != null) ? bean.getSpecialist() : null;

        if (date == null || specialistId == null || specialistId.trim().isEmpty()) {
            return new ArrayList<>();
        }

        // 1. Generazione di tutti gli slot (08:00 - 20:00, step 1 ora)
        List<LocalTime> allSlots = new ArrayList<>();
        for (int hour = 8; hour <= 20; hour++) {
            allSlots.add(LocalTime.of(hour, 0));
        }

        // 2. Recupero del repository tramite la configurazione attuale
        StartupSettingsEntity settings = StartupSettingsEntity.getInstance();
        if (settings == null) {
            LOGGER.severe("StartupSettingsEntity.getInstance() returned null!");
            return new ArrayList<>();
        }

        StartupConfigBean config = new StartupConfigBean(settings.isInterfaceMode(), settings.getStorageOption());
        LOGGER.info(() -> "[DEBUG] Using storage option: " + config.getStorageOption());

        authentication.factory.DAOFactory.DAOPair daos = authentication.factory.DAOFactory.createDAOs(config);
        if (daos == null || daos.appointmentRepository == null) {
            LOGGER.severe("DAOFactory failed to provide appointmentRepository!");
            return new ArrayList<>();
        }

        AppointmentRepository repo = daos.appointmentRepository;

        // 3. Recupero prenotazioni esistenti
        List<Visita> existingAppointments = repo.findByDateAndSpecialist(date, specialistId);
        if (existingAppointments == null) {
            existingAppointments = new ArrayList<>();
        }

        // 4. Filtraggio degli slot occupati
        List<LocalTime> occupiedTimes = existingAppointments.stream()
                .filter(v -> v != null && v.getOrario() != null)
                .map(Visita::getOrario)
                .toList();

        return allSlots.stream()
                .filter(slot -> !occupiedTimes.contains(slot))
                .sorted()
                .toList();
    }
}
