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

    // Input fields
    private String serviceType;
    private String specialist;
    private int specialistId;
    private String reason;
    private LocalDate date;
    private LocalTime time;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String dateOfBirth;

    @Override
    public void show(javafx.stage.Stage stage, StartupConfigBean config) {
        printMessage("\n--- MindLab: Prenotazione Visita ---");

        try {
            // 1. Service Type
            printMessage("Seleziona tipo di prestazione:");
            printMessage("1) Online");
            printMessage("2) In presenza");
            int choice = readInt(1, 2);
            this.serviceType = (choice == 1 ? "Online" : "In presenza");

            // 2. Details
            java.util.List<model.Specialista> specialists = graphicController.getAvailableSpecialists(config);
            printMessage("Seleziona specialista:");
            if (specialists.isEmpty()) {
                printMessage("[WARNING] Nessun specialista trovato nel sistema.");
                printMessage("Inserisci nome specialista manualmente: ");
                this.specialist = scanner.nextLine();
                this.specialistId = 0; // Or handle as needed
            } else {
                for (int i = 0; i < specialists.size(); i++) {
                    model.Specialista s = specialists.get(i);
                    printMessage(String.format("%d) %s %s (%s)", i + 1, s.getNome(), s.getCognome(),
                            s.getSpecializzazione()));
                }
                int specChoice = readInt(1, specialists.size());
                model.Specialista selected = specialists.get(specChoice - 1);
                this.specialist = selected.getEmail();
                this.specialistId = selected.getId();
            }

            printMessage("Motivo della visita (invio per saltare): ");
            this.reason = scanner.nextLine();

            // 3. Date & Time
            this.date = readDate("Data della visita (GG/MM/AAAA): ");

            // Need a temp bean just for slot selection helper, or refactor helper too.
            // For now, let's create a temp bean solely for the slot checking,
            // but the official data is stored in the view fields.
            BookAppointmentBean tempBeanForSlots = new BookAppointmentBean();
            tempBeanForSlots.setDate(this.date);
            tempBeanForSlots.setSpecialistId(this.specialistId);
            tempBeanForSlots.setSpecialist(this.specialist);

            LocalTime selectedTime = selectSlot(this.date, tempBeanForSlots);
            if (selectedTime == null) {
                printMessage("\n[INFO] Operazione annullata.");
                return;
            }
            this.time = selectedTime;

            // 4. Personal details (Confirmation or new)
            printMessage("\nDati Personali:");
            Paziente loggedPatient = graphicController.getLoggedPatient();
            printMessage("Conferma dati di " + loggedPatient.getNome() + " " + loggedPatient.getCognome() + "? (S/N)");
            String confirm = scanner.nextLine();
            if (confirm.equalsIgnoreCase("S") || confirm.isEmpty()) {
                this.name = loggedPatient.getNome();
                this.surname = loggedPatient.getCognome();
                this.email = loggedPatient.getEmail();
                this.phone = loggedPatient.getNumeroTelefonico();
                this.dateOfBirth = loggedPatient.getDataDiNascita().format(dateFormatter);
            } else {
                printMessage("Inserisci nome: ");
                this.name = scanner.nextLine();
                printMessage("Inserisci cognome: ");
                this.surname = scanner.nextLine();
                printMessage("Inserisci email: ");
                this.email = scanner.nextLine();
                printMessage("Inserisci telefono: ");
                this.phone = scanner.nextLine();
                printMessage("Inserisci data di nascita (GG/MM/AAAA): ");
                this.dateOfBirth = scanner.nextLine();
            }

            // 5. Submit
            String result = graphicController.bookAppointment(this);
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

    // Getters for Graphic Controller
    public String getServiceType() {
        return serviceType;
    }

    public String getSpecialist() {
        return specialist;
    }

    public int getSpecialistId() {
        return specialistId;
    }

    public String getReason() {
        return reason;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getTime() {
        return time;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
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
                LocalDate parsedDate = LocalDate.parse(input, dateFormatter);

                BookAppointmentBean tempBean = new BookAppointmentBean();
                tempBean.setDate(parsedDate);
                String error = graphicController.validateDate(tempBean);
                if (error != null) {
                    printMessage("[errore] " + error);
                    continue;
                }
                return parsedDate;
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
