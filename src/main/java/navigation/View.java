package navigation; // Dichiara il package 'navigation' che raggruppa le classi per la gestione del flusso

import javafx.stage.Stage; // Importa la classe Stage di JavaFX, necessaria per le interfacce grafiche
import startupconfig.StartupConfigBean; // Importa il bean di configurazione per passare i dati alle viste

// Interfaccia che definisce il contratto per tutte le viste (schermate) dell'applicazione
public interface View {
    // Metodo astratto che ogni vista concreta deve implementare per mostrarsi
    // all'utente
    // stage: La finestra grafica in cui mostrare la vista (può essere null per CLI)
    // config: Il bean contenente le configurazioni scelte all'avvio (es. modalità,
    // storage)
    void show(Stage stage, StartupConfigBean config);
}
