package specialist_dashboard.manage_agenda;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import model.Paziente;
import model.Specialista;
import model.Visita;
import patient_dashboard.book_appointment.AppointmentRepository;
import session_manager.SessionManagerSpecialista;
import startupconfig.StartupConfigBean;
import authentication.UserDAO;
import authentication.factory.DAOFactory;

/**
 * Application Controller for the Manage Agenda use case.
 * Contains business logic for retrieving and rejecting specialist appointments.
 */
public class ManagerAgendaControllerApp {
    private static final Logger LOGGER = Logger.getLogger(ManagerAgendaControllerApp.class.getName());

    private final AppointmentRepository appointmentRepository;
    private final UserDAO<Paziente> pazienteDAO;

    public ManagerAgendaControllerApp(StartupConfigBean config) {
        DAOFactory.DAOPair daos = DAOFactory.createDAOs(config);
        this.appointmentRepository = daos.appointmentRepository;
        this.pazienteDAO = daos.pazienteDAO;
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

    /**
     * Retrieves all future visits (today and onwards) for the logged specialist.
     * 
     * @return List of future visits, sorted by date and time.
     */
    public List<ManageAgendaBean> getFutureVisits() {
        checkSession();
        try {
            Specialista logged = getLoggedSpecialist();
            LOGGER.log(Level.INFO, "[AGENDA-DEBUG-1] Logged specialist ID: {0}, Email: {1}",
                    new Object[] { logged.getId(), logged.getEmail() });

            List<Visita> allVisits = appointmentRepository.findBySpecialistId(logged.getId());
            LOGGER.log(Level.INFO, "[AGENDA-DEBUG-3] Total visits found for specialist ID: {0}", allVisits.size());

            // Log first few visits for debugging
            for (int i = 0; i < Math.min(3, allVisits.size()); i++) {
                Visita v = allVisits.get(i);
                LOGGER.log(Level.INFO, "[AGENDA-DEBUG-4] Visit {0}: date={1}, specialistId={2}",
                        new Object[] { i, v.getData(), v.getSpecialistaId() });
            }

            LocalDate today = LocalDate.now();
            LOGGER.log(Level.INFO, "[AGENDA-DEBUG-5] Today''s date: {0}", today);

            List<Visita> futureVisits = allVisits.stream()
                    .filter(v -> !v.getData().isBefore(today)) // >= today
                    .sorted((v1, v2) -> {
                        int dateComp = v1.getData().compareTo(v2.getData());
                        if (dateComp != 0)
                            return dateComp;
                        return v1.getOrario().compareTo(v2.getOrario());
                    })
                    .toList();

            LOGGER.log(Level.INFO, "[AGENDA-DEBUG-6] Future visits after filtering: {0}", futureVisits.size());

            return futureVisits.stream()
                    .map(v -> {
                        Optional<Paziente> p = pazienteDAO.findById(v.getPazienteCodiceFiscale());
                        String name = p.map(value -> value.getNome() + " " + value.getCognome())
                                .orElse("Paziente non trovato (" + v.getPazienteCodiceFiscale() + ")");
                        return new ManageAgendaBean(
                                v.getData(),
                                v.getOrario(),
                                name,
                                v.getTipoVisita(),
                                v.getMotivoVisita(),
                                v.getStato());
                    })
                    .toList();
        } catch (Exception e) {
            LOGGER.severe("[AGENDA-DEBUG-ERROR] Error retrieving future visits: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    /**
     * Rejects (deletes) a visit.
     * 
     * @param bean The visit bean to reject.
     * @return true if rejection was successful, false otherwise.
     */
    public boolean rejectVisit(ManageAgendaBean bean) {
        checkSession();
        if (bean == null) {
            LOGGER.warning("Attempted to reject null visit.");
            return false;
        }
        try {
            Specialista logged = getLoggedSpecialist();
            List<Visita> allVisits = appointmentRepository.findBySpecialistId(logged.getId());

            // Find the original Visita object that matches the bean's data
            Visita toReject = allVisits.stream()
                    .filter(v -> v.getData().equals(bean.getDate()) &&
                            v.getOrario().equals(bean.getTime()))
                    .findFirst()
                    .orElse(null);

            if (toReject == null) {
                LOGGER.log(Level.WARNING, "Could not find matching visit to reject for bean: {0}", bean);
                return false;
            }

            boolean result = appointmentRepository.delete(toReject);
            if (result) {
                LOGGER.log(Level.INFO, "Visit rejected successfully: {0}", bean);
            } else {
                LOGGER.log(Level.WARNING, "Failed to reject visit: {0}", bean);
            }
            return result;
        } catch (Exception e) {
            LOGGER.severe("Error rejecting visit: " + e.getMessage());
            return false;
        }
    }
}
