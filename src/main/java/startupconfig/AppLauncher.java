package startupconfig; // Dichiarazione del package di appartenenza

import javafx.application.Application; // Importa la classe base per le applicazioni JavaFX
import java.util.logging.Logger; // Importa classe per il logging

// Classe principale per l'avvio dell'applicazione
public class AppLauncher {

    private static final Logger logger = Logger.getLogger(AppLauncher.class.getName());

    // Metodo main, punto di ingresso standard per le applicazioni Java
    public static void main(String[] args) {
        // Resetta eventuali sessioni precedenti prima di avviare l'app (placeholder per
        // logica futura)

        // Avvia l'applicazione JavaFX lanciando la classe StartupSettingsBoundary
        // Application.launch gestisce il ciclo di vita dell'interfaccia grafica
        logger.info("AppLauncher calling Application.launch");
        Application.launch(StartupSettingsBoundary.class, args);
        logger.info("AppLauncher Application.launch returned");
    }
}