package patient_dashboard.book_appointment;

import javafx.scene.control.Alert;
import javafx.stage.Stage;
import navigation.AppNavigator;
import navigation.GuiViewFactory;
import startupconfig.StartupConfigBean;
import model.Paziente;
import model.Specialista;
import patient_dashboard.PatientDashboardController;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Logger;

/**
 * Graphic Controller for the Book Appointment GUI.
 * Handles data collection from the UI, communication with the App Controller,
 * and navigation.
 */
public class BookAppointmentGraphicControllerGui {
    private static final Logger LOGGER = Logger.getLogger(BookAppointmentGraphicControllerGui.class.getName());
    private final BookAppointmentControllerApp appController = new BookAppointmentControllerApp();
    private final PatientDashboardController dashboardController = new PatientDashboardController();

    public void bookAppointment(BookAppointmentBean bean, StartupConfigBean config, Stage stage) {
        LOGGER.info("[DEBUG] BookAppointmentGraphicControllerGui.bookAppointment called");

        try {
            // Get the logged-in patient
            Paziente loggedPatient = dashboardController.getLoggedPatient();

            // Call the Application Controller
            String result = appController.bookAppointment(bean, loggedPatient);

            if ("SUCCESS".equals(result)) {
                showInfoAlert("Successo", "Prenotazione effettuata con successo!");
                navigateToDashboard(config, stage);
            } else {
                showErrorAlert("Errore di Validazione", result);
            }
        } catch (Exception e) {
            LOGGER.severe("[DEBUG] Error during booking: " + e.getMessage());
            showErrorAlert("Errore", "Si è verificato un errore imprevisto: " + e.getMessage());
        }
    }

    public List<Specialista> getAvailableSpecialists(StartupConfigBean config) {
        return appController.getAvailableSpecialists(config);
    }

    public List<LocalTime> getAvailableSlots(BookAppointmentBean bean) {
        return appController.getAvailableSlots(bean);
    }

    public String validateDate(BookAppointmentBean bean) {
        return appController.validateDate(bean);
    }

    public void navigateToDashboard(StartupConfigBean config, Stage stage) {
        try {
            GuiViewFactory factory = new GuiViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo("PatientDashboard", config, stage);
        } catch (Exception e) {
            LOGGER.severe("Navigation to Dashboard failed: " + e.getMessage());
        }
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
