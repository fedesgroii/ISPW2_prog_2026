package startupconfig; // Dichiarazione del package di appartenenza

import javafx.application.Application; // Importa la classe base per le applicazioni JavaFX
import javafx.geometry.Insets; // Importa classe per gestire i margini dei layout
import javafx.geometry.Pos; // Importa enumerazione per l'allineamento degli elementi
import javafx.scene.Scene; // Importa classe per il contenitore di alto livello per il contenuto della scena
import javafx.scene.control.Button; // Importa classe per il componente bottone
import javafx.scene.control.RadioButton; // Importa classe per i pulsanti di scelta esclusiva
import javafx.scene.control.ToggleGroup; // Importa classe per raggruppare i RadioButton
import javafx.scene.image.Image; // Importa classe per gestire le immagini
import javafx.scene.image.ImageView; // Importa classe per visualizzare le immagini nella scena
import javafx.scene.layout.HBox; // Importa layout per disporre elementi orizzontalmente
import javafx.scene.layout.VBox; // Importa layout per disporre elementi verticalmente
import javafx.scene.text.Text; // Importa classe per visualizzare testo semplice
import javafx.stage.Screen; // Importa classe per ottenere informazioni sullo schermo
import javafx.stage.Stage; // Importa classe per la finestra principale dell'applicazione

import java.util.Objects; // Importa classe utilitaria per la gestione degli oggetti (es. null-check)

// Classe che rappresenta la View (Interfaccia Grafica) per le impostazioni iniziali
// Estende Application per integrarsi con il ciclo di vita di JavaFX
public class StartupSettingsBoundary extends Application {

    // Dichiarazione dei riferimenti ai componenti UI per accedere al loro stato
    // (selezionato/non selezionato)
    private RadioButton guiMode; // Pulsante per selezionare la modalità grafica
    private RadioButton databaseOption; // Pulsante per selezionare l'archiviazione su Database
    private RadioButton fileSystemOption; // Pulsante per selezionare l'archiviazione su File System

    @Override
    public void start(Stage primaryStage) {
        // Inizializza il controller che gestirà le azioni dell'utente
        StartupSettingsController controller = new StartupSettingsController();

        // Imposta il titolo della finestra principale
        primaryStage.setTitle("MindLab");

        // Carica l'immagine dell'icona dell'applicazione dalle risorse
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icone/logo_ML.png")));
        // Aggiunge l'icona alla lista delle icone dello Stage (finestra)
        primaryStage.getIcons().add(appIcon);

        // Crea un contenitore verticale (VBox) per organizzare gli elementi
        // dell'interfaccia
        VBox container = new VBox();
        container.setSpacing(20); // Imposta una spaziatura di 20 pixel tra gli elementi verticali
        container.setPadding(new Insets(20)); // Imposta un margine interno di 20 pixel su tutti i lati
        container.setAlignment(Pos.CENTER); // Allinea il contenuto al centro del contenitore
        container.getStyleClass().add("root"); // Aggiunge la classe CSS "root" per lo styling

        // Carica l'immagine dell'icona "Power" dalle risorse
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icone/power-off.png")));

        // Crea un componente ImageView per visualizzare l'immagine caricata
        ImageView iconaPower = new ImageView(icon);
        iconaPower.setFitHeight(50); // Imposta l'altezza di visualizzazione a 50 pixel
        iconaPower.setFitWidth(50); // Imposta la larghezza di visualizzazione a 50 pixel

        // Crea un oggetto Text per il titolo della schermata
        Text title = new Text("Configurazione Avvio di MindLab");
        title.getStyleClass().add("title"); // Aggiunge la classe CSS "title" per lo styling del testo

        // Crea un ToggleGroup per gestire l'esclusività della selezione tra GUI e CLI
        ToggleGroup modeGroup = new ToggleGroup();
        guiMode = new RadioButton("Modalità GUI"); // Crea opzione per GUI
        guiMode.setToggleGroup(modeGroup); // Associa al gruppo
        guiMode.setSelected(true); // Imposta questa opzione come selezionata di default

        RadioButton cliMode = new RadioButton("Modalità CLI"); // Crea opzione per CLI
        cliMode.setToggleGroup(modeGroup); // Associa allo stesso gruppo

        // Crea un contenitore orizzontale (HBox) per le opzioni di modalità
        HBox modeBox = new HBox(10, guiMode, cliMode); // 10 pixel di spazio tra gli elementi
        modeBox.setAlignment(Pos.CENTER); // Allinea al centro
        modeBox.getStyleClass().add("option-box"); // Aggiunge classe CSS

        // Crea un nuovo ToggleGroup per le opzioni di storage (RAM vs Database vs
        // FileSystem)
        ToggleGroup storageGroup = new ToggleGroup();
        RadioButton memoryOption = new RadioButton("Memoria RAM"); // Opzione RAM
        memoryOption.setToggleGroup(storageGroup); // Associa al gruppo
        memoryOption.setSelected(true); // Selezionata di default

        databaseOption = new RadioButton("Database"); // Opzione Database
        databaseOption.setToggleGroup(storageGroup); // Associa al gruppo

        fileSystemOption = new RadioButton("File System"); // Opzione File System
        fileSystemOption.setToggleGroup(storageGroup); // Associa al gruppo

        // Crea contenitore orizzontale per le opzioni di storage
        HBox storageBox = new HBox(10, memoryOption, databaseOption, fileSystemOption);
        storageBox.setAlignment(Pos.CENTER); // Allinea al centro
        storageBox.getStyleClass().add("option-box"); // Aggiunge classe CSS

        // Crea il pulsante di conferma
        Button confirmButton = new Button("Conferma");
        confirmButton.getStyleClass().add("button"); // Aggiunge classe CSS

        // Definisce l'azione da eseguire quando il pulsante viene cliccato (Event
        // Handler)
        confirmButton.setOnAction(event -> {
            // Recupera i dati selezionati dall'interfaccia incapsulati in un Bean
            StartupConfigBean bean = getSettingsBean();
            // Passa il Bean al controller per l'elaborazione
            controller.processSettings(bean);

            // Chiude la finestra corrente
            primaryStage.close();

            // Delega la navigazione verso la login selection al Graphic Controller dedicato
            // MVC strict: la Boundary chiama un Graphic Controller per cambiare vista
            selectTypeLogin.SelectTypeLoginGraphicController graphicController = new selectTypeLogin.SelectTypeLoginGraphicController();
            graphicController.start(bean);

        });

        // Aggiunge tutti i componenti creati al contenitore principale
        container.getChildren().addAll(iconaPower, title, modeBox, storageBox, confirmButton);

        // Crea la scena contenente il layout principale, dimensionandola a tutto
        // schermo
        // scena, larghezza e altezza della scena
        Scene scene = new Scene(container, Screen.getPrimary().getBounds().getWidth(),
                Screen.getPrimary().getBounds().getHeight());

        // Carica e applica il foglio di stile CSS esterno
        scene.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/style/style_avvio.css")).toExternalForm());

        // Imposta la scena sullo Stage primario
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true); // Abilita modalità schermo intero
        primaryStage.setResizable(false); // Disabilita il ridimensionamento manuale della finestra
        primaryStage.setFullScreenExitHint(""); // Rimuove il messaggio di suggerimento per uscire dal full screen
        primaryStage.show(); // Rende visibile la finestra
    } // fine metodo start

    /**
     * Metodo privato (helper) per raccogliere i dati dalla UI e creare il Bean.
     * Separa la logica di estrazione dati dalla logica dell'event handler.
     *
     * @return Oggetto StartupConfigBean popolato con le scelte dell'utente
     */
    private StartupConfigBean getSettingsBean() {
        boolean isGuiMode = guiMode.isSelected(); // Legge lo stato del RadioButton guiMode
        int storageOption = getSelectedStorageOption(); // Determina l'opzione di storage tramite metodo helper
        return new StartupConfigBean(isGuiMode, storageOption); // Restituisce il nuovo Bean
    }

    /**
     * Metodo privato per tradurre la selezione dei RadioButton in un codice intero.
     * Mappa i componenti UI a valori di logica di business.
     *
     * @return Intero: 0=RAM, 1=Database, 2=File System
     */
    private int getSelectedStorageOption() {
        if (databaseOption.isSelected()) {
            return 1; // Ritorna 1 se è selezionato Database
        } else if (fileSystemOption.isSelected()) {
            return 2; // Ritorna 2 se è selezionato File System
        }
        return 0; // Default a 0 (RAM) se nessuna delle precedenti è vera (o se è selezionata
                  // memoryOption)
    }
}