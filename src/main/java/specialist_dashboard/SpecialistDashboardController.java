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

/**
 * Application Controller for the Specialist Dashboard.
 * Shared between GUI and CLI views.
 * Handles session logic and data retrieval for specialists.
 * Acts as a ConcreteObserver in the Observer pattern.
 */
public class SpecialistDashboardController implements Observer {
    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardController.class.getName());

    private final java.util.List<Visita> unreadNotifications = new java.util.ArrayList<>();
    private Runnable onNotificationReceived;
    private final UserDAO<Paziente> pazienteDAO;
    private startupconfig.StartupConfigBean startupConfig;
    private final java.util.List<Visita> observedVisits = new java.util.ArrayList<>();

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
                if (LOGGER.isLoggable(Level.INFO)) {
                    LOGGER.log(Level.INFO, "[DEBUG-SPEC-CTRL-7] Processing appointment and attaching: {0}", v);
                }
                if (!unreadNotifications.contains(v)) {
                    unreadNotifications.add(v);
                }
                // Attach as observer to each visit
                v.attach(this);
                observedVisits.add(v);
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

    /**
     * Implementation of the Observer interface.
     * Called by the Subject (Visita) when its state changes.
     */
    @Override
    public void update() {
        LOGGER.info("[DEBUG-SPEC-UPDATE] update() called by a Visita subject.");
        // In a real scenario, we might want to know which Visita changed,
        // but for the dashboard refresh, triggering the callback is often enough.
        if (onNotificationReceived != null) {
            javafx.application.Platform.runLater(onNotificationReceived);
        }
    }

    private void onNewVisit(model.Visita visit) {
        // This method is now legacy as the notify logic moved to direct Visita ->
        // Controller.
        // However, if BookAppointmentController still uses NotificationManager to
        // "dispatch"
        // new visits to potentially interested controllers, we might still need a way
        // to
        // register this controller as an observer for NEW visits if they aren't in the
        // DB yet.
        // But the requirements say: "Aggiungi una dipendenza per registrarsi su Visit
        // (es. visit.attach(this) nel costruttore o inizializzazione)".
        // For new visits created during the app lifecycle, they should be attached
        // here.
        if (visit != null && !observedVisits.contains(visit)) {
            visit.attach(this);
            observedVisits.add(visit);
            unreadNotifications.add(visit);
            if (onNotificationReceived != null) {
                javafx.application.Platform.runLater(onNotificationReceived);
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
