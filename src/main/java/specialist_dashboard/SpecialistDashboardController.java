package specialist_dashboard;

import model.Specialista;
import model.Paziente;
import model.Visita;
import authentication.UserDAO;
import navigation.NavigationInstruction;
import session_manager.SessionManagerSpecialista;
import java.util.logging.Level;
import java.util.logging.Logger;

import observer.Observer;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Application Controller for the Specialist Dashboard.
 * Shared between GUI and CLI views.
 * Handles session logic and data retrieval for specialists.
 * Acts as a ConcreteObserver in the Observer pattern.
 */
public class SpecialistDashboardController implements Observer {
    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardController.class.getName());

    private final java.util.List<Visita> unreadNotifications = new CopyOnWriteArrayList<>();
    private Runnable onNotificationReceived;
    private final UserDAO<Paziente> pazienteDAO;
    private startupconfig.StartupConfigBean startupConfig;
    private final java.util.List<Visita> observedVisits = new CopyOnWriteArrayList<>();

    public SpecialistDashboardController() {
        LOGGER.info("[DEBUG-SPEC-CTRL-1] SpecialistDashboardController constructor called.");

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

            // Process each appointment as notification and attach observer
            for (model.Visita v : specialistAppointments) {
                if (!unreadNotifications.contains(v)) {
                    unreadNotifications.add(v);
                }
                // Attach as observer to each visit
                v.attach(this);
                observedVisits.add(v);
            }
        } catch (Exception e) {
            if (LOGGER.isLoggable(Level.WARNING)) {
                LOGGER.log(Level.WARNING, "[DEBUG-SPEC-CTRL-ERROR] Error loading appointments: {0}", e.getMessage());
            }
        }

        // Register as observer for NEW visits (Subject: NotificationManager)
        observer.NotificationManager.getInstance().attach(this);
        LOGGER.info("[DEBUG-SPEC-CTRL-9] Registered to NotificationManager for real-time notifications.");
    }

    /**
     * Implementation of the Observer interface.
     * Called by Subject (either a specific Visita change or NotificationManager for
     * new visits).
     */
    @Override
    public void update(Object arg) {
        LOGGER.info(() -> "[DEBUG-SPEC-UPDATE] update() triggered with arg: " + arg);

        // Check for session BEFORE processing. Orphan observers (post-logout) should
        // do nothing.
        if (!SessionManagerSpecialista.isLoggedIn()) {
            LOGGER.warning("[DEBUG-SPEC-UPDATE] Specialist NOT logged in. Detaching self from NotificationManager.");
            observer.NotificationManager.getInstance().detach(this);
            return;
        }

        if (arg instanceof Visita visit) {
            Specialista logged = SessionManagerSpecialista.getSpecialistaLoggato();
            if (logged != null && visit.getSpecialistaId() == logged.getId()) {
                if (!observedVisits.contains(visit)) {
                    LOGGER.info(() -> "[DEBUG-SPEC-NOTIFICATION] New visit received: " + visit);
                    unreadNotifications.add(visit);
                    visit.attach(this); // Observe future state changes
                    observedVisits.add(visit);
                } else {
                    LOGGER.info(() -> "[DEBUG-SPEC-NOTIFICATION] Update for known visit: " + visit);
                }
            }
        }

        // Trigger UI refresh
        if (onNotificationReceived != null) {
            javafx.application.Platform.runLater(onNotificationReceived);
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
     */
    public void checkSession() {
        if (!SessionManagerSpecialista.isLoggedIn()) {
            // If we're here, we are likely an active controller. detach just in case.
            observer.NotificationManager.getInstance().detach(this);
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
     * Processes the user's semantic selection and determines the next navigation
     * step.
     * 
     * @param option The option selected by the user.
     * @return A NavigationInstruction indicating where to navigate.
     * @throws IllegalArgumentException if the option is unknown.
     */
    public NavigationInstruction processSelection(SpecialistDashboardOption option) {
        checkSession();
        LOGGER.info(() -> String.format("Processing dashboard selection: %s", option));

        return switch (option) {
            case MANAGE_AGENDA -> new NavigationInstruction("Agenda");
            case PATIENTS_LIST -> new NavigationInstruction("PatientsList");
            case REPORTS -> new NavigationInstruction("Reports");
            case VISITS -> new NavigationInstruction("Visits");
            case LOGOUT -> {
                SessionManagerSpecialista.resetSession();
                yield new NavigationInstruction("Login");
            }
            default -> throw new IllegalArgumentException("Unsupported option: " + option);
        };
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
