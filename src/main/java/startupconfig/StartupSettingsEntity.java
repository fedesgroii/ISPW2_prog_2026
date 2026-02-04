package startupconfig; // Dichiarazione del package di appartenenza

import java.io.Serializable; // Importa interfaccia per rendere l'oggetto serializzabile (salvabile su disco/rete)

// Classe Model (Entity) che rappresenta i dati di configurazione
// Implementa Serializable per permettere la persistenza dello stato se necessario
@SuppressWarnings("java:S6548")
public class StartupSettingsEntity implements Serializable {
    // Identificativo univoco per la serializzazione, garantisce compatibilità tra
    // versioni
    private static final long serialVersionUID = 1L;

    // Classe interna statica per implementare il pattern Singleton in modo
    // thread-safe (Bill Pugh Singleton)
    // Questa classe viene caricata solo quando viene richiamata getInstance()
    private static class SingletonHelper {
        // Crea l'unica istanza statica della classe StartupSettingsEntity
        private static final StartupSettingsEntity INSTANCE = new StartupSettingsEntity();
    }

    // Variabili di istanza per memorizzare le scelte di configurazione dell'utente
    private boolean interfaceMode; // true = Modalità GUI, false = Modalità CLI
    private int storageOption; // Codice intero per il tipo di storage: 0 = RAM, 1 = DB, 2 = File System

    // Costruttore protetto per impedire l'istanziazione diretta dall'esterno
    // Fondamentale per garantire l'unicità dell'istanza (pattern Singleton)
    private StartupSettingsEntity() {
        // Imposta i valori di default alla creazione dell'istanza
        this.interfaceMode = true; // Imposta di default la modalità grafica (GUI)
        this.storageOption = 0; // Imposta di default il salvataggio in RAM
    }

    // Metodo pubblico statico per ottenere l'istanza unica della classe
    // Punto di accesso globale ai dati di configurazione
    public static StartupSettingsEntity getInstance() {
        return SingletonHelper.INSTANCE; // Restituisce l'istanza creata nella classe helper
    }

    /**
     * Metodo protetto readResolve per garantire il rispetto del pattern Singleton
     * durante la deserializzazione.
     * Se l'oggetto viene deserializzato, restituisce l'istanza esistente invece di
     * crearne una nuova.
     * Questo risolve il code smell di SonarQube relativo ai Singleton
     * serializzabili.
     * 
     * @return L'unica istanza esistente della classe
     */
    protected Object readResolve() { // messo se un giorno voglio salvare su file la configurazione e deserializzare
                                     // tramite il
                                     // readResolve
        return getInstance();
    }

    // --- Metodi Getter e Setter per accedere e modificare i dati incapsulati ---

    // Restituisce la modalità di interfaccia corrente
    public boolean isInterfaceMode() {
        return interfaceMode;
    }

    // Imposta la modalità di interfaccia
    public void setInterfaceMode(boolean interfaceMode) {
        this.interfaceMode = interfaceMode; // Aggiorna il valore della variabile d'istanza
    }

    // Restituisce l'opzione di storage corrente
    public int getStorageOption() {
        return storageOption;
    }

    // Imposta l'opzione di storage
    public void setStorageOption(int storageOption) {
        this.storageOption = storageOption; // Aggiorna il valore della variabile d'istanza
    }

}
