package observer;

import model.Visita;

/**
 * Observer interface for the Observer pattern.
 * Implementing classes will receive updates when a new Visit is booked.
 */
public interface SpecialistNotificationObserver {
    /**
     * Called when a new visit is booked.
     * 
     * @param visit The new visit details.
     */
    void update(Visita visit);
}
