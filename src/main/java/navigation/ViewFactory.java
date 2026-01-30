package navigation; // Package di appartenenza

import startupconfig.StartupConfigBean; // Importa il bean di configurazione
import select_type_login.LoginViewBoundaryCli;
import select_type_login.LoginViewBoundaryGui;
import login_insert_data.LoginViewPatient;
import login_insert_data.LoginViewSpecialist;
import login_insert_data.LoginViewCli;

// Factory Class: responsabile della creazione delle istanze delle Viste in base ai parametri
public class ViewFactory {

    // Metodo che crea e restituisce una vista (View) specifica
    // viewName: identificatore della vista richiesta (es. "Login")
    // config: configurazione per decidere quale implementazione creare (GUI o CLI)
    public View createView(String viewName, StartupConfigBean config) {
        boolean isGui = config.isInterfaceMode();

        try {
            return switch (viewName) {
                case "Login" -> isGui ? new LoginViewBoundaryGui() : new LoginViewBoundaryCli();
                case "Patient" -> isGui ? new LoginViewPatient() : new LoginViewCli("Patient");
                case "Specialist" -> isGui ? new LoginViewSpecialist() : new LoginViewCli("Specialist");
                default -> null;
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Errore durante la creazione della vista: " + viewName + ". Errore: " + e.getMessage());
        }
    }
}
