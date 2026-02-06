package observer;

import model.Visita;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class that acts as the concrete Subject in the Observer pattern.
 * It manages the list of registered observers and dispatches notifications.
 * This decouples the App Controllers from the Dashboard Controllers.
 */
public class NotificationManager implements VisitSubject {
    private static final Logger LOGGER = Logger.getLogger(NotificationManager.class.getName());
    private static NotificationManager instance;
    private final List<SpecialistNotificationObserver> observers;
    private final List<Visita> notificationHistory;

    private NotificationManager() {
        observers = new ArrayList<>();
        notificationHistory = new ArrayList<>();
    }

    public static synchronized NotificationManager getInstance() {
        if (instance == null) {
            instance = new NotificationManager();
        }
        return instance;
    }

    @Override
    public synchronized void registerObserver(SpecialistNotificationObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG] Observer registered: {0}", observer.getClass().getSimpleName());
            }
        }
    }

    @Override
    public synchronized void removeObserver(SpecialistNotificationObserver observer) {
        observers.remove(observer);
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "[DEBUG] Observer removed: {0}", observer.getClass().getSimpleName());
        }
    }

    @Override
    public synchronized void notifyObservers(Visita visit) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR-1] notifyObservers() called. Number of registered observers: {0}",
                    observers.size());
            LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR-2] Visit to notify - SpecialistaId: [{0}], Data: [{1}]",
                    new Object[] { visit.getSpecialistaId(), visit.getData() });
        }

        notificationHistory.add(visit); // Store in history

        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR-3] Added to history. Total history size: {0}",
                    notificationHistory.size());
        }

        for (int i = 0; i < observers.size(); i++) {
            SpecialistNotificationObserver observer = observers.get(i);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR-4] Notifying observer #{0}: {1}",
                        new Object[] { i + 1, observer.getClass().getSimpleName() });
            }
            observer.update(visit);
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR-5] Observer #{0} notified.", i + 1);
            }
        }
        LOGGER.info("[DEBUG-NOTIF-MGR-6] All observers notified.");
    }

    public synchronized List<Visita> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }
}
