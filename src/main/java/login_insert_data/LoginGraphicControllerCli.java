package login_insert_data;

import authentication.AuthenticationResult;
import navigation.AppNavigator;
import navigation.CliViewFactory;
import navigation.ViewFactory;
import startupconfig.StartupConfigBean;

import java.util.logging.Logger;

/**
 * Controller Grafico per la gestione del login via CLI.
 * Media tra la View CLI e il Controller Applicativo.
 */
public class LoginGraphicControllerCli {
    private static final Logger LOGGER = Logger.getLogger(LoginGraphicControllerCli.class.getName());
    private final LoginController appController;

    public LoginGraphicControllerCli(StartupConfigBean config) {
        this.appController = new LoginController(config);
    }

    /**
     * Gestisce il tentativo di login dalla CLI.
     */
    public boolean handleLogin(String email, String password, StartupConfigBean config) {
        LoginBean bean = new LoginBean(email, password);
        AuthenticationResult result = appController.authenticate(bean);

        if (result.isSuccess()) {
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Login CLI successful for %s",
                    Thread.currentThread().getName(), email));

            // Avvio sessione
            appController.startUserSession(result);

            // Navigazione verso la dashboard CLI appropriata
            String dashboardView = "Patient".equals(result.getUserType())
                    ? "PatientDashboard"
                    : "SpecialistDashboard";

            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Navigazione CLI verso Dashboard %s",
                    Thread.currentThread().getName(), dashboardView));

            ViewFactory factory = new CliViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo(dashboardView, config, null);

            return true;
        } else {
            LOGGER.warning(() -> String.format("[DEBUG][Thread: %s] Login CLI failed for %s: %s",
                    Thread.currentThread().getName(), email, result.getErrorMessage()));
            printMessage("\n[ERROR] Login fallito: " + result.getErrorMessage() + ". Riprova.");
            return false;
        }
    }

    private void printMessage(String message) {
        System.out.println(message);
    }
}
