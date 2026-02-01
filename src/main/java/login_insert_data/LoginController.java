package login_insert_data;

import authentication.AuthenticationResult;
import authentication.AuthenticationService;
import authentication.factory.DAOFactory;
import model.Paziente;
import model.Specialista;
import session_manager.SessionManagerPaziente;
import session_manager.SessionManagerSpecialista;
import startupconfig.StartupConfigBean;

import java.util.logging.Logger;

/**
 * Controller Applicativo responsabile per la logicadi autenticazione.
 * Utilizza il DAOFactory per ottenere i DAO corretti in base alla
 * configurazione,
 * e l'AuthenticationService per gestire l'autenticazione.
 */
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    private final AuthenticationService authService;
    private final StartupConfigBean config;

    public LoginController(StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Initializing LoginController",
                Thread.currentThread().getName()));
        this.config = config;

        // Crea i DAO in base alla configurazione
        DAOFactory.DAOPair daos = DAOFactory.createDAOs(config);

        // Inizializza il service
        this.authService = new AuthenticationService(daos.pazienteDAO, daos.specialistaDAO);
    }

    /**
     * Gestisce il tentativo di autenticazione.
     * 
     * @param bean Il bean con le credenziali
     * @return AuthenticationResult con l'esito
     */
    public AuthenticationResult authenticate(LoginBean bean) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering authenticate for email: %s",
                Thread.currentThread().getName(), (bean != null ? bean.getEmail() : "null")));
        return authService.authenticate(bean);
    }

    /**
     * Avvia la sessione appropriata dopo l'autenticazione riuscita.
     * 
     * @param result Il risultato dell'autenticazione
     * @throws IllegalStateException se il result non è successful
     */
    public void startUserSession(AuthenticationResult result) {
        LOGGER.info(
                () -> String.format("[DEBUG][Thread: %s] Entering startUserSession", Thread.currentThread().getName()));
        if (!result.isSuccess()) {
            LOGGER.severe(() -> String.format("[DEBUG][Thread: %s] Attempted to start session with failed result",
                    Thread.currentThread().getName()));
            throw new IllegalStateException("Cannot start session for failed authentication");
        }

        if ("Patient".equals(result.getUserType())) {
            Paziente paziente = (Paziente) result.getUser();
            SessionManagerPaziente.setPazienteLoggato(paziente);
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Sessione Paziente avviata per: %s",
                    Thread.currentThread().getName(), paziente.getEmail()));
        } else if ("Specialist".equals(result.getUserType())) {
            Specialista specialista = (Specialista) result.getUser();
            SessionManagerSpecialista.setSpecialistaLoggato(specialista);
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Sessione Specialista avviata per: %s",
                    Thread.currentThread().getName(), specialista.getEmail()));
        }
    }
}
