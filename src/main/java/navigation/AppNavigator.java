package navigation; // Package di navigazione

import javafx.stage.Stage; // Importa Stage per gestire le finestre JavaFX
import startupconfig.StartupConfigBean; // Importa il bean per le configurazioni

// Classe Navigator che orchestra il cambio delle viste (Pattern Application Controller / Navigator)
public class AppNavigator {

    // Metodo principale per navigare verso una nuova vista
    // viewName: Nome logico della vista (es. "Login")
    // config: I dati di configurazione necessari alla vista
    // currentStage: Lo Stage della vista precedente, da chiudere (può essere null)
    public void navigateTo(String viewName, StartupConfigBean config, Stage currentStage) {

        // 1. Creazione della nuova vista tramite Factory
        ViewFactory factory = new ViewFactory();
        View view = factory.createView(viewName, config);

        // 2. Visualizzazione della nuova vista
        if (view != null) {
            if (config.isInterfaceMode()) {
                // GUI Mode: Creiamo una nuova finestra (Stage) per la nuova vista
                Stage newStage = new Stage();
                view.show(newStage, config);
            } else {
                // CLI Mode: Non serve uno Stage grafico
                view.show(null, config);
            }
        }

        // 3. Gestione della chiusura della finestra precedente
        // Chiudiamo la vecchia finestra SOLO DOPO aver aperto la nuova,
        // per evitare che JavaFX termini l'applicazione se chiudiamo l'ultima finestra
        // attiva.
        if (currentStage != null) {
            currentStage.close();
        }
    }
}
