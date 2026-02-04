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
     * Metodo di orchestrazione: riceve i dati, processa la logica di business
     * e infine usa il controllore grafico per cambiare l'interfaccia.
     */
    public void orchestrateConfiguration(StartupConfigBean configBean, javafx.stage.Stage stage) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Application Controller orchestrating...",
                Thread.currentThread().getName()));

        // 1. Logica di business
        processSettings(configBean);

        // 2. Uso del controllore grafico per la navigazione (Orchestra l'interfaccia)
        StartupSettingsGraphicController graphicController = new StartupSettingsGraphicController();
        graphicController.executeNavigation(configBean, stage);
    }

    /**
     * Metodo per elaborare le impostazioni (Logica di Business Pura).
     */
    public void processSettings(StartupConfigBean configBean) {
        LOGGER.info(
                () -> String.format("[DEBUG][Thread: %s] Processing business settings",
                        Thread.currentThread().getName()));

        config.setInterfaceMode(configBean.isInterfaceMode());
        config.setStorageOption(configBean.getStorageOption());

        if (configBean.getStorageOption() == 1) {
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Starting SQL server...",
                    Thread.currentThread().getName()));
            ComandoDaTerminale.avviaServerSQL();
        }
    }
}