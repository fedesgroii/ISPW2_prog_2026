package select_type_login; // Package di appartenenza della classe

import java.util.Scanner; // Importa la classe Scanner per leggere l'input da tastiera
import java.util.logging.Logger; // Importa il Logger per registrare messaggi di sistema

public class LoginViewBoundaryCli implements navigation.View { // Definizione della classe pubblica
                                                               // LoginViewBoundary_cli

    private static final Logger logger = Logger.getLogger(LoginViewBoundaryCli.class.getName()); // Inizializza il
                                                                                                 // logger per
                                                                                                 // tracciare le
                                                                                                 // operazioni

    @Override
    public void show(javafx.stage.Stage stage, startupconfig.StartupConfigBean config) {

        this.start();
    }

    public void start() { // Metodo principale per avviare l'interfaccia CLI
        Scanner scanner = new Scanner(System.in); // Crea un oggetto Scanner collegato allo standard input (tastiera)
        boolean running = true; // Variabile di controllo per il ciclo principale dell'applicazione

        while (running) { // Inizio del ciclo per mantenere l'interfaccia attiva finché l'utente non esce
                          // o completa un'azione
            printLine("--------------------------------------------------"); // Stampa una linea separatrice per
                                                                             // chiarezza visuale
            printLine("                  Portale MindLab                 "); // Stampa il titolo principale centrato
                                                                             // (simulato)
            printLine("--------------------------------------------------"); // Stampa una linea separatrice sotto il
                                                                             // titolo
            printLine(""); // Stampa una riga vuota per spaziatura
            printLine("Accedi come:"); // Stampa il sottotitolo come nella GUI
            printLine("1. Specialista"); // Stampa l'opzione 1 corrispondente al bottone "Specialista"
            printLine("2. Paziente"); // Stampa l'opzione 2 corrispondente al bottone "Paziente"
            printLine(""); // Stampa una riga vuota per spaziatura
            printLine("oppure"); // Stampa il testo di raccordo "oppure"
            printLine(""); // Stampa una riga vuota per spaziatura
            printLine("3. Registrati"); // Stampa l'opzione 3 corrispondente al bottone "Registrati"
            printLine(""); // Stampa una riga vuota per spaziatura
            printLine("altrimenti"); // Stampa il testo di raccordo "altrimenti"
            printLine(""); // Stampa una riga vuota per spaziatura
            printLine("4. Prenota un appuntamento senza registrarti"); // Stampa l'opzione 4 per l'appuntamento rapido
            printLine(""); // Stampa una riga vuota per separare il menu dal prompt
            printLine("Inserisci il numero dell'opzione desiderata: "); // Chiede all'utente di inserire una scelta

            String input = scanner.nextLine(); // Legge la riga di testo inserita dall'utente

            switch (input) { // Inizia la struttura switch per gestire le diverse opzioni inserite
                case "1": // Caso in cui l'utente inserisce "1"
                    handleSpecialistLogin(); // Chiama il metodo per gestire il login specialista
                    break; // Interrompe il caso dello switch
                case "2": // Caso in cui l'utente inserisce "2"
                    handlePatientLogin(); // Chiama il metodo per gestire il login paziente
                    break; // Interrompe il caso dello switch
                case "3": // Caso in cui l'utente inserisce "3"
                    handleRegistration(); // Chiama il metodo per gestire la registrazione
                    break; // Interrompe il caso dello switch
                case "4": // Caso in cui l'utente inserisce "4"
                    handleAppointment(); // Chiama il metodo per gestire l'appuntamento
                    break; // Interrompe il caso dello switch
                default: // Caso di default se l'input non corrisponde a nessun caso previsto
                    printLine("Opzione non valida, riprova."); // Comunica all'utente che l'input non è valido
            } // Chiude il blocco switch
        } // Chiude il ciclo while

        // Nota: non chiudiamo lo scanner qui se System.in deve essere riutilizzato
        // altrove nell'app,
        // ma in una classe standalone CLI potremmo farlo. Per sicurezza lo lasciamo
        // aperto come standard in System.in wrapper.
    } // Chiude il metodo start

    private void handleSpecialistLogin() { // Metodo per gestire il login specialista
        logger.info("Specialist Login request");
        startupconfig.StartupSettingsEntity configEntity = startupconfig.StartupSettingsEntity.getInstance();
        startupconfig.StartupConfigBean configBean = new startupconfig.StartupConfigBean(
                configEntity.isInterfaceMode(),
                configEntity.getStorageOption());
        new navigation.AppNavigator().navigateTo("Specialist", configBean, null);
    } // Chiude il metodo handleSpecialistLogin

    private void handlePatientLogin() { // Metodo per gestire il login paziente
        logger.info("Patient Login request");
        startupconfig.StartupSettingsEntity configEntity = startupconfig.StartupSettingsEntity.getInstance();
        startupconfig.StartupConfigBean configBean = new startupconfig.StartupConfigBean(
                configEntity.isInterfaceMode(),
                configEntity.getStorageOption());
        new navigation.AppNavigator().navigateTo("Patient", configBean, null);
    } // Chiude il metodo handlePatientLogin

    private void handleRegistration() { // Metodo privato per gestire la registrazione
        logger.info("Registration request"); // Logga l'azione come nella GUI
        printLine("[CLI] La registrazione non è possibile per gli utenti non autorizzati dalla dottoressa."); // Feedback
                                                                                                              // visivo
                                                                                                              // all'utente
                                                                                                              // CLI
    } // Chiude il metodo handleRegistration

    private void handleAppointment() { // Metodo privato per gestire l'appuntamento rapido
        logger.info("Appointment request"); // Logga l'azione come nella GUI
        printLine("[CLI] Navigazione verso Prenotazione Appuntamento..."); // Feedback visivo all'utente CLI
    } // Chiude il metodo handleAppointment

    private void printLine(String message) { // Metodo wrapper privato per incapsulare System.out.println
        System.out.println(message); // Stampa il messaggio passato come argomento sulla console
    } // Chiude il metodo printLine

} // Chiude la classe LoginViewBoundary_cli
