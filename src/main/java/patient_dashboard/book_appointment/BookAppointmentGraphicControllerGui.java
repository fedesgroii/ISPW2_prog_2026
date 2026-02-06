package patient_dashboard.book_appointment;

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
                showToast(stage, "Prenotazione confermata!", true);
                navigateToDashboard(config, stage);
            } else {
                showToast(stage, "Impossibile completare la prenotazione: " + result, false);
            }
        } catch (Exception e) {
            LOGGER.severe("[DEBUG] Error during booking: " + e.getMessage());
            showToast(stage, "Impossibile completare la prenotazione: " + e.getMessage(), false);
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

    private void showToast(Stage owner, String message, boolean success) {
        javafx.application.Platform.runLater(() -> {
            javafx.stage.Popup popup = new javafx.stage.Popup();
            javafx.scene.control.Label label = new javafx.scene.control.Label(message);
            label.setStyle(String.format(
                    "-fx-background-color: %s; -fx-text-fill: white; -fx-padding: 15; -fx-background-radius: 8; -fx-font-size: 14px; -fx-font-weight: bold; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.3), 10, 0, 0, 0);",
                    success ? "#1E8449" : "#C0392B"));

            popup.getContent().add(label);
            popup.setAutoHide(true);

            // Show at bottom center of owner stage
            popup.show(owner);
            popup.setX(owner.getX() + (owner.getWidth() - label.getWidth()) / 2);
            popup.setY(owner.getY() + owner.getHeight() - 100); // 100px from bottom

            // Auto-close after 3 seconds
            javafx.animation.PauseTransition delay = new javafx.animation.PauseTransition(
                    javafx.util.Duration.seconds(3));
            delay.setOnFinished(_ -> popup.hide());
            delay.play();
        });
    }
}
