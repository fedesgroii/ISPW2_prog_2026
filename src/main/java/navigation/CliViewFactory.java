package navigation; // Package di appartenenza

import select_type_login.LoginViewBoundaryCli;
import login_insert_data.LoginViewCli;

// Concrete Factory per le viste CLI
// Implementa il Factory Method per creare solo viste a linea di comando
public class CliViewFactory extends ViewFactory {

    @Override
    public View createView(String viewName) {
        try {
            return switch (viewName) {
                case "Login" -> new LoginViewBoundaryCli();
                case "Patient" -> new LoginViewCli("Patient");
                case "Specialist" -> new LoginViewCli("Specialist");
                default -> null;
            };
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Errore durante la creazione della vista CLI: " + viewName + ". Errore: " + e.getMessage());
        }
    }
}
