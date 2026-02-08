package patient_dashboard.book_appointment;

import dashboard_helper.DashboardStyleHelper;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import navigation.View;
import startupconfig.StartupConfigBean;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;
import session_manager.SessionManagerPaziente;
import model.Paziente;

/**
 * GUI Boundary for booking an appointment.
 * Replicates the design of prenotazione_visita.html using JavaFX.
 */
public class BookAppointmentViewGui implements View {
    private static final Logger LOGGER = Logger.getLogger(BookAppointmentViewGui.class.getName());
    private static final String CSS_PATH = "/style/style_prenotazione_visita_view_a_colori.css";
    private final BookAppointmentGraphicControllerGui graphicController = new BookAppointmentGraphicControllerGui();

    // UI Components promoted to fields
    private final ComboBox<String> serviceTypeCombo = new ComboBox<>();
    private final ComboBox<model.Specialista> specialistCombo = new ComboBox<>();
    private final TextField nameField = createTextField("Nome");
    private final TextField surnameField = createTextField("Cognome");
    private final TextField dobField = createTextField("Data di nascita (GG/MM/AAAA)");
    private final TextField phoneField = createTextField("Numero di telefono");
    private final TextField emailField = createTextField("E-mail");
    private final TextField reasonField = createTextField("Motivo visita (facoltativo)");
    private final DatePicker datePicker = createDatePicker();
    private final ComboBox<String> timeCombo = new ComboBox<>();

    @Override
    public void show(Stage stage, StartupConfigBean config) {
        if (Platform.isFxApplicationThread()) {
            initUI(stage, config);
        } else {
            Platform.runLater(() -> initUI(stage, config));
        }
    }

    private void initUI(Stage stage, StartupConfigBean config) {
        VBox rootContent = DashboardStyleHelper.createRootContainer();
        rootContent.setAlignment(Pos.TOP_CENTER);
        rootContent.setSpacing(15);

        setupScene(stage, rootContent);

        // Header
        VBox header = createHeader();

        // Form
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        VBox formContainer = new VBox(12);
        formContainer.setPadding(new Insets(20, 40, 20, 40));
        formContainer.setMaxWidth(460);
        formContainer.setStyle(
                "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-background-radius: 12;");

        // UI Components initialization
        serviceTypeCombo.getItems().addAll("Online", "In presenza");
        serviceTypeCombo.setPromptText("Seleziona un'opzione...");
        serviceTypeCombo.setMaxWidth(Double.MAX_VALUE);

        setupSpecialistCombo(graphicController.getAvailableSpecialists(config));

        timeCombo.setPromptText("Scegli prima data e specialista");
        timeCombo.setDisable(true);

        // Pre-fill fields with logged-in patient data
        if (SessionManagerPaziente.isLoggedIn()) {
            Paziente logged = SessionManagerPaziente.getPazienteLoggato();
            nameField.setText(logged.getNome());
            surnameField.setText(logged.getCognome());
            if (logged.getDataDiNascita() != null) {
                dobField.setText(logged.getDataDiNascita().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            }
            phoneField.setText(logged.getNumeroTelefonico());
            emailField.setText(logged.getEmail());
        }

        setupSlotUpdateListener(datePicker, specialistCombo, timeCombo);

        formContainer.getChildren().addAll(
                createLabel("Tipo di prestazione"), serviceTypeCombo,
                createLabel("Specialista"), specialistCombo,
                createLabel("Nome"), nameField,
                createLabel("Cognome"), surnameField,
                createLabel("Data di nascita"), dobField,
                createLabel("Telefono"), phoneField,
                createLabel("Email"), emailField,
                createLabel("Data Visita"), datePicker,
                createLabel("Orario Visita"), timeCombo,
                createLabel("Motivo"), reasonField);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button cancelButton = DashboardStyleHelper.createStyledButton("Annulla", false);
        Button submitButton = DashboardStyleHelper.createStyledButton("Prenota ora", true);

        cancelButton.setOnAction(_ -> graphicController.navigateToDashboard(config, stage));
        submitButton.setOnAction(_ -> graphicController.bookAppointment(this, config, stage));

        buttonBox.getChildren().addAll(cancelButton, submitButton);
        formContainer.getChildren().add(buttonBox);

        StackPane centeredWrapper = new StackPane(formContainer);
        centeredWrapper.setAlignment(Pos.CENTER);
        centeredWrapper.setPadding(new Insets(0, 0, 40, 0));

        scrollPane.setContent(centeredWrapper);
        rootContent.getChildren().addAll(header, scrollPane);

        stage.setTitle("MindLab - Prenotazione");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
    }

    private void setupScene(Stage stage, VBox rootContent) {
        if (stage.getScene() == null) {
            Scene scene = new Scene(rootContent, 800, 750);
            loadStyleSheet(scene);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(rootContent);
            loadStyleSheet(stage.getScene());
        }
    }

    private void setupSpecialistCombo(List<model.Specialista> specialists) {
        specialistCombo.getItems().addAll(specialists);
        specialistCombo.setPromptText("Seleziona uno specialista...");
        specialistCombo.setMaxWidth(Double.MAX_VALUE);
        specialistCombo.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(model.Specialista s) {
                return (s == null) ? "" : s.getNome() + " " + s.getCognome() + " (" + s.getSpecializzazione() + ")";
            }

            @Override
            public model.Specialista fromString(String string) {
                return null;
            }
        });
    }

    private DatePicker createDatePicker() {
        DatePicker dp = new DatePicker();
        dp.setMaxWidth(Double.MAX_VALUE);
        dp.setPromptText("Scegli la data");
        dp.setDayCellFactory(_ -> new DateCell() {
            @Override
            public void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    BookAppointmentBean tempBean = new BookAppointmentBean();
                    tempBean.setDate(item);
                    if (empty || graphicController.validateDate(tempBean) != null) {
                        setDisable(true);
                        setStyle("-fx-background-color: #8a3737;");
                    }
                }
            }
        });
        return dp;
    }

    private void setupSlotUpdateListener(DatePicker datePicker, ComboBox<model.Specialista> specialistCombo,
            ComboBox<String> timeCombo) {
        Runnable updateSlots = () -> {
            LocalDate date = datePicker.getValue();
            model.Specialista specialist = specialistCombo.getValue();
            if (date != null && specialist != null) {
                timeCombo.setDisable(true);
                timeCombo.getItems().clear();
                timeCombo.setPromptText("Caricamento...");

                BookAppointmentBean tempBean = new BookAppointmentBean();
                tempBean.setDate(date);
                tempBean.setSpecialistId(specialist.getId());
                tempBean.setSpecialist(specialist.getNome() + " " + specialist.getCognome());

                CompletableFuture
                        .supplyAsync(() -> graphicController.getAvailableSlots(tempBean))
                        .thenAccept(slots -> Platform.runLater(() -> {
                            if (slots.isEmpty()) {
                                timeCombo.setPromptText("Nessun orario disponibile");
                            } else {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                                List<String> formattedSlots = slots.stream().map(s -> s.format(formatter))
                                        .toList();
                                timeCombo.getItems().setAll(formattedSlots);
                                timeCombo.setDisable(false);
                                timeCombo.setPromptText("Seleziona orario");
                            }
                        }))
                        .exceptionally(ex -> {
                            Platform.runLater(() -> {
                                timeCombo.setPromptText("Errore nel caricamento");
                                LOGGER.severe(() -> "Async slot loading failed: " + ex.getMessage());
                            });
                            return null;
                        });
            }
        };
        datePicker.valueProperty().addListener((_, _, _) -> updateSlots.run());
        specialistCombo.valueProperty().addListener((_, _, _) -> updateSlots.run());
    }

    // Getters for raw data
    public String getServiceType() {
        return serviceTypeCombo.getValue();
    }

    public model.Specialista getSelectedSpecialist() {
        return specialistCombo.getValue();
    }

    public String getName() {
        return nameField.getText();
    }

    public String getSurname() {
        return surnameField.getText();
    }

    public String getDateOfBirth() {
        return dobField.getText();
    }

    public String getPhone() {
        return phoneField.getText();
    }

    public String getEmail() {
        return emailField.getText();
    }

    public String getReason() {
        return reasonField.getText();
    }

    public LocalDate getSelectedDate() {
        return datePicker.getValue();
    }

    public String getSelectedTime() {
        return timeCombo.getValue();
    }

    private void loadStyleSheet(Scene scene) {
        try {
            String styleSheet = java.util.Objects.requireNonNull(
                    getClass().getResource(CSS_PATH),
                    "Resource non trovata: " + CSS_PATH).toExternalForm();
            scene.getStylesheets().add(styleSheet);
        } catch (Exception e) {
            LOGGER.warning("Impossibile caricare il CSS: " + CSS_PATH + ". Errore: " + e.getMessage());
        }
    }

    private VBox createHeader() {
        VBox header = new VBox(8);
        header.setAlignment(Pos.CENTER);

        try {
            // Using local resource for logo
            ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/icone/logo_ML_sfondo.png")));
            logo.setFitWidth(100);
            logo.setPreserveRatio(true);
            header.getChildren().add(logo);
        } catch (Exception e) {
            LOGGER.warning("Could not load logo image: " + e.getMessage());
        }

        Text title = new Text("Prenota Visita");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setFill(javafx.scene.paint.Color.web("#111827"));
        header.getChildren().add(title);

        return header;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #4b5563;");
        return label;
    }

    private TextField createTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle("-fx-padding: 8 10; -fx-background-color: white; -fx-border-color: #d1d5db; -fx-border-radius: 6;");
        return tf;
    }

}
