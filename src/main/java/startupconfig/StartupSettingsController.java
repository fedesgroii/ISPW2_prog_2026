package startupconfig;

import javafx.stage.Stage;

public class StartupSettingsController {
    private final StartupSettingsEntity config;

    public StartupSettingsController() {
        this.config = StartupSettingsEntity.getInstance();
    }

    /**
     * Metodo che riceve i dati delle impostazioni dalla boundary e li elabora
     */
    public void processSettings(StartupSettingsBoundary.SettingsData settingsData) {
        config.setInterfaceMode(settingsData.isGuiMode());
        config.setStorageOption(settingsData.getStorageOption());

        if (settingsData.getStorageOption() == 1) {
            ComandoDaTerminale.avviaServerSQL();
        }
    }

    /**
     * Gestisce la transizione alla prossima vista dopo la configurazione.
     * Questo metodo coordina la UI, quindi conosce Stage e LoginView_gui.
     */
    public void navigateToNextView(Stage currentStage) {
        // NOTA: per ora assumiamo che si vada sempre al login.
        // In futuro, se isGuiMode == false, potresti avviare una CLI.

        currentStage.close();
    }
}