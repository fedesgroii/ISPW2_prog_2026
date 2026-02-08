package observer;

import model.Visita;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class that manages notification history and logging.
 * It is no longer a Subject in the Observer pattern (real-time updates
 * are handled directly by Visita objects).
 */
@SuppressWarnings("java:S6548")
public class NotificationManager {
    private static final Logger LOGGER = Logger.getLogger(NotificationManager.class.getName());
    private static NotificationManager instance;
    private final List<Visita> notificationHistory;

    private NotificationManager() {
        notificationHistory = new ArrayList<>();
    }

    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    /**
     * Records a visit for history and logging.
     * Note: This no longer notifies dashboard observers directly.
     */
    public synchronized void notifyObservers(Visita visit) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR] Recording visit for history - SpecialistaId: [{0}], Data: [{1}]",
                    new Object[] { visit.getSpecialistaId(), visit.getData() });
        }

        notificationHistory.add(visit); // Store in history

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR] Added to history. Total history size: {0}",
                    notificationHistory.size());
        }
    }

    public synchronized List<Visita> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }
}
