package startupconfig; // Dichiarazione del package di appartenenza

import javafx.application.Application; // Importa la classe base per le applicazioni JavaFX
import javafx.application.Platform;
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
import java.util.logging.Logger; // Importa classe per il logging

// Classe che rappresenta la View (Interfaccia Grafica) per le impostazioni iniziali
// Estende Application per integrarsi con il ciclo di vita di JavaFX
public class StartupSettingsBoundary extends Application {

        private static final Logger logger = Logger.getLogger(StartupSettingsBoundary.class.getName());

        // Dichiarazione dei riferimenti ai componenti UI per accedere al loro stato
        // (selezionato/non selezionato)
        private RadioButton guiMode; // Pulsante per selezionare la modalità grafica
        private RadioButton databaseOption; // Pulsante per selezionare l'archiviazione su Database
        private RadioButton fileSystemOption; // Pulsante per selezionare l'archiviazione su File System

        @Override
        public void start(Stage primaryStage) {
                logger.info(() -> String.format("[DEBUG][Thread: %s] Entering StartupSettingsBoundary.start",
                                Thread.currentThread().getName()));
                // Inizializza il controller che gestirà le azioni dell'utente
                StartupSettingsController controller = new StartupSettingsController();

                // Configura JavaFX per non terminare automaticamente alla chiusura dell'ultima
                // finestra.
                Platform.setImplicitExit(false);

                // Imposta il titolo della finestra principale
                primaryStage.setTitle("MindLab");

                // Carica l'immagine dell'icona dell'applicazione dalle risorse
                Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icone/logo_ML.png")));
                // Aggiunge l'icona alla lista delle icone dello Stage (finestra)
                primaryStage.getIcons().add(appIcon);

                // Crea un contenitore verticale (VBox) per organizzare gli elementi
                VBox container = new VBox();
                container.setSpacing(20);
                container.setPadding(new Insets(20));
                container.setAlignment(Pos.CENTER);
                container.getStyleClass().add("root");

                // Carica l'immagine dell'icona "Power" dalle risorse
                Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icone/power-off.png")));

                // Crea un componente ImageView per visualizzare l'immagine caricata
                ImageView iconaPower = new ImageView(icon);
                iconaPower.setFitHeight(50);
                iconaPower.setFitWidth(50);

                // Crea un oggetto Text per il titolo della schermata
                Text title = new Text("Configurazione Avvio di MindLab");
                title.getStyleClass().add("title");

                // Crea un ToggleGroup per gestire l'esclusività della selezione tra GUI e CLI
                ToggleGroup modeGroup = new ToggleGroup();
                guiMode = new RadioButton("Modalità GUI");
                guiMode.setToggleGroup(modeGroup);
                guiMode.setSelected(true);

                RadioButton cliMode = new RadioButton("Modalità CLI");
                cliMode.setToggleGroup(modeGroup);

                // Crea un contenitore orizzontale (HBox) per le opzioni di modalità
                HBox modeBox = new HBox(10, guiMode, cliMode);
                modeBox.setAlignment(Pos.CENTER);
                modeBox.getStyleClass().add("option-box");

                // Crea un nuovo ToggleGroup per le opzioni di storage
                ToggleGroup storageGroup = new ToggleGroup();
                RadioButton memoryOption = new RadioButton("Memoria RAM");
                memoryOption.setToggleGroup(storageGroup);
                memoryOption.setSelected(true);

                databaseOption = new RadioButton("Database");
                databaseOption.setToggleGroup(storageGroup);

                fileSystemOption = new RadioButton("File System");
                fileSystemOption.setToggleGroup(storageGroup);

                // Crea contenitore orizzontale per le opzioni di storage
                HBox storageBox = new HBox(10, memoryOption, databaseOption, fileSystemOption);
                storageBox.setAlignment(Pos.CENTER);
                storageBox.getStyleClass().add("option-box");

                // Crea il pulsante di conferma
                Button confirmButton = new Button("Conferma");
                confirmButton.getStyleClass().add("button");

                // Definisce l'azione da eseguire quando il pulsante viene cliccato
                confirmButton.setOnAction(event -> {
                        logger.info(() -> String.format("[DEBUG][Thread: %s] Confirm button clicked",
                                        Thread.currentThread().getName()));
                        // Recupera i dati selezionati dall'interfaccia incapsulati in un Bean
                        StartupConfigBean bean = getSettingsBean();

                        // Delegazione totale al Controller
                        controller.completeConfiguration(bean, primaryStage);

                        logger.info(() -> String.format(
                                        "[DEBUG][Thread: %s] Configuration finalization delegated to controller.",
                                        Thread.currentThread().getName()));
                });

                // Aggiunge tutti i componenti creati al contenitore principale
                container.getChildren().addAll(iconaPower, title, modeBox, storageBox, confirmButton);

                // Crea la scena
                Scene scene = new Scene(container, Screen.getPrimary().getBounds().getWidth(),
                                Screen.getPrimary().getBounds().getHeight());

                // Carica e applica il foglio di stile CSS esterno
                scene.getStylesheets()
                                .add(Objects.requireNonNull(getClass().getResource("/style/style_avvio.css"))
                                                .toExternalForm());

                // Imposta la scena sullo Stage primario
                primaryStage.setScene(scene);
                primaryStage.setFullScreen(true);
                primaryStage.setResizable(false);
                primaryStage.setFullScreenExitHint("");
                logger.info(() -> String.format("[DEBUG][Thread: %s] Calling primaryStage.show()",
                                Thread.currentThread().getName()));
                primaryStage.show();
                logger.info(() -> String.format("[DEBUG][Thread: %s] primaryStage.show() returned",
                                Thread.currentThread().getName()));
        }
        // fine metodo start

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