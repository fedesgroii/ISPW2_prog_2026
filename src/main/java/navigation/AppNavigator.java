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

        // 1. Gestione della chiusura della finestra precedente
        // Se c'è uno stage aperto, lo chiudiamo per pulire l'interfaccia
        if (currentStage != null) {
            currentStage.close();
        }

        // 2. Creazione della nuova vista tramite Factory
        // Usiamo la factory per ottenere l'istanza corretta (GUI o CLI) senza
        // accoppiamento diretto
        ViewFactory factory = new ViewFactory();
        View view = factory.createView(viewName, config);

        // 3. Visualizzazione della nuova vista
        if (view != null) {
            // Controlliamo se siamo in modalità Interfaccia Grafica
            if (config.isInterfaceMode()) {
                // GUI Mode: Creiamo una nuova finestra (Stage) per la nuova vista
                Stage newStage = new Stage();
                // Chiamiamo il metodo show passando il nuovo stage
                view.show(newStage, config);
            } else {
                // CLI Mode: Non serve uno Stage grafico, passiamo null
                view.show(null, config);
            }
        }
    }
}
