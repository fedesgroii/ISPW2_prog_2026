package specialist_dashboard;

import javafx.stage.Stage;
import model.Specialista;
import navigation.AppNavigator;
import navigation.CliViewFactory;
import navigation.View;
import startupconfig.StartupConfigBean;

import navigation.ConsoleScanner;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * Dashboard view for authenticated specialists in CLI mode.
 * 
 * <p>
 * This view displays a text-based menu with options for:
 * <ul>
 * <li>Manage agenda and availability</li>
 * <li>View patient list</li>
 * <li>Generate clinical reports</li>
 * <li>Check the bulletin board</li>
 * <li>View visits</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The dashboard validates that a specialist session is active before displaying
 * and provides a loop-based menu system for navigation.
 * </p>
 * 
 * @author MindLab Development Team
 * @version 1.0
 * @see SpecialistDashboardView
 */
public class SpecialistDashboardViewCli implements View {

    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardViewCli.class.getName());
    private final SpecialistDashboardController controller = new SpecialistDashboardController();

    /**
     * Displays the specialist dashboard CLI menu.
     * 
     * @param stage  Not used in CLI mode (null)
     * @param config The startup configuration
     * @throws IllegalStateException if no specialist session is active
     */
    @Override
    public void show(Stage stage, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] SpecialistDashboardViewCli.show() called",
                Thread.currentThread().getName()));

        // Session validation via Application Controller
        controller.checkSession();

        // Retrieve logged-in specialist via Application Controller
        Specialista specialista = controller.getLoggedSpecialist();
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Displaying CLI dashboard for specialist: %s",
                Thread.currentThread().getName(), specialista.getNome()));

        // Display menu loop
        displayMenuLoop(specialista, config);
    }

    /**
     * Displays the menu and handles user selection.
     */
    private void displayMenuLoop(Specialista specialista, StartupConfigBean config) {
        Scanner scanner = ConsoleScanner.getScanner();
        boolean exitRequested = false;

        while (!exitRequested) {
            printDashboardMenu(specialista);

            printMessage("\nSeleziona un'opzione: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> navigateToView("Agenda", config);
                case "2" -> navigateToView("PatientsList", config);
                case "3" -> navigateToView("Reports", config);
                case "4" -> {
                    printMessage("\n=== BACHECA ===");
                    printMessage("Comunicazioni e aggiornamenti dallo staff");
                    printMessage("(Funzionalità non ancora implementata)");
                    printMessage("\nPremi INVIO per continuare...");
                    scanner.nextLine();
                }
                case "5" -> navigateToView("Visits", config);
                case "0" -> {
                    exitRequested = true;
                    printMessage("\nArrivederci, Dott. " + specialista.getCognome() + "!");
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
    private void printDashboardMenu(Specialista specialista) {
        printMessage("\n" + "=".repeat(50));
        printMessage("    HOME - MINDLAB PORTAL (SPECIALISTA)");
        printMessage("=".repeat(50));
        printMessage("Ciao, " + specialista.getNome() + "!");
        printMessage("");
        printMessage("1. La mia agenda");
        printMessage("   Gestisci i tuoi appuntamenti e la disponibilità");
        printMessage("");
        printMessage("2. Pazienti");
        printMessage("   Visualizza la lista dei tuoi pazienti");
        printMessage("");
        printMessage("3. Report");
        printMessage("   Genera report delle tue attività cliniche");
        printMessage("");
        printMessage("4. Bacheca");
        printMessage("   Comunicazioni e aggiornamenti dallo staff");
        printMessage("");
        printMessage("5. Visite");
        printMessage("   Gestisci le visite programmate");
        printMessage("");
        printMessage("0. Esci");
        printMessage("=".repeat(50));
    }

    /**
     * Navigates to a specified view.
     */
    private void navigateToView(String viewName, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] CLI navigation from SpecialistDashboard to: %s",
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
