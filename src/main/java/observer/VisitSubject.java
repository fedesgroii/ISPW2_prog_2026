package observer;

/**
 * Subject interface for the Observer pattern.
 * Manages the registration, removal, and notification of observers
 * interested in Visit-related events.
 */
public interface VisitSubject {
    void registerObserver(SpecialistNotificationObserver observer);

    void removeObserver(SpecialistNotificationObserver observer);

    void notifyObservers(model.Visita visit);
}
