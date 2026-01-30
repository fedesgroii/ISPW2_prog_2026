package login_insert_data;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import navigation.View;
import startupconfig.StartupConfigBean;

import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Base abstract class for GUI Login Views.
 */
public abstract class LoginViewBase implements View {
    private static final Logger LOGGER = Logger.getLogger(LoginViewBase.class.getName());
    private static final String CSS_PATH = "/style/style_login_insert_specialist_colori.css";
    private static final int ERROR_TIMEOUT_SECONDS = 5;

    private final LoginGraphicController grafCon = new LoginGraphicController(this);
    private Text errorText;
    private TextField emailField;
    private PasswordField passwordField;
    private Stage primaryStage;
    private StartupConfigBean configBean; // Bean per conservare le impostazioni

    protected abstract String getTipo();

    protected abstract String getTitleText();

    protected abstract String getSubtitleText();

    @Override
    public void show(Stage stage, StartupConfigBean config) {
        this.configBean = config; // Salva la configurazione iniettata
        this.start(stage);
    }

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        Text title = new Text(getTitleText());
        title.setId("title");

        Text subtitle = new Text(getSubtitleText());
        subtitle.setId("subtitle");

        errorText = new Text("Credenziali errate, riprova.");
        errorText.setVisible(false);
        errorText.setStyle("-fx-fill: red;");

        emailField = new TextField();
        emailField.setPromptText("Inserisci la tua email");
        emailField.setId("inputField");

        passwordField = new PasswordField();
        passwordField.setPromptText("Inserisci la tua password");
        passwordField.setId("inputField");

        Scene scene = getScene(title, subtitle);

        loadStyleSheet(scene);

        primaryStage.setTitle(getTitleText());
        primaryStage.setScene(scene);
        primaryStage.setFullScreen(true);
        primaryStage.setResizable(false);

        primaryStage.show();
    }

    private void loadStyleSheet(Scene scene) {
        // Ora carichiamo sempre lo stile a colori, come da nuovi requisiti
        try {
            String styleSheet = Objects.requireNonNull(
                    getClass().getResource(CSS_PATH),
                    "Resource non trovata: " + CSS_PATH).toExternalForm();
            scene.getStylesheets().add(styleSheet);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile caricare il CSS: {0}. Uso stile di sistema.", CSS_PATH);
        }
    }

    private Scene getScene(Text title, Text subtitle) {
        Button loginButton = new Button("Accedi");
        loginButton.setId("specialistButton");
        loginButton.setOnAction(event -> grafCon.handleLoginAttempt(getTipo()));

        Button switchLoginButton = new Button(getSwitchButtonText());
        switchLoginButton.setId("backButton");
        switchLoginButton.setOnAction(event -> grafCon.handleSwitchLoginAction(primaryStage, getTipo()));

        VBox vbox = new VBox(20, title, subtitle, errorText, emailField, passwordField, loginButton, switchLoginButton);
        vbox.setId("vbox");
        vbox.setAlignment(javafx.geometry.Pos.CENTER);

        return new Scene(vbox, 800, 600);
    }

    public void showError() {
        errorText.setVisible(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(ERROR_TIMEOUT_SECONDS));
        delay.setOnFinished(event -> errorText.setVisible(false));
        delay.play();
    }

    public PasswordField getPasswordField() {
        return passwordField;
    }

    public TextField getEmailField() {
        return emailField;
    }

    public void closeView() {
        if (primaryStage != null) {
            primaryStage.close();
            LOGGER.log(Level.INFO, "Finestra di login chiusa con successo.");
        }
    }

    private String getSwitchButtonText() {
        return "Patient".equals(getTipo()) ? "Accedi come Specialista" : "Accedi come Paziente";
    }

    public StartupConfigBean getConfigBean() {
        return configBean;
    }
}
