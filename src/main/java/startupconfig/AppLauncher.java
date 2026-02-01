package startupconfig; // Dichiarazione del package di appartenenza

import javafx.application.Application; // Importa la classe base per le applicazioni JavaFX
import java.util.logging.Logger; // Importa classe per il logging

// Classe principale per l'avvio dell'applicazione
public class AppLauncher {

        private static final Logger logger = Logger.getLogger(AppLauncher.class.getName());

        // Metodo main, punto di ingresso standard per le applicazioni Java
        public static void main(String[] args) {
                logger.info(
                                () -> String.format("[DEBUG][Thread: %s] Entering AppLauncher.main",
                                                Thread.currentThread().getName()));

                // Avvia l'applicazione JavaFX lanciando la classe StartupSettingsBoundary
                logger.info(() -> String.format("[DEBUG][Thread: %s] AppLauncher calling Application.launch",
                                Thread.currentThread().getName()));
                Application.launch(StartupSettingsBoundary.class, args);
                logger.info(() -> String.format("[DEBUG][Thread: %s] AppLauncher Application.launch returned",
                                Thread.currentThread().getName()));
        }
}