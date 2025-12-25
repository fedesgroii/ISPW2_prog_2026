package startupconfig;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.util.Objects;

public class StartupSettingsBoundary extends Application {

    // Riferimenti ai componenti UI per accedere ai loro stati
    private RadioButton guiMode;
    private RadioButton databaseOption;
    private RadioButton fileSystemOption;

    @Override
    public void start(Stage primaryStage) {
        // Metodo principale chiamato da JavaFX per avviare l'interfaccia
        StartupSettingsController controller = new StartupSettingsController(); // Crea un'istanza del controller per
        // gestire le azioni dell'utente
        primaryStage.setTitle("MindLab");

        // **Aggiunta dell'icona dell'applicazione**
        Image appIcon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icone/logo_ML.png"))); // Carica
        // l'immagine
        // dell'icona
        primaryStage.getIcons().add(appIcon); // Aggiunge l'icona alla finestra principale (Stage)

        VBox container = new VBox(); // Crea un contenitore verticale per organizzare gli elementi
        container.setSpacing(20); // Spaziatura tra gli elementi
        container.setPadding(new Insets(20)); // Margini interni del contenitore
        container.setAlignment(Pos.CENTER); // Allinea gli elementi al centro
        container.getStyleClass().add("root"); // Aggiunge una classe di stile CSS al contenitore

        // Carica e visualizza l'icona principale nella UI
        Image icon = new Image(Objects.requireNonNull(getClass().getResourceAsStream("/icone/power-off.png"))); // Carica
        // l'immagine
        ImageView iconaPower = new ImageView(icon); // Crea un visualizzatore per l'immagine
        iconaPower.setFitHeight(50); // Imposta l'altezza dell'immagine
        iconaPower.setFitWidth(50); // Imposta la larghezza dell'immagine

        // Crea il titolo
        Text title = new Text("Configurazione Avvio di MindLab"); // Testo del titolo
        title.getStyleClass().add("title"); // Aggiunge una classe di stile CSS al testo

        // Crea i pulsanti di scelta per la modalità di interazione: GUI oppure
        // terminale (CLI)
        ToggleGroup modeGroup = new ToggleGroup(); // Gruppo di pulsanti di scelta
        guiMode = new RadioButton("Modalità GUI");
        guiMode.setToggleGroup(modeGroup); // Aggiunge il pulsante al gruppo
        guiMode.setSelected(true); // Imposta l'opzione come predefinita
        RadioButton cliMode = new RadioButton("Modalità CLI"); // Opzione per la modalità terminale
        cliMode.setToggleGroup(modeGroup); // Aggiunge il pulsante al gruppo
        HBox modeBox = new HBox(10, guiMode, cliMode); // Contenitore orizzontale per le opzioni
        modeBox.setAlignment(Pos.CENTER); // Allinea le opzioni al centro
        modeBox.getStyleClass().add("option-box"); // Aggiunge una classe di stile CSS

        // Crea il gruppo di pulsanti di scelta per "Memoria RAM", "Database", "File
        // System"
        ToggleGroup storageGroup = new ToggleGroup(); // Gruppo di pulsanti di scelta
        RadioButton memoryOption = new RadioButton("Memoria RAM"); // Opzione per Memoria RAM
        memoryOption.setToggleGroup(storageGroup); // Aggiunge il pulsante al gruppo
        memoryOption.setSelected(true); // Imposta l'opzione come predefinita
        databaseOption = new RadioButton("Database"); // Opzione per Database
        databaseOption.setToggleGroup(storageGroup); // Aggiunge il pulsante al gruppo
        fileSystemOption = new RadioButton("File System"); // Opzione per File System
        fileSystemOption.setToggleGroup(storageGroup); // Aggiunge il pulsante al gruppo
        HBox storageBox = new HBox(10, memoryOption, databaseOption, fileSystemOption); // Contenitore orizzontale per
        // le opzioni
        storageBox.setAlignment(Pos.CENTER); // Allinea le opzioni al centro
        storageBox.getStyleClass().add("option-box"); // Aggiunge una classe di stile CSS

        // Crea il pulsante di conferma
        Button confirmButton = new Button("Conferma"); // Pulsante per confermare le scelte
        confirmButton.getStyleClass().add("button"); // Aggiunge una classe di stile CSS

        // Aggiunge tutti gli elementi al contenitore principale
        container.getChildren().addAll(iconaPower, title, modeBox, storageBox, confirmButton);

        // Configura e mostra la scena
        Scene scene = new Scene(container, Screen.getPrimary().getBounds().getWidth(),
                Screen.getPrimary().getBounds().getHeight()); // Crea una scena a schermo intero
        scene.getStylesheets()
                .add(Objects.requireNonNull(getClass().getResource("/style/style_avvio.css")).toExternalForm()); // Aggiunge
        // lo
        // stile
        // CSS
        primaryStage.setScene(scene); // Imposta la scena sulla finestra principale
        primaryStage.setFullScreen(true); // Imposta la finestra a schermo intero
        primaryStage.setResizable(false);
        primaryStage.setFullScreenExitHint("");
        primaryStage.show(); // Mostra la finestra
    }

    /**
     * Metodo pubblico per ottenere i dati delle impostazioni selezionate
     * dall'utente
     *
     * @return Oggetto SettingsData contenente le impostazioni selezionate
     */
    public SettingsData getSettingsData() {
        boolean isGuiMode = guiMode.isSelected();
        int storageOption = getSelectedStorageOption();
        return new SettingsData(isGuiMode, storageOption);
    }

    /**
     * Metodo privato per determinare l'opzione di archiviazione selezionata
     * Nasconde la logica di implementazione interna della boundary
     *
     * @return Intero rappresentante l'opzione di archiviazione (0=RAM, 1=Database,
     *         2=File System)
     */
    private int getSelectedStorageOption() {
        if (databaseOption.isSelected()) {
            return 1;
        } else if (fileSystemOption.isSelected()) {
            return 2;
        }
        return 0; // RAM
    }

    /**
     * Classe interna per incapsulare i dati delle impostazioni selezionate
     * Favorisce il rispetto del principio di incapsulamento e rende esplicita
     * l'interfaccia
     * tra boundary e controller
     */
    public static class SettingsData {
        private final boolean guiMode;
        private final int storageOption;

        public SettingsData(boolean guiMode, int storageOption) {
            this.guiMode = guiMode;
            this.storageOption = storageOption;
        }

        public boolean isGuiMode() {
            return guiMode;
        }

        public int getStorageOption() {
            return storageOption;
        }
    }
}