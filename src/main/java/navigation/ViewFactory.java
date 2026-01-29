package navigation; // Package di appartenenza

import startupconfig.StartupConfigBean; // Importa il bean di configurazione
import select_type_login.LoginViewBoundaryCli;
import select_type_login.LoginViewBoundaryGui;

// Factory Class: responsabile della creazione delle istanze delle Viste in base ai parametri
public class ViewFactory {

    // Metodo che crea e restituisce una vista (View) specifica
    // viewName: identificatore della vista richiesta (es. "Login")
    // config: configurazione per decidere quale implementazione creare (GUI o CLI)
    public View createView(String viewName, StartupConfigBean config) {

        try {
            // Controlla se la vista richiesta è la pagina di "Login"
            if ("Login".equals(viewName)) {
                // Controlla nel bean di configurazione se l'utente ha scelto la modalità
                // Grafica (GUI)
                if (config.isInterfaceMode()) {
                    // Se GUI, restituisce la Boundary grafica del Login
                    return new LoginViewBoundaryGui();
                } else {
                    // Se CLI, restituisce la Boundary testuale del Login
                    return new LoginViewBoundaryCli();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(
                    "Errore durante la creazione della vista: " + viewName + "Errore: " + e.getMessage());
        }

        return null;
    }
}
