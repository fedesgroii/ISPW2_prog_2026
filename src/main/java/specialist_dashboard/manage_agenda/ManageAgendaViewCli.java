package specialist_dashboard.manage_agenda;

import navigation.View;
import startupconfig.StartupConfigBean;
import java.util.List;
import java.util.Scanner;
import navigation.ConsoleScanner;

/**
 * CLI View for managing the Specialist's Agenda.
 */
public class ManageAgendaViewCli implements View {

    @Override
    public void show(javafx.stage.Stage stage, StartupConfigBean config) {
        ManagerAgendaControllerApp appController = new ManagerAgendaControllerApp(config);
        ManagerAgendaGraphicControllerCli graphicController = new ManagerAgendaGraphicControllerCli();

        try {
            appController.checkSession();
        } catch (IllegalStateException e) {
            printMessage("Errore sessione: " + e.getMessage());
            return;
        }

        Scanner scanner = ConsoleScanner.getScanner();
        boolean running = true;

        while (running) {
            printMessage("\n--- GESTIONE AGENDA ---");
            List<ManageAgendaBean> visits = graphicController.getFutureVisits(appController);
            displayFutureVisits(visits);

            printMessage("\nOpzioni:");
            printMessage("R <numero> - Rifiuta visita (es. R 1)");
            printMessage("0 - Indietro");
            printMessage("Scelta: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                running = false;
                navigateBack(config, graphicController);
            } else if (input.toUpperCase().startsWith("R ")) {
                handleRejectionCommand(input, visits, appController, graphicController, scanner);
            } else {
                printMessage("Comando non riconosciuto.");
            }
        }
    }

    private void displayFutureVisits(List<ManageAgendaBean> visits) {
        if (visits.isEmpty()) {
            printMessage("Nessun appuntamento futuro in programma.");
            return;
        }

        printMessage("Appuntamenti futuri:");
        for (int i = 0; i < visits.size(); i++) {
            ManageAgendaBean v = visits.get(i);
            System.out.printf("%d. %s %s - Paziente: %s - %s%n",
                    i + 1, v.getDate(), v.getTime(), v.getPatientName(), v.getType());
        }
    }

    private void handleRejectionCommand(String input, List<ManageAgendaBean> visits,
            ManagerAgendaControllerApp appController, ManagerAgendaGraphicControllerCli graphicController,
            Scanner scanner) {
        try {
            int index = Integer.parseInt(input.substring(2).trim()) - 1;
            if (index < 0 || index >= visits.size()) {
                printMessage("Indice non valido.");
                return;
            }

            ManageAgendaBean toReject = visits.get(index);
            printMessage("Sei sicuro di voler rifiutare la visita di " + toReject.getPatientName() + "? (s/n): ");
            String confirm = scanner.nextLine().trim();

            if (confirm.equalsIgnoreCase("s")) {
                if (graphicController.rejectVisit(appController, toReject)) {
                    printMessage("Visita rifiutata con successo.");
                } else {
                    printMessage("Errore durante il rifiuto della visita.");
                }
            } else {
                printMessage("Operazione annullata.");
            }
        } catch (NumberFormatException _) {
            printMessage("Formato non valido. Usa 'R <numero>'.");
        }
    }

    private void navigateBack(StartupConfigBean config, ManagerAgendaGraphicControllerCli graphicController) {
        graphicController.navigateToSpecialistDashboard(config);
    }

    private void printMessage(String message) {
        System.out.println(message);
    }

}
