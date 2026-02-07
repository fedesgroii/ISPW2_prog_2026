package patient_dashboard;

import model.Paziente;
import navigation.NavigationInstruction;
import session_manager.SessionManagerPaziente;
import java.util.logging.Logger;

/**
 * Application Controller for the Patient Dashboard.
 * Strictly adheres to MVC: completely independent of UI technologies (JavaFX,
 * CLI).
 * 
 * Responsibilities:
 * - Session validation
 * - Processing semantic user choices (PatientDashboardOption)
 * - Returning neutral navigation.NavigationInstruction result
 */
public class PatientDashboardController {
    private static final Logger LOGGER = Logger.getLogger(PatientDashboardController.class.getName());

    /**
     * Verifies if a patient session is active.
     */
    public void checkSession() {
        if (!SessionManagerPaziente.isLoggedIn()) {
            LOGGER.severe("Session validation failed: No patient logged in.");
            throw new IllegalStateException("Nessun paziente loggato. Effettua il login.");
        }
    }

    /**
     * Retrieves the currently logged-in patient entity.
     * 
     * @return The Paziente object.
     */
    public Paziente getLoggedPatient() {
        checkSession();
        return SessionManagerPaziente.getPazienteLoggato();
    }

    /**
     * Processes the user's semantic selection and determines the next navigation
     * step.
     * 
     * @param option The option selected by the user.
     * @return A NavigationInstruction indicating where to navigate.
     * @throws IllegalArgumentException if the option is unknown.
     */
    public NavigationInstruction processSelection(PatientDashboardOption option) {
        checkSession();
        LOGGER.info(() -> String.format("Processing dashboard selection: %s", option));

        return switch (option) {
            case BOOK_VISIT -> new NavigationInstruction("Booking");

            case MANAGE_APPOINTMENTS -> new NavigationInstruction("Agenda");

            case LOGOUT -> {
                SessionManagerPaziente.resetSession();
                yield new NavigationInstruction("Login");
            }
            default -> throw new IllegalArgumentException("Unsupported option: " + option);
        };
    }
}
