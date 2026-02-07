package patient_dashboard;

import navigation.AppNavigator;
import navigation.CliViewFactory;
import navigation.NavigationInstruction;
import startupconfig.StartupConfigBean;
import java.util.logging.Logger;

/**
 * Graphic Controller for the Patient Dashboard CLI.
 * Acts as a bridge between the CLI View and the Application Controller.
 * 
 * Responsibilities:
 * - Translates user inputs into semantic options.
 * - Delegates logic to the Application Controller.
 * - Executes navigation instructions via AppNavigator (with null stage).
 */
public class PatientDashboardGraphicControllerCli {
    private static final Logger LOGGER = Logger.getLogger(PatientDashboardGraphicControllerCli.class.getName());
    private final PatientDashboardController appController = new PatientDashboardController();

    /**
     * Handles a semantic selection from the user.
     * 
     * @param option The option selected by the user.
     * @param config Configuration bean.
     */
    public void handleSelection(PatientDashboardOption option, StartupConfigBean config) {
        try {
            // 1. Ask Application Controller what to do
            NavigationInstruction instruction = appController.processSelection(option);

            // 2. Execute navigation
            String viewName = instruction.getViewName();
            LOGGER.info(() -> String.format("[CLI] Translated selection %s to navigation view: %s", option, viewName));

            CliViewFactory factory = new CliViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            // In CLI mode, stage is always null
            navigator.navigateTo(viewName, config, null);

        } catch (Exception e) {
            LOGGER.severe(() -> String.format("[CLI] Error handling selection %s: %s", option, e.getMessage()));
            System.out.println("[ERRORE] Impossibile completare l'operazione: " + e.getMessage());
        }
    }
}
