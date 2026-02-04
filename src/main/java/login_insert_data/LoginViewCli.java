package login_insert_data;

import javafx.stage.Stage;
import navigation.View;
import startupconfig.StartupConfigBean;

import navigation.ConsoleScanner;
import java.util.Scanner;

/**
 * Implementazione CLI per l'inserimento dei dati di login.
 */
public class LoginViewCli implements View {
    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(LoginViewCli.class.getName());

    private final String tipo;

    public LoginViewCli(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void show(Stage stage, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering LoginViewCli.show for tipo: %s",
                Thread.currentThread().getName(), tipo));

        LoginGraphicControllerCli grafCon = new LoginGraphicControllerCli(config);
        Scanner scanner = ConsoleScanner.getScanner();
        boolean authenticated = false;

        while (!authenticated) {
            printMessage("\n--------------------------------------------------");
            printMessage("            Login " + ("Patient".equals(tipo) ? "Paziente" : "Specialista"));
            printMessage("--------------------------------------------------");

            printMessage("Email: ");
            String email = scanner.nextLine();

            printMessage("Password: ");
            String password = scanner.nextLine();

            // Delega la logica al Controller Grafico
            authenticated = grafCon.handleLogin(email, password, config);
        }
    }

    private void printMessage(String message) {
        System.out.println(message);
    }
}
