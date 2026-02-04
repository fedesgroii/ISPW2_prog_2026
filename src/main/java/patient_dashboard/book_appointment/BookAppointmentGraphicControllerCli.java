package patient_dashboard.book_appointment;

import model.Paziente;
import model.Specialista;
import patient_dashboard.PatientDashboardController;
import startupconfig.StartupConfigBean;

import java.time.LocalTime;
import java.util.List;

/**
 * Controller Grafico per la gestione della prenotazione via CLI.
 * Media tra la View CLI e il Controller Applicativo.
 */
public class BookAppointmentGraphicControllerCli {
    private final BookAppointmentControllerApp appController = new BookAppointmentControllerApp();
    private final PatientDashboardController dashboardController = new PatientDashboardController();

    public List<Specialista> getAvailableSpecialists(StartupConfigBean config) {
        return appController.getAvailableSpecialists(config);
    }

    public List<LocalTime> getAvailableSlots(BookAppointmentBean bean) {
        return appController.getAvailableSlots(bean);
    }

    public String validateDate(BookAppointmentBean bean) {
        return appController.validateDate(bean);
    }

    public String bookAppointment(BookAppointmentBean bean) {
        Paziente loggedPatient = dashboardController.getLoggedPatient();
        return appController.bookAppointment(bean, loggedPatient);
    }

    public Paziente getLoggedPatient() {
        return dashboardController.getLoggedPatient();
    }
}
