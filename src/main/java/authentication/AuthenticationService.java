package authentication;

import login_insert_data.LoginBean;
import model.Paziente;
import model.Specialista;

import java.util.Optional;
import java.util.logging.Logger;

/**
 * Servizio per la gestione dell'autenticazione degli utenti.
 * Determina automaticamente il tipo di utente (Paziente o Specialista)
 * basandosi sulle credenziali fornite.
 */
public class AuthenticationService {
    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());

    private final UserDAO<Paziente> pazienteDAO;
    private final UserDAO<Specialista> specialistaDAO;

    public AuthenticationService(UserDAO<Paziente> pazDAO, UserDAO<Specialista> specDAO) {
        this.pazienteDAO = pazDAO;
        this.specialistaDAO = specDAO;
    }

    /**
     * Autentica un utente determinando automaticamente se è un Paziente o uno
     * Specialista.
     * Prova prima l'autenticazione come Paziente, poi come Specialista.
     * 
     * @param loginBean Bean contenente email e password
     * @return AuthenticationResult con l'esito dell'autenticazione
     */
    public AuthenticationResult authenticate(LoginBean loginBean) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering AuthenticationService.authenticate",
                Thread.currentThread().getName()));

        if (loginBean == null || loginBean.getEmail() == null || loginBean.getPassword() == null) {
            LOGGER.warning(() -> String.format("[DEBUG][Thread: %s] Tentativo di autenticazione con credenziali nulle",
                    Thread.currentThread().getName()));
            return AuthenticationResult.failure("Credenziali non valide");
        }

        String email = loginBean.getEmail();
        String password = loginBean.getPassword();

        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Tentativo di autenticazione per: %s",
                Thread.currentThread().getName(), email));

        // 1. Prova autenticazione come Paziente
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Prova autenticazione come Paziente",
                Thread.currentThread().getName()));
        Optional<Paziente> paziente = pazienteDAO.authenticateByEmailAndPassword(email, password);
        if (paziente.isPresent()) {
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Autenticazione come Paziente riuscita per: %s",
                    Thread.currentThread().getName(), email));
            return AuthenticationResult.success("Patient", paziente.get());
        }

        // 2. Prova autenticazione come Specialista
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Prova autenticazione come Specialista",
                Thread.currentThread().getName()));
        Optional<Specialista> specialista = specialistaDAO.authenticateByEmailAndPassword(email, password);
        if (specialista.isPresent()) {
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Autenticazione come Specialista riuscita per: %s",
                    Thread.currentThread().getName(), email));
            return AuthenticationResult.success("Specialist", specialista.get());
        }

        // 3. Credenziali non valide
        LOGGER.warning(() -> String.format("[DEBUG][Thread: %s] Autenticazione fallita per: %s",
                Thread.currentThread().getName(), email));
        return AuthenticationResult.failure("Credenziali non valide");
    }
}
