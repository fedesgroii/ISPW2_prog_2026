package session_manager;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import model.Specialista;

/**
 * Gestisce la sessione dello specialista in modo thread-safe.
 * Tiene traccia dello specialista loggato utilizzando metodi statici.
 */
public class SessionManagerSpecialista {
    private static final AtomicReference<Specialista> specialistaLoggato = new AtomicReference<>(); // Specialista loggato (utilizzabile da un thread alla volta)
    private static final Logger logger = Logger.getLogger(SessionManagerSpecialista.class.getName()); // Logger per i messaggi

    // Costruttore privato per evitare istanziazione
    private SessionManagerSpecialista() {
        throw new UnsupportedOperationException("Questa classe non può essere istanziata.");
    }

    /**
     * Imposta lo specialista loggato, terminando eventualmente una sessione esistente.
     * @param specialista Lo specialista da loggare
     */
    public static void setSpecialistaLoggato(Specialista specialista) {
        Specialista current = specialistaLoggato.get();
        if (current != null) {
            logger.warning("Sessione esistente rilevata. Terminazione in corso per: " + current.getNome());
            // Nessun "reset" esplicito: sovrascriviamo direttamente
        }
        specialistaLoggato.set(specialista);
        logger.info("Nuova sessione avviata per: " + specialista.getNome());
    }

    /**
     * Restituisce lo specialista attualmente loggato.
     * @return Lo specialista loggato
     * @throws IllegalStateException Se nessuno è loggato
     */
    public static Specialista getSpecialistaLoggato() {
        Specialista loggedSpecialista = specialistaLoggato.get();
        if (loggedSpecialista == null) {
            logger.severe("Nessuno specialista loggato. Effettua il login.");
            throw new IllegalStateException("Nessuno specialista loggato. Effettua il login.");
        }
        return loggedSpecialista;
    }

    /**
     * Resetta la sessione (logout dello specialista).
     */
    public static void resetSession() {
        Specialista previous = specialistaLoggato.getAndSet(null);
        if (previous != null) {
            logger.info("Sessione terminata per: " + previous.getNome());
        }
    }

    /**
     * Verifica se c'è uno specialista loggato.
     * @return true se loggato, false altrimenti
     */
    public static boolean isLoggedIn() {
        boolean result = specialistaLoggato.get() != null;
        if (!result) {
            logger.fine("Nessuna sessione attiva.");
        }
        return result;
    }
}