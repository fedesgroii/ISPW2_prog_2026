package specialist_dashboard;

import navigation.AppNavigator;
import navigation.CliViewFactory;
import startupconfig.StartupConfigBean;
import java.util.logging.Logger;

/**
 * Graphic Controller for the Specialist Dashboard CLI.
 * Handles navigation and data routing for the CLI interface.
 */
public class SpecialistDashboardGraphicControllerCli {
    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardGraphicControllerCli.class.getName());

    public void navigateToView(String viewName, StartupConfigBean config) {
        try {
            CliViewFactory factory = new CliViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo(viewName, config, null);
        } catch (Exception e) {
            LOGGER.severe("Navigation error: " + e.getMessage());
        }
    }

    public SpecialistDashboardBean getDashboardData(SpecialistDashboardController appController) {
        return appController.getDashboardData();
    }
}
