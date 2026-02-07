package patient_dashboard;

import javafx.stage.Stage;
import navigation.AppNavigator;
import navigation.GuiViewFactory;
import startupconfig.StartupConfigBean;
import java.util.logging.Logger;

/**
 * Graphic Controller for the Patient Dashboard GUI.
 * Acts as a bridge between the View and the Application Controller.
 */

public class PatientDashboardGraphicControllerGui {
    private static final Logger LOGGER = Logger.getLogger(PatientDashboardGraphicControllerGui.class.getName());
    private final PatientDashboardController appController = new PatientDashboardController();

    /**
     * Handles a semantic selection from the user.
     * 
     * @param option The option selected by the user.
     * @param config Configuration bean.
     * @param stage  The current stage.
     */
    public void handleSelection(PatientDashboardOption option, StartupConfigBean config, Stage stage) {
        try {
            // 1. Ask Application Controller what to do
            NavigationInstruction instruction = appController.processSelection(option);

            // 2. Execute navigation
            String viewName = instruction.getViewName();
            LOGGER.info(() -> String.format("Translated selection %s to navigation view: %s", option, viewName));

            GuiViewFactory factory = new GuiViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo(viewName, config, stage);

        } catch (Exception e) {
            LOGGER.severe(() -> String.format("Error handling selection %s: %s", option, e.getMessage()));
        }
    }
}
