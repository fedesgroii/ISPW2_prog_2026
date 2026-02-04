package authentication;

import java.util.Optional;

/**
 * Interfaccia DAO (Data Access Object) per l'accesso ai dati degli utenti.
 * Fornisce operazioni di ricerca e autenticazione indipendenti dal tipo di
 * storage.
 * 
 * @param <T> Il tipo di utente (Paziente o Specialista)
 */
public interface UserDAO<T> {

    /**
     * Trova un utente per email.
     * 
     * @param email L'email dell'utente da cercare
     * @return Optional contenente l'utente se trovato, Optional.empty() altrimenti
     */
    Optional<T> findByEmail(String email);

    /**
     * Autentica un utente verificando email e password.
     * 
     * @param email    L'email dell'utente
     * @param password La password in chiaro
     * @return Optional contenente l'utente se le credenziali sono valide,
     *         Optional.empty() altrimenti
     */
    Optional<T> authenticateByEmailAndPassword(String email, String password);

    /**
     * Recupera tutte le istanze degli attori gestiti da questo DAO.
     * 
     * @return Una lista contenente tutti gli utenti trovati
     */
    java.util.List<T> getAllInstanceOfActor();
}
