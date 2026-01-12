package selectTypeLogin; // Dichiara il package di appartenenza della classe

import javafx.geometry.Insets; // Importa la classe per gestire i margini (padding)
import javafx.geometry.Pos; // Importa l'enumerazione per gestire l'allineamento (es. CENTER)
import javafx.scene.Scene; // Importa la classe Scene, che contiene tutti gli elementi grafici
import javafx.scene.control.Button; // Importa la classe Button per creare pulsanti
import javafx.scene.layout.HBox; // Importa HBox per layout orizzontali
import javafx.scene.layout.StackPane; // Importa StackPane per sovrapporre elementi (o centrarli)
import javafx.scene.layout.VBox; // Importa VBox per layout verticali
import javafx.scene.text.Text; // Importa la classe Text per visualizzare stringhe
import javafx.stage.Stage; // Importa la classe Stage, che rappresenta la finestra
import javafx.application.Application; // Importa la classe base per le app JavaFX
import java.util.logging.Logger; // Importa il Logger per registrare messaggi di sistema

/**
 * Boundary class for the Login Selection Screen.
 * adheres to the MVC pattern by strictly handling UI presentation.
 * All logic (navigation, business rules) is delegated to the Controller
 */

public class LoginViewBoundary_gui {

    private static final Logger logger = Logger.getLogger(LoginViewBoundary_gui.class.getName()); // Crea un logger per
                                                                                                  // registrare eventi
                                                                                                  // di questa classe
    private static final String SUBTITLE_ID = "subtitle"; // Costante per l'ID CSS dei sottotitoli
    private static final String LOGIN_ID = "login"; // Costante per l'ID CSS dei pulsanti di login
    private static final String CONTAINER_ID = "container"; // Costante per l'ID CSS del contenitore principale
    private static final String ROOT_ID = "root"; // Costante per l'ID CSS della radice

    public void start(Stage primaryStage) { // Metodo principale avviato da JavaFX per costruire la GUI
        primaryStage.setTitle("Portale MindLab"); // Imposta il titolo visibile nella barra della finestra

        VBox mainLayout = createMainLayout(primaryStage); // Chiama il metodo helper per creare il layout principale con
                                                          // i bottoni

        StackPane root = new StackPane(mainLayout); // Crea un pannello radice che centra il contenuto
        root.setId(ROOT_ID); // Assegna l'ID CSS al pannello radice

        Scene scene = new Scene(root, 800, 600); // Crea la scena di dimensioni 800x600 pixel contenente il root

        // Note: Dynamic style loading should be handled by the Controller passing data
        // to the View
        // For now, we load a default style or ensure the method is available for the
        // controller.
        loadStyle(scene, "/style/style_login_a_colori.css"); // Carica il foglio di stile CSS di default (a colori)

        primaryStage.setScene(scene); // Imposta la scena creata all'interno della finestra (Stage)
        primaryStage.setFullScreen(true); // Imposta la finestra in modalità schermo intero
        primaryStage.setResizable(false); // Impedisce all'utente di ridimensionare la finestra manualmente
        primaryStage.show(); // Rende visibile la finestra all'utente

        logger.info("Login Selection View initialized."); // Registra nel log che la vista è stata inizializzata
    }

    private VBox createMainLayout(Stage stage) { // Metodo helper privato per costruire la struttura verticale della
                                                 // pagina
        Text title = new Text("Portale MindLab"); // Crea l'oggetto testo per il titolo principale
        title.setId("title"); // Assegna l'ID CSS "title" per lo stile del titolo

        Text subtitle = createSubtitle("Accedi come:"); // Crea il sottotitolo usando un metodo helper

        Button specialistButton = createButton("Specialista", LOGIN_ID, 200); // Crea il bottone per lo specialista con
                                                                              // larghezza 200
        specialistButton.setOnAction(e -> handleSpecialistLogin(stage)); // Imposta l'azione al click: chiama
                                                                         // handleSpecialistLogin

        Button patientButton = createButton("Paziente", LOGIN_ID, 200); // Crea il bottone per il paziente
        patientButton.setOnAction(e -> handlePatientLogin(stage)); // Imposta l'azione al click: chiama
                                                                   // handlePatientLogin

        Text subtitle2 = createSubtitle("oppure"); // Crea un altro testo di collegamento

        Button registerButton = createButton("Registrati", "registrazione", 200); // Crea il bottone per la
                                                                                  // registrazione
        registerButton.setOnAction(e -> handleRegistration(stage)); // Imposta l'azione al click: chiama
                                                                    // handleRegistration

        Text subtitle3 = createSubtitle("altrimenti"); // Crea un altro testo di collegamento

        Button appointmentButton = createButton("Prenota un appuntamento senza registrarti", "appointmentButton", 400); // Crea
                                                                                                                        // bottone
                                                                                                                        // appuntamento
        appointmentButton.setOnAction(e -> handleAppointment(stage)); // Imposta l'azione al click: chiama
                                                                      // handleAppointment

        HBox buttonBox = new HBox(20, specialistButton, patientButton); // Crea un contenitore orizzontale per
                                                                        // affiancare i primi due bottoni con spaziatura
                                                                        // 20
        buttonBox.setAlignment(Pos.CENTER); // Allinea i bottoni al centro orizzontalmente

        VBox contentBox = new VBox(30, title, subtitle, buttonBox, subtitle2, registerButton, subtitle3,
                appointmentButton); // Crea contenitore verticale con tutti gli elementi spaziati di 30
        contentBox.setAlignment(Pos.CENTER); // Allinea tutto il contenuto al centro
        contentBox.setPadding(new Insets(40)); // Aggiunge un margine interno di 40 pixel su tutti i lati

        VBox container = new VBox(contentBox); // Crea un contenitore esterno per ulteriore styling
        container.setId(CONTAINER_ID); // Assegna l'ID CSS al contenitore
        container.setAlignment(Pos.CENTER); // Allinea il contenitore al centro
        container.setPadding(new Insets(20)); // Aggiunge un margine interno di 20 pixel

        return container; // Restituisce il layout completo costruito
    }

    private Text createSubtitle(String text) { // Metodo helper per creare testi con stile sottotitolo, evita
                                               // duplicazioni
        Text t = new Text(text); // Crea il nuovo oggetto Text con la stringa passata
        t.setId(SUBTITLE_ID); // Assegna l'ID CSS standard per i sottotitoli
        return t; // Restituisce l'oggetto Text configurato
    }

    private Button createButton(String text, String id, double width) { // Metodo helper per configurare bottoni in modo
                                                                        // uniforme
        Button b = new Button(text); // Crea il bottone con il testo specificato
        b.setId(id); // Assegna l'ID CSS specifico
        b.setPrefWidth(width); // Imposta la larghezza preferita del bottone
        return b; // Restituisce il bottone configurato
    }

    private void loadStyle(Scene scene, String resourcePath) { // Metodo per caricare il file CSS in modo sicuro
        try { // Inizia un blocco try per gestire eventuali errori di caricamento
            String style = getClass().getResource(resourcePath).toExternalForm(); // Ottiene l'URL del file CSS come
                                                                                  // stringa esterna
            scene.getStylesheets().add(style); // Aggiunge il foglio di stile alla scena
        } catch (NullPointerException e) { // Cattura l'eccezione se il file non viene trovato
            logger.severe("Could not load style resource: " + resourcePath); // Logga un errore grave se il CSS manca
        }
    }

    /*
     * Placeholder methods for Controller interactions.
     * In a full MVC implementation, these would call a GraphicController.
     */

    private void handleSpecialistLogin(Stage stage) { // Metodo segnaposto per gestire il login specialista
        logger.info("Specialist Login request - To be handled by Controller"); // Logga l'evento (da sostituire con
                                                                               // chiamata al Controller)
    }

    private void handlePatientLogin(Stage stage) { // Metodo segnaposto per gestire il login paziente
        logger.info("Patient Login request - To be handled by Controller"); // Logga l'evento (da sostituire con
                                                                            // chiamata al Controller)
    }

    private void handleRegistration(Stage stage) { // Metodo segnaposto per gestire la registrazione
        logger.info("Registration request - To be handled by Controller"); // Logga l'evento (da sostituire con chiamata
                                                                           // al Controller)
    }

    private void handleAppointment(Stage stage) { // Metodo segnaposto per gestire l'appuntamento rapido
        logger.info("Appointment request - To be handled by Controller"); // Logga l'evento (da sostituire con chiamata
                                                                          // al Controller)
    }
}
