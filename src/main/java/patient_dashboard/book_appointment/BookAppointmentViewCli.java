package patient_dashboard.book_appointment;

import model.Paziente;
import navigation.View;
import startupconfig.StartupConfigBean;
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
    private final BookAppointmentGraphicControllerCli graphicController = new BookAppointmentGraphicControllerCli();
    private final Scanner scanner = new Scanner(System.in);
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public void show(javafx.stage.Stage stage, StartupConfigBean config) {
        printMessage("\n--- MindLab: Prenotazione Visita ---");

        try {
            BookAppointmentBean bean = new BookAppointmentBean();

            // 1. Service Type
            printMessage("Seleziona tipo di prestazione:");
            printMessage("1) Online");
            printMessage("2) In presenza");
            int choice = readInt(1, 2);
            bean.setServiceType(choice == 1 ? "Online" : "In presenza");

            // 2. Details
            java.util.List<model.Specialista> specialists = graphicController.getAvailableSpecialists(config);
            printMessage("Seleziona specialista:");
            if (specialists.isEmpty()) {
                printMessage("[WARNING] Nessun specialista trovato nel sistema.");
                printMessage("Inserisci nome specialista manualmente: ");
                bean.setSpecialist(scanner.nextLine());
            } else {
                for (int i = 0; i < specialists.size(); i++) {
                    model.Specialista s = specialists.get(i);
                    printMessage(String.format("%d) %s %s (%s)", i + 1, s.getNome(), s.getCognome(),
                            s.getSpecializzazione()));
                }
                int specChoice = readInt(1, specialists.size());
                model.Specialista selected = specialists.get(specChoice - 1);
                bean.setSpecialist(selected.getEmail());
                bean.setSpecialistId(selected.getId());
            }

            printMessage("Motivo della visita (invio per saltare): ");
            bean.setReason(scanner.nextLine());

            // 3. Date & Time
            LocalDate selectedDate = readDate("Data della visita (GG/MM/AAAA): ");
            bean.setDate(selectedDate);

            LocalTime selectedTime = selectSlot(selectedDate, bean);
            if (selectedTime == null) {
                printMessage("\n[INFO] Operazione annullata.");
                return;
            }
            bean.setTime(selectedTime);

            // 4. Personal details (Confirmation or new)
            printMessage("\nDati Personali:");
            Paziente loggedPatient = graphicController.getLoggedPatient();
            printMessage("Conferma dati di " + loggedPatient.getNome() + " " + loggedPatient.getCognome() + "? (S/N)");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("S") || confirm.isEmpty()) {
                bean.setName(loggedPatient.getNome());
                bean.setSurname(loggedPatient.getCognome());
                bean.setEmail(loggedPatient.getEmail());
                bean.setPhone(loggedPatient.getNumeroTelefonico());
                bean.setDateOfBirth(loggedPatient.getDataDiNascita().format(dateFormatter));
            } else {
                printMessage("Inserisci nome: ");
                bean.setName(scanner.nextLine());
                printMessage("Inserisci cognome: ");
                bean.setSurname(scanner.nextLine());
                printMessage("Inserisci email: ");
                bean.setEmail(scanner.nextLine());
                printMessage("Inserisci telefono: ");
                bean.setPhone(scanner.nextLine());
                printMessage("Inserisci data di nascita (GG/MM/AAAA): ");
                bean.setDateOfBirth(scanner.nextLine());
            }

            // 5. Submit
            String result = graphicController.bookAppointment(bean);
            if ("SUCCESS".equals(result)) {
                printMessage("Prenotazione confermata con successo!");
            } else {
                printMessage("Errore durante il salvataggio: " + result);
                printMessage("Premi invio per riprovare o digita 'esci' per annullare.");
                if (!scanner.nextLine().equalsIgnoreCase("esci")) {
                    show(stage, config);
                    return;
                }
            }

        } catch (Exception e) {
            printMessage("[ERRORE] Si è verificato un errore: " + e.getMessage());
        }

        printMessage("\nRitorno alla Dashboard...");
    }

    private int readInt(int min, int max) {
        while (true) {
            try {
                printMessage("> ");
                int val = Integer.parseInt(scanner.nextLine());
                if (val >= min && val <= max)
                    return val;
            } catch (NumberFormatException _) {
                // Ignore
            }
            printMessage("Inserimento non valido. Riprova.");
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            try {
                printMessage(prompt);
                String input = scanner.nextLine();
                LocalDate date = LocalDate.parse(input, dateFormatter);

                BookAppointmentBean tempBean = new BookAppointmentBean();
                tempBean.setDate(date);
                String error = graphicController.validateDate(tempBean);
                if (error != null) {
                    printMessage("[errore] " + error);
                    continue;
                }
                return date;
            } catch (DateTimeParseException _) {
                printMessage("Formato data non valido. Usa GG/MM/AAAA.");
            }
        }
    }

    private LocalTime selectSlot(LocalDate date, BookAppointmentBean fullBean) {
        printMessage("Caricamento orari disponibili...");
        BookAppointmentBean tempBean = new BookAppointmentBean();
        tempBean.setDate(date);
        tempBean.setSpecialist(fullBean.getSpecialist());
        tempBean.setSpecialistId(fullBean.getSpecialistId());

        java.util.List<LocalTime> slots = graphicController.getAvailableSlots(tempBean);
        // Rest of the method...

        if (slots.isEmpty()) {
            printMessage("[ATTENZIONE] Nessun orario disponibile per questa data e specialista.");
            printMessage("Seleziona un'altra data o specialista.");
            return null;
        }

        printMessage("Orari disponibili:");
        for (int i = 0; i < slots.size(); i++) {
            printMessage(String.format("%d) %s", i + 1, slots.get(i).format(timeFormatter)));
        }
        printMessage(String.format("%d) Annulla e torna alla dashboard", slots.size() + 1));

        int choice = readInt(1, slots.size() + 1);
        if (choice > slots.size())
            return null;

        return slots.get(choice - 1);
    }

    /**
     * Prints a message to stdout.
     */
    private void printMessage(String message) {
        System.out.println(message);
    }
}
