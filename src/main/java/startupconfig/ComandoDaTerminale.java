package startupconfig; // Dichiarazione del package di appartenenza

import java.io.BufferedReader; // Importa classe per leggere testo da un flusso di input
import java.io.IOException; // Importa eccezione per errori di I/O
import java.io.InputStreamReader; // Importa classe per convertire byte stream in character stream
import java.util.logging.Level; // Importa livelli di logging (INFO, SEVERE, ecc.)
import java.util.logging.Logger; // Importa la classe Logger per il tracciamento degli eventi

// Classe di utilità per eseguire comandi di sistema tramite terminale
public class ComandoDaTerminale {
    // Inizializza il logger per registrare eventi e messaggi di errore relativi a
    // questa classe
    private static final Logger logger = Logger.getLogger(ComandoDaTerminale.class.getName());

    // Metodo main per testare la funzionalità indipendentemente
    public static void main(String[] args) {
        // Definisce un comando di esempio per avviare il server MySQL
        String comando = "sudo /usr/local/mysql/support-files/mysql.server start";
        eseguiComando(comando); // Invoca il metodo per eseguire il comando
    }

    // Metodo che esegue un comando specificato nella shell del sistema
    public static void eseguiComando(String comando) {
        try {
            // Prepara il processo per eseguire il comando usando bash
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", comando);
            processBuilder.redirectErrorStream(true); // Reindirizza lo standard error nello standard output per
                                                      // catturare tutti i messaggi

            // Avvia il processo del sistema operativo
            Process process = processBuilder.start();

            // Crea un reader per leggere l'output del processo riga per riga
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line; // Variabile temporanea per memorizzare ogni riga letta

            // Ciclo per leggere e loggare l'output finché ci sono righe disponibili
            while ((line = reader.readLine()) != null) {
                logger.info(line); // Registra ogni riga dell'output nel log
            }

            // Attende che il processo termini e recupera il codice di uscita
            int exitCode = process.waitFor();
            // Registra il codice di terminazione (0 solitamente indica successo)
            logger.log(Level.INFO, "Processo terminato con codice: {0}", exitCode);

        } catch (IOException | InterruptedException e) { // Gestisce eccezioni di I/O o interruzione del thread
            // Registra l'errore con livello SEVERE se l'esecuzione fallisce
            logger.log(Level.SEVERE, e, () -> "Errore durante l'esecuzione del comando: " + comando);

            // Se l'eccezione è di tipo InterruptedException, ripristina lo stato di
            // interruzione del thread
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt(); // Segnala che il thread corrente è stato interrotto
            }
        }
    }

    // Metodo specifico per avviare il server SQL, incapsulando il comando
    // necessario
    public static void avviaServerSQL() {
        // Chiama il metodo generico con il comando specifico per MySQL su macOS
        eseguiComando("sudo /usr/local/mysql/support-files/mysql.server start");
    }
}