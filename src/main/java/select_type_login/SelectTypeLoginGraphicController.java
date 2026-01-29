package selectTypeLogin;

import javafx.stage.Stage;
import startupconfig.StartupConfigBean;

/**
 * Graphic Controller per la gestione della navigazione nel caso d'uso "Select
 * Type Login".
 * Gestisce la transizione tra l'interfaccia di configurazione e la schermata di
 * selezione login.
 */
public class SelectTypeLoginGraphicController {

    /**
     * Avvia l'interfaccia di selezione login (GUI o CLI) in base alla
     * configurazione.
     * 
     * @param bean Il bean contenente la configurazione scelta dall'utente (GUI vs
     *             CLI).
     */
    public void start(StartupConfigBean bean) {
        if (bean.isInterfaceMode()) {
            // Modalità GUI
            try {
                // Crea un nuovo Stage per la nuova finestra
                Stage stage = new Stage();
                // Istanzia e avvia la Boundary GUI
                new LoginViewBoundary_gui().start(stage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // Modalità CLI
            // Istanzia e avvia la Boundary CLI
            new LoginViewBoundary_cli().start();
        }
    }
}
