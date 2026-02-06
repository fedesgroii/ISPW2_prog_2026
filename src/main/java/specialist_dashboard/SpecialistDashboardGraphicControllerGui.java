package specialist_dashboard;

import javafx.stage.Stage;
import navigation.AppNavigator;
import navigation.GuiViewFactory;
import startupconfig.StartupConfigBean;
import java.util.logging.Logger;

/**
 * Graphic Controller for the Specialist Dashboard GUI.
 * Handles navigation and stage management for specialists.
 */
public class SpecialistDashboardGraphicControllerGui {
    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardGraphicControllerGui.class.getName());

    /**
     * Navigates to the specified view and closes the current stage.
     * 
     * @param viewName     Name of the target view.
     * @param config       Configuration bean.
     * @param currentStage The current stage to transition from.
     */
    public void navigateToView(String viewName, StartupConfigBean config, Stage currentStage) {
        LOGGER.info(() -> String.format("Navigating to %s from Specialist Dashboard", viewName));
        try {
            GuiViewFactory factory = new GuiViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo(viewName, config, currentStage);
        } catch (Exception e) {
            String message = String.format("Navigation to %s failed", viewName);
            LOGGER.log(java.util.logging.Level.SEVERE, message, e);
        }
    }

    public SpecialistDashboardBean getDashboardData(SpecialistDashboardController appController) {
        return appController.getDashboardData();
    }
}
