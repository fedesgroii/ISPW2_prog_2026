package test_class;

import model.Paziente;
import model.Visita;
import org.junit.jupiter.api.*;
import org.mockito.MockedStatic;
import patient_dashboard.book_appointment.*;
import authentication.factory.DAOFactory;
import observer.NotificationManager;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AppointmentBookingUseCaseTest {

    private BookAppointmentControllerApp controller;
    private BookAppointmentBean validBean;
    private Paziente loggedPatient;

    @BeforeEach
    void setUp() {
        controller = new BookAppointmentControllerApp();

        loggedPatient = new Paziente.Builder()
                .nome("MARIO")
                .cognome("ROSSI")
                .codiceFiscalePaziente("MRARSS80A01H501Z")
                .email("mario.rossi@email.com")
                .password("password")
                .dataDiNascita(LocalDate.of(1980, 1, 1))
                .build();

        validBean = new BookAppointmentBean();
        validBean.setSpecialistId(1);
        validBean.setSpecialist("Dr. Smith");
        validBean.setName("Mario");
        validBean.setSurname("Rossi");
        validBean.setPhone("3331234567");
        validBean.setDateOfBirth("01/01/1980");
        validBean.setServiceType("Online");
        validBean.setReason("Consultazione");

        // Ensure we pick a valid date
        LocalDate testDate = LocalDate.now().plusDays(1);
        while (testDate.getDayOfWeek().getValue() >= 6 || ItalianHolidayCalendar.isItalianHoliday(testDate)) {
            testDate = testDate.plusDays(1);
        }
        validBean.setDate(testDate);
        validBean.setTime(LocalTime.of(10, 0));
    }

    @Test
    @Order(1)
    void testCompleteBookingFlow() {

        try (MockedStatic<DAOFactory> daoFactoryMockedStatic = mockStatic(DAOFactory.class)) {
            AppointmentRepository mockRepo = mock(AppointmentRepository.class);
            DAOFactory.DAOPair mockPair = new DAOFactory.DAOPair(null, null, mockRepo);

            daoFactoryMockedStatic.when(() -> DAOFactory.createDAOs(any())).thenReturn(mockPair);
            when(mockRepo.save(any(Visita.class))).thenReturn(true);

            String result = controller.bookAppointment(validBean, loggedPatient);

            assertEquals("SUCCESS", result, "The booking should be successful.");
            verify(mockRepo, times(1)).save(any(Visita.class));

            // Verify NotificationManager has the last visit
            Visita lastVisit = NotificationManager.getInstance().getLastNewVisit();
            assertNotNull(lastVisit);
            assertEquals(validBean.getSpecialistId(), lastVisit.getSpecialistaId());
        }
    }

    @Test
    @Order(2)
    void testInvalidDateWeekends() {
        // Saturday
        LocalDate saturday = LocalDate.now();
        while (saturday.getDayOfWeek().getValue() != 6) {
            saturday = saturday.plusDays(1);
        }
        validBean.setDate(saturday);

        String result = controller.bookAppointment(validBean, loggedPatient);
        assertEquals("Non è possibile prenotare visite di sabato o domenica.", result);
    }

    @Test
    @Order(3)
    void testInvalidDateHoliday() {
        // Jan 1st is always a holiday in Italy
        LocalDate holiday = LocalDate.of(LocalDate.now().getYear() + 1, 1, 1);
        // If it falls on weekend, we don't care, it will trigger the weekend check
        // first or holiday check
        validBean.setDate(holiday);

        String result = controller.bookAppointment(validBean, loggedPatient);
        // It might return weekend error if Jan 1st is Sat/Sun, otherwise holiday error
        assertTrue(result.contains("sabato o domenica") || result.contains("giorno festivo"));
    }

    @Test
    @Order(4)
    void testInvalidServiceType() {
        validBean.setServiceType("Invalido");
        String result = controller.bookAppointment(validBean, loggedPatient);
        assertEquals("Tipo di prestazione non valido (deve essere 'Online' o 'In presenza').", result);
    }

    @Test
    @Order(5)
    void testValidServiceTypeInPresenza() {
        validBean.setServiceType("In presenza");

        try (MockedStatic<DAOFactory> daoFactoryMockedStatic = mockStatic(DAOFactory.class)) {
            AppointmentRepository mockRepo = mock(AppointmentRepository.class);
            DAOFactory.DAOPair mockPair = new DAOFactory.DAOPair(null, null, mockRepo);
            daoFactoryMockedStatic.when(() -> DAOFactory.createDAOs(any())).thenReturn(mockPair);
            when(mockRepo.save(any(Visita.class))).thenReturn(true);

            String result = controller.bookAppointment(validBean, loggedPatient);
            assertEquals("SUCCESS", result);
        }
    }
}
