package patient_dashboard.book_appointment;

import model.Paziente;
import model.Visita;
import java.time.LocalDate;
import java.util.logging.Logger;

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

            // In a real scenario, here we would call a DAO to save to DB/File
            LOGGER.info(() -> "[DEBUG] Successfully created Visita object: " + nuevaVisita);

            return "SUCCESS";
        } catch (Exception e) {
            LOGGER.severe(() -> "[DEBUG] Error creating Visita: " + e.getMessage());
            return "Errore interno durante la creazione della visita.";
        }
    }

    private String validateBean(BookAppointmentBean bean) {
        if (bean.getSpecialist() == null || bean.getSpecialist().trim().isEmpty()) {
            return "Lo specialista è obbligatorio.";
        }
        if (bean.getDate() == null || bean.getDate().isBefore(LocalDate.now())) {
            return "La data della visita non può essere nel passato.";
        }
        if (bean.getTime() == null) {
            return "L'orario della visita è obbligatorio.";
        }
        if (bean.getServiceType() == null
                || (!bean.getServiceType().equals("Online") && !bean.getServiceType().equals("In presenza"))) {
            return "Tipo di prestazione non valido (deve essere 'Online' o 'In presenza').";
        }
        return null; // No errors
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
}
