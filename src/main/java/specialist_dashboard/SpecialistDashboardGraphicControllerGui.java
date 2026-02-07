package specialist_dashboard;

import javafx.stage.Stage;
import navigation.AppNavigator;
import navigation.GuiViewFactory;
import navigation.NavigationInstruction;
import startupconfig.StartupConfigBean;
import java.util.logging.Logger;

/**
 * Graphic Controller for the Specialist Dashboard GUI.
 * Handles navigation and stage management for specialists.
 */
public class SpecialistDashboardGraphicControllerGui {
    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardGraphicControllerGui.class.getName());
    private final SpecialistDashboardController appController = new SpecialistDashboardController();

    /**
     * Handles a semantic selection from the user.
     * 
     * @param option The option selected by the user.
     * @param config Configuration bean.
     * @param stage  The current stage.
     */
    public void handleSelection(SpecialistDashboardOption option, StartupConfigBean config, Stage stage) {
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
