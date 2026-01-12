package startupconfig; // Dichiarazione del package di appartenenza

import javafx.application.Application; // Importa la classe base per le applicazioni JavaFX

import selectTypeLogin.LoginViewBoundary_gui; // Importa la nuova view

// Classe principale per l'avvio dell'applicazione
public class AppLauncher {

    // Metodo main, punto di ingresso standard per le applicazioni Java
    public static void main(String[] args) {
        // Resetta eventuali sessioni precedenti prima di avviare l'app (placeholder per
        // logica futura)

        // Avvia l'applicazione JavaFX lanciando la classe StartupSettingsBoundary
        // Application.launch gestisce il ciclo di vita dell'interfaccia grafica
        Application.launch(StartupSettingsBoundary.class, args);
    }
}