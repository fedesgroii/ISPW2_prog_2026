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
    private static final String CSS_PATH = "/style/style_login_insert_specialist_a_colori.css";
    private static final int ERROR_TIMEOUT_SECONDS = 5;

    private final LoginGraphicControllerGui grafCon = new LoginGraphicControllerGui(this);
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

        VBox rootContent = createContent();

        // Fluid Transition: Swap root if scene exists, otherwise create new Scene
        if (primaryStage.getScene() == null) {
            Scene scene = new Scene(rootContent, 800, 600);
            loadStyleSheet(scene);
            primaryStage.setScene(scene);
        } else {
            primaryStage.getScene().setRoot(rootContent);
            loadStyleSheet(primaryStage.getScene());
        }

        primaryStage.setTitle(getTitleText());
        primaryStage.setFullScreen(true);
        primaryStage.setFullScreenExitHint("");
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
        } catch (Exception _) {
            LOGGER.log(Level.WARNING, "Impossibile caricare il CSS: {0}. Uso stile di sistema.", CSS_PATH);
        }
    }

    private VBox createContent() {
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

        // Logo
        javafx.scene.image.ImageView logoView = new javafx.scene.image.ImageView();
        try {
            javafx.scene.image.Image logo = new javafx.scene.image.Image(
                    Objects.requireNonNull(getClass().getResourceAsStream("/icone/logo_ML_sfondo.png")));
            logoView.setImage(logo);
            logoView.setFitWidth(250);
            logoView.setPreserveRatio(true);
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Impossibile caricare il logo: {0}", e.getMessage());
        }

        Button loginButton = new Button("Accedi");
        loginButton.setId("specialistButton");
        loginButton.setPrefWidth(250);
        loginButton.setOnAction(event -> grafCon.handleLoginAttempt(getTipo()));

        Button switchLoginButton = new Button(getSwitchButtonText());
        switchLoginButton.setId("backButton");
        switchLoginButton.setPrefWidth(250);
        switchLoginButton.setOnAction(event -> grafCon.handleSwitchLoginAction(primaryStage, getTipo()));

        // Card container
        VBox card = new VBox(20, title, subtitle, errorText, emailField, passwordField, loginButton, switchLoginButton);
        card.setId("container"); // Matches the ID used in CSS for the card
        card.setAlignment(javafx.geometry.Pos.CENTER);
        card.setMaxWidth(500);
        card.setPadding(new javafx.geometry.Insets(40));

        // Main layout
        VBox mainLayout = new VBox(30, logoView, card);
        mainLayout.setId("vbox"); // Root layout
        mainLayout.setAlignment(javafx.geometry.Pos.CENTER);
        mainLayout.setStyle("-fx-background-color: transparent;"); // Background handled by .root in CSS

        return mainLayout;
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

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public StartupConfigBean getConfigBean() {
        return configBean;
    }
}
