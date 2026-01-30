package login_insert_data;

import java.util.logging.Logger;

/**
 * Controller Applicativo responsabile per la logica di autenticazione.
 */
public class LoginController {
    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    /**
     * Esegue il tentativo di login.
     * 
     * @param bean Il bean contenente le credenziali.
     * @param tipo Il tipo di utente (Patient/Specialist).
     * @return true se il login ha successo, false altrimenti.
     */
    public boolean authenticate(LoginBean bean, String tipo) {
        LOGGER.info(() -> "Tentativo di login per: " + bean.getEmail() + " come " + tipo);

        // Placeholder per la logica di autenticazione reale
        // Per ora accettiamo qualsiasi cosa che non sia vuota
        return bean.getEmail() != null && !bean.getEmail().isEmpty() &&
                bean.getPassword() != null && !bean.getPassword().isEmpty();
    }
}
