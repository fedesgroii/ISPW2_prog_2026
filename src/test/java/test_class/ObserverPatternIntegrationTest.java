package test_class;

import model.Visita;
import observer.NotificationManager;
import observer.Observer;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ObserverPatternIntegrationTest {

    private NotificationManager subject;
    private Observer mockObserver;
    private Visita testVisit;

    @BeforeEach
    void setUp() {
        subject = NotificationManager.getInstance();
        mockObserver = mock(Observer.class);

        testVisit = new Visita(
                "MRARSS80A01H501Z",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                1,
                "Online",
                "Controllo",
                "Prenotata");
    }

    @Test
    @Order(1)
    void testObserverUpdateIsCalled() {
        // Register observer
        subject.attach(mockObserver);

        // Simulate new visit notification
        subject.notifyObservers(testVisit);

        // Verify update() was called with the visit
        ArgumentCaptor<Object> captor = ArgumentCaptor.forClass(Object.class);
        verify(mockObserver, times(1)).update(captor.capture());

        assertEquals(testVisit, captor.getValue(), "The observer should receive the notified visit.");
    }

    @Test
    @Order(2)
    void testObserverDeregistration() {
        // Ensure observer is attached
        subject.attach(mockObserver);

        // Detach observer
        subject.detach(mockObserver);

        // Simulate another notification
        subject.notifyObservers(testVisit);

        // Verify update() was NOT called after detachment

        Observer newMock = mock(Observer.class);
        subject.attach(newMock);
        subject.detach(newMock);
        subject.notifyObservers(testVisit);

        verify(newMock, never()).update(any());
    }

    @Test
    @Order(3)
    void testMultipleObservers() {
        Observer obs1 = mock(Observer.class);
        Observer obs2 = mock(Observer.class);

        subject.attach(obs1);
        subject.attach(obs2);

        subject.notifyObservers(testVisit);

        verify(obs1, times(1)).update(testVisit);
        verify(obs2, times(1)).update(testVisit);

        subject.detach(obs1);
        subject.detach(obs2);
    }
}
