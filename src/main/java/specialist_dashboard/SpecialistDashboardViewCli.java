package specialist_dashboard;

import javafx.stage.Stage;
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
 * @see SpecialistDashboardViewGui
 */
public class SpecialistDashboardViewCli implements View {

    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardViewCli.class.getName());
    private final SpecialistDashboardController controller = new SpecialistDashboardController();
    private final SpecialistDashboardGraphicControllerCli graphicController = new SpecialistDashboardGraphicControllerCli();

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

        // Retrieve data via Graphic Controller and Bean
        SpecialistDashboardBean bean = graphicController.getDashboardData(controller);
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Displaying CLI dashboard for specialist: %s",
                Thread.currentThread().getName(), bean.getNome()));

        // Display menu loop
        displayMenuLoop(bean, config);
    }

    /**
     * Displays the menu and handles user selection.
     */
    private void displayMenuLoop(SpecialistDashboardBean bean, StartupConfigBean config) {
        Scanner scanner = ConsoleScanner.getScanner();
        boolean exitRequested = false;

        while (!exitRequested) {
            printDashboardMenu(bean);

            printMessage("\nSeleziona un'opzione: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1" -> graphicController.navigateToView("Agenda", config);
                case "2" -> graphicController.navigateToView("PatientsList", config);
                case "3" -> graphicController.navigateToView("Reports", config);
                case "4" -> {
                    printMessage("\n=== BACHECA ===");
                    printMessage("Comunicazioni e aggiornamenti dallo staff");
                    printMessage("(Funzionalità non ancora implementata)");
                    printMessage("\nPremi INVIO per continuare...");
                    scanner.nextLine();
                }
                case "5" -> graphicController.navigateToView("Visits", config);
                case "0" -> {
                    exitRequested = true;
                    printMessage("\nArrivederci, Dott. " + bean.getCognome() + "!");
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
    private void printDashboardMenu(SpecialistDashboardBean bean) {
        printMessage("\n" + "=".repeat(50));
        printMessage("    HOME - MINDLAB PORTAL (SPECIALISTA)");
        printMessage("=".repeat(50));
        printMessage("Ciao, " + bean.getNome() + "!");
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
    private void printMessage(String message) {
        System.out.println(message);
    }
}
