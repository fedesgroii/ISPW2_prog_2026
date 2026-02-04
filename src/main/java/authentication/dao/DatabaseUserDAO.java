package authentication.dao;

import authentication.UserDAO;
import model.Paziente;
import model.Specialista;
import storage_db.DataStorageStrategy;

import java.util.Optional;

/**
 * Implementazione del DAO per storage su Database.
 * Delega alle strategie esistenti
 * (DatabaseStorageStrategyPaziente/Specialista).
 * 
 * @param <T> Il tipo di utente (Paziente o Specialista)
 */
public class DatabaseUserDAO<T> implements UserDAO<T> {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(DatabaseUserDAO.class.getName());
    private final DataStorageStrategy<T> strategy;

    public DatabaseUserDAO(DataStorageStrategy<T> strategy) {
        this.strategy = strategy;
    }

    @Override
    public Optional<T> findByEmail(String email) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering DatabaseUserDAO.findByEmail: %s",
                Thread.currentThread().getName(), email));
        // Delega alla strategy che implementa findByEmail
        return strategy.findByEmail(email);
    }

    @Override
    public Optional<T> authenticateByEmailAndPassword(String email, String password) {
        LOGGER.info(
                () -> String.format("[DEBUG][Thread: %s] Entering DatabaseUserDAO.authenticateByEmailAndPassword: %s",
                        Thread.currentThread().getName(), email));
        Optional<T> user = findByEmail(email);
        return user.filter(u -> checkPassword(u, password));
    }

    @Override
    public java.util.List<T> getAllInstanceOfActor() {
        return strategy.getAllInstanceOfActor();
    }

    /**
     * Verifica la corrispondenza della password.
     * Supporta sia Paziente che Specialista.
     */
    private boolean checkPassword(T user, String password) {
        String userPassword = null;
        if (user instanceof Paziente paziente) {
            userPassword = paziente.getPassword();
        } else if (user instanceof Specialista specialista) {
            userPassword = specialista.getPassword();
        }
        return userPassword != null && userPassword.equals(password);
    }
}
