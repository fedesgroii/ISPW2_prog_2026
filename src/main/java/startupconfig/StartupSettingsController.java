package startupconfig; // Dichiarazione del package di appartenenza

// Importa le classi necessarie se presenti (in questo caso nessuna importazione esterna è necessaria oltre al package locale)

// Classe Controller secondo il pattern MVC
// Responsabile della gestione della logica applicativa relativa alla configurazione di avvio
public class StartupSettingsController {
    // Riferimento all'Entity (Model) che mantiene lo stato della configurazione
    private final StartupSettingsEntity config;

    // Costruttore del controller
    public StartupSettingsController() {
        // Ottiene l'istanza Singleton dell'Entity
        // Questo garantisce che il controller operi sull'unica istanza condivisa dei
        // dati
        this.config = StartupSettingsEntity.getInstance();
    }

    /**
     * Metodo principale per elaborare le impostazioni ricevute dalla View.
     * Riceve un Bean (DTO) per disaccoppiare il controller dai dettagli della UI.
     * 
     * @param configBean Il bean contenente le configurazioni (modalità interfaccia
     *                   e storage) scelte dall'utente
     */
    public void processSettings(StartupConfigBean configBean) {
        // Aggiorna la modalità interfaccia nel Model usando il valore del Bean
        config.setInterfaceMode(configBean.isInterfaceMode());

        // Aggiorna l'opzione di storage nel Model usando il valore del Bean
        config.setStorageOption(configBean.getStorageOption());

        // Verifica se l'opzione di storage scelta è Database (valore 1)
        if (configBean.getStorageOption() == 1) {
            // Se è Database, invoca il metodo statico per avviare il servizio SQL
            // Questa è una chiamata a una logica di sistema esterna
            ComandoDaTerminale.avviaServerSQL();
        }
    }
}