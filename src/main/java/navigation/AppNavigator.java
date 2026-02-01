package navigation; // Package di navigazione

import javafx.stage.Stage; // Importa Stage per gestire le finestre JavaFX
import startupconfig.StartupConfigBean; // Importa il bean per le configurazioni

import java.util.logging.Logger;

// Classe Navigator che orchestra il cambio delle viste (Pattern Application Controller / Navigator)
// Ora utilizza il Factory Method Pattern ricevendo la factory tramite Dependency Injection
public class AppNavigator {
    private static final Logger LOGGER = Logger.getLogger(AppNavigator.class.getName());

    private final ViewFactory factory; // Factory iniettata per la creazione delle viste (salvare la decisione GUI o
                                       // CLI)

    // Costruttore con Dependency Injection della factory
    // factory: La factory concreta (GuiViewFactory o CliViewFactory) che crea le
    // viste appropriate
    public AppNavigator(ViewFactory factory) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Initializing AppNavigator with factory: %s",
                Thread.currentThread().getName(), factory.getClass().getSimpleName()));
        this.factory = factory;
    }

    // Metodo principale per navigare verso una nuova vista
    // viewName: Nome logico della vista (es. "Login")
    // config: I dati di configurazione necessari alla vista
    // currentStage: Lo Stage della vista precedente, da chiudere (può essere null
    // per la CLI)
    public void navigateTo(String viewName, StartupConfigBean config, Stage currentStage) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering navigateTo: viewName=%s, currentStage=%s",
                Thread.currentThread().getName(), viewName, (currentStage != null ? "active" : "null")));

        // 1. Creazione della nuova vista tramite Factory Method
        View view = factory.createView(viewName);
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Created view: %s",
                Thread.currentThread().getName(), (view != null ? view.getClass().getSimpleName() : "null")));

        // 2. Visualizzazione della nuova vista
        if (view != null) {
            if (config.isInterfaceMode()) {
                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Navigating in GUI mode",
                        Thread.currentThread().getName()));
                // GUI Mode: Creiamo una nuova finestra (Stage) per la nuova vista
                Stage newStage = new Stage();
                view.show(newStage, config);

                // Gestione della chiusura della finestra precedente per GUI
                if (currentStage != null) {
                    LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Closing current GUI stage",
                            Thread.currentThread().getName()));
                    currentStage.close();
                }
            } else {
                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Navigating in CLI mode",
                        Thread.currentThread().getName()));
                // CLI Mode: Chiudiamo la finestra grafica PRIMA di avviare il loop della CLI.
                if (currentStage != null) {
                    LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Closing current stage before CLI",
                            Thread.currentThread().getName()));
                    currentStage.close();
                }

                // Avvio della CLI (bloccante)
                view.show(null, config);
            }
        } else {
            LOGGER.severe(() -> String.format("[DEBUG][Thread: %s] Failed to create view: %s",
                    Thread.currentThread().getName(), viewName));
            throw new IllegalArgumentException("Vista non inserita");
        }
    }
}
