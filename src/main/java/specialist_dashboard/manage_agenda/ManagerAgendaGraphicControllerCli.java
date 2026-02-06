package specialist_dashboard.manage_agenda;

import navigation.AppNavigator;
import navigation.CliViewFactory;
import startupconfig.StartupConfigBean;

import java.util.logging.Logger;

/**
 * Graphic Controller for the Manage Agenda CLI.
 * Handles navigation for the CLI interface.
 */
public class ManagerAgendaGraphicControllerCli {
    private static final Logger LOGGER = Logger.getLogger(ManagerAgendaGraphicControllerCli.class.getName());

    /**
     * Navigates back to the Specialist Dashboard.
     * 
     * @param config The startup configuration.
     */
    public void navigateToSpecialistDashboard(StartupConfigBean config) {
        try {
            CliViewFactory factory = new CliViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo("SpecialistDashboard", config, null);
        } catch (Exception e) {
            LOGGER.severe("Navigation error: " + e.getMessage());
        }
    }

    public java.util.List<ManageAgendaBean> getFutureVisits(ManagerAgendaControllerApp appController) {
        return appController.getFutureVisits();
    }

    public boolean rejectVisit(ManagerAgendaControllerApp appController, ManageAgendaBean bean) {
        return appController.rejectVisit(bean);
    }
}
