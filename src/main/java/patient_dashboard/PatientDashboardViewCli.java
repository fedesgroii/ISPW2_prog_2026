package patient_dashboard;

import javafx.stage.Stage;
import model.Paziente;

import navigation.View;
import startupconfig.StartupConfigBean;

import navigation.ConsoleScanner;
import java.util.Scanner;
import java.util.logging.Logger;

public class PatientDashboardViewCli implements View {

    private static final Logger LOGGER = Logger.getLogger(PatientDashboardViewCli.class.getName());
    private final PatientDashboardGraphicControllerCli graphicController = new PatientDashboardGraphicControllerCli();
    private final PatientDashboardController controller = new PatientDashboardController();

    /**
     * Displays the patient dashboard CLI menu.
     * 
     * @param stage  Not used in CLI mode (null)
     * @param config The startup configuration
     * @throws IllegalStateException if no patient session is active
     */
    @Override
    public void show(Stage stage, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] PatientDashboardViewCli.show() called",
                Thread.currentThread().getName()));

        // Session validation via Application Controller
        controller.checkSession();

        // Retrieve logged-in patient via Application Controller
        Paziente paziente = controller.getLoggedPatient();
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Displaying CLI dashboard for patient: %s",
                Thread.currentThread().getName(), paziente.getNome()));

        // Display menu loop
        displayMenuLoop(paziente, config);
    }

    /**
     * Displays the menu and handles user selection.
     */
    private void displayMenuLoop(Paziente paziente, StartupConfigBean config) {
        Scanner scanner = ConsoleScanner.getScanner();
        boolean exitRequested = false;

        while (!exitRequested) {
            printDashboardMenu(paziente);

            printMessage("\nSeleziona un'opzione: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> graphicController.handleSelection(PatientDashboardOption.BOOK_VISIT, config);
                case "2" -> graphicController.handleSelection(PatientDashboardOption.VISIT_HISTORY, config);
                case "3" -> graphicController.handleSelection(PatientDashboardOption.SHOP, config);
                case "4" -> graphicController.handleSelection(PatientDashboardOption.BULLETIN_BOARD, config);
                case "5" -> graphicController.handleSelection(PatientDashboardOption.MANAGE_APPOINTMENTS, config);
                case "0" -> {
                    exitRequested = true;
                    graphicController.handleSelection(PatientDashboardOption.LOGOUT, config);
                    printMessage("\nArrivederci, " + paziente.getNome() + "!");
                }
                default -> printMessage("\n[ERRORE] Opzione non valida. Riprova.");
            }
        }
    }

    /**
     * Prints the main dashboard menu.
     */
    private void printDashboardMenu(Paziente paziente) {
        printMessage("\n" + "=".repeat(50));
        printMessage("    HOME - MINDLAB PORTAL (PAZIENTE)");
        printMessage("=".repeat(50));
        printMessage("Ciao, " + paziente.getNome() + "!");
        printMessage("");
        printMessage("1. Prenota una visita");
        printMessage("   Cerca il momento perfetto per la tua prossima visita");
        printMessage("");
        printMessage("2. Storico Visite");
        printMessage("   Visualizza e gestisci le tue visite");
        printMessage("");
        printMessage("3. Shop");
        printMessage("   Scopri i nostri prodotti");
        printMessage("");
        printMessage("4. Bacheca");
        printMessage("   Ultime notifiche e annunci");
        printMessage("");
        printMessage("5. Visite");
        printMessage("   Gestisci le tue visite programmate");
        printMessage("");
        printMessage("0. Esci");
        printMessage("=".repeat(50));
    }

    /**
     * Prints a message to stdout.
     */
    private void printMessage(String message) {
        System.out.println(message);
    }
}
