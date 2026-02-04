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
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.logging.Logger;

/**
 * GUI Boundary for booking an appointment.
 * Replicates the design of prenotazione_visita.html using JavaFX.
 */
public class BookAppointmentViewGui implements View {
    private static final Logger LOGGER = Logger.getLogger(BookAppointmentViewGui.class.getName());
    private final BookAppointmentGraphicController graphicController = new BookAppointmentGraphicController();
    private final BookAppointmentControllerApp appController = new BookAppointmentControllerApp();

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

        // Header mimicking HTML
        VBox header = createHeader();

        // Form mimicking HTML structure
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-padding: 0;");

        VBox formContainer = new VBox(12);
        formContainer.setPadding(new Insets(20, 40, 20, 40));
        formContainer.setMaxWidth(460); // Reduced size for better look
        formContainer.setStyle(
                "-fx-background-color: white; -fx-border-color: #e5e7eb; -fx-border-radius: 12; -fx-background-radius: 12;");

        // Fetch specialists from controller
        java.util.List<model.Specialista> availableSpecialists = appController.getAvailableSpecialists(config);

        // Fields
        ComboBox<String> serviceTypeCombo = new ComboBox<>();
        serviceTypeCombo.getItems().addAll("Online", "In presenza");
        serviceTypeCombo.setPromptText("Seleziona un'opzione...");
        serviceTypeCombo.setMaxWidth(Double.MAX_VALUE);

        ComboBox<model.Specialista> specialistCombo = new ComboBox<>();
        specialistCombo.getItems().addAll(availableSpecialists);
        specialistCombo.setPromptText("Seleziona uno specialista...");
        specialistCombo.setMaxWidth(Double.MAX_VALUE);
        specialistCombo.setConverter(new javafx.util.StringConverter<model.Specialista>() {
            @Override
            public String toString(model.Specialista s) {
                return (s == null) ? "" : s.getNome() + " " + s.getCognome() + " (" + s.getSpecializzazione() + ")";
            }

            @Override
            public model.Specialista fromString(String string) {
                return null; // Not needed for read-only ComboBox
            }
        });

        TextField nameField = createTextField("Nome");
        TextField surnameField = createTextField("Cognome");
        TextField dobField = createTextField("Data di nascita (GG/MM/AAAA)");
        TextField phoneField = createTextField("Numero di telefono");
        TextField emailField = createTextField("E-mail");
        TextField reasonField = createTextField("Motivo visita (facoltativo)");

        // Additional fields for Visita model (needed for functional booking)
        DatePicker datePicker = new DatePicker();
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setPromptText("Scegli la data");

        TextField timeField = createTextField("Orario (HH:mm)");

        // Layout
        formContainer.getChildren().addAll(
                createLabel("Tipo di prestazione"), serviceTypeCombo,
                createLabel("Specialista"), specialistCombo,
                createLabel("Nome"), nameField,
                createLabel("Cognome"), surnameField,
                createLabel("Data di nascita"), dobField,
                createLabel("Telefono"), phoneField,
                createLabel("Email"), emailField,
                createLabel("Data Visita"), datePicker,
                createLabel("Orario Visita"), timeField,
                createLabel("Motivo"), reasonField);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setPadding(new Insets(20, 0, 0, 0));

        Button cancelButton = DashboardStyleHelper.createStyledButton("Annulla", false);
        Button submitButton = DashboardStyleHelper.createStyledButton("Prenota ora", true);

        cancelButton.setOnAction(_ -> graphicController.navigateToDashboard(config, stage));
        submitButton.setOnAction(_ -> {
            BookAppointmentBean bean = new BookAppointmentBean();
            bean.setServiceType(serviceTypeCombo.getValue());
            model.Specialista selectedSpec = specialistCombo.getValue();
            if (selectedSpec != null) {
                bean.setSpecialist(selectedSpec.getNome() + " " + selectedSpec.getCognome());
            }
            bean.setName(nameField.getText());
            bean.setSurname(surnameField.getText());
            bean.setDateOfBirth(dobField.getText());
            bean.setPhone(phoneField.getText());
            bean.setEmail(emailField.getText());
            bean.setReason(reasonField.getText());
            bean.setDate(datePicker.getValue());
            try {
                if (timeField.getText() != null && !timeField.getText().isEmpty()) {
                    bean.setTime(LocalTime.parse(timeField.getText()));
                }
            } catch (DateTimeParseException _) {
                // Controller will handle validation error
            }

            graphicController.bookAppointment(bean, config, stage);
        });

        buttonBox.getChildren().addAll(cancelButton, submitButton);
        formContainer.getChildren().add(buttonBox);

        // Center the form container horizontally
        StackPane centeredWrapper = new StackPane(formContainer);
        centeredWrapper.setAlignment(Pos.CENTER);
        centeredWrapper.setPadding(new Insets(0, 0, 40, 0)); // Bottom margin

        scrollPane.setContent(centeredWrapper);
        rootContent.getChildren().addAll(header, scrollPane);

        // Fluid Transition: Swap root if scene exists, otherwise create new Scene
        if (stage.getScene() == null) {
            Scene scene = new Scene(rootContent, 800, 750);
            stage.setScene(scene);
        } else {
            stage.getScene().setRoot(rootContent);
        }

        stage.setTitle("MindLab - Prenotazione");
        stage.setFullScreen(true);
        stage.setFullScreenExitHint("");
        stage.show();
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
