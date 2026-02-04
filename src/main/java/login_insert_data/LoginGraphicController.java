package login_insert_data;

import javafx.stage.Stage;
import startupconfig.StartupConfigBean;
import authentication.AuthenticationResult;

import java.util.logging.Logger;

/**
 * Controller Grafico per la gestione dell'inserimento dati di login.
 */
public class LoginGraphicController {
        private static final Logger LOGGER = Logger.getLogger(LoginGraphicController.class.getName());
        private LoginController appController; // Changed to non-final
        private final LoginViewBase guiView;

        /**
         * Costruttore per la versione GUI.
         */
        private static final String USER_TYPE_PATIENT = "Patient";
        private static final String USER_TYPE_SPECIALIST = "Specialist";

        public LoginGraphicController(LoginViewBase guiView) {
                this.guiView = guiView;
                // appController initialization removed from constructor
        }

        // New method for lazy initialization of appController
        private LoginController getAppController() {
                if (appController == null) {
                        appController = new LoginController(guiView.getConfigBean());
                }
                return appController;
        }

        /**
         * Gestisce il tentativo di login dalla GUI.
         */
        public void handleLoginAttempt(String requestedTipo) {
                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering handleLoginAttempt: requestedTipo=%s",
                                Thread.currentThread().getName(), requestedTipo));
                LoginBean bean = new LoginBean(
                                guiView.getEmailField().getText(),
                                guiView.getPasswordField().getText());

                AuthenticationResult result = getAppController().authenticate(bean);

                if (result.isSuccess()) {
                        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Login riuscito come: %s",
                                        Thread.currentThread().getName(), result.getUserType()));

                        // Avvio della sessione (il LoginController gestisce la sessione)
                        getAppController().startUserSession(result);

                        // Navigazione verso la dashboard appropriata
                        String dashboardView = USER_TYPE_PATIENT.equals(result.getUserType())
                                        ? "PatientDashboard"
                                        : "SpecialistDashboard";

                        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Navigazione verso Dashboard %s",
                                        Thread.currentThread().getName(), dashboardView));

                        StartupConfigBean configBean = guiView.getConfigBean();
                        navigation.ViewFactory factory = new navigation.GuiViewFactory();
                        navigation.AppNavigator navigator = new navigation.AppNavigator(factory);
                        navigator.navigateTo(dashboardView, configBean, guiView.getPrimaryStage());
                } else {
                        LOGGER.warning(() -> String.format("[DEBUG][Thread: %s] Login fallito: %s",
                                        Thread.currentThread().getName(), result.getErrorMessage()));
                        guiView.showError();
                }
        }

        /**
         * Gestisce il cambio di tipo di login (da Paziente a Specialista e viceversa).
         */
        public void handleSwitchLoginAction(Stage stage, String currentTipo) {
                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering handleSwitchLoginAction: currentTipo=%s",
                                Thread.currentThread().getName(), currentTipo));
                String nextTipo = USER_TYPE_PATIENT.equals(currentTipo) ? USER_TYPE_SPECIALIST : USER_TYPE_PATIENT;
                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Cambio login da %s a %s",
                                Thread.currentThread().getName(), currentTipo, nextTipo));

                // Recupera il bean di configurazione già presente nella vista
                StartupConfigBean configBean = guiView.getConfigBean();

                // Factory Method Pattern: Creiamo la factory corretta in base alla modalità
                navigation.ViewFactory factory = configBean.isInterfaceMode()
                                ? new navigation.GuiViewFactory()
                                : new navigation.CliViewFactory();
                navigation.AppNavigator navigator = new navigation.AppNavigator(factory);
                navigator.navigateTo(nextTipo, configBean, stage);
        }
}
