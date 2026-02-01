package session_manager;

import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;
import model.Paziente;

/**
 * Gestisce la sessione del paziente in modo thread-safe.
 * Tiene traccia del paziente loggato utilizzando metodi statici.
 */
public class SessionManagerPaziente {
    private static final AtomicReference<Paziente> pazienteLoggato = new AtomicReference<>();
    private static final Logger logger = Logger.getLogger(SessionManagerPaziente.class.getName());

    // Costruttore privato per evitare istanziazione
    private SessionManagerPaziente() {
        throw new UnsupportedOperationException("Questa classe non può essere istanziata.");
    }

    /**
     * Imposta il paziente loggato, terminando eventualmente una sessione esistente.
     * @param paziente Il paziente da loggare
     */
    public static void setPazienteLoggato(Paziente paziente) {
        Paziente current = pazienteLoggato.get();
        if (current != null) {
            logger.warning("Sessione esistente rilevata. Terminazione in corso per: " + current.getNome());
            // Sovrascriviamo direttamente: non serve chiamare resetSession()
        }
        pazienteLoggato.set(paziente);
        logger.info("Nuova sessione avviata per: " + paziente.getNome());
    }

    /**
     * Restituisce il paziente attualmente loggato.
     * @return Il paziente loggato
     * @throws IllegalStateException Se nessuno è loggato
     */
    public static Paziente getPazienteLoggato() {
        Paziente loggedPaziente = pazienteLoggato.get();
        if (loggedPaziente == null) {
            logger.severe("Nessun paziente loggato. Effettua il login.");
            throw new IllegalStateException("Nessun paziente loggato. Effettua il login.");
        }
        return loggedPaziente;
    }

    /**
     * Resetta la sessione (logout del paziente).
     */
    public static void resetSession() {
        Paziente previous = pazienteLoggato.getAndSet(null);
        if (previous != null) {
            logger.info("Sessione terminata per: " + previous.getNome());
        } else {
            logger.info("Nessuna sessione attiva da terminare.");
        }
    }

    /**
     * Verifica se c'è un paziente loggato.
     * @return true se loggato, false altrimenti
     */
    public static boolean isLoggedIn() {
        boolean result = pazienteLoggato.get() != null;
        if (!result) {
            logger.fine("Nessuna sessione attiva.");
        } else {
            logger.fine("Sessione attiva rilevata.");
        }
        return result;
    }
}