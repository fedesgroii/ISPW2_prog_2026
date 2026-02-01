package login_insert_data;

import javafx.stage.Stage;
import navigation.View;
import startupconfig.StartupConfigBean;

import java.util.Scanner;

/**
 * Implementazione CLI per l'inserimento dei dati di login.
 */
public class LoginViewCli implements View {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(LoginViewCli.class.getName());
    private LoginController appController;
    private final String tipo;

    public LoginViewCli(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void show(Stage stage, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering LoginViewCli.show for tipo: %s",
                Thread.currentThread().getName(), tipo));

        if (appController == null) {
            appController = new LoginController(config);
        }

        Scanner scanner = new Scanner(System.in);
        boolean authenticated = false;

        while (!authenticated) {
            printMessage("\n--------------------------------------------------");
            printMessage("            Login " + ("Patient".equals(tipo) ? "Paziente" : "Specialista"));
            printMessage("--------------------------------------------------");

            printMessage("Email: ");
            String email = scanner.nextLine();

            printMessage("Password: ");
            String password = scanner.nextLine();

            LoginBean bean = new LoginBean(email, password);
            authentication.AuthenticationResult result = appController.authenticate(bean);

            if (result.isSuccess()) {
                LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Login CLI successful for %s",
                        Thread.currentThread().getName(), email));
                printMessage("\n[SUCCESS] Login effettuato con successo come " + result.getUserType() + "!");

                // Avvio sessione
                appController.startUserSession(result);

                authenticated = true;
                // Qui andrebbe la navigazione alla dashboard CLI
            } else {
                LOGGER.warning(() -> String.format("[DEBUG][Thread: %s] Login CLI failed for %s: %s",
                        Thread.currentThread().getName(), email, result.getErrorMessage()));
                printMessage("\n[ERROR] Login fallito: " + result.getErrorMessage() + ". Riprova.");
            }
        }
    }

    private void printMessage(String message) {
        System.out.println(message);
    }
}
