package specialist_dashboard;

import model.Specialista;
import session_manager.SessionManagerSpecialista;
import java.util.logging.Logger;

/**
 * Application Controller for the Specialist Dashboard.
 * Shared between GUI and CLI views.
 * Handles session logic and data retrieval for specialists.
 */
public class SpecialistDashboardController {
    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardController.class.getName());

    /**
     * Verifies if a specialist session is active.
     * 
     * @throws IllegalStateException if no session is active.
     */
    public void checkSession() {
        if (!SessionManagerSpecialista.isLoggedIn()) {
            LOGGER.severe("Session validation failed: No specialist logged in.");
            throw new IllegalStateException("Nessuno specialista loggato. Effettua il login.");
        }
    }

    /**
     * Retrieves the currently logged-in specialist entity.
     * 
     * @return The Specialista object.
     */
    public Specialista getLoggedSpecialist() {
        checkSession();
        return SessionManagerSpecialista.getSpecialistaLoggato();
    }
}
