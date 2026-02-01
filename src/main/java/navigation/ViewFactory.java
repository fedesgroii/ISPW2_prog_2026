package navigation; // Package di appartenenza

// Abstract Factory Class: definisce il contratto per la creazione delle viste
// Le sottoclassi concrete implementeranno il metodo factory per creare viste specifiche (GUI o CLI)
public abstract class ViewFactory {

    private static final java.util.logging.Logger LOGGER = java.util.logging.Logger
            .getLogger(ViewFactory.class.getName());

    /**
     * Metodo statico di utility per ottenere la factory corretta.
     */
    public static ViewFactory getFactory(boolean isGui) {
        LOGGER.info(() -> String.format("[DEBUG][Thread: %s] Entering ViewFactory.getFactory: isGui=%b",
                Thread.currentThread().getName(), isGui));
        return isGui ? new GuiViewFactory() : new CliViewFactory();
    }

    // Factory Method astratto: ogni sottoclasse concreta implementa questo metodo
    // per creare le viste del proprio tipo (GUI o CLI)
    // viewName: identificatore della vista richiesta (es. "Login", "Patient",
    // "Specialist")
    // Restituisce una View concreta o null se il viewName non è riconosciuto
    public abstract View createView(String viewName);
}
