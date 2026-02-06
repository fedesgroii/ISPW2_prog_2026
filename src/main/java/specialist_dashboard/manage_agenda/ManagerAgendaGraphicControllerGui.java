package specialist_dashboard.manage_agenda;

import javafx.stage.Stage;
import navigation.AppNavigator;
import navigation.GuiViewFactory;
import startupconfig.StartupConfigBean;

import java.util.logging.Logger;

/**
 * Graphic Controller for the Manage Agenda GUI.
 * Handles navigation for the GUI interface.
 */
public class ManagerAgendaGraphicControllerGui {
    private static final Logger LOGGER = Logger.getLogger(ManagerAgendaGraphicControllerGui.class.getName());

    /**
     * Navigates back to the Specialist Dashboard.
     * 
     * @param config The startup configuration.
     * @param stage  The current stage.
     */
    public void navigateToSpecialistDashboard(StartupConfigBean config, Stage stage) {
        try {
            GuiViewFactory factory = new GuiViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo("SpecialistDashboard", config, stage);
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
