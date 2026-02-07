package patient_dashboard;

/**
 * Encapsulates navigation instructions from the Application Controller.
 * Decouples the controller from specific UI framework knowledge.
 */
public class NavigationInstruction {
    private final String viewName;

    /**
     * Creates a new navigation instruction.
     * 
     * @param viewName The logical name of the view to navigate to.
     */
    public NavigationInstruction(String viewName) {
        this.viewName = viewName;
    }

    /**
     * Gets the logical name of the target view.
     * 
     * @return The view name.
     */
    public String getViewName() {
        return viewName;
    }
}
