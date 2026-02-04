package navigation; // Package di navigazione

import javafx.stage.Stage; // Importa Stage per gestire le finestre JavaFX
import javafx.application.Platform;
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
    public void navigateTo(String viewName, StartupConfigBean config, Stage stage) {
        logDebug("Entering navigateTo: viewName=%s, currentStage=%s",
                viewName, (stage != null ? "active" : "null"));

        View view = factory.createView(viewName);
        logDebug("Created view: %s", (view != null ? view.getClass().getSimpleName() : "null"));

        if (view == null) {
            handleViewCreationFailure(viewName);
            return;
        }

        if (config.isInterfaceMode()) {
            handleGuiNavigation(view, stage, config);
        } else {
            handleCliNavigation(view, stage, config);
        }
    }

    private void handleGuiNavigation(View view, Stage stage, StartupConfigBean config) {
        logDebug("Navigating in GUI mode");
        view.show(stage, config);
    }

    private void handleCliNavigation(View view, Stage stage, StartupConfigBean config) {
        logDebug("Navigating in CLI mode");

        if (stage != null) {
            logDebug("Closing current stage before CLI");
            Platform.runLater(stage::close);
        }

        if (Platform.isFxApplicationThread()) {
            new Thread(() -> executeCliShow(view, config)).start();
        } else {
            executeCliShow(view, config);
        }
    }

    private void executeCliShow(View view, StartupConfigBean config) {
        try {
            view.show(null, config);
        } catch (Exception e) {
            LOGGER.severe(() -> String.format("[DEBUG][Thread: %s] Error in CLI view: %s",
                    Thread.currentThread().getName(), e.getMessage()));
        }
    }

    private void handleViewCreationFailure(String viewName) {
        LOGGER.severe(() -> String.format("[DEBUG][Thread: %s] Failed to create view: %s",
                Thread.currentThread().getName(), viewName));
        throw new IllegalArgumentException("Vista non inserita nella Factory: " + viewName);
    }

    private void logDebug(String format, Object... args) {
        String messageTemplate = "[DEBUG][Thread: %s] " + format;
        Object[] allArgs = new Object[args.length + 1];
        allArgs[0] = Thread.currentThread().getName();
        System.arraycopy(args, 0, allArgs, 1, args.length);
        LOGGER.info(() -> String.format(messageTemplate, allArgs));
    }

    private Object[] combineArgs(String first, Object[] rest) {
        Object[] all = new Object[rest.length + 1];
        all[0] = first;
        System.arraycopy(rest, 0, all, 1, rest.length);
        return all;
    }
}
