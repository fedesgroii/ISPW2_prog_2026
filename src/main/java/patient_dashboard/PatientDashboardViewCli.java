package patient_dashboard;

import javafx.stage.Stage;
import model.Paziente;
import navigation.AppNavigator;
import navigation.CliViewFactory;
import navigation.View;
import startupconfig.StartupConfigBean;

import navigation.ConsoleScanner;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Dashboard view for authenticated patients in CLI mode.
 * 
 * <p>
 * This view displays a text-based menu with options for:
 * <ul>
 * <li>Book a new visit</li>
 * <li>View visit history</li>
 * <li>Browse the shop</li>
 * <li>Check the bulletin board</li>
 * <li>View visits</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The dashboard validates that a patient session is active before displaying
 * and provides a loop-based menu system for navigation.
 * </p>
 * 
 * @author MindLab Development Team
 * @version 1.0
 * @see PatientDashboardView
 */
public class PatientDashboardViewCli implements View {

    private static final Logger LOGGER = Logger.getLogger(PatientDashboardViewCli.class.getName());
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
                case "1" -> navigateToView("Booking", config);
                case "2" -> navigateToView("History", config);
                case "3" -> navigateToView("Shop", config);
                case "4" -> {
                    printMessage("\n=== BACHECA ===");
                    printMessage("Ultime notifiche e annunci");
                    printMessage("(Funzionalità non ancora implementata)");
                    printMessage("\nPremi INVIO per continuare...");
                    scanner.nextLine();
                }
                case "5" -> navigateToView("Appointments", config);
                case "0" -> {
                    exitRequested = true;
                    printMessage("\nArrivederci, " + paziente.getNome() + "!");
                    LOGGER.info(() -> String.format("[DEBUG][Thread: %s] User requested exit from dashboard",
                            Thread.currentThread().getName()));
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
     * Navigates to a specified view.
     */
    private void navigateToView(String viewName, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] CLI navigation from PatientDashboard to: %s",
                Thread.currentThread().getName(), viewName));

        try {
            CliViewFactory factory = new CliViewFactory();
            AppNavigator navigator = new AppNavigator(factory);
            navigator.navigateTo(viewName, config, null);
        } catch (Exception e) {
            LOGGER.warning(() -> String.format("[DEBUG][Thread: %s] CLI navigation failed to %s: %s",
                    Thread.currentThread().getName(), viewName, e.getMessage()));
            printMessage("\n[ERRORE] Impossibile navigare a " + viewName + ": " + e.getMessage());
            printMessage("Premi INVIO per continuare...");
            ConsoleScanner.getScanner().nextLine();
        }
    }

    /**
     * Prints a message to stdout.
     */
    private void printMessage(String message) {
        System.out.println(message);
    }
}
