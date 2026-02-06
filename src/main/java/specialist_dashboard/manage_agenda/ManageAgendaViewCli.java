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

    private ManagerAgendaControllerApp appController;
    private ManagerAgendaGraphicControllerCli graphicController;

    @Override
    public void show(javafx.stage.Stage stage, StartupConfigBean config) {
        appController = new ManagerAgendaControllerApp(config);
        graphicController = new ManagerAgendaGraphicControllerCli();

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

            if (visits.isEmpty()) {
                printMessage("Nessun appuntamento futuro in programma.");
            } else {
                printMessage("Appuntamenti futuri:");
                for (int i = 0; i < visits.size(); i++) {
                    ManageAgendaBean v = visits.get(i);
                    System.out.printf("%d. %s %s - Paziente: %s - %s\n",
                            i + 1,
                            v.getDate(),
                            v.getTime(),
                            v.getPatientName(),
                            v.getType());
                }
            }

            printMessage("\nOpzioni:");
            printMessage("R <numero> - Rifiuta visita (es. R 1)");
            printMessage("0 - Indietro");
            printMessage("Scelta: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0")) {
                running = false;
                navigateBack(config);
            } else if (input.toUpperCase().startsWith("R ")) {
                try {
                    int index = Integer.parseInt(input.substring(2).trim()) - 1;
                    if (index >= 0 && index < visits.size()) {
                        ManageAgendaBean toReject = visits.get(index);
                        System.out.print("Sei sicuro di voler rifiutare la visita di " +
                                toReject.getPatientName() + "? (s/n): ");
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
                    } else {
                        printMessage("Indice non valido.");
                    }
                } catch (NumberFormatException e) {
                    printMessage("Formato non valido. Usa 'R <numero>'.");
                }
            } else {
                printMessage("Comando non riconosciuto.");
            }
        }
    }

    private void navigateBack(StartupConfigBean config) {
        graphicController.navigateToSpecialistDashboard(config);
    }

    private void printMessage(String message) {
        System.out.println(message);
    }

}
