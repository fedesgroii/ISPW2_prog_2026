package observer;

/**
 * Generic Subject interface for the Observer pattern.
 */
public interface Subject {
    void attach(Observer observer);

    void detach(Observer observer);

    void notifyObservers(Object arg);
}
