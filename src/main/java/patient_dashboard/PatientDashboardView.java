package patient_dashboard;

import dashboard_helper.DashboardStyleHelper;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import model.Paziente;
import navigation.AppNavigator;
import navigation.View;
import startupconfig.StartupConfigBean;

import java.util.logging.Logger;

/**
 * Dashboard view for authenticated patients in GUI mode.
 * 
 * <p>
 * This view displays a personalized home screen with quick access to:
 * <ul>
 * <li>Book a new visit</li>
 * <li>View visit history</li>
 * <li>Browse the shop</li>
 * <li>Check the bulletin board</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The dashboard implements the View interface and validates that a patient
 * session is active before displaying. It follows the design system defined
 * in {@link DashboardStyleHelper} to ensure visual consistency.
 * </p>
 * 
 * <p>
 * <strong>Navigation Flow:</strong> This dashboard is typically shown after
 * successful patient login. Each card's button navigates to a specific view
 * using the {@link AppNavigator}.
 * </p>
 * 
 */
public class PatientDashboardView implements View {

    private static final Logger LOGGER = Logger.getLogger(PatientDashboardView.class.getName());
    private final PatientDashboardController controller = new PatientDashboardController();
    private final PatientDashboardGraphicController graphicController = new PatientDashboardGraphicController();

    /**
     * Displays the patient dashboard in the provided stage.
     * 
     * <p>
     * This method:
     * <ol>
     * <li>Validates that a patient session is active</li>
     * <li>Retrieves the logged-in patient's information</li>
     * <li>Constructs the dashboard UI with personalized greeting</li>
     * <li>Wires navigation buttons to appropriate views</li>
     * <li>Shows the dashboard in the stage</li>
     * </ol>
     * </p>
     * 
     * @param stage  The JavaFX stage to display the dashboard in
     * @param config The startup configuration containing mode and storage settings
     * @throws IllegalStateException if no patient session is active
     */
    @Override
    public void show(Stage stage, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] PatientDashboardView.show() called",
                Thread.currentThread().getName()));

        // Ensure we're on the JavaFX Application Thread using a clear dispatcher
        // pattern
        if (Platform.isFxApplicationThread()) {
            startDashboard(stage, config);
        } else {
            Platform.runLater(() -> startDashboard(stage, config));
        }
    }

    /**
     * Internal method to initialize and display the dashboard.
     * Must be called on the JavaFX Application Thread.
     */
    private void startDashboard(Stage stage, StartupConfigBean config) {
        try {
            // Session validation via Application Controller
            controller.checkSession();

            // Retrieve logged-in patient via Application Controller
            Paziente paziente = controller.getLoggedPatient();
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Displaying dashboard for patient: %s",
                    Thread.currentThread().getName(), paziente.getNome()));

            // Build the UI
            VBox root = buildDashboardUI(paziente, config, stage);

            // Fluid Transition: Swap root if scene exists, otherwise create new Scene
            if (stage.getScene() == null) {
                Scene scene = new Scene(root, 800, 700);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }

            stage.setTitle("Home - MindLab Portal (Paziente)");
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.setResizable(true);
            stage.show();

            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] PatientDashboardView displayed successfully",
                    Thread.currentThread().getName()));

        } catch (Exception e) {
            LOGGER.severe(() -> String.format("[DEBUG][Thread: %s] Error displaying PatientDashboardView: %s",
                    Thread.currentThread().getName(), e.getMessage()));
            throw e;
        }
    }

    /**
     * Builds the complete dashboard UI structure.
     * 
     * @param paziente The logged-in patient
     * @param config   Configuration bean
     * @param stage    Current stage for navigation
     * @return Root VBox containing the entire dashboard
     */
    private VBox buildDashboardUI(Paziente paziente, StartupConfigBean config, Stage stage) {
        // Create root container
        VBox root = DashboardStyleHelper.createRootContainer();

        // Create header
        HBox header = DashboardStyleHelper.createHeaderBox(
                "Home - MindLab",
                "Ciao, " + paziente.getNome() + "!");

        // Create cards (Bacheca is moved to footer)
        VBox card1 = createBookingCard(config, stage);
        VBox card2 = createHistoryCard(config, stage);
        VBox card3 = createShopCard(config, stage);

        // Create footer
        HBox footer = createFooter(config, stage);

        // Assemble dashboard
        root.getChildren().addAll(header, card1, card2, card3, footer);

        return root;
    }

    /**
     * Creates the "Prenota una visita" card.
     */
    private VBox createBookingCard(StartupConfigBean config, Stage stage) {
        VBox card = DashboardStyleHelper.createCard(
                "Prenota una visita",
                "Cerca il momento perfetto per la tua prossima visita",
                "Prenota ora");

        // Apply green theme to this card as per medical style requirements
        card.setStyle(card.getStyle() + String.format("; -fx-background-color: %s; -fx-border-color: transparent;",
                DashboardStyleHelper.COLOR_CARD_GREEN));
        ((javafx.scene.text.Text) card.getChildren().get(0)).setFill(javafx.scene.paint.Color.WHITE);
        ((javafx.scene.text.Text) card.getChildren().get(1)).setFill(javafx.scene.paint.Color.WHITE);

        // Wire button to navigate to Booking view
        Button button = (Button) card.getChildren().get(2);
        button.setStyle(button.getStyle() + "; -fx-background-color: white; -fx-text-fill: #1E8449;");
        addInteractiveHoverEffect(button);
        button.setOnAction(_ -> navigateToView("Booking", config, stage));

        return card;
    }

    /**
     * Creates the "Storico Visite" card.
     */
    private VBox createHistoryCard(StartupConfigBean config, Stage stage) {
        VBox card = DashboardStyleHelper.createCard(
                "Storico Visite",
                "Visualizza e gestisci le tue visite",
                "Vedi storico");

        // Apply green theme to this card as per medical style requirements
        card.setStyle(card.getStyle() + String.format("; -fx-background-color: %s; -fx-border-color: transparent;",
                DashboardStyleHelper.COLOR_CARD_GREEN));
        ((javafx.scene.text.Text) card.getChildren().get(0)).setFill(javafx.scene.paint.Color.WHITE);
        ((javafx.scene.text.Text) card.getChildren().get(1)).setFill(javafx.scene.paint.Color.WHITE);

        // Wire button to navigate to History view
        Button button = (Button) card.getChildren().get(2);
        button.setStyle(button.getStyle() + "; -fx-background-color: white; -fx-text-fill: #1E8449;");
        addInteractiveHoverEffect(button);
        button.setOnAction(_ -> navigateToView("History", config, stage));

        return card;
    }

    /**
     * Creates the "Shop" card.
     */
    private VBox createShopCard(StartupConfigBean config, Stage stage) {
        VBox card = DashboardStyleHelper.createCard(
                "Shop",
                "Scopri i nostri prodotti",
                "Esplora");

        // Wire button to navigate to Shop view
        Button button = (Button) card.getChildren().get(2);
        addInteractiveHoverEffect(button);
        button.setOnAction(_ -> navigateToView("Shop", config, stage));

        return card;
    }

    /**
     * Creates the footer navigation bar.
     */
    private HBox createFooter(StartupConfigBean config, Stage stage) {
        Button bachecaButton = new Button("Bacheca");
        Button homeButton = new Button("Home");
        Button visiteButton = new Button("Visite");

        // Add visual interactivity to footer buttons
        addInteractiveHoverEffect(bachecaButton);
        addInteractiveHoverEffect(homeButton);
        addInteractiveHoverEffect(visiteButton);

        // Bacheca button action
        bachecaButton.setOnAction(_ -> {
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Opening Bacheca notification",
                    Thread.currentThread().getName()));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bacheca - MindLab");
            alert.setHeaderText("Ultime notifiche e annunci");
            alert.setContentText("Non ci sono nuovi annunci in bacheca al momento.");
            alert.showAndWait();
        });

        // Home button is already on home, so no action needed
        homeButton.setOnAction(_ -> LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Already on Home dashboard",
                Thread.currentThread().getName())));

        // Visite button navigates to Appointments view
        visiteButton.setOnAction(_ -> navigateToView("Appointments", config, stage));

        return DashboardStyleHelper.createFooter(1, bachecaButton, homeButton, visiteButton);
    }

    /**
     * Adds a modern and interactive scale effect to buttons on hover.
     */
    private void addInteractiveHoverEffect(Button button) {
        javafx.animation.ScaleTransition scaleIn = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(150), button);
        scaleIn.setToX(1.05);
        scaleIn.setToY(1.05);

        javafx.animation.ScaleTransition scaleOut = new javafx.animation.ScaleTransition(
                javafx.util.Duration.millis(150), button);
        scaleOut.setToX(1.0);
        scaleOut.setToY(1.0);

        button.setOnMouseEntered(_ -> {
            scaleOut.stop();
            scaleIn.playFromStart();
        });
        button.setOnMouseExited(_ -> {
            scaleIn.stop();
            scaleOut.playFromStart();
        });
    }

    /**
     * Navigates to a specified view using the Graphic Controller.
     * 
     * @param viewName     Name of the target view
     * @param config       Configuration bean
     * @param currentStage Current stage to close after navigation
     */
    private void navigateToView(String viewName, StartupConfigBean config, Stage currentStage) {
        try {
            graphicController.navigateToView(viewName, config, currentStage);
        } catch (Exception e) {
            showErrorAlert("Navigazione fallita",
                    "Impossibile navigare a " + viewName + ": " + e.getMessage());
        }
    }

    /**
     * Shows an error alert dialog to the user.
     * 
     * @param title   Alert title
     * @param message Alert message
     */
    private void showErrorAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
