package startupconfig; // Dichiarazione del package di appartenenza

// Classe Bean (Data Transfer Object) per trasportare le configurazioni di avvio
// Serve a disaccoppiare la logica di visualizzazione (View) dalla logica di controllo (Controller)
public class StartupConfigBean {
    private boolean interfaceMode; // Variabile booleana per la modalità interfaccia (true = GUI, false = CLI)
    private int storageOption; // Intero che rappresenta l'opzione di storage (0 = RAM, 1 = DB, 2 = FS)

    // Costruttore vuoto (no-args constructor) necessario per le specifiche
    // JavaBeans
    public StartupConfigBean() {
    }

    // Costruttore completo per inizializzare tutti i campi in una volta
    public StartupConfigBean(boolean interfaceMode, int storageOption) {
        this.interfaceMode = interfaceMode; // Assegna il valore passato al campo interfaceMode
        this.storageOption = storageOption; // Assegna il valore passato al campo storageOption
    }

    // Metodo getter per ottenere la modalità interfaccia
    public boolean isInterfaceMode() {
        return interfaceMode; // Restituisce il valore corrente di interfaceMode
    }

    // Metodo setter per impostare la modalità interfaccia
    public void setInterfaceMode(boolean interfaceMode) {
        this.interfaceMode = interfaceMode; // Aggiorna il valore di interfaceMode
    }

    // Metodo getter per ottenere l'opzione di storage selezionata
    public int getStorageOption() {
        return storageOption; // Restituisce il valore corrente di storageOption
    }

    // Metodo setter per impostare l'opzione di storage
    public void setStorageOption(int storageOption) {
        this.storageOption = storageOption; // Aggiorna il valore di storageOption
    }
}
