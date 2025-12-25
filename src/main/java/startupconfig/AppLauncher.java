package startupconfig;

import javafx.application.Application;

public class AppLauncher {

    public static void main(String[] args) {
        // Resetta le sessioni prima di avviare l'app

        // Avvia JavaFX lanciando la classe principale (che estende Application)
        Application.launch(StartupSettingsBoundary.class, args);
    }
}