package authentication.dao;

import authentication.UserDAO;
import model.Paziente;
import model.Specialista;
import storage_file.FileManagerPazienti;
import storage_file.FileManagerSpecialisti;

import java.util.Optional;

/**
 * Implementazione del DAO per storage su File System.
 * Delega ai FileManager esistenti.
 * 
 * @param <T> Il tipo di utente (Paziente o Specialista)
 */
public class FileUserDAO<T> implements UserDAO<T> {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(FileUserDAO.class.getName());
    private final Object fileManager;

    public FileUserDAO(Object fileManager) {
        this.fileManager = fileManager;
    }

    @Override
    public Optional<T> findByEmail(String email) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering FileUserDAO.findByEmail: %s",
                Thread.currentThread().getName(), email));
        // Casting based sul tipo del fileManager
        if (fileManager instanceof FileManagerPazienti) {
            @SuppressWarnings("unchecked")
            Optional<T> result = (Optional<T>) ((FileManagerPazienti) fileManager).findByEmail(email);
            return result;
        } else if (fileManager instanceof FileManagerSpecialisti) {
            @SuppressWarnings("unchecked")
            Optional<T> result = (Optional<T>) ((FileManagerSpecialisti) fileManager).findByEmail(email);
            return result;
        }
        return Optional.empty();
    }

    @Override
    public java.util.List<T> getAllInstanceOfActor() {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering FileUserDAO.getAllInstanceOfActor",
                Thread.currentThread().getName()));
        if (fileManager instanceof FileManagerPazienti) {
            @SuppressWarnings("unchecked")
            java.util.List<T> result = (java.util.List<T>) ((FileManagerPazienti) fileManager).getAllInstanceOfActor();
            return result;
        } else if (fileManager instanceof FileManagerSpecialisti) {
            @SuppressWarnings("unchecked")
            java.util.List<T> result = (java.util.List<T>) ((FileManagerSpecialisti) fileManager)
                    .getAllInstanceOfActor();
            return result;
        }
        return java.util.List.of();
    }

    @Override
    public Optional<T> authenticateByEmailAndPassword(String email, String password) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering FileUserDAO.authenticateByEmailAndPassword: %s",
                Thread.currentThread().getName(), email));
        Optional<T> user = findByEmail(email);
        return user.filter(u -> checkPassword(u, password));
    }

    /**
     * Verifica la corrispondenza della password.
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
