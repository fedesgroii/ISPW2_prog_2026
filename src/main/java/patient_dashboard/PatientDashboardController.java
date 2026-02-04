package patient_dashboard;

import model.Paziente;
import session_manager.SessionManagerPaziente;
import java.util.logging.Logger;

/**
 * Application Controller for the Patient Dashboard.
 * Shared between GUI and CLI views.
 * Handles session logic and data retrieval.
 */
public class PatientDashboardController {
    private static final Logger LOGGER = Logger.getLogger(PatientDashboardController.class.getName());

    /**
     * Verifies if a patient session is active.
     * 
     * @throws IllegalStateException if no session is active.
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
}
