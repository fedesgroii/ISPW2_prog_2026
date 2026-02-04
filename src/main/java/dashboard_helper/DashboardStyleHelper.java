package dashboard_helper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

/**
 * Utility class for generating consistent styles across all dashboard views.
 * Provides shared color constants, layout components, and styling methods
 * to ensure visual consistency between Patient and Specialist dashboards.
 */
public class DashboardStyleHelper {

    // ==================== COLOR CONSTANTS ====================

    /**
     * Primary color - Used for headers, buttons, and main UI elements (medical
     * green)
     */
    public static final String COLOR_PRIMARY = "#1E8449"; // Vibrant medical green

    /** Secondary color - Used for hover effects and highlights */
    public static final String COLOR_SECONDARY = "#157347"; // Darker green for hover

    /** Background color - Light cream-green, like the HTML page */
    public static final String COLOR_BACKGROUND = "#F0F4E8";

    /** Card background color - Pure white for Shop card */
    public static final String COLOR_CARD_BACKGROUND = "#FFFFFF";

    /** Green card background - For "Prenota" and "Storico" cards */
    public static final String COLOR_CARD_GREEN = "#1E8449";

    /** Text color for titles (black, as in HTML) */
    public static final String COLOR_TEXT_TITLE = "#202124";

    /** Text color for descriptions and secondary content */
    public static final String COLOR_TEXT_SECONDARY = "#5F6368";

    /** Border color for white cards */
    public static final String COLOR_CARD_BORDER = "#DDDDDD";

    // ==================== SPACING CONSTANTS ====================

    /** Root container padding */
    public static final double PADDING_ROOT = 24.0;

    /** Spacing between cards */
    public static final double SPACING_CARDS = 20.0;

    /** Internal card padding */
    public static final double PADDING_CARD = 20.0;

    /** Header and footer padding */
    public static final double PADDING_HEADER_FOOTER = 16.0;

    /** Footer height */
    public static final double HEIGHT_FOOTER = 56.0;

    // ==================== FONT SIZES ====================

    /** Title font size (headers and card titles) */
    public static final int FONT_SIZE_TITLE = 20;

    /** Description font size */
    public static final int FONT_SIZE_DESCRIPTION = 15;

    /** Button font size */
    public static final int FONT_SIZE_BUTTON = 15;

    /** Footer button font size */
    public static final int FONT_SIZE_FOOTER = 16;

    // Private constructor to prevent instantiation of utility class
    private DashboardStyleHelper() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Creates a styled header box with left and right text components.
     * 
     * @param leftText  Text to display on the left side (e.g., "Home - MindLab")
     * @param rightText Text to display on the right side (e.g., "Ciao, [Nome]!")
     * @return Configured HBox with proper styling and layout
     */
    public static HBox createHeaderBox(String leftText, String rightText) {
        Text left = new Text(leftText);
        left.setFont(Font.font("System", FontWeight.BOLD, FONT_SIZE_TITLE));
        left.setFill(Color.web(COLOR_TEXT_TITLE));

        Text right = new Text(rightText);
        right.setFont(Font.font("System", FontWeight.NORMAL, FONT_SIZE_TITLE));
        right.setFill(Color.web(COLOR_TEXT_TITLE));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(left, spacer, right);
        header.setPadding(new Insets(PADDING_HEADER_FOOTER));
        header.setAlignment(Pos.CENTER_LEFT);
        header.setStyle(String.format(
                "-fx-background-color: %s;",
                COLOR_BACKGROUND)); // No border, clean look

        return header;
    }

    /**
     * Creates a content card with title, description, and an optional button.
     * 
     * @param title       Card title text
     * @param description Card description text
     * @param buttonText  Text for the action button (null for no button)
     * @return Configured VBox representing a card with shadow effect
     */
    public static VBox createCard(String title, String description, String buttonText) {
        Text titleText = new Text(title);
        titleText.setFont(Font.font("System", FontWeight.BOLD, FONT_SIZE_TITLE));
        titleText.setFill(Color.web(COLOR_TEXT_TITLE));

        Text descText = new Text(description);
        descText.setFont(Font.font("System", FontWeight.NORMAL, FONT_SIZE_DESCRIPTION));
        descText.setFill(Color.web(COLOR_TEXT_SECONDARY));
        descText.setWrappingWidth(420);

        VBox card = new VBox(12);
        card.getChildren().addAll(titleText, descText);

        // Add button if provided
        if (buttonText != null && !buttonText.isEmpty()) {
            Button button = createStyledButton(buttonText, true);
            card.getChildren().add(button);
        }

        card.setPadding(new Insets(PADDING_CARD));
        // Default: white card (e.g., Shop)
        String style = String.format(
                "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1; -fx-border-radius: 6;",
                COLOR_CARD_BACKGROUND, COLOR_CARD_BORDER);
        card.setStyle(style);

        // Apply green background for specific cards? → handled externally (e.g., in
        // view)
        // For now: this method creates generic card; caller can override style if
        // needed
        // But to match your HTML: we’ll let the view set green via .setStyle() after
        // creation
        // So we keep base as white — minimal change.

        return card;
    }

    /**
     * Creates a styled button with consistent appearance and hover effects.
     * 
     * @param text      Button text
     * @param isPrimary Whether this is a primary action button (affects styling)
     * @return Configured Button with proper styling
     */
    public static Button createStyledButton(String text, boolean isPrimary) {
        Button button = new Button(text);

        String baseColor = isPrimary ? COLOR_PRIMARY : "#DADCE0";
        String textColor = isPrimary ? "white" : "#202124";

        String baseStyle = String.format(
                "-fx-background-color: %s; -fx-text-fill: %s; -fx-font-weight: bold; " +
                        "-fx-font-size: %dpx; -fx-padding: 10 24 10 24; -fx-cursor: hand; " +
                        "-fx-background-radius: 6;",
                baseColor,
                textColor,
                FONT_SIZE_BUTTON);

        button.setStyle(baseStyle);

        // Hover effect
        button.setOnMouseEntered(_ -> {
            String hoverColor = isPrimary ? COLOR_SECONDARY : "#BDC1C6";
            button.setStyle(baseStyle.replace(baseColor, hoverColor));
        });

        button.setOnMouseExited(_ -> button.setStyle(baseStyle));

        return button;
    }

    /**
     * Creates a footer navigation bar with provided buttons.
     * The first button is styled as active (highlighted).
     * 
     * @param buttons Varargs of buttons to include in footer
     * @return Configured HBox footer with proper styling
     */
    public static HBox createFooter(int activeIndex, Button... buttons) {
        HBox footer = new HBox(32);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(0, PADDING_HEADER_FOOTER, 0, PADDING_HEADER_FOOTER));
        footer.setStyle(String.format("-fx-background-color: %s;", COLOR_BACKGROUND));
        footer.setMinHeight(HEIGHT_FOOTER);
        footer.setMaxHeight(HEIGHT_FOOTER);

        for (int i = 0; i < buttons.length; i++) {
            Button btn = buttons[i];
            boolean isActive = (i == activeIndex);

            String footerButtonStyle = String.format(
                    "-fx-background-color: transparent; -fx-text-fill: %s; " +
                            "-fx-font-size: %dpx; -fx-padding: 12 8 12 8; -fx-border-width: 0; " +
                            "-fx-font-weight: %s; -fx-cursor: hand;",
                    isActive ? COLOR_PRIMARY : "#5F6368",
                    FONT_SIZE_FOOTER,
                    isActive ? "bold" : "normal");

            btn.setStyle(footerButtonStyle);

            if (!isActive) {
                btn.setOnMouseEntered(_ -> btn.setStyle(
                        footerButtonStyle.replace("#5F6368", "#1E8449")));
                btn.setOnMouseExited(_ -> btn.setStyle(footerButtonStyle));
            }

            footer.getChildren().add(btn);
        }

        return footer;
    }

    /**
     * Applies a drop shadow effect to a node (typically used for cards).
     * 
     * @param node The node to apply shadow to
     */
    public static void applyCardShadow(Node node) {
        // Not used — cards are flat in your design
        // Left as-is for backward compatibility, but not called by createCard()
        DropShadow shadow = new DropShadow();
        shadow.setRadius(6.0);
        shadow.setOffsetX(0.0);
        shadow.setOffsetY(1.0);
        shadow.setColor(Color.web("#E0E0E0"));
        shadow.setSpread(0.05);
        node.setEffect(shadow);
    }

    /**
     * Creates a root container VBox with proper background and spacing.
     * 
     * @return Configured VBox for dashboard root
     */
    public static VBox createRootContainer() {
        VBox root = new VBox(SPACING_CARDS);
        root.setPadding(new Insets(PADDING_ROOT));
        root.setStyle(String.format("-fx-background-color: %s;", COLOR_BACKGROUND));
        return root;
    }
}