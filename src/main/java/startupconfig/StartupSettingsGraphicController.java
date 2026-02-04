package startupconfig;

import java.util.logging.Logger;
import javafx.stage.Stage;
import navigation.AppNavigator;
import navigation.ViewFactory;

/**
 * Controllore Grafico (Graphic Controller).
 * Si occupa puramente della gestione dell'interfaccia e della navigazione.
 * Viene invocato dal Controllore Applicativo per eseguire azioni sulla UI.
 */
public class StartupSettingsGraphicController {
    private static final Logger LOGGER = Logger.getLogger(StartupSettingsGraphicController.class.getName());

    public StartupSettingsGraphicController() {
        // Nessun riferimento al Controllore Applicativo qui (Inversione del flusso)
    }

    /**
     * Esegue la navigazione verso la schermata di Login.
     * 
     * @param bean  I dati di configurazione
     * @param stage Lo stage su cui navigare
     */
    public void executeNavigation(StartupConfigBean bean, Stage stage) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] GraphicController executing navigation",
                Thread.currentThread().getName()));

        ViewFactory factory = ViewFactory.getFactory(bean.isInterfaceMode());
        AppNavigator navigator = new AppNavigator(factory);
        navigator.navigateTo("Login", bean, stage);

        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Navigation completed by GraphicController",
                Thread.currentThread().getName()));
    }
}
