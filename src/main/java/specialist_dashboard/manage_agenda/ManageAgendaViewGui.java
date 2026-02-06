package specialist_dashboard.manage_agenda;

import dashboard_helper.DashboardStyleHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import navigation.View;
import startupconfig.StartupConfigBean;

import java.util.List;
import java.util.logging.Logger;

/**
 * View for managing the Specialist's Agenda.
 * Allows viewing and rejecting future appointments.
 */
public class ManageAgendaViewGui implements View {

    private static final Logger LOGGER = Logger.getLogger(ManageAgendaViewGui.class.getName());
    private ManagerAgendaControllerApp appController;
    private ManagerAgendaGraphicControllerGui graphicController;
    private VBox visitsContainer;
    private StartupConfigBean config;

    @Override
    public void show(Stage stage, StartupConfigBean config) {
        this.config = config;
        if (Platform.isFxApplicationThread()) {
            startView(stage);
        } else {
            Platform.runLater(() -> startView(stage));
        }
    }

    private void startView(Stage stage) {
        try {
            appController = new ManagerAgendaControllerApp(config);
            graphicController = new ManagerAgendaGraphicControllerGui();
            appController.checkSession();

            VBox root = buildUI(stage);

            if (stage.getScene() == null) {
                Scene scene = new Scene(root, 800, 700);
                stage.setScene(scene);
            } else {
                stage.getScene().setRoot(root);
            }
            stage.setTitle("Gestione Agenda - MindLab");
        } catch (Exception e) {
            LOGGER.severe("Error showing Agenda View: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR, "Errore caricamento agenda: " + e.getMessage());
            alert.showAndWait();
        }
    }

    private VBox buildUI(Stage stage) {
        VBox root = DashboardStyleHelper.createRootContainer();

        // Header
        HBox header = DashboardStyleHelper.createHeaderBox("La tua Agenda", "Gestisci i tuoi appuntamenti");

        // Main Content - Scrollable List of Visits
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        visitsContainer = new VBox(10);
        visitsContainer.setPadding(new Insets(20));

        loadVisits();

        scrollPane.setContent(visitsContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Footer - Back Button
        Button backButton = new Button("Indietro");
        styleButton(backButton, false);
        backButton.setOnAction(_ -> navigateBack(stage));

        HBox footer = new HBox(backButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.setPadding(new Insets(20));
        footer.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef; -fx-border-width: 1 0 0 0;");

        root.getChildren().addAll(header, scrollPane, footer);
        return root;
    }

    private void loadVisits() {
        visitsContainer.getChildren().clear();

        List<ManageAgendaBean> visits = graphicController.getFutureVisits(appController);

        if (visits.isEmpty()) {
            Text noVisits = new Text("Nessun appuntamento futuro in programma.");
            noVisits.setStyle("-fx-font-size: 16px; -fx-fill: #6c757d;");
            visitsContainer.getChildren().add(noVisits);
        } else {
            for (ManageAgendaBean v : visits) {
                visitsContainer.getChildren().add(createVisitCard(v));
            }
        }
    }

    private HBox createVisitCard(ManageAgendaBean v) {
        HBox card = new HBox(15);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setPadding(new Insets(15));
        card.setStyle("-fx-background-color: white; -fx-background-radius: 8; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 3, 0, 0, 1);");

        VBox info = new VBox(5);
        Text date = new Text(v.getDate().toString() + " - " + v.getTime().toString());
        date.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Text patient = new Text("Paziente: " + v.getPatientName());
        Text type = new Text(v.getType() + " - " + v.getReason());

        info.getChildren().addAll(date, patient, type);
        HBox.setHgrow(info, Priority.ALWAYS);

        Button rejectBtn = new Button("Rifiuta");
        styleButton(rejectBtn, true);
        rejectBtn.setOnAction(_ -> handleReject(v));

        card.getChildren().addAll(info, rejectBtn);
        return card;
    }

    private void handleReject(ManageAgendaBean v) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Sei sicuro di voler rifiutare questa visita?");
        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                boolean success = graphicController.rejectVisit(appController, v);
                if (success) {
                    Alert successAlert = new Alert(Alert.AlertType.INFORMATION, "Visita rifiutata con successo.");
                    successAlert.showAndWait();
                    loadVisits(); // Refresh
                } else {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Impossibile rifiutare la visita.");
                    errorAlert.showAndWait();
                }
            }
        });
    }

    private void styleButton(Button btn, boolean isDanger) {
        String baseStyle = "-fx-font-size: 14px; -fx-padding: 8 16; -fx-cursor: hand; -fx-background-radius: 4;";
        if (isDanger) {
            btn.setStyle(baseStyle + "-fx-background-color: #dc3545; -fx-text-fill: white;");
        } else {
            btn.setStyle(baseStyle + "-fx-background-color: #6c757d; -fx-text-fill: white;");
        }
    }

    private void navigateBack(Stage stage) {
        graphicController.navigateToSpecialistDashboard(config, stage);
    }
}
