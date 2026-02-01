package startupconfig; // Dichiarazione del package di appartenenza

// Importa le classi necessarie se presenti (in questo caso nessuna importazione esterna è necessaria oltre al package locale)

import java.util.logging.Logger;

// Classe Controller secondo il pattern MVC
// Responsabile della gestione della logica applicativa relativa alla configurazione di avvio
public class StartupSettingsController {
    private static final Logger LOGGER = Logger.getLogger(StartupSettingsController.class.getName());
    // Riferimento all'Entity (Model) che mantiene lo stato della configurazione
    private final StartupSettingsEntity config;

    // Costruttore del controller
    public StartupSettingsController() {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Initializing StartupSettingsController",
                Thread.currentThread().getName()));
        // Ottiene l'istanza Singleton dell'Entity
        this.config = StartupSettingsEntity.getInstance();
    }

    /**
     * Metodo principale per elaborare le impostazioni ricevute dalla Boundary.
     */
    public void processSettings(StartupConfigBean configBean) {
        LOGGER.info(
                () -> String.format("[DEBUG][Thread: %s] Entering processSettings: interfaceMode=%s, storageOption=%d",
                        Thread.currentThread().getName(), configBean.isInterfaceMode(), configBean.getStorageOption()));

        config.setInterfaceMode(configBean.isInterfaceMode());
        config.setStorageOption(configBean.getStorageOption());

        if (configBean.getStorageOption() == 1) {
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Starting SQL server...",
                    Thread.currentThread().getName()));
            ComandoDaTerminale.avviaServerSQL();
        }
    }

    /**
     * Metodo per completare la configurazione e avviare la navigazione.
     */
    public void completeConfiguration(StartupConfigBean configBean, javafx.stage.Stage stage) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering completeConfiguration",
                Thread.currentThread().getName()));

        // 1. Processa le impostazioni
        processSettings(configBean);

        // 2. Scelta della factory centralizzata
        navigation.ViewFactory factory = navigation.ViewFactory.getFactory(configBean.isInterfaceMode());
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Factory selected: %s",
                Thread.currentThread().getName(), factory.getClass().getSimpleName()));

        // 3. Esecuzione della navigazione
        navigation.AppNavigator navigator = new navigation.AppNavigator(factory);
        navigator.navigateTo("Login", configBean, stage);
    }
}