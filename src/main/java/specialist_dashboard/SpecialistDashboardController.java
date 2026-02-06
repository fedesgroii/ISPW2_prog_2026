package specialist_dashboard;

import model.Specialista;
import model.Paziente;
import model.Visita;
import authentication.UserDAO;
import session_manager.SessionManagerSpecialista;
import java.util.logging.Level;
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
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-SPEC-CTRL-5] Querying appointments for specialist: {0}",
                        logged.getCognome());
            }

            java.util.List<model.Visita> specialistAppointments = daos.appointmentRepository
                    .findBySpecialistId(logged.getId());

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-SPEC-CTRL-6] Found {0} appointments from storage.",
                        specialistAppointments.size());
            }

            // Process each appointment as notification
            for (model.Visita v : specialistAppointments) {
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "[DEBUG-SPEC-CTRL-7] Processing appointment: {0}", v);
                }
                if (!unreadNotifications.contains(v)) {
                    unreadNotifications.add(v);
                }
            }
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-SPEC-CTRL-8] Constructor completed. Unread notifications: {0}",
                        unreadNotifications.size());
            }
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "[DEBUG-SPEC-CTRL-ERROR] Error loading appointments: {0}", e.getMessage());
            }
        }
    }

    private void onNewVisit(model.Visita visit) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "[DEBUG-SPEC-ON-VISIT-1] onNewVisit() called with visit: {0}", visit);
        }
        try {
            LOGGER.info("[DEBUG-SPEC-ON-VISIT-2] Attempting to get logged specialist...");
            Specialista logged = getLoggedSpecialist();

            if (logged == null) {
                if (LOGGER.isLoggable(Level.WARNING)) {
                    LOGGER.log(Level.WARNING, "[DEBUG-SPEC-ON-VISIT-12] Null check failed. logged={0}", logged);
                }
                return;
            }

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-SPEC-ON-VISIT-3] Logged specialist: {0}", logged.getCognome());
            }

            LOGGER.info("[DEBUG-SPEC-ON-VISIT-4] Logged is non-null. Proceeding with matching...");
            int visitSpecId = visit.getSpecialistaId();
            Integer loggedId = logged.getId();

            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-SPEC-ON-VISIT-5] Comparing: visitSpecId=[{0}] vs loggedId=[{1}]",
                        new Object[] { visitSpecId, loggedId });
            }

            if (loggedId == null || visitSpecId != loggedId) {
                LOGGER.info("[DEBUG-SPEC-ON-VISIT-11] NO MATCH. Visit is for different specialist.");
                return;
            }

            LOGGER.info("[DEBUG-SPEC-ON-VISIT-6] MATCH FOUND! Adding to unread notifications.");
            if (unreadNotifications.contains(visit)) {
                LOGGER.info("[DEBUG-SPEC-ON-VISIT-10] Visit already in unread list (duplicate).");
                return;
            }

            unreadNotifications.add(visit);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-SPEC-ON-VISIT-7] Visit added. Total unread: {0}",
                        unreadNotifications.size());
            }

            if (onNotificationReceived == null) {
                LOGGER.warning("[DEBUG-SPEC-ON-VISIT-9] UI callback is NULL!");
                return;
            }

            LOGGER.info("[DEBUG-SPEC-ON-VISIT-8] Triggering UI update callback...");
            javafx.application.Platform.runLater(onNotificationReceived);

        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "Error processing notification: {0}", e.getMessage());
            }
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
                .orElseGet(() -> "Paziente non trovato (" + cf + ")");
    }
}
