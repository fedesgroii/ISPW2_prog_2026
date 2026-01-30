package login_insert_data;

import javafx.stage.Stage;
import navigation.AppNavigator;
import startupconfig.StartupConfigBean;
import startupconfig.StartupSettingsEntity;

import java.util.logging.Logger;

/**
 * Controller Grafico per la gestione dell'inserimento dati di login.
 */
public class LoginGraphicController {
    private static final Logger LOGGER = Logger.getLogger(LoginGraphicController.class.getName());
    private final LoginViewBase guiView;
    private final LoginController appController;

    /**
     * Costruttore per la versione GUI.
     */
    public LoginGraphicController(LoginViewBase guiView) {
        this.guiView = guiView;
        this.appController = new LoginController();
    }

    /**
     * Gestisce il tentativo di login dalla GUI.
     */
    public void handleLoginAttempt(String tipo) {
        LoginBean bean = new LoginBean(
                guiView.getEmailField().getText(),
                guiView.getPasswordField().getText());

        if (appController.authenticate(bean, tipo)) {
            LOGGER.info("Login riuscito!");
            // Qui andrebbe la navigazione verso la dashboard
        } else {
            LOGGER.warning("Login fallito.");
            guiView.showError();
        }
    }

    /**
     * Gestisce il cambio di tipo di login (da Paziente a Specialista e viceversa).
     */
    public void handleSwitchLoginAction(Stage stage, String currentTipo) {
        String nextTipo = "Patient".equals(currentTipo) ? "Specialist" : "Patient";
        LOGGER.info(() -> "Cambio login da " + currentTipo + " a " + nextTipo);

        // Recupera il bean di configurazione già presente nella vista
        StartupConfigBean configBean = guiView.getConfigBean();

        new AppNavigator().navigateTo(nextTipo, configBean, stage);
    }
}
