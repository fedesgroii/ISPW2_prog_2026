package login_insert_data;

import javafx.stage.Stage;
import navigation.View;
import startupconfig.StartupConfigBean;

import java.util.Scanner;

/**
 * Implementazione CLI per l'inserimento dei dati di login.
 */
public class LoginViewCli implements View {
    private final LoginController appController = new LoginController();
    private final String tipo;

    public LoginViewCli(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public void show(Stage stage, StartupConfigBean config) {
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

            if (appController.authenticate(bean, tipo)) {
                printMessage("\n[SUCCESS] Login effettuato con successo!");
                authenticated = true;
                // Qui andrebbe la navigazione alla dashboard CLI
            } else {
                printMessage("\n[ERROR] Credenziali errate. Riprova.");
            }
        }
    }

    private void printMessage(String message) {
        System.out.println(message);
    }
}
