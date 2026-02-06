package specialist_dashboard;

import dashboard_helper.DashboardStyleHelper;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import navigation.AppNavigator;
import navigation.View;
import session_manager.SessionManagerSpecialista;
import startupconfig.StartupConfigBean;

import java.util.logging.Logger;

/**
 * Dashboard view for authenticated specialists in GUI mode.
 * 
 * <p>
 * This view displays a personalized home screen with quick access to:
 * <ul>
 * <li>Manage agenda and availability</li>
 * <li>View patient list</li>
 * <li>Generate clinical reports</li>
 * <li>Check the bulletin board</li>
 * </ul>
 * </p>
 * 
 * <p>
 * The dashboard implements the View interface and validates that a specialist
 * session is active before displaying. It follows the design system defined
 * in {@link DashboardStyleHelper} to ensure visual consistency with the patient
 * dashboard.
 * </p>
 * 
 * <p>
 * <strong>Navigation Flow:</strong> This dashboard is typically shown after
 * successful specialist login. Each card's button navigates to a specific view
 * using the {@link AppNavigator}.
 * </p>
 * 
 * @author MindLab Development Team
 * @version 1.0
 * @see patient_dashboard.PatientDashboardView
 * @see SessionManagerSpecialista
 */
public class SpecialistDashboardViewGui implements View {

    private static final Logger LOGGER = Logger.getLogger(SpecialistDashboardViewGui.class.getName());
    private SpecialistDashboardController controller; // Initialized in startDashboard()
    private final SpecialistDashboardGraphicControllerGui graphicController = new SpecialistDashboardGraphicControllerGui();

    /**
     * Displays the specialist dashboard in the provided stage.
     * 
     * <p>
     * This method:
     * <ol>
     * <li>Validates that a specialist session is active</li>
     * <li>Retrieves the logged-in specialist's information</li>
     * <li>Constructs the dashboard UI with personalized greeting</li>
     * <li>Wires navigation buttons to appropriate views</li>
     * <li>Shows the dashboard in the stage</li>
     * </ol>
     * </p>
     * 
     * @param stage  The JavaFX stage to display the dashboard in
     * @param config The startup configuration containing mode and storage settings
     * @throws IllegalStateException if no specialist session is active
     */
    @Override
    public void show(Stage stage, StartupConfigBean config) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] SpecialistDashboardViewGui.show() called",
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
            // Initialize controller HERE to ensure it captures notification history AFTER
            // bookings have been made
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Initializing SpecialistDashboardController...",
                    Thread.currentThread().getName()));
            controller = new SpecialistDashboardController();

            // Session validation via Application Controller
            controller.checkSession();

            // Retrieve data via Graphic Controller and Bean
            SpecialistDashboardBean bean = graphicController.getDashboardData(controller);
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Displaying dashboard for specialist: %s",
                    Thread.currentThread().getName(), bean.getNome()));

            // Build the UI
            VBox root = buildDashboardUI(bean, config, stage);

            // Fluid Transition: Swap root if scene exists, otherwise create new Scene
            if (stage.getScene() == null) {
                Scene scene = new Scene(root, 800, 700);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }

            stage.setTitle("Home - MindLab Portal (Specialista)");
            stage.setFullScreen(true);
            stage.setFullScreenExitHint("");
            stage.setResizable(true);
            stage.show();

            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] SpecialistDashboardViewGui displayed successfully",
                    Thread.currentThread().getName()));

        } catch (Exception e) {
            String msg = String.format("Error displaying SpecialistDashboardViewGui: %s", e.getMessage());
            LOGGER.log(java.util.logging.Level.SEVERE, msg, e);
        }
    }

    /**
     * Builds the complete dashboard UI structure.
     * 
     * @param specialista The logged-in specialist
     * @param config      Configuration bean
     * @param stage       Current stage for navigation
     * @return Root VBox containing the entire dashboard
     */
    private VBox buildDashboardUI(SpecialistDashboardBean bean, StartupConfigBean config, Stage stage) {
        // Create root container
        VBox root = DashboardStyleHelper.createRootContainer();

        // Create header
        HBox header = DashboardStyleHelper.createHeaderBox(
                "Home - MindLab",
                "Ciao, " + bean.getNome() + "!");

        // Create cards (Bacheca is moved to footer)
        VBox card1 = createAgendaCard(config, stage);
        VBox card2 = createPatientsCard(config, stage);
        VBox card3 = createReportsCard(config, stage);

        // Create footer
        HBox footer = createFooter(config, stage, bean);

        // Assemble dashboard
        root.getChildren().addAll(header, card1, card2, card3, footer);

        return root;
    }

    /**
     * Creates the "La mia agenda" card.
     */
    private VBox createAgendaCard(StartupConfigBean config, Stage stage) {
        VBox card = DashboardStyleHelper.createCard(
                "La mia agenda",
                "Gestisci i tuoi appuntamenti e la disponibilità",
                "Gestisci agenda");

        // Apply green theme to this card as per medical style requirements
        card.setStyle(card.getStyle() + String.format("; -fx-background-color: %s; -fx-border-color: transparent;",
                DashboardStyleHelper.COLOR_CARD_GREEN));
        ((javafx.scene.text.Text) card.getChildren().get(0)).setFill(javafx.scene.paint.Color.WHITE);
        ((javafx.scene.text.Text) card.getChildren().get(1)).setFill(javafx.scene.paint.Color.WHITE);

        // Wire button to navigate to Agenda view
        Button button = (Button) card.getChildren().get(2);
        button.setStyle(button.getStyle() + "; -fx-background-color: white; -fx-text-fill: #1E8449;");
        addInteractiveHoverEffect(button);
        button.setOnAction(_ -> navigateToView("Agenda", config, stage));

        return card;
    }

    /**
     * Creates the "Pazienti" card.
     */
    private VBox createPatientsCard(StartupConfigBean config, Stage stage) {
        VBox card = DashboardStyleHelper.createCard(
                "Pazienti",
                "Visualizza la lista dei tuoi pazienti",
                "Lista pazienti");

        // Apply green theme to this card as per medical style requirements
        card.setStyle(card.getStyle() + String.format("; -fx-background-color: %s; -fx-border-color: transparent;",
                DashboardStyleHelper.COLOR_CARD_GREEN));
        ((javafx.scene.text.Text) card.getChildren().get(0)).setFill(javafx.scene.paint.Color.WHITE);
        ((javafx.scene.text.Text) card.getChildren().get(1)).setFill(javafx.scene.paint.Color.WHITE);

        // Wire button to navigate to PatientsList view
        Button button = (Button) card.getChildren().get(2);
        button.setStyle(button.getStyle() + "; -fx-background-color: white; -fx-text-fill: #1E8449;");
        addInteractiveHoverEffect(button);
        button.setOnAction(_ -> navigateToView("PatientsList", config, stage));

        return card;
    }

    /**
     * Creates the "Report" card.
     */
    private VBox createReportsCard(StartupConfigBean config, Stage stage) {
        VBox card = DashboardStyleHelper.createCard(
                "Report",
                "Genera report delle tue attività cliniche",
                "Crea report");

        // Wire button to navigate to Reports view
        Button button = (Button) card.getChildren().get(2);
        addInteractiveHoverEffect(button);
        button.setOnAction(_ -> navigateToView("Reports", config, stage));

        return card;
    }

    /**
     * Creates the footer navigation bar.
     */
    private HBox createFooter(StartupConfigBean config, Stage stage, SpecialistDashboardBean bean) {
        Button bachecaButton = new Button("Bacheca");
        Button homeButton = new Button("Home");
        Button visiteButton = new Button("Visite");
        Button notificheButton = new Button("Notifiche");

        // Add visual interactivity to footer buttons
        addInteractiveHoverEffect(bachecaButton);
        addInteractiveHoverEffect(homeButton);
        addInteractiveHoverEffect(visiteButton);
        addInteractiveHoverEffect(notificheButton);

        // Badge for notifications
        javafx.scene.shape.Circle badge = new javafx.scene.shape.Circle(10, javafx.scene.paint.Color.RED);
        javafx.scene.text.Text badgeText = new javafx.scene.text.Text("0");
        badgeText.setFill(javafx.scene.paint.Color.WHITE);
        badgeText.setStyle("-fx-font-weight: bold; -fx-font-size: 11px;");
        javafx.scene.layout.StackPane badgePane = new javafx.scene.layout.StackPane(badge, badgeText);
        badgePane.setTranslateX(30); // Position badge relative to button
        badgePane.setTranslateY(-15);
        badgePane.setVisible(false);

        // Wrap button and badge
        javafx.scene.layout.StackPane notificheWrapper = new javafx.scene.layout.StackPane(notificheButton, badgePane);
        notificheWrapper.setAlignment(javafx.geometry.Pos.CENTER);

        // Callback to update badge
        Runnable updateBadge = () -> {
            SpecialistDashboardBean currentBean = graphicController.getDashboardData(controller);
            int count = currentBean.getUnreadNotificationsCount();
            if (count > 0) {
                badgeText.setText(String.valueOf(count));
                badgePane.setVisible(true);
            } else {
                badgePane.setVisible(false);
            }
        };
        controller.setNotificationCallback(updateBadge);
        updateBadge.run(); // Initial check

        // Bacheca button action
        bachecaButton.setOnAction(_ -> {
            LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Opening Bacheca notification",
                    Thread.currentThread().getName()));
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Bacheca - MindLab");
            alert.setHeaderText("Ultime notifiche e staff updates");
            alert.setContentText("Non ci sono nuovi annunci in bacheca al momento.");
            alert.showAndWait();
        });

        // Home button is already on home, so no action needed
        homeButton.setOnAction(_ -> LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Already on Home dashboard",
                Thread.currentThread().getName())));

        // Visite button navigates to Visits view
        visiteButton.setOnAction(_ -> navigateToView("Visits", config, stage));

        // Create footer using helper for first 3 buttons
        HBox footer = DashboardStyleHelper.createFooter(1, bachecaButton, homeButton, visiteButton);

        // Notifiche button action
        notificheButton.setOnAction(_ -> showNotificationsDialog());

        // Manually style notifiche button to match footer
        String footerButtonStyle = String.format(
                "-fx-background-color: transparent; -fx-text-fill: %s; " +
                        "-fx-font-size: %dpx; -fx-padding: 12 8 12 8; -fx-border-width: 0; " +
                        "-fx-font-weight: normal; -fx-cursor: hand;",
                DashboardStyleHelper.COLOR_TEXT_SECONDARY,
                DashboardStyleHelper.FONT_SIZE_FOOTER);
        notificheButton.setStyle(footerButtonStyle);

        // Hover effect for notifiche button (manual since it's not in the helper loop)
        notificheButton.setOnMouseEntered(_ -> notificheButton.setStyle(
                footerButtonStyle.replace(DashboardStyleHelper.COLOR_TEXT_SECONDARY,
                        DashboardStyleHelper.COLOR_PRIMARY)));
        notificheButton.setOnMouseExited(_ -> notificheButton.setStyle(footerButtonStyle));

        // Add the wrapper to the footer
        footer.getChildren().add(notificheWrapper);

        return footer;
    }

    private void showNotificationsDialog() {
        java.util.List<model.Visita> notifications = controller.getUnreadNotifications();
        if (notifications.isEmpty()) {
            showInfoAlert("Notifiche", "Non hai nuove notifiche.");
            return;
        }

        StringBuilder content = new StringBuilder();
        for (model.Visita v : notifications) {
            String patientName = controller.getPatientName(v.getPazienteCodiceFiscale());
            content.append(String.format("- %s: %s con %s\n",
                    v.getData(), v.getTipo_visita(), patientName));
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Notifiche non lette");
        alert.setHeaderText("Hai " + notifications.size() + " nuove prenotazioni!");
        alert.setContentText(content.toString());
        alert.setOnHidden(_ -> controller.clearNotifications()); // Clear on close
        alert.showAndWait();
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

    private void showInfoAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(message);
            alert.showAndWait();
        });
    }
}
