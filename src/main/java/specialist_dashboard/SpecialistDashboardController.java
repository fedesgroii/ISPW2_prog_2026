package specialist_dashboard;

import model.Specialista;
import model.Paziente;
import model.Visita;
import authentication.UserDAO;
import session_manager.SessionManagerSpecialista;
import java.util.logging.Logger;

/**
 * Application Controller for the Specialist Dashboard.
 * Shared between GUI and CLI views.
 * Handles session logic and data retrieval for specialists.
 */
public class SpecialistDashboardController {
    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardController.class.getName());

    private final java.util.List<Visita> unreadNotifications = new java.util.ArrayList<>();
    private Runnable onNotificationReceived;
    private final UserDAO<Paziente> pazienteDAO;
    private startupconfig.StartupConfigBean startupConfig;

    public SpecialistDashboardController() {
        LOGGER.info("[DEBUG-SPEC-CTRL-1] SpecialistDashboardController constructor called.");
        observer.NotificationManager manager = observer.NotificationManager.getInstance();
        LOGGER.info("[DEBUG-SPEC-CTRL-2] Registering observer with NotificationManager...");
        manager.registerObserver(this::onNewVisit);
        LOGGER.info("[DEBUG-SPEC-CTRL-3] Observer registered.");

        // Initialize DAOs before the try block to satisfy final field requirements
        startupconfig.StartupSettingsEntity settings = startupconfig.StartupSettingsEntity.getInstance();
        this.startupConfig = new startupconfig.StartupConfigBean(
                settings.isInterfaceMode(), settings.getStorageOption());
        authentication.factory.DAOFactory.DAOPair daos = authentication.factory.DAOFactory
                .createDAOs(startupConfig);
        this.pazienteDAO = daos.pazienteDAO;

        // Load appointments from persistent storage
        try {
            LOGGER.info("[DEBUG-SPEC-CTRL-4] Loading appointments from persistent storage...");
            Specialista logged = getLoggedSpecialist();
            LOGGER.info("[DEBUG-SPEC-CTRL-5] Querying appointments for specialist: " + logged.getCognome());

            java.util.List<model.Visita> specialistAppointments = daos.appointmentRepository
                    .findBySpecialistId(logged.getId());
            LOGGER.info("[DEBUG-SPEC-CTRL-6] Found " + specialistAppointments.size() + " appointments from storage.");

            // Process each appointment as notification
            for (model.Visita v : specialistAppointments) {
                LOGGER.info("[DEBUG-SPEC-CTRL-7] Processing appointment: " + v);
                if (!unreadNotifications.contains(v)) {
                    unreadNotifications.add(v);
                }
            }
            LOGGER.info(
                    "[DEBUG-SPEC-CTRL-8] Constructor completed. Unread notifications: " + unreadNotifications.size());
        } catch (Exception e) {
            LOGGER.warning("[DEBUG-SPEC-CTRL-ERROR] Error loading appointments: " + e.getMessage());
        }
    }

    private void onNewVisit(model.Visita visit) {
        LOGGER.info("[DEBUG-SPEC-ON-VISIT-1] onNewVisit() called with visit: " + visit);
        try {
            LOGGER.info("[DEBUG-SPEC-ON-VISIT-2] Attempting to get logged specialist...");
            Specialista logged = getLoggedSpecialist();
            LOGGER.info(
                    "[DEBUG-SPEC-ON-VISIT-3] Logged specialist: " + (logged != null ? logged.getCognome() : "NULL"));

            if (logged != null) {
                LOGGER.info(
                        "[DEBUG-SPEC-ON-VISIT-4] Logged is non-null. Proceeding with matching...");
                // Robust matching: Check if visit specialist ID matches logged specialist ID
                int visitSpecId = visit.getSpecialistaId();
                Integer loggedId = logged.getId();

                LOGGER.info(String.format("[DEBUG-SPEC-ON-VISIT-5] Comparing: visitSpecId=[%d] vs loggedId=[%d]",
                        visitSpecId, loggedId));

                if (loggedId != null && visitSpecId == loggedId) {
                    LOGGER.info("[DEBUG-SPEC-ON-VISIT-6] MATCH FOUND! Adding to unread notifications.");
                    // Avoid duplicates
                    if (!unreadNotifications.contains(visit)) {
                        unreadNotifications.add(visit);
                        LOGGER.info("[DEBUG-SPEC-ON-VISIT-7] Visit added. Total unread: " + unreadNotifications.size());
                        if (onNotificationReceived != null) {
                            LOGGER.info("[DEBUG-SPEC-ON-VISIT-8] Triggering UI update callback...");
                            javafx.application.Platform.runLater(onNotificationReceived);
                        } else {
                            LOGGER.warning("[DEBUG-SPEC-ON-VISIT-9] UI callback is NULL!");
                        }
                    } else {
                        LOGGER.info("[DEBUG-SPEC-ON-VISIT-10] Visit already in unread list (duplicate).");
                    }
                } else {
                    LOGGER.info("[DEBUG-SPEC-ON-VISIT-11] NO MATCH. Visit is for different specialist.");
                }
            } else {
                LOGGER.warning("[DEBUG-SPEC-ON-VISIT-12] Null check failed. logged=" + logged);
            }
        } catch (Exception e) {
            // Log warning but don't crash
            LOGGER.warning("Error processing notification: " + e.getMessage());
        }
    }

    public void setNotificationCallback(Runnable callback) {
        this.onNotificationReceived = callback;
    }

    public int getUnreadCount() {
        return unreadNotifications.size();
    }

    public java.util.List<model.Visita> getUnreadNotifications() {
        return new java.util.ArrayList<>(unreadNotifications); // Return copy
    }

    public void clearNotifications() {
        unreadNotifications.clear();
        if (onNotificationReceived != null) {
            javafx.application.Platform.runLater(onNotificationReceived);
        }
    }

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

    public SpecialistDashboardBean getDashboardData() {
        Specialista s = getLoggedSpecialist();
        return new SpecialistDashboardBean(s.getNome(), s.getCognome(), getUnreadCount());
    }

    /**
     * Resolves a patient's full name from their tax code.
     * 
     * @param cf The patient's tax code.
     * @return Full name (name + surname) or a fallback string.
     */
    public String getPatientName(String cf) {
        return pazienteDAO.findById(cf)
                .map(p -> p.getNome() + " " + p.getCognome())
                .orElse("Paziente non trovato (" + cf + ")");
    }
}
