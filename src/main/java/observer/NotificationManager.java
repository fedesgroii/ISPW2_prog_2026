package observer;

import model.Visita;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class that manages notification history and acts as a Subject
 * for real-time notifications of NEW visits.
 */
@SuppressWarnings("java:S6548")
public class NotificationManager implements Subject {
    private static final Logger LOGGER = Logger.getLogger(NotificationManager.class.getName());
    private static NotificationManager instance;
    private final List<Visita> notificationHistory;
    private final List<Observer> observers = new ArrayList<>();
    private Visita lastNewVisit;

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
     * Records a visit for history and notifies observers (GoF Subject).
     */
    public synchronized void notifyObservers(Visita visit) {
        if (LOGGER.isLoggable(Level.INFO)) {
            LOGGER.log(Level.INFO, "[DEBUG-NOTIF-MGR] Recording and notifying new visit - SpecialistaId: [{0}]",
                    visit.getSpecialistaId());
        }

        this.lastNewVisit = visit;
        notificationHistory.add(visit);

        // Notify all registered observers (like SpecialistDashboardController)
        this.notifyObservers((Object) visit);
    }

    @Override
    public void attach(Observer o) {
        synchronized (observers) {
            if (!observers.contains(o)) {
                observers.add(o);
            }
        }
    }

    @Override
    public void detach(Observer o) {
        synchronized (observers) {
            observers.remove(o);
        }
    }

    @Override
    public void notifyObservers(Object arg) {
        List<Observer> observersCopy;
        synchronized (observers) {
            observersCopy = new ArrayList<>(observers);
        }
        for (Observer o : observersCopy) {
            o.update(arg);
        }
    }

    public synchronized Visita getLastNewVisit() {
        return lastNewVisit;
    }

    public synchronized List<Visita> getNotificationHistory() {
        return new ArrayList<>(notificationHistory);
    }
}
