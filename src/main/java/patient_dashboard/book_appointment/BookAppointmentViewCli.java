package patient_dashboard.book_appointment;

import model.Paziente;
import navigation.View;
import startupconfig.StartupConfigBean;
import patient_dashboard.PatientDashboardController;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * CLI boundary for booking an appointment.
 * Interactive terminal interface that mimics the GUI fields.
 */
public class BookAppointmentViewCli implements View {
    private final BookAppointmentControllerApp appController = new BookAppointmentControllerApp();
    private final PatientDashboardController dashboardController = new PatientDashboardController();
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void show(javafx.stage.Stage stage, StartupConfigBean config) {
        System.out.println("\n--- MindLab: Prenotazione Visita ---");

        try {
            Paziente loggedPatient = dashboardController.getLoggedPatient();
            BookAppointmentBean bean = new BookAppointmentBean();

            // 1. Service Type
            System.out.println("Seleziona tipo di prestazione:");
            System.out.println("1) Online");
            System.out.println("2) In presenza");
            int choice = readInt(1, 2);
            bean.setServiceType(choice == 1 ? "Online" : "In presenza");

            // 2. Details
            java.util.List<model.Specialista> specialists = appController.getAvailableSpecialists(config);
            System.out.println("Seleziona specialista:");
            if (specialists.isEmpty()) {
                System.out.println("[WARNING] Nessun specialista trovato nel sistema.");
                System.out.print("Inserisci nome specialista manualmente: ");
                bean.setSpecialist(scanner.nextLine());
            } else {
                for (int i = 0; i < specialists.size(); i++) {
                    model.Specialista s = specialists.get(i);
                    System.out.printf("%d) %s %s (%s)%n", i + 1, s.getNome(), s.getCognome(), s.getSpecializzazione());
                }
                int specChoice = readInt(1, specialists.size());
                model.Specialista selected = specialists.get(specChoice - 1);
                bean.setSpecialist(selected.getNome() + " " + selected.getCognome());
            }

            System.out.print("Motivo della visita (invio per saltare): ");
            bean.setReason(scanner.nextLine());

            // 3. Date & Time
            bean.setDate(readDate("Data della visita (GG/MM/AAAA): "));
            bean.setTime(readTime("Orario della visita (HH:mm): "));

            // 4. Confirm personal details
            System.out.println("\nConferma dati personali di " + loggedPatient.getNome() + " "
                    + loggedPatient.getCognome() + "? (S/N)");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("S") || confirm.isEmpty()) {
                bean.setName(loggedPatient.getNome());
                bean.setSurname(loggedPatient.getCognome());
            } else {
                System.out.print("Inserisci nome: ");
                bean.setName(scanner.nextLine());
                System.out.print("Inserisci cognome: ");
                bean.setSurname(scanner.nextLine());
            }

            // 5. Submit
            String result = appController.bookAppointment(bean, loggedPatient);
            if ("SUCCESS".equals(result)) {
                System.out.println("\n[SUCCESSO] Visita prenotata correttamente!");
            } else {
                System.out.println("\n[ERRORE] " + result);
                System.out.println("Premi invio per riprovare o digita 'esci' per annullare.");
                if (!scanner.nextLine().equalsIgnoreCase("esci")) {
                    show(stage, config);
                    return;
                }
            }

        } catch (Exception e) {
            System.out.println("[ERRORE] Si è verificato un errore: " + e.getMessage());
        }

        System.out.println("\nRitorno alla Dashboard...");
    }

    private int readInt(int min, int max) {
        while (true) {
            try {
                System.out.print("> ");
                int val = Integer.parseInt(scanner.nextLine());
                if (val >= min && val <= max)
                    return val;
            } catch (NumberFormatException e) {
                // Ignore
            }
            System.out.println("Inserimento non valido. Riprova.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return LocalDate.parse(input, dateFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato data non valido. Usa GG/MM/AAAA.");
            }
        }
    }

    private LocalTime readTime(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return LocalTime.parse(input, timeFormatter);
            } catch (DateTimeParseException e) {
                System.out.println("Formato orario non valido. Usa HH:mm (es. 14:30).");
            }
        }
    }
}
