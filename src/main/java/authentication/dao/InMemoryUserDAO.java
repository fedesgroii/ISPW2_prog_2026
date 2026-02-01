package authentication.dao;

import authentication.UserDAO;
import javafx.collections.ObservableList;
import model.Paziente;
import model.Specialista;

import java.util.Optional;

/**
 * Implementazione del DAO per storage in memoria (RAM).
 * Utilizza le ObservableList gestite dai Singleton ListaPazienti e
 * ListaSpecialisti.
 * 
 * @param <T> Il tipo di utente (Paziente o Specialista)
 */
public class InMemoryUserDAO<T> implements UserDAO<T> {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(InMemoryUserDAO.class.getName());
    private final ObservableList<T> lista;

    public InMemoryUserDAO(ObservableList<T> lista) {
        this.lista = lista;
    }

    @Override
    public Optional<T> findByEmail(String email) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering InMemoryUserDAO.findByEmail: %s",
                Thread.currentThread().getName(), email));
        return lista.stream()
                .filter(u -> getEmail(u).equalsIgnoreCase(email))
                .findFirst();
    }

    @Override
    public Optional<T> authenticateByEmailAndPassword(String email, String password) {
        LOGGER.info(
                () -> String.format("[DEBUG][Thread: %s] Entering InMemoryUserDAO.authenticateByEmailAndPassword: %s",
                        Thread.currentThread().getName(), email));
        return findByEmail(email)
                .filter(u -> getPassword(u).equals(password));
    }

    // Helper per estrarre email genericamente in base al tipo
    private String getEmail(T user) {
        if (user instanceof Paziente) {
            return ((Paziente) user).getEmail();
        }
        if (user instanceof Specialista) {
            return ((Specialista) user).getEmail();
        }
        throw new IllegalArgumentException("Tipo utente non supportato: " + user.getClass().getName());
    }

    // Helper per estrarre password genericamente in base al tipo
    private String getPassword(T user) {
        if (user instanceof Paziente) {
            return ((Paziente) user).getPassword();
        }
        if (user instanceof Specialista) {
            return ((Specialista) user).getPassword();
        }
        throw new IllegalArgumentException("Tipo utente non supportato: " + user.getClass().getName());
    }
}
